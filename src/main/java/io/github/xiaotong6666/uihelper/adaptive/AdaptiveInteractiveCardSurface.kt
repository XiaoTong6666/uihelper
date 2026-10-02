/*
 * Copyright (C) 2026 XiaoTong6666
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

@file:Suppress("ktlint:standard:function-naming")

package io.github.xiaotong6666.uihelper.adaptive

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp

/**
 * A stable native card surface whose inner click target may be enabled or removed without swapping
 * the underlying Material/MIUIX card composition.
 *
 * Keeping the indication node and interaction source stable lets press/release feedback finish
 * naturally when the click changes state that also changes [onClick], such as expanding a card.
 * Visual tokens remain caller-owned.
 */
@Composable
fun AdaptiveInteractiveCardSurface(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    role: Role = Role.Button,
    contentPadding: PaddingValues = PaddingValues(),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    materialShape: Shape = MaterialTheme.shapes.large,
    materialContainerColor: Color = Color.Unspecified,
    miuixCornerRadius: Dp? = null,
    miuixContainerColor: Color = Color.Unspecified,
    content: @Composable ColumnScope.() -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val indication = LocalIndication.current
    val interactionModifier = Modifier
        .fillMaxWidth()
        .indication(interactionSource, indication)
        .then(
            if (onClick != null) {
                Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled,
                    role = role,
                    onClick = onClick,
                )
            } else {
                Modifier
            },
        )
        .padding(contentPadding)

    AdaptiveCardSurface(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(),
        materialShape = materialShape,
        materialContainerColor = materialContainerColor,
        miuixCornerRadius = miuixCornerRadius,
        miuixContainerColor = miuixContainerColor,
    ) {
        Column(
            modifier = interactionModifier,
            verticalArrangement = verticalArrangement,
            content = content,
        )
    }
}
