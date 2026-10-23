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

package io.github.xiaotong6666.uihelper.material.primitive

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.xiaotong6666.uihelper.adaptive.DetailField
import io.github.xiaotong6666.uihelper.adaptive.DetailMetric
import io.github.xiaotong6666.uihelper.adaptive.DetailSection
import io.github.xiaotong6666.uihelper.adaptive.DetailTextRow
import io.github.xiaotong6666.uihelper.material.materialSurfaceLadder

/** M3E section heading corresponding to the MIUIX SmallTitle. */
@Composable
fun MaterialDetailSectionTitle(
    text: String,
    modifier: Modifier = Modifier,
    topPadding: Dp = 4.dp,
    bottomPadding: Dp = 6.dp,
) {
    Text(
        text = text,
        modifier = modifier.padding(start = 16.dp, top = topPadding, end = 16.dp, bottom = bottomPadding),
        style = MaterialTheme.typography.titleSmallEmphasized,
        color = MaterialTheme.colorScheme.primary,
    )
}

/** A single Material tonal card with the same labelled metric hierarchy as MIUIX. */
@Composable
fun MaterialMetricSummaryCard(
    metrics: List<DetailMetric>,
    modifier: Modifier = Modifier,
) {
    if (metrics.isEmpty()) return
    TonalCardMaterial(
        modifier = modifier.fillMaxWidth(),
        containerColor = materialSurfaceLadder().grouped,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            metrics.forEachIndexed { index, metric ->
                if (index > 0) {
                    VerticalDivider(
                        modifier = Modifier.height(38.dp).padding(horizontal = 12.dp),
                        color = MaterialTheme.colorScheme.outlineVariant,
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = metric.value,
                        style = MaterialTheme.typography.headlineSmallEmphasized,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = metric.label,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

/** Compact fixed-size group; use [materialGroupedTextRows] for data-dependent long lists. */
@Composable
fun MaterialTextRowsCard(
    rows: List<DetailTextRow>,
    modifier: Modifier = Modifier,
) {
    if (rows.isEmpty()) return
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        rows.forEachIndexed { index, row ->
            MaterialGroupedTextRow(row = row, index = index, count = rows.size)
        }
    }
}

/** Material Expressive analogue of the MIUIX field inspector, using native tonal surfaces. */
@Composable
fun MaterialDetailSectionsCard(
    sections: List<DetailSection>,
    modifier: Modifier = Modifier,
    labelWidth: Dp = 96.dp,
) {
    if (sections.isEmpty()) return
    TonalCardMaterial(modifier = modifier.fillMaxWidth()) {
        sections.forEachIndexed { index, section ->
            if (index > 0) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 14.dp, end = 16.dp, bottom = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = section.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text = section.title,
                    style = MaterialTheme.typography.titleSmallEmphasized,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            section.fields.forEach { field: DetailField ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = field.label,
                        modifier = Modifier.width(labelWidth),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = field.value,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

/** M3E native counterpart of a MIUIX BasicComponent header + optional inset body. */
@Composable
fun MaterialHeaderContentCard(
    title: String,
    summary: String?,
    modifier: Modifier = Modifier,
    content: (@Composable ColumnScope.() -> Unit)? = null,
) {
    TonalCardMaterial(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMediumEmphasized)
            if (!summary.isNullOrEmpty()) {
                Text(
                    text = summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        if (content != null) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 18.dp),
                color = MaterialTheme.colorScheme.outlineVariant,
            )
            content()
        }
    }
}

/** Selectable report text uses a higher surface tone without a second Material card elevation. */
@Composable
fun MaterialInsetTextPanel(
    text: String,
    modifier: Modifier = Modifier,
    monospace: Boolean = false,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = materialSurfaceLadder().input,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        SelectionContainer {
            Text(
                text = text,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = if (monospace) FontFamily.Monospace else FontFamily.Default,
                ),
            )
        }
    }
}
