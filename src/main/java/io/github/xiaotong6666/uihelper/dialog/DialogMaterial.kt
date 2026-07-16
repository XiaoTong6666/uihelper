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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.xiaotong6666.uihelper.material.primitive.ExpressiveDialog

@Composable
fun LoadingDialogMaterial(showDialog: State<Boolean>) {
    if (showDialog.value) {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(dismissOnClickOutside = false, dismissOnBackPress = false),
        ) {
            Surface(
                modifier = Modifier.size(100.dp),
                shape = MaterialTheme.shapes.extraLarge,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    LoadingIndicator()
                }
            }
        }
    }
}

@Composable
fun ConfirmDialogMaterial(
    visuals: ConfirmDialogVisuals,
    confirm: () -> Unit,
    dismiss: () -> Unit,
    showDialog: State<Boolean>,
    messageContent: ConfirmDialogMessageContent?,
) {
    if (!showDialog.value) return

    ExpressiveDialog(
        onDismissRequest = dismiss,
        title = { Text(visuals.title) },
        text = visuals.content?.let {
            {
                if (messageContent != null) {
                    messageContent(visuals)
                } else {
                    DefaultConfirmDialogMessage(visuals)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = confirm) {
                Text(visuals.confirm ?: androidx.compose.ui.res.stringResource(android.R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = dismiss) {
                Text(visuals.dismiss ?: androidx.compose.ui.res.stringResource(android.R.string.cancel))
            }
        },
    )
}
