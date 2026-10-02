/*
 * Copyright (C) 2026 XiaoTong6666
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

@file:Suppress("ktlint:standard:function-naming")

package io.github.xiaotong6666.uihelper.adaptive

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.github.xiaotong6666.uihelper.miuix.primitive.resolveMiuixIcon
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.ExpandLess
import top.yukonga.miuix.kmp.icon.extended.ExpandMore
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.basic.HorizontalDivider as MiuixDivider
import top.yukonga.miuix.kmp.basic.Icon as MiuixIcon

/**
 * Expandable grouped section with native MIUIX and Material containers.
 *
 * Domain-specific badges belong in [trailingContent]; uihelper only owns interaction, native
 * widget selection and motion.
 */
@Composable
fun AdaptiveExpandableSection(
    title: String,
    icon: ImageVector,
    expanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true,
    materialDividerColor: Color = MaterialTheme.colorScheme.outlineVariant,
    materialContainerColor: Color = MaterialTheme.colorScheme.surfaceBright,
    materialIconRotationSpec: FiniteAnimationSpec<Float> = tween(180),
    materialExpandSpec: FiniteAnimationSpec<IntSize> = spring(
        visibilityThreshold = IntSize.VisibilityThreshold,
    ),
    materialFadeSpec: FiniteAnimationSpec<Float> = tween(150),
    materialTopRadius: Dp = 16.dp,
    materialBottomRadius: Dp = 16.dp,
    miuixIcon: ImageVector? = icon,
    trailingContent: @Composable RowScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    AdaptiveContent(
        miuix = {
            Column(modifier = modifier.fillMaxWidth()) {
                BasicComponent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics(mergeDescendants = true) { heading() },
                    title = title,
                    startAction = miuixIcon?.let { icon ->
                        {
                            MiuixIcon(
                                imageVector = resolveMiuixIcon(icon),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = MiuixTheme.colorScheme.onSurface,
                            )
                        }
                    },
                    endActions = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            trailingContent()
                            MiuixIcon(
                                imageVector = if (expanded) MiuixIcons.ExpandLess else MiuixIcons.ExpandMore,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MiuixTheme.colorScheme.onSurfaceVariantActions,
                            )
                        }
                    },
                    onClick = onToggle,
                    role = Role.Button,
                )
                ExpandableSectionBody(
                    expanded = expanded,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp,
                        ),
                    content = content,
                )
                if (showDivider) {
                    MiuixDivider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        },
        material = {
            val interactionSource = remember { MutableInteractionSource() }
            val pressed by interactionSource.collectIsPressedAsState()
            val shapeSpring = spring<Dp>(
                dampingRatio = 0.9f,
                stiffness = 800f,
            )
            val topRadius by animateDpAsState(
                targetValue = if (pressed) 16.dp else materialTopRadius,
                animationSpec = shapeSpring,
                label = "ExpandableSectionTopRadius",
            )
            val bottomRadius by animateDpAsState(
                targetValue = if (pressed) 16.dp else materialBottomRadius,
                animationSpec = shapeSpring,
                label = "ExpandableSectionBottomRadius",
            )
            val materialShape = RoundedCornerShape(
                topStart = topRadius,
                topEnd = topRadius,
                bottomStart = bottomRadius,
                bottomEnd = bottomRadius,
            )
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .clip(materialShape)
                    .background(materialContainerColor),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = LocalIndication.current,
                            role = Role.Button,
                            onClick = onToggle,
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .semantics(mergeDescendants = true) { heading() },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp),
                    )
                    WrapSafeText(
                        text = title,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    trailingContent()
                    AdaptiveExpandIcon(
                        expanded = expanded,
                        materialSize = 20.dp,
                        materialAnimationSpec = materialIconRotationSpec,
                    )
                }
                AnimatedVisibility(
                    visible = expanded,
                    enter = expandVertically(animationSpec = materialExpandSpec) +
                        fadeIn(animationSpec = materialFadeSpec),
                    exit = shrinkVertically(animationSpec = materialExpandSpec) +
                        fadeOut(animationSpec = materialFadeSpec),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
                        content = content,
                    )
                }
                if (showDivider) {
                    androidx.compose.material3.HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = materialDividerColor,
                    )
                }
            }
        },
    )
}
