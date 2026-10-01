/*
 * Copyright (C) 2026 XiaoTong6666
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
@file:Suppress("ktlint:standard:function-naming")

package io.github.xiaotong6666.uihelper.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.xiaotong6666.uihelper.adaptive.WrapSafeText
import io.github.xiaotong6666.uihelper.mode.LocalUiMode
import io.github.xiaotong6666.uihelper.mode.UiMode
import top.yukonga.miuix.kmp.window.WindowDialog
import top.yukonga.miuix.kmp.basic.TextButton as MiuixTextButton

/**
 * Native details-dialog shell with an app-owned body.
 *
 * Business content stays in the caller. uihelper only owns each skin's dialog window, Material
 * header, size contract and close affordance.
 */
@Composable
fun AdaptiveDetailsDialog(
    show: Boolean,
    title: String,
    summary: String,
    icon: ImageVector,
    closeLabel: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onDismissFinished: () -> Unit = {},
    maxWidth: Dp = 560.dp,
    miuixMaxHeight: Dp = 560.dp,
    materialMaxHeight: Dp = 720.dp,
    materialTitleStyle: TextStyle = MaterialTheme.typography.titleLargeEmphasized,
    content: @Composable ColumnScope.() -> Unit,
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> WindowDialog(
            show = show,
            title = title,
            summary = summary,
            onDismissRequest = onDismiss,
            onDismissFinished = onDismissFinished,
            maxWidth = maxWidth,
        ) {
            Column(
                modifier = modifier.fillMaxWidth().heightIn(max = miuixMaxHeight),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                content()
                MiuixTextButton(
                    text = closeLabel,
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        UiMode.Material -> {
            if (!show) return
            Dialog(onDismissRequest = onDismiss) {
                Card(
                    modifier = modifier.fillMaxWidth().heightIn(max = materialMaxHeight),
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    ),
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                shape = MaterialTheme.shapes.large,
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(12.dp).size(22.dp),
                                )
                            }
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(2.dp),
                            ) {
                                WrapSafeText(
                                    text = title,
                                    style = materialTitleStyle,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                WrapSafeText(
                                    text = summary,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                        content()
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            TextButton(onClick = onDismiss) {
                                WrapSafeText(
                                    text = closeLabel,
                                    style = MaterialTheme.typography.labelLarge,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
