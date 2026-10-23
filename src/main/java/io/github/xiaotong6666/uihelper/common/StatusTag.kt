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

package io.github.xiaotong6666.uihelper.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.xiaotong6666.uihelper.mode.LocalUiMode
import io.github.xiaotong6666.uihelper.mode.UiMode
import top.yukonga.miuix.kmp.basic.Text as MiuixText

/**
 * Compatibility status-tag geometry and type scale; consumers can retain their existing
 * appearance without changing the shapes or font measurements.
 */
private object StatusTagVisuals {
    val MaterialShape = RoundedCornerShape(4.dp)
    val MiuixShape = RoundedCornerShape(6.dp)
    val MiuixFontSize = 9.sp
    val MiuixFontWeight = FontWeight(750)
    val HorizontalPadding = 4.dp
    val VerticalPadding = 2.dp
}

@Composable
fun StatusTag(
    label: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    contentColor: Color,
) {
    when (LocalUiMode.current) {
        UiMode.Material -> MaterialStatusTag(
            label = label,
            modifier = modifier,
            backgroundColor = backgroundColor,
            contentColor = contentColor,
        )

        UiMode.Miuix -> MiuixStatusTag(
            label = label,
            modifier = modifier,
            backgroundColor = backgroundColor,
            contentColor = contentColor,
        )
    }
}

@Composable
private fun MaterialStatusTag(
    label: String,
    modifier: Modifier,
    backgroundColor: Color,
    contentColor: Color,
) {
    Box(
        modifier = modifier
            .padding(end = 4.dp)
            .background(
                color = backgroundColor,
                shape = StatusTagVisuals.MaterialShape,
            ),
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(
                vertical = StatusTagVisuals.VerticalPadding,
                horizontal = StatusTagVisuals.HorizontalPadding,
            ),
            style = MaterialTheme.typography.labelSmallEmphasized,
            color = contentColor,
        )
    }
}

@Composable
private fun MiuixStatusTag(
    label: String,
    modifier: Modifier,
    backgroundColor: Color,
    contentColor: Color,
) {
    Box(
        modifier = modifier.background(
            color = backgroundColor,
            shape = StatusTagVisuals.MiuixShape,
        ),
    ) {
        MiuixText(
            modifier = Modifier.padding(
                horizontal = StatusTagVisuals.HorizontalPadding,
                vertical = StatusTagVisuals.VerticalPadding,
            ),
            text = label,
            color = contentColor,
            fontSize = StatusTagVisuals.MiuixFontSize,
            fontWeight = StatusTagVisuals.MiuixFontWeight,
            maxLines = 1,
            softWrap = false,
        )
    }
}
