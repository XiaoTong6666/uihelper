/*
 * Copyright (C) 2026 XiaoTong6666
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

@file:Suppress("ktlint:standard:function-naming")

package io.github.xiaotong6666.uihelper.adaptive

import androidx.compose.foundation.background
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import io.github.xiaotong6666.uihelper.miuix.primitive.SettingsDropdownItemMiuix
import io.github.xiaotong6666.uihelper.miuix.primitive.SettingsToggleItemMiuix
import io.github.xiaotong6666.uihelper.mode.LocalUiMode
import io.github.xiaotong6666.uihelper.mode.UiMode
import io.github.xiaotong6666.uihelper.popup.OffsetAnchoredPopupMenu
import io.github.xiaotong6666.uihelper.popup.PopupMenuAlignment
import io.github.xiaotong6666.uihelper.popup.PopupMenuGroup
import io.github.xiaotong6666.uihelper.popup.PopupMenuItem
import io.github.xiaotong6666.uihelper.popup.trackPopupMenuPressPosition
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text as MiuixText
import top.yukonga.miuix.kmp.theme.MiuixTheme
import kotlin.math.roundToInt

private val NativeSettingsItemInset = 16.dp
private val NativeSettingsItemPadding = PaddingValues(horizontal = NativeSettingsItemInset, vertical = 14.dp)

/**
 * KSU/InstallerX-style settings section with a native MIUIX title and an M3E compact section title.
 */
@Composable
fun NativeSettingsSection(
    title: String,
    modifier: Modifier = Modifier,
    badge: String? = null,
    materialBadgeColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    materialBadgeContentColor: Color = MaterialTheme.colorScheme.onSurface,
    materialBadgeTextStyle: TextStyle = MaterialTheme.typography.labelSmall,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(adaptiveValue(material = 10.dp, miuix = 2.dp)),
    ) {
        when (LocalUiMode.current) {
            UiMode.Miuix -> Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.padding(bottom = 4.dp)) { SmallTitle(title) }
                badge?.let {
                    MiuixText(
                        text = it,
                        modifier = Modifier
                            .background(MiuixTheme.colorScheme.surfaceContainer, RoundedCornerShape(999.dp))
                            .padding(horizontal = 8.dp, vertical = 1.dp),
                        style = MiuixTheme.textStyles.footnote1.copy(fontFeatureSettings = "tnum"),
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    )
                }
            }
            UiMode.Material -> Row(
                modifier = Modifier.padding(horizontal = NativeSettingsItemInset),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                androidx.compose.material3.Text(
                    text = title,
                    modifier = Modifier.semantics { heading() },
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
                badge?.let {
                    androidx.compose.material3.Text(
                        text = it,
                        modifier = Modifier
                            .background(materialBadgeColor, RoundedCornerShape(999.dp))
                            .padding(horizontal = 8.dp, vertical = 1.dp),
                        style = materialBadgeTextStyle.copy(fontFeatureSettings = "tnum"),
                        color = materialBadgeContentColor,
                    )
                }
            }
        }
        content()
    }
}

/**
 * One logical settings block. MIUIX groups rows in one native Card; Material keeps segmented rows
 * separate so expressive state shapes remain intact.
 */
@Composable
fun NativeSettingsGroup(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> Card(
            modifier = modifier.fillMaxWidth(),
            insideMargin = PaddingValues(0.dp),
        ) {
            Column(modifier = Modifier.fillMaxWidth(), content = content)
        }
        UiMode.Material -> Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
            content = content,
        )
    }
}

@Composable
fun NativeSettingsItemShapes(index: Int, count: Int): ListItemShapes {
    val shapes = MaterialTheme.shapes
    return remember(index, count, shapes) {
        val outer = shapes.large.topStart
        fun shape(inner: CornerSize) = nativeSettingsSegmentShape(index, count, inner, outer)
        ListItemShapes(
            shape = shape(shapes.extraSmall.topStart),
            selectedShape = shape(shapes.large.topStart),
            pressedShape = shape(shapes.large.topStart),
            focusedShape = shape(shapes.large.topStart),
            hoveredShape = shape(shapes.medium.topStart),
            draggedShape = shape(shapes.large.topStart),
        )
    }
}

fun nativeSettingsSegmentShape(
    index: Int,
    count: Int,
    inner: CornerSize,
    outer: CornerSize,
): CornerBasedShape {
    val top = if (index == 0) outer else inner
    val bottom = if (index == count - 1) outer else inner
    return RoundedCornerShape(topStart = top, topEnd = top, bottomEnd = bottom, bottomStart = bottom)
}

