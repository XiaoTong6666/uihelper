/*
 * Copyright (C) 2026 XiaoTong6666
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

@file:Suppress("ktlint:standard:function-naming")

package io.github.xiaotong6666.uihelper.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import io.github.xiaotong6666.uihelper.adaptive.WrapSafeText
import io.github.xiaotong6666.uihelper.mode.LocalUiMode
import io.github.xiaotong6666.uihelper.mode.UiMode
import top.yukonga.miuix.kmp.window.WindowDialog
import top.yukonga.miuix.kmp.basic.ButtonDefaults as MiuixButtonDefaults
import top.yukonga.miuix.kmp.basic.TextButton as MiuixTextButton

/**
 * Two explicit choices plus a neutral close path.
 *
 * MIUIX defers callbacks until the native exit animation finishes; Material dispatches the
 * corresponding action immediately. This distinction stays inside uihelper so callers only model
 * the decision semantics.
 */
@Composable
fun AdaptiveDecisionDialog(
    show: Boolean,
    title: String,
    message: String,
    confirmLabel: String,
    dismissLabel: String,
    onConfirm: () -> Unit,
    onDismissButton: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    materialShape: Shape = MaterialTheme.shapes.extraLarge,
    materialContainerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
) {
    when (LocalUiMode.current) {
        UiMode.Material -> {
            if (!show) return
            AlertDialog(
                onDismissRequest = onDismissRequest,
                modifier = modifier,
                shape = materialShape,
                containerColor = materialContainerColor,
                icon = icon?.let { imageVector ->
                    {
                        Icon(
                            imageVector = imageVector,
                            contentDescription = null,
                        )
                    }
                },
                title = { WrapSafeText(text = title) },
                text = { WrapSafeText(text = message) },
                confirmButton = {
                    Button(onClick = onConfirm) {
                        WrapSafeText(text = confirmLabel)
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismissButton) {
                        WrapSafeText(text = dismissLabel)
                    }
                },
            )
        }

        UiMode.Miuix -> {
            var visible by remember { mutableStateOf(show) }
            var pendingAction by remember { mutableStateOf<(() -> Unit)?>(null) }

            LaunchedEffect(show) {
                if (show) {
                    visible = true
                } else if (pendingAction == null) {
                    visible = false
                }
            }

            val dismissWith: (() -> Unit) -> Unit = { action ->
                pendingAction = action
                visible = false
            }

            WindowDialog(
                show = visible,
                modifier = modifier.windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top)),
                title = title,
                summary = message,
                onDismissRequest = { dismissWith(onDismissRequest) },
                onDismissFinished = {
                    pendingAction?.invoke()
                    pendingAction = null
                },
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    MiuixTextButton(
                        text = dismissLabel,
                        onClick = { dismissWith(onDismissButton) },
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(Modifier.width(16.dp))
                    MiuixTextButton(
                        text = confirmLabel,
                        onClick = { dismissWith(onConfirm) },
                        modifier = Modifier.weight(1f),
                        colors = MiuixButtonDefaults.textButtonColorsPrimary(),
                    )
                }
            }
        }
    }
}
