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

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.xiaotong6666.uihelper.material.primitive.MaterialDetailSectionTitle
import io.github.xiaotong6666.uihelper.material.primitive.MaterialDetailSectionsCard
import io.github.xiaotong6666.uihelper.material.primitive.MaterialGroupedTextRow
import io.github.xiaotong6666.uihelper.material.primitive.MaterialHeaderContentCard
import io.github.xiaotong6666.uihelper.material.primitive.MaterialInsetTextPanel
import io.github.xiaotong6666.uihelper.material.primitive.MaterialMetricSummaryCard
import io.github.xiaotong6666.uihelper.material.primitive.MaterialTextRowsCard
import io.github.xiaotong6666.uihelper.material.primitive.groupedTextRowKey
import io.github.xiaotong6666.uihelper.miuix.primitive.MiuixDetailField
import io.github.xiaotong6666.uihelper.miuix.primitive.MiuixDetailSection
import io.github.xiaotong6666.uihelper.miuix.primitive.MiuixDetailSectionsCard
import io.github.xiaotong6666.uihelper.miuix.primitive.MiuixGroupedTextRow
import io.github.xiaotong6666.uihelper.miuix.primitive.MiuixHeaderContentCard
import io.github.xiaotong6666.uihelper.miuix.primitive.MiuixInsetTextPanel
import io.github.xiaotong6666.uihelper.miuix.primitive.MiuixMetricItem
import io.github.xiaotong6666.uihelper.miuix.primitive.MiuixMetricSummaryCard
import io.github.xiaotong6666.uihelper.miuix.primitive.MiuixSectionTitle
import io.github.xiaotong6666.uihelper.miuix.primitive.MiuixTextRow
import io.github.xiaotong6666.uihelper.miuix.primitive.MiuixTextRowsCard
import io.github.xiaotong6666.uihelper.mode.LocalUiMode
import io.github.xiaotong6666.uihelper.mode.UiMode
import androidx.compose.material3.ListItemDefaults

@Composable
fun AdaptiveDetailSectionTitle(
    text: String,
    modifier: Modifier = Modifier,
    topPadding: Dp = 4.dp,
    bottomPadding: Dp = 6.dp,
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> MiuixSectionTitle(text, modifier, topPadding, bottomPadding)
        UiMode.Material -> MaterialDetailSectionTitle(text, modifier, topPadding, bottomPadding)
    }
}

@Composable
fun AdaptiveMetricSummaryCard(metrics: List<DetailMetric>, modifier: Modifier = Modifier) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> MiuixMetricSummaryCard(metrics.map { MiuixMetricItem(it.value, it.label) }, modifier)
        UiMode.Material -> MaterialMetricSummaryCard(metrics, modifier)
    }
}

/** Short, static rows only. For dynamic lists use [adaptiveGroupedTextRows] in a LazyColumn. */
@Composable
fun AdaptiveTextRowsCard(rows: List<DetailTextRow>, modifier: Modifier = Modifier) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> MiuixTextRowsCard(rows.map { MiuixTextRow(it.title, it.summary) }, modifier)
        UiMode.Material -> MaterialTextRowsCard(rows, modifier)
    }
}

@Composable
fun AdaptiveDetailSectionsCard(
    sections: List<DetailSection>,
    modifier: Modifier = Modifier,
    labelWidth: Dp = 96.dp,
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> MiuixDetailSectionsCard(
            sections = sections.map { section ->
                MiuixDetailSection(
                    title = section.title,
                    icon = section.icon,
                    fields = section.fields.map { MiuixDetailField(it.label, it.value) },
                )
            },
            modifier = modifier,
            labelWidth = labelWidth,
        )
        UiMode.Material -> MaterialDetailSectionsCard(sections, modifier, labelWidth)
    }
}

@Composable
fun AdaptiveHeaderContentCard(
    title: String,
    summary: String?,
    modifier: Modifier = Modifier,
    content: (@Composable ColumnScope.() -> Unit)? = null,
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> MiuixHeaderContentCard(title, summary, modifier, content)
        UiMode.Material -> MaterialHeaderContentCard(title, summary, modifier, content)
    }
}

@Composable
fun AdaptiveInsetTextPanel(text: String, modifier: Modifier = Modifier, monospace: Boolean = false) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> MiuixInsetTextPanel(text, modifier, monospace)
        UiMode.Material -> MaterialInsetTextPanel(text, modifier, monospace)
    }
}

/**
 * One entry per LazyColumn item for both skins. MIUIX keeps its joined squircle, while M3E
 * applies segmented shapes and gaps. Stable optional row keys survive insertions/reordering.
 */
fun LazyListScope.adaptiveGroupedTextRows(
    keyPrefix: String,
    rows: List<DetailTextRow>,
    miuixBottomPadding: Dp = 0.dp,
    materialBottomPadding: Dp = 0.dp,
    miuixRowPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
) {
    rows.forEachIndexed { index, row ->
        item(key = groupedTextRowKey(keyPrefix, row, index)) {
            when (LocalUiMode.current) {
                UiMode.Miuix -> MiuixGroupedTextRow(
                    row = MiuixTextRow(row.title, row.summary),
                    index = index,
                    count = rows.size,
                    modifier = Modifier.padding(bottom = if (index == rows.lastIndex) miuixBottomPadding else 0.dp),
                    rowPadding = miuixRowPadding,
                )
                UiMode.Material -> MaterialGroupedTextRow(
                    row = row,
                    index = index,
                    count = rows.size,
                    modifier = Modifier.padding(
                        top = if (index == 0) 0.dp else ListItemDefaults.SegmentedGap,
                        bottom = if (index == rows.lastIndex) materialBottomPadding else 0.dp,
                    ),
                )
            }
        }
    }
}