@Composable
fun NativeSettingsItemColors(
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    supportingColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
): ListItemColors = ListItemDefaults.segmentedColors(
    containerColor = containerColor,
    contentColor = contentColor,
    leadingContentColor = contentColor,
    trailingContentColor = supportingColor,
    supportingContentColor = supportingColor,
    disabledContainerColor = containerColor,
    disabledContentColor = contentColor,
    disabledLeadingContentColor = contentColor,
    disabledTrailingContentColor = supportingColor,
    disabledSupportingContentColor = supportingColor,
)

/**
 * Slot-based row for app-specific settings that are richer than the high-level Settings*Item
 * helpers. Callers own the semantic content; uihelper owns the native widget family.
 */
@Composable
fun NativeSettingsItem(
    headline: String,
    modifier: Modifier = Modifier,
    materialShapes: ListItemShapes? = null,
    index: Int = 0,
    count: Int = 1,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    materialColors: ListItemColors = NativeSettingsItemColors(),
    leadingContent: (@Composable () -> Unit)? = null,
    supportingContent: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> BasicComponent(
            modifier = modifier,
            startAction = leadingContent,
            endActions = trailingContent?.let { trailing -> { trailing() } },
            onClick = onClick,
            enabled = enabled,
            insideMargin = NativeSettingsItemPadding,
        ) {
            MiuixText(
                text = headline,
                style = MiuixTheme.textStyles.headline1,
                color = MiuixTheme.colorScheme.onSurface,
            )
            CompositionLocalProvider(
                LocalTextStyle provides MiuixTheme.textStyles.body2,
                LocalContentColor provides MiuixTheme.colorScheme.onSurfaceVariantSummary,
            ) {
                supportingContent?.invoke()
            }
        }
        UiMode.Material -> {
            val materialSupporting: (@Composable () -> Unit)? = if (supportingContent == null) {
                null
            } else {
                {
                    CompositionLocalProvider(
                        LocalTextStyle provides MaterialTheme.typography.bodyMedium,
                        LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
                    ) {
                        supportingContent()
                    }
                }
            }
            val shapes = materialShapes ?: NativeSettingsItemShapes(index = index, count = count)
            val headlineContent: @Composable () -> Unit = {
                androidx.compose.material3.Text(
                    text = headline,
                    style = MaterialTheme.typography.bodyLargeEmphasized,
                )
            }
            if (onClick == null) {
                SegmentedListItem(
                    shapes = shapes,
                    modifier = modifier,
                    enabled = enabled,
                    leadingContent = leadingContent,
                    trailingContent = trailingContent,
                    verticalAlignment = Alignment.CenterVertically,
                    supportingContent = materialSupporting,
                    colors = materialColors,
                    contentPadding = NativeSettingsItemPadding,
                    content = headlineContent,
                )
            } else {
                SegmentedListItem(
                    onClick = onClick,
                    shapes = shapes,
                    modifier = modifier,
                    enabled = enabled,
                    leadingContent = leadingContent,
                    trailingContent = trailingContent,
                    verticalAlignment = Alignment.CenterVertically,
                    supportingContent = materialSupporting,
                    colors = materialColors,
                    contentPadding = NativeSettingsItemPadding,
                    content = headlineContent,
                )
            }
        }
    }
}

/** Native switch-preference semantics while callers retain their Material token choices. */
@Composable
fun NativeSettingsToggleItem(
    checked: Boolean,
    title: String,
    description: String,
    icon: ImageVector? = null,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    materialShapes: ListItemShapes? = null,
    materialColors: ListItemColors = NativeSettingsItemColors(),
    materialIconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> SettingsToggleItemMiuix(
            checked = checked,
            title = title,
            description = description,
            icon = icon,
            onToggle = { onCheckedChange(!checked) },
        )
        UiMode.Material -> {
            val shapes = materialShapes ?: NativeSettingsItemShapes(index = 0, count = 1)
            NativeSettingsItem(
                headline = title,
                materialShapes = shapes,
                modifier = modifier
                    .clip(shapes.shape)
                    .toggleable(
                        value = checked,
                        role = Role.Switch,
                        onValueChange = onCheckedChange,
                    ),
                materialColors = materialColors,
                leadingContent = icon?.let {
                    { NativeSettingsIconTile(icon = it, materialTint = materialIconTint) }
                },
                supportingContent = { WrapSafeText(text = description) },
                trailingContent = {
                    Switch(
                        checked = checked,
                        onCheckedChange = null,
                        thumbContent = if (checked) {
                            {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize),
                                )
                            }
                        } else {
                            null
                        },
                    )
                },
            )
        }
    }
}

