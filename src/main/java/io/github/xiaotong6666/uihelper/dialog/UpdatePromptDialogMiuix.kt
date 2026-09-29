/*
 * Copyright (C) 2026 XiaoTong6666
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("ktlint:standard:function-naming")

package io.github.xiaotong6666.uihelper.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.window.WindowDialog

/** Display data; fetching, downloading and installing remain the caller's responsibility. */
data class UpdatePromptMetadata(
    val label: String,
    val value: String,
    val icon: ImageVector? = null,
    val monospace: Boolean = false,
)

data class UpdatePromptChange(val title: String, val reference: String? = null)

/** Native MIUIX update confirmation, with scrollable details and fixed confirmation buttons. */
@Composable
fun UpdatePromptDialogMiuix(
    show: Boolean,
    title: String,
    summary: String,
    metadata: List<UpdatePromptMetadata>,
    changesTitle: String,
    changes: List<UpdatePromptChange>,
    dismissLabel: String,
    confirmLabel: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    confirmEnabled: Boolean = true,
    moreChangesLabel: String? = null,
    viewAllLabel: String? = null,
    onViewAll: (() -> Unit)? = null,
) {
    WindowDialog(
        show = show,
        modifier = modifier.windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top)),
        title = title,
        summary = summary,
        onDismissRequest = onDismiss,
    ) {
        Layout(content = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                if (metadata.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        insideMargin = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                    ) {
                        metadata.forEachIndexed { index, item ->
                            UpdatePromptMetadataRow(item)
                            if (index != metadata.lastIndex) HorizontalDivider()
                        }
                    }
                }
                if (changes.isNotEmpty() || moreChangesLabel != null) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = changesTitle,
                            style = MiuixTheme.textStyles.headline1,
                            color = MiuixTheme.colorScheme.onSurface,
                        )
                        changes.forEach { change ->
                            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Text(
                                    text = change.title,
                                    style = MiuixTheme.textStyles.body2,
                                    color = MiuixTheme.colorScheme.onSurface,
                                )
                                change.reference?.takeIf { it.isNotBlank() }?.let { reference ->
                                    Text(
                                        text = reference,
                                        style = MiuixTheme.textStyles.footnote1,
                                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                                    )
                                }
                            }
                        }
                        moreChangesLabel?.let { more ->
                            Text(
                                text = more,
                                style = MiuixTheme.textStyles.footnote1,
                                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            )
                        }
                    }
                }
                if (viewAllLabel != null && onViewAll != null) {
                    TextButton(text = viewAllLabel, onClick = onViewAll, modifier = Modifier.fillMaxWidth())
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(text = dismissLabel, onClick = onDismiss, modifier = Modifier.weight(1f))
                Spacer(Modifier.width(16.dp))
                TextButton(
                    text = confirmLabel,
                    onClick = onConfirm,
                    enabled = confirmEnabled,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.textButtonColorsPrimary(),
                )
            }
        }) { measurables, constraints ->
            // Same footer-first measurement contract as KernelSU: long notes scroll; buttons stay visible.
            val footer = measurables[1].measure(constraints.copy(minHeight = 0))
            val body = measurables[0].measure(
                constraints.copy(minHeight = 0, maxHeight = (constraints.maxHeight - footer.height).coerceAtLeast(0)),
            )
            layout(constraints.maxWidth, body.height + footer.height) {
                body.place(0, 0)
                footer.place(0, body.height)
            }
        }
    }
}

@Composable
private fun UpdatePromptMetadataRow(item: UpdatePromptMetadata) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item.icon?.let { icon ->
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            )
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text = item.label,
                style = MiuixTheme.textStyles.footnote1,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            )
            Text(
                text = item.value,
                style = MiuixTheme.textStyles.body2.copy(
                    fontFamily = if (item.monospace) FontFamily.Monospace else null,
                ),
                color = MiuixTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
