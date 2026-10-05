/*
 * Copyright (C) 2026 XiaoTong6666
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

@file:Suppress("ktlint:standard:function-naming")

package io.github.xiaotong6666.uihelper.adaptive

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import io.github.xiaotong6666.uihelper.mode.LocalUiMode
import io.github.xiaotong6666.uihelper.mode.UiMode
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.basic.Text as MiuixText

/**
 * Equal-width single-choice buttons using each skin's native selection treatment.
 *
 * Material uses the connected expressive toggle group while MIUIX uses native buttons with the
 * selected item promoted to the primary treatment. The caller owns only labels and selection.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AdaptiveSingleChoiceButtonGroup(
    labels: List<String>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    if (labels.isEmpty()) return

    when (LocalUiMode.current) {
        UiMode.Material -> {
            val haptic = LocalHapticFeedback.current
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
            ) {
                labels.forEachIndexed { index, label ->
                    val selected = index == selectedIndex
                    ToggleButton(
                        checked = selected,
                        onCheckedChange = {
                            if (!selected) {
                                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                onSelectedIndexChange(index)
                            }
                        },
                        enabled = enabled,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = ToggleButtonDefaults.MinHeight)
                            .semantics { role = Role.RadioButton },
                        colors = ToggleButtonDefaults.colors(
                            checkedContainerColor = MaterialTheme.colorScheme.primary,
                            checkedContentColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        ),
                        shapes = when {
                            labels.size == 1 -> ToggleButtonDefaults.shapesFor(ToggleButtonDefaults.MinHeight)
                            index == 0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                            index == labels.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                            else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                        },
                    ) {
                        Text(text = label, maxLines = 1)
                    }
                }
            }
        }

        UiMode.Miuix -> Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            labels.forEachIndexed { index, label ->
                val selected = index == selectedIndex
                Button(
                    onClick = {
                        if (!selected) onSelectedIndexChange(index)
                    },
                    enabled = enabled,
                    modifier = Modifier
                        .weight(1f)
                        .semantics {
                            role = Role.RadioButton
                            this.selected = selected
                        },
                    colors = if (selected) {
                        ButtonDefaults.buttonColorsPrimary()
                    } else {
                        ButtonDefaults.buttonColors()
                    },
                ) {
                    MiuixText(
                        text = label,
                        style = MiuixTheme.textStyles.button,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}