/** Native dropdown preference with MIUIX overlay selection and an M3E segmented trailing pill. */
@Composable
fun NativeSettingsDropdownItem(
    title: String,
    description: String,
    items: List<String>,
    selectedIndex: Int,
    icon: ImageVector? = null,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    materialShapes: ListItemShapes? = null,
    materialColors: ListItemColors = NativeSettingsItemColors(),
    materialIconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    materialSelectionContainerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    materialSelectionContentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
) {
    if (items.isEmpty()) return
    when (LocalUiMode.current) {
        UiMode.Miuix -> SettingsDropdownItemMiuix(
            title = title,
            description = description,
            items = items,
            selectedIndex = selectedIndex,
            icon = icon,
            onItemSelected = onItemSelected,
        )
        UiMode.Material -> {
            var expanded by remember { mutableStateOf(false) }
            var anchorOffset by remember { mutableStateOf(IntOffset.Zero) }
            val haptics = LocalHapticFeedback.current
            val safeIndex = selectedIndex.coerceIn(0, items.lastIndex)
            Box(
                modifier = modifier.trackPopupMenuPressPosition { position ->
                    anchorOffset = IntOffset(position.x.roundToInt(), 0)
                },
            ) {
                NativeSettingsItem(
                    headline = title,
                    materialShapes = materialShapes ?: NativeSettingsItemShapes(index = 0, count = 1),
                    materialColors = materialColors,
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.VirtualKey)
                        expanded = true
                    },
                    leadingContent = icon?.let {
                        { NativeSettingsIconTile(icon = it, materialTint = materialIconTint) }
                    },
                    supportingContent = { Text(description) },
                    trailingContent = {
                        Row(
                            modifier = Modifier
                                .background(materialSelectionContainerColor, MaterialTheme.shapes.small)
                                .padding(horizontal = 9.dp, vertical = 5.dp),
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = items[safeIndex],
                                color = materialSelectionContentColor,
                                style = MaterialTheme.typography.labelLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Icon(
                                imageVector = Icons.Rounded.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = materialSelectionContentColor,
                            )
                        }
                    },
                )
                OffsetAnchoredPopupMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    anchorOffset = anchorOffset,
                    alignment = PopupMenuAlignment.TopEnd,
                    groups = listOf(
                        PopupMenuGroup(
                            items = items.mapIndexed { index, label ->
                                PopupMenuItem(
                                    label = label,
                                    selected = index == safeIndex,
                                    onClick = {
                                        expanded = false
                                        onItemSelected(index)
                                    },
                                )
                            },
                        ),
                    ),
                )
            }
        }
    }
}

@Composable
fun NativeSettingsIconTile(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    materialTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    miuixTint: Color = MaterialTheme.colorScheme.primary,
) {
    NativeSettingsIconTile(modifier = modifier) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = adaptiveValue(material = materialTint, miuix = miuixTint),
        )
    }
}

@Composable
fun NativeSettingsIconTile(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier.size(28.dp),
        contentAlignment = Alignment.Center,
        content = content,
    )
}

@Composable
fun adaptiveMonochromeIconColor(
    materialColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
): Color = when (LocalUiMode.current) {
    UiMode.Material -> materialColor
    UiMode.Miuix -> if (MiuixTheme.colorScheme.background.luminance() < 0.5f) Color.White else Color.Black
}

@Composable
fun NativeSettingsFootnote(
    text: String,
    modifier: Modifier = Modifier,
    title: String? = null,
    icon: ImageVector? = null,
    materialTitleStyle: TextStyle = MaterialTheme.typography.labelMedium,
    materialBodyStyle: TextStyle = MaterialTheme.typography.bodySmall,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = NativeSettingsItemInset)
            .padding(top = adaptiveValue(material = 0.dp, miuix = 6.dp)),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 1.dp).size(18.dp),
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            if (title != null) {
                WrapSafeText(
                    text = title,
                    style = adaptiveValue(
                        material = materialTitleStyle,
                        miuix = MiuixTheme.textStyles.footnote1,
                    ),
                    color = adaptiveValue(
                        material = MaterialTheme.colorScheme.onSurfaceVariant,
                        miuix = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    ),
                )
            }
            WrapSafeText(
                text = text,
                style = adaptiveValue(
                    material = materialBodyStyle,
                    miuix = MiuixTheme.textStyles.footnote2,
                ),
                color = adaptiveValue(
                    material = MaterialTheme.colorScheme.onSurfaceVariant,
                    miuix = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                ),
            )
        }
    }
}
