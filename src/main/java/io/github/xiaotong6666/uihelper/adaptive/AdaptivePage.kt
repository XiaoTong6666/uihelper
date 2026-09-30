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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.xiaotong6666.uihelper.mode.LocalUiMode
import io.github.xiaotong6666.uihelper.mode.UiMode
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

@Composable
fun Modifier.adaptiveVerticalScrollFeedback(): Modifier = when (LocalUiMode.current) {
    UiMode.Miuix -> this.scrollEndHaptic().overScrollVertical()
    UiMode.Material -> this
}

@Composable
fun Modifier.adaptiveViewportOverscroll(materialEffect: OverscrollEffect?): Modifier =
    when (LocalUiMode.current) {
        UiMode.Miuix -> this
        UiMode.Material -> if (materialEffect != null) this.overscroll(materialEffect) else this
    }

@Composable
fun adaptiveScrollableOverscrollEffect(materialEffect: OverscrollEffect?): OverscrollEffect? =
    when (LocalUiMode.current) {
        UiMode.Miuix -> null
        UiMode.Material -> materialEffect
    }

@Composable
fun AdaptiveScrollColumn(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = 16.dp,
    verticalPadding: Dp = 16.dp,
    verticalSpacing: Dp = 14.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    val scrollState = rememberScrollState()
    val scrollFeedbackModifier = when (LocalUiMode.current) {
        UiMode.Miuix -> Modifier.scrollEndHaptic().overScrollVertical()
        UiMode.Material -> Modifier
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(scrollFeedbackModifier)
            .then(modifier)
            .verticalScroll(scrollState)
            .padding(contentPadding)
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        verticalArrangement = Arrangement.spacedBy(verticalSpacing),
        content = content,
    )
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    val horizontalPadding = when (LocalUiMode.current) {
        UiMode.Miuix -> 12.dp
        UiMode.Material -> 16.dp
    }

    Column {
        SettingsGroupHeader(title)
        Box(modifier = Modifier.padding(horizontal = horizontalPadding)) {
            SettingsGroup(content)
        }
    }
}
