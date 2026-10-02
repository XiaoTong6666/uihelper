/*
 * Copyright (C) 2026 XiaoTong6666
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

@file:Suppress("ktlint:standard:function-naming")

package io.github.xiaotong6666.uihelper.adaptive

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.xiaotong6666.uihelper.miuix.primitive.resolveMiuixIcon
import io.github.xiaotong6666.uihelper.mode.LocalUiMode
import io.github.xiaotong6666.uihelper.mode.UiMode
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.PressFeedbackType
import top.yukonga.miuix.kmp.basic.Card as MiuixCard
import top.yukonga.miuix.kmp.basic.CardDefaults as MiuixCardDefaults
import top.yukonga.miuix.kmp.basic.Icon as MiuixIcon
import top.yukonga.miuix.kmp.basic.Text as MiuixText

/**
 * Native clickable card with caller-owned project colors/geometry.
 *
 * Material can retain elevated-card semantics while MIUIX keeps its own indication and optional
 * press transform. No MIUIX types escape through the public API.
 */
@Composable
fun AdaptiveClickableCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    materialShape: Shape = MaterialTheme.shapes.large,
    materialContainerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    materialElevated: Boolean = false,
    materialContentPadding: PaddingValues = PaddingValues(0.dp),
    miuixCornerRadius: Dp = 16.dp,
    miuixContainerColor: Color = Color.Unspecified,
    miuixContentPadding: PaddingValues = PaddingValues(0.dp),
    miuixPressTransformEnabled: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    when (LocalUiMode.current) {
        UiMode.Material -> {
            val body: @Composable ColumnScope.() -> Unit = {
                Column(modifier = Modifier.padding(materialContentPadding), content = content)
            }
            if (materialElevated) {
                ElevatedCard(
                    onClick = onClick,
                    modifier = modifier,
                    shape = materialShape,
                    colors = CardDefaults.elevatedCardColors(containerColor = materialContainerColor),
                    content = body,
                )
            } else {
                Card(
                    onClick = onClick,
                    modifier = modifier,
                    shape = materialShape,
                    colors = CardDefaults.cardColors(containerColor = materialContainerColor),
                    content = body,
                )
            }
        }

        UiMode.Miuix -> {
            val colors = if (miuixContainerColor == Color.Unspecified) {
                MiuixCardDefaults.defaultColors()
            } else {
                MiuixCardDefaults.defaultColors(color = miuixContainerColor)
            }
            MiuixCard(
                modifier = modifier,
                cornerRadius = miuixCornerRadius,
                insideMargin = miuixContentPadding,
                colors = colors,
                pressFeedbackType = if (miuixPressTransformEnabled) {
                    PressFeedbackType.Tilt
                } else {
                    PressFeedbackType.None
                },
                showIndication = true,
                onClick = onClick,
                content = content,
            )
        }
    }
}

/** Generic summary card for inventories/about pages: icon, title, summary and a short count/value. */
@Composable
fun AdaptiveSummaryCard(
    icon: ImageVector,
    title: String,
    summary: String,
    trailingText: String,
    modifier: Modifier = Modifier,
    materialContainerColor: Color = MaterialTheme.colorScheme.surfaceBright,
    materialShape: Shape = MaterialTheme.shapes.extraLarge,
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> MiuixCard(
            modifier = modifier,
            insideMargin = PaddingValues(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MiuixIcon(
                    imageVector = resolveMiuixIcon(icon),
                    contentDescription = null,
                    tint = MiuixTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp),
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    MiuixText(
                        text = title,
                        style = MiuixTheme.textStyles.headline1,
                        color = MiuixTheme.colorScheme.onSurface,
                    )
                    MiuixText(
                        text = summary,
                        style = MiuixTheme.textStyles.body2,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    )
                }
                MiuixText(
                    text = trailingText,
                    style = MiuixTheme.textStyles.headline1,
                    color = MiuixTheme.colorScheme.primary,
                )
            }
        }

        UiMode.Material -> ElevatedCard(
            modifier = modifier,
            shape = materialShape,
            colors = CardDefaults.elevatedCardColors(containerColor = materialContainerColor),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(18.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp),
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    WrapSafeText(
                        text = title,
                        style = MaterialTheme.typography.titleMediumEmphasized,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    WrapSafeText(
                        text = summary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                ) {
                    WrapSafeText(
                        text = trailingText,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
            }
        }
    }
}
