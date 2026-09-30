/*
 * Copyright (C) 2026 XiaoTong6666
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
@file:Suppress("ktlint:standard:function-naming")
package io.github.xiaotong6666.uihelper.adaptive

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.xiaotong6666.uihelper.mode.LocalUiMode
import io.github.xiaotong6666.uihelper.mode.UiMode
import top.yukonga.miuix.kmp.basic.Card as MiuixCard
import top.yukonga.miuix.kmp.basic.CardDefaults as MiuixCardDefaults
import top.yukonga.miuix.kmp.theme.MiuixTheme

enum class AdaptiveSurfaceTone {
    Low,
    High,
    Highest,
}

@Composable
fun adaptiveSurfaceColor(tone: AdaptiveSurfaceTone): Color = when (LocalUiMode.current) {
    UiMode.Material -> when (tone) {
        AdaptiveSurfaceTone.Low -> MaterialTheme.colorScheme.surfaceContainerLow
        AdaptiveSurfaceTone.High -> MaterialTheme.colorScheme.surfaceContainerHigh
        AdaptiveSurfaceTone.Highest -> MaterialTheme.colorScheme.surfaceContainerHighest
    }
    UiMode.Miuix -> when (tone) {
        AdaptiveSurfaceTone.Low -> MiuixTheme.colorScheme.surfaceContainer
        AdaptiveSurfaceTone.High -> MiuixTheme.colorScheme.surfaceContainerHigh
        AdaptiveSurfaceTone.Highest -> MiuixTheme.colorScheme.surfaceContainerHighest
    }
}

/** Native tonal surface with caller-owned geometry. */
@Composable
fun AdaptiveTonalSurface(
    tone: AdaptiveSurfaceTone,
    modifier: Modifier = Modifier,
    materialShape: Shape = MaterialTheme.shapes.large,
    miuixCornerRadius: Dp = 16.dp,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable () -> Unit,
) {
    when (LocalUiMode.current) {
        UiMode.Material -> Surface(
            modifier = modifier,
            color = adaptiveSurfaceColor(tone),
            shape = materialShape,
        ) {
            Column(modifier = Modifier.padding(contentPadding)) { content() }
        }
        UiMode.Miuix -> MiuixCard(
            modifier = modifier,
            cornerRadius = miuixCornerRadius,
            insideMargin = contentPadding,
            colors = MiuixCardDefaults.defaultColors(color = adaptiveSurfaceColor(tone)),
        ) {
            content()
        }
    }
}
