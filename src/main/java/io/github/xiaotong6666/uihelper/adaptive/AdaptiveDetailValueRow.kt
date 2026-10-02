/*
 * Copyright (C) 2026 XiaoTong6666
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

@file:Suppress("ktlint:standard:function-naming")

package io.github.xiaotong6666.uihelper.adaptive

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.xiaotong6666.uihelper.miuix.primitive.resolveMiuixIcon
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.basic.Icon as MiuixIcon

/**
 * Diagnostic/detail value row shared by utility-style apps.
 *
 * MIUIX deliberately stacks label/value to avoid intrinsic-width measurement of long identifiers;
 * Material keeps the compact labeled-value layout. Domain status stays with the caller via [icon].
 */
@Composable
fun AdaptiveDetailValueRow(
    label: String,
    value: String,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier,
    valueModifier: Modifier = Modifier,
    detail: String? = null,
    detailMonospace: Boolean = false,
    materialVerticalPadding: Dp = 12.dp,
    materialLabelStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    materialValueStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    materialDetailStyle: TextStyle = MaterialTheme.typography.bodyMedium,
) {
    AdaptiveContent(
        miuix = {
            Column(
                modifier = modifier.fillMaxWidth().padding(vertical = 9.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                WrapSafeText(
                    text = label,
                    modifier = Modifier.fillMaxWidth(),
                    style = MiuixTheme.textStyles.footnote1,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    MiuixIcon(
                        imageVector = resolveMiuixIcon(icon),
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.padding(top = 2.dp).size(16.dp),
                    )
                    WrapSafeText(
                        text = value,
                        modifier = Modifier.weight(1f).then(valueModifier),
                        style = MiuixTheme.textStyles.body1.copy(lineHeight = 22.sp),
                        color = MiuixTheme.colorScheme.onSurface,
                    )
                }
                detail?.takeIf { it.isNotBlank() }?.let { raw ->
                    WrapSafeText(
                        text = raw,
                        modifier = Modifier.fillMaxWidth().padding(start = 24.dp),
                        style = MiuixTheme.textStyles.body2.copy(
                            fontFamily = if (detailMonospace) FontFamily.Monospace else null,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                        ),
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    )
                }
            }
        },
        material = {
            Column(
                modifier = modifier.fillMaxWidth().padding(vertical = materialVerticalPadding),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                LabeledValueLayout(
                    label = {
                        WrapSafeText(
                            text = label,
                            style = materialLabelStyle,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    value = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = iconTint,
                                modifier = Modifier.size(16.dp),
                            )
                            WrapSafeText(
                                text = value,
                                modifier = valueModifier,
                                style = materialValueStyle,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    },
                )
                detail?.takeIf { it.isNotBlank() }?.let { raw ->
                    WrapSafeText(
                        text = raw,
                        modifier = Modifier.fillMaxWidth(),
                        style = if (detailMonospace) {
                            materialDetailStyle.copy(fontFamily = FontFamily.Monospace)
                        } else {
                            materialDetailStyle
                        },
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
    )
}
