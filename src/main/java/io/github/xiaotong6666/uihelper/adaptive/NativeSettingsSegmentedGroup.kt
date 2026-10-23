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

@file:Suppress("ktlint:standard:function-naming")

package io.github.xiaotong6666.uihelper.adaptive

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier

internal data class NativeSettingsGroupPosition(val index: Int, val count: Int)

internal val LocalNativeSettingsGroupPosition = compositionLocalOf<NativeSettingsGroupPosition?> { null }

internal data class NativeSettingsGroupEntry(
    val key: Any,
    val visible: Boolean,
    val content: @Composable () -> Unit,
)

/**
 * Registers logical settings rows before composition, so the group (not its callers) owns the
 * visible item count and the first/middle/last shape assignment.
 */
@DslMarker
annotation class NativeSettingsGroupDsl

@NativeSettingsGroupDsl
class NativeSettingsGroupScope {
    internal val entries = mutableListOf<NativeSettingsGroupEntry>()

    fun item(
        key: Any,
        visible: Boolean = true,
        content: @Composable () -> Unit,
    ) {
        require(entries.none { it.key == key }) { "Duplicate settings group key: $key" }
        entries += NativeSettingsGroupEntry(key, visible, content)
    }
}

internal fun NativeSettingsGroupScope.visiblePositions(): List<Pair<NativeSettingsGroupEntry, NativeSettingsGroupPosition>> {
    val visibleEntries = entries.filter { it.visible }
    return visibleEntries.mapIndexed { index, entry ->
        entry to NativeSettingsGroupPosition(index = index, count = visibleEntries.size)
    }
}

/**
 * A single MIUIX Card or M3E segmented group. Declare each visible row once via [NativeSettingsGroupScope.item].
 * A row added, removed, or hidden automatically recalculates the M3E outer and inner corners.
 */
@Composable
fun NativeSettingsSegmentedGroup(
    modifier: Modifier = Modifier,
    content: NativeSettingsGroupScope.() -> Unit,
) {
    val positions = NativeSettingsGroupScope().apply(content).visiblePositions()
    if (positions.isEmpty()) return

    NativeSettingsGroup(modifier = modifier) {
        positions.forEach { (entry, position) ->
            key(entry.key) {
                CompositionLocalProvider(LocalNativeSettingsGroupPosition provides position) {
                    entry.content()
                }
            }
        }
    }
}
