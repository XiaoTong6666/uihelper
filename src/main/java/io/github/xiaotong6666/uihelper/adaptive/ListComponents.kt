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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallExtendedFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.xiaotong6666.uihelper.material.primitive.TonalCardMaterial
import io.github.xiaotong6666.uihelper.miuix.primitive.SectionDescriptionMiuix
import io.github.xiaotong6666.uihelper.mode.LocalUiMode
import io.github.xiaotong6666.uihelper.mode.UiMode
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.FloatingActionButton
import top.yukonga.miuix.kmp.basic.InfiniteProgressIndicator
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.basic.Icon as MiuixIcon
import top.yukonga.miuix.kmp.basic.Text as MiuixText
import top.yukonga.miuix.kmp.basic.TextButton as MiuixTextButton

@Composable
fun AdaptiveFloatingActionButton(
    visible: Boolean,
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    expanded: Boolean = true,
    contentDescription: String? = label,
    iconSize: Dp = 24.dp,
    showOutline: Boolean = false,
) {
    if (!visible) return

    when (LocalUiMode.current) {
        UiMode.Material -> SmallExtendedFloatingActionButton(
            modifier = modifier,
            expanded = expanded,
            onClick = onClick,
            icon = {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    modifier = Modifier.size(iconSize),
                )
            },
            text = { Text(text = label) },
        )

        UiMode.Miuix -> FloatingActionButton(
            modifier = if (showOutline) {
                modifier.border(0.05.dp, MiuixTheme.colorScheme.outline.copy(alpha = 0.5f), CircleShape)
            } else {
                modifier
            },
            containerColor = MiuixTheme.colorScheme.primary,
            shadowElevation = 0.dp,
            onClick = onClick,
            content = {
                Row(
                    modifier = Modifier.padding(horizontal = if (expanded) 14.dp else 0.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(if (expanded) 6.dp else 0.dp),
                ) {
                    MiuixIcon(
                        imageVector = icon,
                        contentDescription = contentDescription,
                        modifier = Modifier.size(iconSize),
                        tint = MiuixTheme.colorScheme.onPrimary,
                    )
                    if (expanded) {
                        MiuixText(
                            text = label,
                            color = MiuixTheme.colorScheme.onPrimary,
                        )
                    }
                }
            },
        )
    }
}

@Composable
fun CardListItem(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    when (LocalUiMode.current) {
        UiMode.Material -> TonalCardMaterial(
            modifier = modifier,
            onClick = onClick,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 14.dp, end = 16.dp, bottom = 10.dp),
                content = content,
            )
        }

        UiMode.Miuix -> {
            if (onClick != null) {
                Card(
                    modifier = modifier,
                    insideMargin = PaddingValues(16.dp),
                    onClick = onClick,
                ) {
                    Column(content = content)
                }
            } else {
                Card(
                    modifier = modifier,
                    insideMargin = PaddingValues(16.dp),
                ) {
                    Column(content = content)
                }
            }
        }
    }
}

@Composable
fun StatusActionCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    header: @Composable RowScope.() -> Unit,
    supportingContent: (@Composable ColumnScope.() -> Unit)? = null,
    summary: String? = null,
    summaryExpanded: Boolean = false,
    onSummaryExpandedChange: ((Boolean) -> Unit)? = null,
    summaryMaxCollapsedLines: Int = 4,
    statusRow: (@Composable RowScope.() -> Unit)? = null,
    footer: (@Composable RowScope.() -> Unit)? = null,
) {
    CardListItem(
        modifier = modifier,
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            content = header,
        )

        if (supportingContent != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                content = supportingContent,
            )
        }

        if (!summary.isNullOrBlank()) {
            val clickableModifier = if (onSummaryExpandedChange != null) {
                Modifier.clickable { onSummaryExpandedChange(!summaryExpanded) }
            } else {
                Modifier
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp)
                    .animateContentSize()
                    .then(clickableModifier),
            ) {
                when (LocalUiMode.current) {
                    UiMode.Material -> Text(
                        text = summary,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                        overflow = if (summaryExpanded) TextOverflow.Clip else TextOverflow.Ellipsis,
                        maxLines = if (summaryExpanded) Int.MAX_VALUE else summaryMaxCollapsedLines,
                    )

                    UiMode.Miuix -> MiuixText(
                        text = summary,
                        fontSize = 14.sp,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        overflow = if (summaryExpanded) TextOverflow.Clip else TextOverflow.Ellipsis,
                        maxLines = if (summaryExpanded) Int.MAX_VALUE else summaryMaxCollapsedLines,
                    )
                }
            }
        }

        if (statusRow != null) {
            Row(
                modifier = Modifier.padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                content = statusRow,
            )
        }

        if (footer != null) {
            when (LocalUiMode.current) {
                UiMode.Material -> HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = Dp.Hairline,
                )

                UiMode.Miuix -> top.yukonga.miuix.kmp.basic.HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 0.5.dp,
                    color = MiuixTheme.colorScheme.outline.copy(alpha = 0.5f),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                content = footer,
            )
        }
    }
}

@Composable
fun LoadableContentHost(
    loading: Boolean,
    message: String? = null,
    actionLabel: String = "",
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    when {
        !message.isNullOrBlank() -> {
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    when (LocalUiMode.current) {
                        UiMode.Material -> Text(
                            text = message,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )

                        UiMode.Miuix -> SectionDescriptionMiuix(message, io.github.xiaotong6666.uihelper.model.SectionDescriptionStyle.Body)
                    }
                    if (onActionClick != null && actionLabel.isNotBlank()) {
                        when (LocalUiMode.current) {
                            UiMode.Material -> TextButton(onClick = onActionClick) { Text(actionLabel) }
                            UiMode.Miuix -> MiuixTextButton(text = actionLabel, onClick = onActionClick)
                        }
                    }
                }
            }
        }

        loading -> {
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center,
            ) {
                when (LocalUiMode.current) {
                    UiMode.Material -> LoadingIndicator()
                    UiMode.Miuix -> InfiniteProgressIndicator()
                }
            }
        }

        else -> content()
    }
}
