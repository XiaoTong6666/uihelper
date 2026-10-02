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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.xiaotong6666.uihelper.common.SegmentedGeometry

@Immutable
data class AdaptiveSectionGeometry(
    val topRadius: Dp,
    val bottomRadius: Dp,
)

object AdaptiveSectionGroupDefaults {
    val OuterRadius: Dp = SegmentedGeometry.OuterRadius
    val InnerRadius: Dp = SegmentedGeometry.InnerRadius
}

val LocalAdaptiveSectionGeometry = compositionLocalOf {
    AdaptiveSectionGeometry(
        topRadius = AdaptiveSectionGroupDefaults.OuterRadius,
        bottomRadius = AdaptiveSectionGroupDefaults.OuterRadius,
    )
}

@DslMarker
annotation class AdaptiveSectionGroupDsl

@AdaptiveSectionGroupDsl
class AdaptiveSectionGroupScope internal constructor() {
    internal data class Entry(
        val content: @Composable () -> Unit,
    )

    internal val entries = mutableListOf<Entry>()

    fun item(
        visible: Boolean = true,
        content: @Composable () -> Unit,
    ) {
        if (visible) entries += Entry(content)
    }
}

/**
 * Cross-skin grouping for vertically adjacent sections.
 *
 * Material receives segmented outer/inner geometry and its standard segmented gap. MIUIX keeps a
 * continuous native group with no artificial gap. Child content owns its business semantics and
 * reads [LocalAdaptiveSectionGeometry] only when it needs the Material segment shape.
 */
@Composable
fun AdaptiveSectionGroup(
    modifier: Modifier = Modifier,
    materialOuterRadius: Dp = AdaptiveSectionGroupDefaults.OuterRadius,
    materialInnerRadius: Dp = AdaptiveSectionGroupDefaults.InnerRadius,
    materialGap: Dp = ListItemDefaults.SegmentedGap,
    miuixGap: Dp = 0.dp,
    content: AdaptiveSectionGroupScope.() -> Unit,
) {
    val entries = AdaptiveSectionGroupScope().apply(content).entries
    if (entries.isEmpty()) return

    AdaptiveContent(
        material = {
            Column(
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(materialGap),
            ) {
                entries.forEachIndexed { index, entry ->
                    CompositionLocalProvider(
                        LocalAdaptiveSectionGeometry provides AdaptiveSectionGeometry(
                            topRadius = if (index == 0) materialOuterRadius else materialInnerRadius,
                            bottomRadius = if (index == entries.lastIndex) materialOuterRadius else materialInnerRadius,
                        ),
                    ) {
                        entry.content()
                    }
                }
            }
        },
        miuix = {
            Column(
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(miuixGap),
            ) {
                entries.forEach { entry -> entry.content() }
            }
        },
    )
}
