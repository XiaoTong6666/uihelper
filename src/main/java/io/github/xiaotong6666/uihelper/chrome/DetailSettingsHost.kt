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

package io.github.xiaotong6666.uihelper.chrome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import io.github.xiaotong6666.uihelper.material.materialAccentIconButtonColors
import io.github.xiaotong6666.uihelper.material.materialChromeIconButtonColors
import io.github.xiaotong6666.uihelper.material.scaffold.ExpressiveScaffold
import io.github.xiaotong6666.uihelper.material.scaffold.expressiveTopAppBarColors
import io.github.xiaotong6666.uihelper.material.scaffold.materialScaffoldEdgeToEdgeInsets
import io.github.xiaotong6666.uihelper.material.scaffold.materialTopBarEdgeToEdgeInsets
import io.github.xiaotong6666.uihelper.mode.LocalUiMode
import io.github.xiaotong6666.uihelper.mode.UiMode
import top.yukonga.miuix.kmp.basic.DropdownImpl
import top.yukonga.miuix.kmp.basic.ListPopupColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.PopupPositionProvider
import top.yukonga.miuix.kmp.overlay.OverlayListPopup
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic
import top.yukonga.miuix.kmp.basic.IconButton as MiuixIconButton
import top.yukonga.miuix.kmp.basic.Scaffold as MiuixScaffold
import top.yukonga.miuix.kmp.basic.TopAppBar as MiuixTopAppBar

data class DetailSettingsOverflowAction(
    val label: String,
    val onClick: () -> Unit,
)

@Composable
fun DetailSettingsHost(
    title: String,
    onBack: () -> Unit,
    onSave: () -> Unit,
    overflowActions: List<DetailSettingsOverflowAction> = emptyList(),
    content: @Composable (PaddingValues, Modifier) -> Unit,
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> DetailSettingsHostMiuix(title, onBack, onSave, overflowActions, content)
        UiMode.Material -> DetailSettingsHostMaterial(title, onBack, onSave, overflowActions, content)
    }
}

@Composable
fun DetailSettingsBody(
    contentPadding: PaddingValues,
    scrollModifier: Modifier,
    content: @Composable () -> Unit,
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> DetailSettingsBodyMiuix(contentPadding, scrollModifier, content)
        UiMode.Material -> DetailSettingsBodyMaterial(contentPadding, scrollModifier, content)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun DetailSettingsHostMaterial(
    title: String,
    onBack: () -> Unit,
    onSave: () -> Unit,
    overflowActions: List<DetailSettingsOverflowAction>,
    content: @Composable (PaddingValues, Modifier) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val haptic = LocalHapticFeedback.current
    var showOverflowMenu by remember { mutableStateOf(false) }
    ExpressiveScaffold(
        contentWindowInsets = materialScaffoldEdgeToEdgeInsets(),
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text(text = title, maxLines = 1) },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        colors = materialChromeIconButtonColors(),
                    ) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    if (overflowActions.isNotEmpty()) {
                        IconButton(
                            onClick = { showOverflowMenu = true },
                            colors = materialChromeIconButtonColors(),
                        ) {
                            Icon(Icons.Default.MoreVert, contentDescription = null)
                            DropdownMenuPopup(
                                expanded = showOverflowMenu,
                                onDismissRequest = { showOverflowMenu = false },
                            ) {
                                DropdownMenuGroup(shapes = MenuDefaults.groupShapes()) {
                                    overflowActions.forEachIndexed { index, action ->
                                        DropdownMenuItem(
                                            selected = false,
                                            text = { Text(action.label) },
                                            onClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                                                showOverflowMenu = false
                                                action.onClick()
                                            },
                                            shapes = MenuDefaults.itemShape(index = index, count = overflowActions.size),
                                        )
                                    }
                                }
                            }
                        }
                    }
                    IconButton(
                        onClick = onSave,
                        colors = materialAccentIconButtonColors(),
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                    }
                },
                colors = expressiveTopAppBarColors(),
                windowInsets = materialTopBarEdgeToEdgeInsets(),
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        content(paddingValues, Modifier.nestedScroll(scrollBehavior.nestedScrollConnection))
    }
}

@Composable
private fun DetailSettingsBodyMaterial(
    contentPadding: PaddingValues,
    scrollModifier: Modifier,
    content: @Composable () -> Unit,
) {
    val scrollState = androidx.compose.foundation.rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(scrollModifier)
            .padding(contentPadding)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        content()
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun DetailSettingsHostMiuix(
    title: String,
    onBack: () -> Unit,
    onSave: () -> Unit,
    overflowActions: List<DetailSettingsOverflowAction>,
    content: @Composable (PaddingValues, Modifier) -> Unit,
) {
    val scrollBehavior = MiuixScrollBehavior()
    var showOverflowMenu by remember { mutableStateOf(false) }
    MiuixScaffold(
        topBar = {
            MiuixTopAppBar(
                title = title,
                color = MiuixTheme.colorScheme.surface,
                titleColor = MiuixTheme.colorScheme.onSurface,
                navigationIcon = {
                    MiuixIconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = null,
                            tint = MiuixTheme.colorScheme.onSurface,
                        )
                    }
                },
                actions = {
                    if (overflowActions.isNotEmpty()) {
                        MiuixIconButton(
                            onClick = { showOverflowMenu = true },
                            holdDownState = showOverflowMenu,
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                tint = MiuixTheme.colorScheme.onSurface,
                                contentDescription = null,
                            )
                        }
                        OverlayListPopup(
                            show = showOverflowMenu,
                            alignment = PopupPositionProvider.Align.TopEnd,
                            onDismissRequest = { showOverflowMenu = false },
                        ) {
                            ListPopupColumn {
                                overflowActions.forEachIndexed { index, action ->
                                    DropdownImpl(
                                        text = action.label,
                                        optionSize = overflowActions.size,
                                        isSelected = false,
                                        index = index,
                                        onSelectedIndexChange = {
                                            showOverflowMenu = false
                                            action.onClick()
                                        },
                                    )
                                }
                            }
                        }
                    }
                    MiuixIconButton(onClick = onSave) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MiuixTheme.colorScheme.onSurface,
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        content(paddingValues, Modifier.nestedScroll(scrollBehavior.nestedScrollConnection))
    }
}

@Composable
private fun DetailSettingsBodyMiuix(
    contentPadding: PaddingValues,
    scrollModifier: Modifier,
    content: @Composable () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .then(scrollModifier)
            .scrollEndHaptic()
            .overScrollVertical(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = contentPadding.calculateTopPadding() + 12.dp,
            bottom = contentPadding.calculateBottomPadding() + 20.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        overscrollEffect = null,
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                content()
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
