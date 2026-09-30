/*
 * Copyright (C) 2026 XiaoTong6666
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

@file:Suppress("ktlint:standard:function-naming")

package io.github.xiaotong6666.uihelper.adaptive

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Compact icon + label + value block used for small summary metrics. */
@Composable
fun AdaptiveMetricChip(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    materialShape: Shape = MaterialTheme.shapes.large,
    miuixCornerRadius: Dp = 12.dp,
    iconTint: Color = adaptivePrimaryColor(),
    labelStyle: TextStyle = adaptiveFootnoteStyle(MaterialTheme.typography.labelSmall),
    valueStyle: TextStyle = adaptiveBodyStyle(MaterialTheme.typography.labelLarge),
    labelColor: Color = adaptiveValue(
        material = MaterialTheme.colorScheme.onSurfaceVariant,
        miuix = adaptiveOnSurfaceColor(),
    ),
    valueColor: Color = adaptiveOnSurfaceColor(),
) {
    AdaptiveTonalSurface(
        tone = AdaptiveSurfaceTone.Highest,
        modifier = modifier,
        materialShape = materialShape,
        miuixCornerRadius = miuixCornerRadius,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AdaptiveIcon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(16.dp),
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                WrapSafeText(
                    text = label,
                    style = labelStyle,
                    color = labelColor,
                )
                WrapSafeText(
                    text = value,
                    style = valueStyle,
                    color = valueColor,
                )
            }
        }
    }
}

/** Small semantic chip with an icon and one short label. */
@Composable
fun AdaptiveIconLabelChip(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    materialShape: Shape = MaterialTheme.shapes.small,
    miuixCornerRadius: Dp = 8.dp,
    iconTint: Color = adaptivePrimaryColor(),
    iconSize: Dp = 14.dp,
    materialTextStyle: TextStyle = MaterialTheme.typography.labelSmall,
    materialContainerColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
    miuixContainerColor: Color = adaptiveSurfaceColor(AdaptiveSurfaceTone.Highest),
    contentPadding: PaddingValues = PaddingValues(horizontal = 10.dp, vertical = 7.dp),
    useNativeCardSurface: Boolean = false,
) {
    val content: @Composable () -> Unit = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            AdaptiveIcon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(iconSize),
            )
            WrapSafeText(
                text = label,
                style = adaptiveFootnoteStyle(materialTextStyle),
                color = adaptiveOnSurfaceColor(),
            )
        }
    }
    if (useNativeCardSurface) {
        AdaptiveTonalSurface(
            tone = AdaptiveSurfaceTone.Highest,
            modifier = modifier,
            materialShape = materialShape,
            miuixCornerRadius = miuixCornerRadius,
            contentPadding = contentPadding,
            content = content,
        )
    } else {
        Row(
            modifier = modifier
                .adaptiveSurfaceBackground(
                    materialColor = materialContainerColor,
                    materialShape = materialShape,
                    miuixColor = miuixContainerColor,
                    miuixCornerRadius = miuixCornerRadius,
                )
                .padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            content()
        }
    }
}

/** Short text-only chip with skin-specific geometry and caller-owned Material typography. */
@Composable
fun AdaptiveLabelChip(
    label: String,
    modifier: Modifier = Modifier,
    materialShape: Shape = MaterialTheme.shapes.small,
    miuixCornerRadius: Dp = 6.dp,
    materialContainerColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
    miuixContainerColor: Color = adaptiveSurfaceColor(AdaptiveSurfaceTone.Highest),
    materialContentPadding: PaddingValues = PaddingValues(horizontal = 10.dp, vertical = 3.dp),
    miuixContentPadding: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
    materialTextStyle: TextStyle = MaterialTheme.typography.labelSmall,
    contentColor: Color = adaptiveOnSurfaceColor(),
) {
    WrapSafeText(
        text = label,
        modifier = modifier
            .adaptiveSurfaceBackground(
                materialColor = materialContainerColor,
                materialShape = materialShape,
                miuixColor = miuixContainerColor,
                miuixCornerRadius = miuixCornerRadius,
            )
            .padding(
                adaptiveValue(
                    material = materialContentPadding,
                    miuix = miuixContentPadding,
                ),
            ),
        style = adaptiveFootnoteStyle(materialTextStyle),
        color = contentColor,
    )
}
