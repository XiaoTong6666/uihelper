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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.Badge
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.squircle.squircleClip
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.PressFeedbackType

/** Short status text using the native MIUIX badge, not a Material chip painted with MIUIX colors. */
@Composable
fun StatusLabelBadgeMiuix(
    label: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
) {
    Badge(
        modifier = modifier.heightIn(min = 24.dp),
        containerColor = containerColor,
        contentColor = contentColor,
    ) {
        Text(
            text = label,
            style = MiuixTheme.textStyles.footnote1.copy(fontWeight = FontWeight.Medium),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** A status-first MIUIX hero: a large decorative signal anchored to the lower-right corner. */
@Composable
fun StatusHeroCardMiuix(
    title: String,
    summary: String,
    icon: ImageVector,
    containerColor: Color,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    footer: (@Composable ColumnScope.() -> Unit)? = null,
    metaContent: (@Composable ColumnScope.() -> Unit)? = null,
    actionContent: (@Composable ColumnScope.() -> Unit)? = null,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.defaultColors(
            color = containerColor,
            contentColor = MiuixTheme.colorScheme.onSurface,
        ),
        // Match FuseHide HomeStatusCard: the native TiltFeedback rotates toward the
        // touch position and springs back. Passive status cards have no press feedback.
        onClick = onClick,
        pressFeedbackType = if (onClick != null) PressFeedbackType.Tilt else PressFeedbackType.None,
        // MIUIX Card defaults showIndication to false. Tilt alone changes geometry,
        // but does not draw the pressed highlight expected on an actionable card.
        showIndication = onClick != null,
        insideMargin = PaddingValues(0.dp),
    ) {
        val structured = metaContent != null || actionContent != null
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (structured) {
                            Modifier
                        } else {
                            Modifier.heightIn(
                                min = if (footer == null) 136.dp else 108.dp,
                            )
                        },
                    )
                    .squircleClip(16.dp),
            ) {
                // KSU HomeMiuix StatusCard deliberately pushes its decorative signal
                // past the bottom/right of the clipped card. A larger structured glyph
                // follows that treatment, but keeps a little more of its lower interior
                // visible (e.g. the dot in ErrorOutline must not be cropped).
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = if (structured) 36.dp else 18.dp, y = if (structured) 27.dp else 25.dp)
                        .size(if (structured) 148.dp else 112.dp),
                    tint = accentColor.copy(alpha = 0.65f),
                )
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 20.dp,
                                top = 20.dp,
                                end = if (structured) 20.dp else 94.dp,
                                bottom = if (structured) {
                                    0.dp
                                } else if (footer == null) {
                                    24.dp
                                } else {
                                    14.dp
                                },
                            ),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = title,
                            style = MiuixTheme.textStyles.title3.copy(fontWeight = FontWeight.SemiBold),
                            color = MiuixTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = summary,
                            style = MiuixTheme.textStyles.body2,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    if (metaContent != null) {
                        // The status glyph owns the lower-right, while app-owned
                        // metadata uses the full available *left* column.
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp, end = 108.dp, top = 6.dp, bottom = 5.dp),
                            verticalArrangement = Arrangement.spacedBy(5.dp),
                            content = metaContent,
                        )
                    }
                    if (actionContent != null) {
                        Column(modifier = Modifier.fillMaxWidth(), content = actionContent)
                    }
                }
            }
            // Preserve the original footer contract for existing consumers.
            if (footer != null) Column(modifier = Modifier.fillMaxWidth(), content = footer)
        }
    }
}
