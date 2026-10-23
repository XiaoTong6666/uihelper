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

package io.github.xiaotong6666.uihelper.miuix.primitive

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.squircle.squircleSurface
import top.yukonga.miuix.kmp.theme.LocalContentColor
import top.yukonga.miuix.kmp.theme.MiuixTheme

internal enum class GroupedCardPosition { Single, First, Middle, Last }

internal fun groupedCardPosition(index: Int, size: Int): GroupedCardPosition {
    require(size > 0 && index in 0 until size)
    return when {
        size == 1 -> GroupedCardPosition.Single
        index == 0 -> GroupedCardPosition.First
        index == size - 1 -> GroupedCardPosition.Last
        else -> GroupedCardPosition.Middle
    }
}

/**
 * A large MIUIX text-row group, with each row hosted in its own lazy item. The first and last
 * segments only round their outside corners; the middle rows form the same uninterrupted card
 * surface as [MiuixTextRowsCard]. No row animations or inter-row gaps are introduced.
 */
fun LazyListScope.miuixGroupedTextRows(
    keyPrefix: String,
    rows: List<MiuixTextRow>,
    bottomPadding: Dp = 0.dp,
    rowPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
) {
    rows.forEachIndexed { index, row ->
        item(key = "${keyPrefix}:${index}:${row.title}") {
            MiuixGroupedTextRow(
                row = row,
                index = index,
                count = rows.size,
                modifier = Modifier.padding(bottom = if (index == rows.lastIndex) bottomPadding else 0.dp),
                rowPadding = rowPadding,
            )
        }
    }
}

@Composable
fun MiuixGroupedTextRow(
    row: MiuixTextRow,
    index: Int,
    count: Int,
    modifier: Modifier = Modifier,
    rowPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
) {
    val position = groupedCardPosition(index, count)
    val backgroundColor = MiuixTheme.colorScheme.surfaceContainer
    val cornerRadius = 16.dp
    val background = when (position) {
        GroupedCardPosition.Middle -> Modifier.background(backgroundColor)
        GroupedCardPosition.Single -> Modifier.squircleSurface(
            backgroundColor, cornerRadius, cornerRadius, cornerRadius, cornerRadius,
        )
        GroupedCardPosition.First -> Modifier.squircleSurface(
            backgroundColor, cornerRadius, cornerRadius, 0.dp, 0.dp,
        )
        GroupedCardPosition.Last -> Modifier.squircleSurface(
            backgroundColor, 0.dp, 0.dp, cornerRadius, cornerRadius,
        )
    }
    CompositionLocalProvider(LocalContentColor provides MiuixTheme.colorScheme.onSurfaceContainer) {
        Column(modifier = modifier.fillMaxWidth().then(background)) {
            BasicComponent(
                insideMargin = rowPadding,
                title = row.title,
                summary = row.summary,
            )
        }
    }
}
