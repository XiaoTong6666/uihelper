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

package io.github.xiaotong6666.uihelper.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.InfiniteProgressIndicator
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.theme.LocalDismissState
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.window.WindowDialog

@Composable
fun LoadingDialogMiuix(showDialog: State<Boolean>, message: State<String?>) {
    val loadingMessage = message.value
    WindowDialog(
        show = showDialog.value,
        content = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                ) {
                    InfiniteProgressIndicator(color = MiuixTheme.colorScheme.onBackground)
                    loadingMessage?.takeIf { it.isNotBlank() }?.let { text ->
                        Text(
                            modifier = Modifier.padding(start = 12.dp),
                            text = text,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
        },
    )
}

@Composable
fun ConfirmDialogMiuix(
    visuals: ConfirmDialogVisuals,
    confirm: () -> Unit,
    dismiss: () -> Unit,
    showDialog: State<Boolean>,
    messageContent: ConfirmDialogMessageContent?,
) {
    WindowDialog(
        show = showDialog.value,
        modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top)),
        title = visuals.title,
        onDismissRequest = dismiss,
        content = {
            Layout(
                content = {
                    val dismissState = LocalDismissState.current
                    visuals.content?.let { content ->
                        if (messageContent != null) {
                            messageContent(visuals)
                        } else {
                            DefaultConfirmDialogMessage(visuals)
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(top = 12.dp),
                    ) {
                        TextButton(
                            text = visuals.dismiss ?: androidx.compose.ui.res.stringResource(android.R.string.cancel),
                            onClick = {
                                dismiss()
                                dismissState?.invoke()
                            },
                            modifier = Modifier.weight(1f),
                        )
                        Spacer(Modifier.width(20.dp))
                        TextButton(
                            text = visuals.confirm ?: androidx.compose.ui.res.stringResource(android.R.string.ok),
                            onClick = {
                                confirm()
                                dismissState?.invoke()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColorsPrimary(),
                        )
                    }
                },
            ) { measurables, constraints ->
                if (measurables.size != 2) {
                    val button = measurables[0].measure(constraints)
                    layout(constraints.maxWidth, button.height) {
                        button.place(0, 0)
                    }
                } else {
                    val button = measurables[1].measure(constraints)
                    val content = measurables[0].measure(
                        constraints.copy(maxHeight = constraints.maxHeight - button.height),
                    )
                    layout(constraints.maxWidth, content.height + button.height) {
                        content.place(0, 0)
                        button.place(0, content.height)
                    }
                }
            }
        },
    )
}
