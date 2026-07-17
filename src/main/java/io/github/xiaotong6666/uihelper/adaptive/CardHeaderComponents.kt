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
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.xiaotong6666.uihelper.mode.LocalUiMode
import io.github.xiaotong6666.uihelper.mode.UiMode
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun MetadataHeader(
    title: String,
    metadataLines: List<String>,
    modifier: Modifier = Modifier,
    titleTextDecoration: TextDecoration? = null,
    titleInlineContent: (@Composable RowScope.() -> Unit)? = null,
    metadataExtraContent: (@Composable ColumnScope.() -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 4.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            when (LocalUiMode.current) {
                UiMode.Material -> Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    androidx.compose.material3.Text(
                        text = title,
                        fontWeight = FontWeight.SemiBold,
                        style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                        lineHeight = androidx.compose.material3.MaterialTheme.typography.bodySmall.lineHeight,
                        textDecoration = titleTextDecoration,
                    )
                    titleInlineContent?.invoke(this)
                }

                UiMode.Miuix -> Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    top.yukonga.miuix.kmp.basic.Text(
                        text = title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight(550),
                        color = MiuixTheme.colorScheme.onSurface,
                        textDecoration = titleTextDecoration,
                    )
                    titleInlineContent?.invoke(this)
                }
            }

            metadataLines.forEach { line ->
                when (LocalUiMode.current) {
                    UiMode.Material -> androidx.compose.material3.Text(
                        text = line,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                    )

                    UiMode.Miuix -> top.yukonga.miuix.kmp.basic.Text(
                        text = line,
                        fontSize = 12.sp,
                        fontWeight = FontWeight(550),
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    )
                }
            }

            if (metadataExtraContent != null) {
                Column(
                    modifier = Modifier.padding(top = 4.dp),
                    content = metadataExtraContent,
                )
            }
        }

        trailingContent?.invoke()
    }
}
