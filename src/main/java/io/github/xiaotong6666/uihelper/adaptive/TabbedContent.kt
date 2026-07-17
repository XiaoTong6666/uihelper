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

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import io.github.xiaotong6666.uihelper.mode.LocalUiMode
import io.github.xiaotong6666.uihelper.mode.UiMode
import top.yukonga.miuix.kmp.basic.InfiniteProgressIndicator
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

@Composable
fun LazyTabContent(
    contentPadding: PaddingValues,
    pageModifier: Modifier = Modifier,
    content: LazyListScope.() -> Unit,
) {
    when (LocalUiMode.current) {
        UiMode.Material -> LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .then(pageModifier),
            contentPadding = contentPadding,
            content = content,
        )

        UiMode.Miuix -> LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .scrollEndHaptic()
                .overScrollVertical()
                .then(pageModifier),
            contentPadding = contentPadding,
            overscrollEffect = null,
            content = content,
        )
    }
}

@Composable
fun LoadingContentPane(
    loaded: Boolean,
    ready: Boolean = true,
    modifier: Modifier = Modifier,
    placeholderModifier: Modifier = Modifier.fillMaxSize(),
    content: @Composable () -> Unit,
) {
    val contentAlpha = animateFloatAsState(
        targetValue = if (loaded) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "AsyncContentAlpha",
    )
    val placeholderAlpha = animateFloatAsState(
        targetValue = if (loaded) 0f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "AsyncPlaceholderAlpha",
    )

    Box(modifier = modifier.animateContentSize(animationSpec = tween(durationMillis = 300))) {
        if (ready) {
            Box(modifier = Modifier.graphicsLayer { alpha = contentAlpha.value }) {
                content()
            }
        }
        if (placeholderAlpha.value > 0f) {
            Box(
                modifier = placeholderModifier.graphicsLayer { alpha = placeholderAlpha.value },
                contentAlignment = Alignment.Center,
            ) {
                when (LocalUiMode.current) {
                    UiMode.Material -> androidx.compose.material3.LoadingIndicator()
                    UiMode.Miuix -> InfiniteProgressIndicator()
                }
            }
        }
    }
}
