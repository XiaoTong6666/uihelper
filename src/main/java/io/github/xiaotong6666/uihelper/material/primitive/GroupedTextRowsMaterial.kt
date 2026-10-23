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

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.xiaotong6666.uihelper.adaptive.DetailTextRow
import io.github.xiaotong6666.uihelper.material.materialSurfaceLadder

/**
 * Material Expressive uses native read-only segmented items and narrow inter-item gaps instead
 * of attempting to mimic MIUIX's joined squircle. No click semantics are added.
 */
@Composable
fun MaterialGroupedTextRow(
    row: DetailTextRow,
    index: Int,
    count: Int,
    modifier: Modifier = Modifier,
) {
    val shapes = if (count == 1) {
        ListItemDefaults.segmentedShapes(index, count).copy(shape = MaterialTheme.shapes.large)
    } else {
        ListItemDefaults.segmentedShapes(index, count)
    }
    SegmentedListItem(
        modifier = modifier.fillMaxWidth(),
        shapes = shapes,
        colors = ListItemDefaults.segmentedColors(
            containerColor = materialSurfaceLadder().grouped,
            contentColor = MaterialTheme.colorScheme.onSurface,
            supportingContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        supportingContent = row.summary?.let { summary ->
            { Text(text = summary, style = MaterialTheme.typography.bodyMedium) }
        },
    ) {
        Text(text = row.title, style = MaterialTheme.typography.titleMedium)
    }
}

/** One lazy item per row, stable keys, and native M3E segment geometry. */
fun LazyListScope.materialGroupedTextRows(
    keyPrefix: String,
    rows: List<DetailTextRow>,
    bottomPadding: Dp = 0.dp,
) {
    rows.forEachIndexed { index, row ->
        item(key = groupedTextRowKey(keyPrefix, row, index)) {
            MaterialGroupedTextRow(
                row = row,
                index = index,
                count = rows.size,
                modifier = Modifier.padding(
                    top = if (index == 0) 0.dp else ListItemDefaults.SegmentedGap,
                    bottom = if (index == rows.lastIndex) bottomPadding else 0.dp,
                ),
            )
        }
    }
}

internal fun groupedTextRowKey(prefix: String, row: DetailTextRow, index: Int): String =
    "$prefix:${row.key ?: "$index:${row.title}"}"
