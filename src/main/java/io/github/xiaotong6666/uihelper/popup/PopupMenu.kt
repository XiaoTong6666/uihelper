/*
 * Copyright (C) 2026 XiaoTong6666
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("ktlint:standard:function-naming")

package io.github.xiaotong6666.uihelper.popup

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.xiaotong6666.uihelper.material.materialChromeIconButtonColors
import io.github.xiaotong6666.uihelper.mode.LocalUiMode
import io.github.xiaotong6666.uihelper.mode.UiMode
import top.yukonga.miuix.kmp.basic.DropdownImpl
import top.yukonga.miuix.kmp.basic.ListPopupColumn
import top.yukonga.miuix.kmp.basic.PopupPositionProvider
import top.yukonga.miuix.kmp.overlay.OverlayListPopup
import top.yukonga.miuix.kmp.theme.MiuixTheme
import kotlin.math.roundToInt
import top.yukonga.miuix.kmp.basic.HorizontalDivider as MiuixHorizontalDivider
import top.yukonga.miuix.kmp.basic.Icon as MiuixIcon
import top.yukonga.miuix.kmp.basic.IconButton as MiuixIconButton

data class PopupMenuItem(
    val label: String,
    val onClick: () -> Unit,
    val selected: Boolean = false,
)

data class PopupMenuGroup(
    val items: List<PopupMenuItem>,
)

internal fun hasPopupMenuItems(groups: List<PopupMenuGroup>): Boolean = groups.any { it.items.isNotEmpty() }

enum class PopupMenuAlignment {
    TopStart,
    TopEnd,
    BottomStart,
    BottomEnd,
}

object PopupMenuDefaults {
    val MenuPositionProvider = object : PopupPositionProvider {
        override fun calculatePosition(
            anchorBounds: IntRect,
            windowBounds: IntRect,
            layoutDirection: LayoutDirection,
            popupContentSize: IntSize,
            popupMargin: IntRect,
            alignment: PopupPositionProvider.Align,
        ): IntOffset {
            val resolvedAlignment = alignment.resolve(layoutDirection)
            val offsetX: Int
            val offsetY: Int
            when (resolvedAlignment) {
                PopupPositionProvider.Align.TopStart -> {
                    offsetX = anchorBounds.left + popupMargin.left
                    offsetY = anchorBounds.bottom + popupMargin.top
                }

                PopupPositionProvider.Align.TopEnd -> {
                    offsetX = anchorBounds.right - popupContentSize.width - popupMargin.right
                    offsetY = anchorBounds.bottom + popupMargin.top
                }

                PopupPositionProvider.Align.BottomStart -> {
                    offsetX = anchorBounds.left + popupMargin.left
                    offsetY = anchorBounds.top - popupContentSize.height - popupMargin.bottom
                }

                PopupPositionProvider.Align.BottomEnd -> {
                    offsetX = anchorBounds.right - popupContentSize.width - popupMargin.right
                    offsetY = anchorBounds.top - popupContentSize.height - popupMargin.bottom
                }

                else -> {
                    offsetX = if (resolvedAlignment == PopupPositionProvider.Align.End) {
                        anchorBounds.right - popupContentSize.width - popupMargin.right
                    } else {
                        anchorBounds.left + popupMargin.left
                    }
                    offsetY = if (windowBounds.bottom - anchorBounds.bottom > popupContentSize.height) {
                        anchorBounds.bottom + popupMargin.bottom
                    } else if (anchorBounds.top - windowBounds.top > popupContentSize.height) {
                        anchorBounds.top - popupContentSize.height - popupMargin.top
                    } else {
                        anchorBounds.top + anchorBounds.height / 2 - popupContentSize.height / 2
                    }
                }
            }
            return IntOffset(
                x = offsetX.coerceIn(
                    windowBounds.left,
                    (windowBounds.right - popupContentSize.width - popupMargin.right).coerceAtLeast(windowBounds.left),
                ),
                y = offsetY.coerceIn(
                    (windowBounds.top + popupMargin.top).coerceAtMost(windowBounds.bottom - popupContentSize.height - popupMargin.bottom),
                    windowBounds.bottom - popupContentSize.height - popupMargin.bottom,
                ),
            )
        }

        override fun getMargins(): PaddingValues = PaddingValues(start = 20.dp)
    }
}

@Composable
fun PopupMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    groups: List<PopupMenuGroup>,
    alignment: PopupMenuAlignment = PopupMenuAlignment.TopEnd,
) {
    if (!hasPopupMenuItems(groups)) return

    when (LocalUiMode.current) {
        UiMode.Material -> MaterialPopupMenu(expanded, onDismissRequest, groups)
        UiMode.Miuix -> MiuixPopupMenu(expanded, onDismissRequest, groups, alignment)
    }
}

@Composable
fun PopupMenuIconButton(
    icon: ImageVector,
    contentDescription: String?,
    groups: List<PopupMenuGroup>,
    modifier: Modifier = Modifier,
    alignment: PopupMenuAlignment = PopupMenuAlignment.TopEnd,
) {
    if (!hasPopupMenuItems(groups)) return

    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        when (LocalUiMode.current) {
            UiMode.Material -> {
                IconButton(
                    onClick = { expanded = true },
                    colors = materialChromeIconButtonColors(),
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = contentDescription,
                    )
                }
            }

            UiMode.Miuix -> {
                MiuixIconButton(
                    onClick = { expanded = true },
                    holdDownState = expanded,
                ) {
                    MiuixIcon(
                        imageVector = icon,
                        contentDescription = contentDescription,
                        tint = MiuixTheme.colorScheme.onSurface,
                    )
                }
            }
        }

        PopupMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            groups = groups,
            alignment = alignment,
        )
    }
}

@Composable
fun OffsetAnchoredPopupMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    groups: List<PopupMenuGroup>,
    anchorOffset: IntOffset = IntOffset.Zero,
    alignment: PopupMenuAlignment = PopupMenuAlignment.TopStart,
) {
    Box(
        modifier = Modifier.layout { measurable, constraints ->
            val placeable = measurable.measure(Constraints())
            val width = if (constraints.hasBoundedWidth) constraints.maxWidth else 0
            layout(width, 0) {
                placeable.place(anchorOffset.x, anchorOffset.y)
            }
        },
    ) {
        PopupMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
            groups = groups,
            alignment = alignment,
        )
    }
}

fun Modifier.trackPopupMenuPressPosition(onPress: (Offset) -> Unit): Modifier = pointerInput(Unit) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        onPress(down.position)
    }
}

@Composable
private fun MaterialPopupMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    groups: List<PopupMenuGroup>,
) {
    val haptic = LocalHapticFeedback.current
    DropdownMenuPopup(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
    ) {
        groups.forEachIndexed { groupIndex, group ->
            DropdownMenuGroup(
                shapes = MenuDefaults.groupShape(index = groupIndex, count = groups.size),
                modifier = Modifier.verticalScroll(rememberScrollState()),
            ) {
                group.items.forEachIndexed { itemIndex, item ->
                    DropdownMenuItem(
                        text = { Text(item.label) },
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                            onDismissRequest()
                            item.onClick()
                        },
                        selected = item.selected,
                        shapes = MenuDefaults.itemShape(index = itemIndex, count = group.items.size),
                        selectedLeadingIcon = if (item.selected) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(MenuDefaults.LeadingIconSize),
                                )
                            }
                        } else {
                            null
                        },
                    )
                }
            }

            if (groupIndex != groups.lastIndex) {
                Spacer(Modifier.height(MenuDefaults.GroupSpacing))
            }
        }
    }
}

@Composable
private fun MiuixPopupMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    groups: List<PopupMenuGroup>,
    alignment: PopupMenuAlignment,
) {
    val haptic = LocalHapticFeedback.current
    OverlayListPopup(
        show = expanded,
        popupPositionProvider = PopupMenuDefaults.MenuPositionProvider,
        alignment = alignment.toMiuixAlignment(),
        onDismissRequest = onDismissRequest,
    ) {
        ListPopupColumn {
            groups.forEachIndexed { groupIndex, group ->
                group.items.forEachIndexed { itemIndex, item ->
                    DropdownImpl(
                        text = item.label,
                        optionSize = group.items.size,
                        isSelected = item.selected,
                        index = itemIndex,
                        onSelectedIndexChange = {
                            haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                            onDismissRequest()
                            item.onClick()
                        },
                    )
                }

                if (groupIndex != groups.lastIndex) {
                    MiuixHorizontalDivider(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                        thickness = 1.5.dp,
                    )
                }
            }
        }
    }
}

private fun PopupMenuAlignment.toMiuixAlignment(): PopupPositionProvider.Align = when (this) {
    PopupMenuAlignment.TopStart -> PopupPositionProvider.Align.TopStart
    PopupMenuAlignment.TopEnd -> PopupPositionProvider.Align.TopEnd
    PopupMenuAlignment.BottomStart -> PopupPositionProvider.Align.BottomStart
    PopupMenuAlignment.BottomEnd -> PopupPositionProvider.Align.BottomEnd
}

private fun PopupPositionProvider.Align.resolve(layoutDirection: LayoutDirection): PopupPositionProvider.Align {
    if (layoutDirection == LayoutDirection.Ltr) return this
    return when (this) {
        PopupPositionProvider.Align.Start -> PopupPositionProvider.Align.End
        PopupPositionProvider.Align.End -> PopupPositionProvider.Align.Start
        PopupPositionProvider.Align.TopStart -> PopupPositionProvider.Align.TopEnd
        PopupPositionProvider.Align.TopEnd -> PopupPositionProvider.Align.TopStart
        PopupPositionProvider.Align.BottomStart -> PopupPositionProvider.Align.BottomEnd
        PopupPositionProvider.Align.BottomEnd -> PopupPositionProvider.Align.BottomStart
    }
}
