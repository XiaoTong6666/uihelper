/*
 * Copyright (C) 2026 XiaoTong6666
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.xiaotong6666.uihelper.extensions.androidapp

import android.content.Context
import android.content.pm.ApplicationInfo
import android.graphics.Bitmap
import android.os.Process
import android.util.Log
import android.util.LruCache
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import me.zhanghai.android.appiconloader.AppIconLoader
import java.util.concurrent.ConcurrentHashMap

object AppIconCache {
    private const val TAG = "AppIconCache"
    private val maxMemoryKb = Runtime.getRuntime().maxMemory() / 1024
    private val cacheSizeKb = (maxMemoryKb / 8).toInt()
    private val loadSemaphore = Semaphore(4)
    private val iconLoaders = ConcurrentHashMap<Int, AppIconLoader>()
    private val remappedUserIds = ConcurrentHashMap.newKeySet<Int>()

    private val memoryCache = object : LruCache<String, Bitmap>(cacheSizeKb) {
        override fun sizeOf(key: String, value: Bitmap): Int = value.allocationByteCount / 1024
    }

    private fun buildKey(appInfo: ApplicationInfo, sizePx: Int): String = "${appInfo.packageName}:${appInfo.uid}:${appInfo.sourceDir}:$sizePx"

    private fun userId(uid: Int): Int = uid / 100000

    private fun ApplicationInfo.withCurrentUserUid(): ApplicationInfo {
        val currentUserId = userId(Process.myUid())
        val appId = uid % 100000
        val targetUid = currentUserId * 100000 + appId
        if (uid == targetUid) return this
        return ApplicationInfo(this).apply { uid = targetUid }
    }

    fun getCached(appInfo: ApplicationInfo, sizePx: Int): Bitmap? {
        val key = buildKey(appInfo, sizePx)
        return memoryCache.get(key)?.takeIf { !it.isRecycled }
    }

    private fun loadIconBitmap(
        context: Context,
        appInfo: ApplicationInfo,
        sizePx: Int,
    ): Bitmap {
        val loader = iconLoaders.getOrPut(sizePx) {
            AppIconLoader(sizePx, false, context.applicationContext)
        }
        val source = if (userId(appInfo.uid) in remappedUserIds) {
            appInfo.withCurrentUserUid()
        } else {
            appInfo
        }
        return try {
            loader.loadIcon(source)
        } catch (e: SecurityException) {
            remappedUserIds += userId(appInfo.uid)
            Log.d(TAG, "Cross-profile icon load denied for ${appInfo.packageName}; retrying with current-user uid")
            try {
                loader.loadIcon(appInfo.withCurrentUserUid())
            } catch (retryError: Throwable) {
                Log.d(TAG, "Remapped AppIconLoader failed for ${appInfo.packageName}, falling back to PackageManager", retryError)
                val drawable = appInfo.loadIcon(context.applicationContext.packageManager)
                drawable.toBitmap(width = sizePx, height = sizePx)
            }
        } catch (t: Throwable) {
            Log.d(TAG, "AppIconLoader failed for ${appInfo.packageName}, falling back to PackageManager", t)
            val drawable = appInfo.loadIcon(context.applicationContext.packageManager)
            drawable.toBitmap(width = sizePx, height = sizePx)
        }
    }

    suspend fun loadIcon(context: Context, appInfo: ApplicationInfo, sizePx: Int): Bitmap? {
        val key = buildKey(appInfo, sizePx)
        getCached(appInfo, sizePx)?.let { return it }

        return loadSemaphore.withPermit {
            getCached(appInfo, sizePx)?.let { return@withPermit it }

            withContext(Dispatchers.IO) {
                val softwareBitmap = loadIconBitmap(context, appInfo, sizePx)
                val bitmap = try {
                    softwareBitmap.copy(Bitmap.Config.HARDWARE, false)?.also {
                        if (it !== softwareBitmap) {
                            softwareBitmap.recycle()
                        }
                    } ?: softwareBitmap.also { it.prepareToDraw() }
                } catch (t: Throwable) {
                    Log.d(TAG, "Failed to copy app icon to hardware bitmap", t)
                    softwareBitmap.also { it.prepareToDraw() }
                }

                memoryCache.put(key, bitmap)
                bitmap
            }
        }
    }

    suspend fun preloadIcons(
        context: Context,
        applicationInfos: Iterable<ApplicationInfo>,
        sizePx: Int,
    ) {
        for (appInfo in applicationInfos) {
            if (getCached(appInfo, sizePx) == null) {
                loadIcon(context, appInfo, sizePx)
            }
            yield()
        }
    }
}
