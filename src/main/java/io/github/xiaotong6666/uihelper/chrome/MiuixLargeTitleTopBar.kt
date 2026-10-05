/*
 * Copyright (C) 2026 XiaoTong6666
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

@file:Suppress("ktlint:standard:function-naming")

package io.github.xiaotong6666.uihelper.chrome

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.anim.folmeSpring
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.TopAppBarDefaults
import top.yukonga.miuix.kmp.theme.MiuixTheme
import kotlin.math.roundToInt

private const val STABLE_LARGE_TITLE_MEASURE_TEXT = "\u00A0"

/**
 * Native MIUIX collapsing large-title bar with an optional leading slot in the expanded title.
 *
 * MIUIX's [TopAppBar] only accepts text for its large title. This helper preserves the native
 * scroll geometry while drawing a slot-based expanded title as a pure overlay. When an expanded
 * leading slot is present, the compact leading slot is revealed with the same spring as the title
 * collapses.
 */
@Composable
fun MiuixLargeTitleTopBar(
    title: String,
    scrollBehavior: ScrollBehavior,
    modifier: Modifier = Modifier,
    compactTitle: String = title,
    color: Color = MiuixTheme.colorScheme.surface,
    titleColor: Color = MiuixTheme.colorScheme.onSurface,
    expandedLeadingContent: (@Composable () -> Unit)? = null,
    compactLeadingContent: (@Composable () -> Unit)? = expandedLeadingContent,
    actions: @Composable RowScope.() -> Unit = {},
) {
    val hasExpandedLeading = expandedLeadingContent != null
    val compactLeadingVisible by remember(scrollBehavior, hasExpandedLeading) {
        derivedStateOf {
            !hasExpandedLeading || scrollBehavior.state.collapsedFraction * 3f >= 1f
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clipToBounds(),
    ) {
        TopAppBar(
            title = compactTitle,
            largeTitle = STABLE_LARGE_TITLE_MEASURE_TEXT,
            color = color,
            titleColor = titleColor,
            titlePadding = TopAppBarDefaults.TitlePadding,
            navigationIcon = {
                if (hasExpandedLeading) {
                    AnimatedCompactLeading(visible = compactLeadingVisible) {
                        compactLeadingContent?.invoke()
                    }
                } else {
                    compactLeadingContent?.invoke()
                }
            },
            actions = actions,
            scrollBehavior = scrollBehavior,
        )

        Row(
            modifier = Modifier
                .matchParentSize()
                .windowInsetsPadding(WindowInsets.statusBars.only(WindowInsetsSides.Top))
                .padding(top = TopAppBarDefaults.CollapsedHeight)
                .offset { IntOffset(0, scrollBehavior.state.heightOffset.roundToInt()) }
                .graphicsLayer {
                    alpha = (1f - scrollBehavior.state.collapsedFraction * 3f).coerceIn(0f, 1f)
                }
                .padding(horizontal = TopAppBarDefaults.TitlePadding),
            horizontalArrangement = Arrangement.spacedBy(if (hasExpandedLeading) 10.dp else 0.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            expandedLeadingContent?.invoke()
            Text(
                text = title,
                modifier = Modifier.weight(1f, fill = false),
                color = titleColor,
                fontSize = MiuixTheme.textStyles.title1.fontSize,
                fontWeight = FontWeight.Normal,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun AnimatedCompactLeading(
    visible: Boolean,
    content: @Composable () -> Unit,
) {
    val alpha = remember { Animatable(if (visible) 1f else 0f) }
    val translationY = remember { Animatable(if (visible) 0f else 20f) }

    LaunchedEffect(visible) {
        val spec = folmeSpring<Float>(
            damping = 1.0f,
            response = if (visible) 0.30f else 0.15f,
        )
        launch { alpha.animateTo(if (visible) 1f else 0f, spec) }
        launch { translationY.animateTo(if (visible) 0f else 20f, spec) }
    }

    Box(
        modifier = Modifier.graphicsLayer {
            this.alpha = alpha.value
            this.translationY = translationY.value
        },
    ) {
        content()
    }
}
