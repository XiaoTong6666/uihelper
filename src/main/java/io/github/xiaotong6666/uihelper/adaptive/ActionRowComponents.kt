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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.xiaotong6666.uihelper.material.primitive.SegmentedColumn
import io.github.xiaotong6666.uihelper.material.primitive.SegmentedItemContainer
import io.github.xiaotong6666.uihelper.material.primitive.SegmentedListItem
import io.github.xiaotong6666.uihelper.mode.LocalUiMode
import io.github.xiaotong6666.uihelper.mode.UiMode
import io.github.xiaotong6666.uihelper.model.AsyncActionState
import io.github.xiaotong6666.uihelper.model.TextActionItem
import top.yukonga.miuix.kmp.basic.CircularProgressIndicator
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.basic.Text as MiuixText

@Composable
fun HeaderRowsCard(
    title: String,
    subtitle: String? = null,
    metaText: String? = null,
    modifier: Modifier = Modifier,
    descriptionContent: (@Composable () -> Unit)? = null,
    rows: List<@Composable () -> Unit> = emptyList(),
) {
    when (LocalUiMode.current) {
        UiMode.Material -> SegmentedColumn(
            modifier = modifier.fillMaxWidth(),
            content = buildList {
                add {
                    SegmentedListItem(
                        headlineContent = { Text(text = title) },
                        supportingContent = subtitle?.takeIf { it.isNotBlank() }?.let { text -> { Text(text = text) } },
                        trailingContent = metaText?.takeIf { it.isNotBlank() }?.let { text ->
                            { Text(text = text, style = MaterialTheme.typography.bodyMedium) }
                        },
                    )
                }
                if (descriptionContent != null) {
                    add {
                        SegmentedItemContainer {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                            ) {
                                descriptionContent()
                            }
                        }
                    }
                }
                addAll(rows)
            },
        )

        UiMode.Miuix -> CardListItem(modifier = modifier) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp, end = 16.dp, top = 16.dp),
                    ) {
                        MiuixText(
                            text = title,
                            fontSize = 17.sp,
                            fontWeight = FontWeight(550),
                            color = MiuixTheme.colorScheme.onSurface,
                        )
                        if (!subtitle.isNullOrBlank()) {
                            MiuixText(
                                text = subtitle,
                                fontSize = 12.sp,
                                fontWeight = FontWeight(550),
                                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                                modifier = Modifier.padding(top = 2.dp),
                            )
                        }
                    }
                    if (!metaText.isNullOrBlank()) {
                        MiuixText(
                            text = metaText,
                            fontSize = 12.sp,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            modifier = Modifier
                                .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                                .align(Alignment.Top),
                        )
                    }
                }
                if (descriptionContent != null || rows.isNotEmpty()) {
                    Column {
                        if (descriptionContent != null) {
                            HorizontalDivider(
                                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp),
                                thickness = 0.5.dp,
                                color = MiuixTheme.colorScheme.outline.copy(alpha = 0.5f),
                            )
                            Column(modifier = Modifier.fillMaxWidth()) {
                                descriptionContent()
                            }
                        }
                        if (rows.isNotEmpty()) {
                            HorizontalDivider(
                                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                                thickness = 0.5.dp,
                                color = MiuixTheme.colorScheme.outline.copy(alpha = 0.5f),
                            )
                            rows.forEachIndexed { index, row ->
                                row()
                                if (index != rows.lastIndex) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                                        thickness = 0.5.dp,
                                        color = MiuixTheme.colorScheme.outline.copy(alpha = 0.5f),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AsyncActionRow(
    title: String,
    subtitle: String,
    state: AsyncActionState,
    actionLabel: String,
    completedLabel: String,
    actionIcon: ImageVector,
    completedIcon: ImageVector,
    progress: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    actionTint: Color? = null,
    actionContainerColor: Color? = null,
) {
    when (LocalUiMode.current) {
        UiMode.Material -> SegmentedListItem(
            modifier = modifier,
            headlineContent = { Text(text = title) },
            supportingContent = { Text(text = subtitle) },
            trailingContent = {
                FilledTonalButton(
                    onClick = onClick,
                    enabled = enabled && state != AsyncActionState.InProgress,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = actionContainerColor ?: MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = actionTint ?: MaterialTheme.colorScheme.onSecondaryContainer,
                    ),
                    contentPadding = ButtonDefaults.TextButtonContentPadding,
                ) {
                    when (state) {
                        AsyncActionState.InProgress -> CircularWavyProgressIndicator(
                            progress = { progress.coerceIn(0f, 1f) },
                            modifier = Modifier.size(20.dp),
                        )

                        AsyncActionState.Completed -> {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                imageVector = completedIcon,
                                contentDescription = completedLabel,
                            )
                            Text(
                                modifier = Modifier.padding(start = 7.dp),
                                text = completedLabel,
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }

                        AsyncActionState.Idle -> {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                imageVector = actionIcon,
                                contentDescription = actionLabel,
                            )
                            Text(
                                modifier = Modifier.padding(start = 7.dp),
                                text = actionLabel,
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                    }
                }
            },
        )

        UiMode.Miuix -> {
            val resolvedActionTint = actionTint ?: MiuixTheme.colorScheme.onSurface
            val resolvedActionContainerColor = actionContainerColor ?: MiuixTheme.colorScheme.secondaryContainer

            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Column(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                        .weight(1f),
                ) {
                    MiuixText(
                        text = title,
                        fontSize = 14.sp,
                        color = MiuixTheme.colorScheme.onSurface,
                    )
                    MiuixText(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
                IconButton(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                    backgroundColor = resolvedActionContainerColor,
                    minHeight = 35.dp,
                    minWidth = 35.dp,
                    enabled = enabled && state != AsyncActionState.InProgress,
                    onClick = onClick,
                ) {
                    when (state) {
                        AsyncActionState.InProgress -> CircularProgressIndicator(
                            progress = progress.coerceIn(0f, 1f),
                            size = 20.dp,
                            strokeWidth = 2.dp,
                        )

                        AsyncActionState.Completed,
                        AsyncActionState.Idle,
                        -> {
                            val label = if (state == AsyncActionState.Completed) completedLabel else actionLabel
                            val icon = if (state == AsyncActionState.Completed) completedIcon else actionIcon
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                top.yukonga.miuix.kmp.basic.Icon(
                                    modifier = Modifier.size(20.dp),
                                    imageVector = icon,
                                    tint = resolvedActionTint,
                                    contentDescription = label,
                                )
                                MiuixText(
                                    modifier = Modifier.padding(start = 4.dp, end = 2.dp),
                                    text = label,
                                    color = resolvedActionTint,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 15.sp,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TextActionGroup(
    title: String,
    items: List<TextActionItem>,
    actionIcon: ImageVector,
    modifier: Modifier = Modifier,
    actionContentDescription: String? = null,
    actionTint: Color? = null,
    actionContainerColor: Color? = null,
) {
    if (items.isEmpty()) return

    when (LocalUiMode.current) {
        UiMode.Material -> SegmentedColumn(
            title = title,
            modifier = modifier,
            content = items.map { item ->
                {
                    SegmentedListItem(
                        headlineContent = {
                            Text(
                                text = item.text,
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight,
                                fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
                            )
                        },
                        trailingContent = {
                            FilledTonalButton(
                                modifier = Modifier.defaultMinSize(52.dp, 32.dp),
                                onClick = item.onClick,
                                enabled = item.enabled,
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = actionContainerColor ?: MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = actionTint ?: MaterialTheme.colorScheme.onSecondaryContainer,
                                ),
                                contentPadding = ButtonDefaults.TextButtonContentPadding,
                            ) {
                                Icon(
                                    modifier = Modifier.size(20.dp),
                                    imageVector = actionIcon,
                                    contentDescription = actionContentDescription,
                                )
                            }
                        },
                    )
                }
            },
        )

        UiMode.Miuix -> {
            val resolvedActionTint = actionTint ?: MiuixTheme.colorScheme.onSurface
            val resolvedActionContainerColor = actionContainerColor ?: MiuixTheme.colorScheme.secondaryContainer

            SmallTitle(text = title, modifier = modifier.padding(top = 6.dp))
            CardListItem(modifier = Modifier.padding(horizontal = 12.dp)) {
                Column {
                    items.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            MiuixText(
                                text = item.text,
                                fontSize = 14.sp,
                                color = MiuixTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f),
                            )
                            IconButton(
                                backgroundColor = resolvedActionContainerColor,
                                minHeight = 35.dp,
                                minWidth = 35.dp,
                                enabled = item.enabled,
                                onClick = item.onClick,
                            ) {
                                top.yukonga.miuix.kmp.basic.Icon(
                                    modifier = Modifier.size(20.dp),
                                    imageVector = actionIcon,
                                    tint = if (item.enabled) resolvedActionTint else resolvedActionTint.copy(alpha = 0.35f),
                                    contentDescription = actionContentDescription,
                                )
                            }
                        }
                        if (index != items.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                thickness = 0.5.dp,
                                color = MiuixTheme.colorScheme.outline.copy(alpha = 0.5f),
                            )
                        }
                    }
                }
            }
        }
    }
}
