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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import io.github.xiaotong6666.uihelper.material.materialAccentIconButtonColors
import io.github.xiaotong6666.uihelper.material.materialChromeIconButtonColors
import io.github.xiaotong6666.uihelper.material.scaffold.ExpressiveScaffold
import io.github.xiaotong6666.uihelper.material.scaffold.expressiveTopAppBarColors
import io.github.xiaotong6666.uihelper.material.scaffold.materialScaffoldEdgeToEdgeInsets
import io.github.xiaotong6666.uihelper.material.scaffold.materialTopBarEdgeToEdgeInsets
import io.github.xiaotong6666.uihelper.mode.LocalUiMode
import io.github.xiaotong6666.uihelper.mode.UiMode
import io.github.xiaotong6666.uihelper.popup.PopupMenuGroup
import io.github.xiaotong6666.uihelper.popup.PopupMenuIconButton
import io.github.xiaotong6666.uihelper.popup.PopupMenuItem
import io.github.xiaotong6666.uihelper.popup.hasPopupMenuItems
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
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

enum class DetailToolbarActionStyle {
    Default,
    Accent,
}

data class DetailToolbarAction(
    val icon: ImageVector,
    val onClick: () -> Unit,
    val contentDescription: String? = null,
    val style: DetailToolbarActionStyle = DetailToolbarActionStyle.Default,
)

@Composable
fun DetailPageHost(
    title: String,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    actions: List<DetailToolbarAction> = emptyList(),
    overflowGroups: List<PopupMenuGroup> = emptyList(),
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    contentWindowInsets: WindowInsets? = null,
    content: @Composable (PaddingValues, Modifier) -> Unit,
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> DetailPageHostMiuix(
            title = title,
            subtitle = subtitle,
            onBack = onBack,
            actions = actions,
            overflowGroups = overflowGroups,
            contentWindowInsets = contentWindowInsets,
            content = content,
        )

        UiMode.Material -> DetailPageHostMaterial(
            title = title,
            subtitle = subtitle,
            onBack = onBack,
            actions = actions,
            overflowGroups = overflowGroups,
            snackbarHost = snackbarHost,
            floatingActionButton = floatingActionButton,
            floatingActionButtonPosition = floatingActionButtonPosition,
            contentWindowInsets = contentWindowInsets,
            content = content,
        )
    }
}

@Composable
fun DetailPageBody(
    contentPadding: PaddingValues,
    scrollModifier: Modifier,
    content: @Composable () -> Unit,
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> DetailPageBodyMiuix(contentPadding, scrollModifier, content)
        UiMode.Material -> DetailPageBodyMaterial(contentPadding, scrollModifier, content)
    }
}

@Composable
fun DetailSettingsHost(
    title: String,
    onBack: () -> Unit,
    onSave: () -> Unit,
    overflowActions: List<DetailSettingsOverflowAction> = emptyList(),
    content: @Composable (PaddingValues, Modifier) -> Unit,
) {
    DetailPageHost(
        title = title,
        subtitle = null,
        onBack = onBack,
        actions = listOf(
            DetailToolbarAction(
                icon = Icons.Default.Check,
                onClick = onSave,
                style = DetailToolbarActionStyle.Accent,
            ),
        ),
        overflowGroups = overflowActions.toOverflowGroups(),
        content = content,
    )
}

@Composable
fun DetailSettingsBody(
    contentPadding: PaddingValues,
    scrollModifier: Modifier,
    content: @Composable () -> Unit,
) {
    DetailPageBody(contentPadding, scrollModifier, content)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun DetailPageHostMaterial(
    title: String,
    subtitle: String?,
    onBack: (() -> Unit)?,
    actions: List<DetailToolbarAction>,
    overflowGroups: List<PopupMenuGroup>,
    snackbarHost: @Composable () -> Unit,
    floatingActionButton: @Composable () -> Unit,
    floatingActionButtonPosition: FabPosition,
    contentWindowInsets: WindowInsets?,
    content: @Composable (PaddingValues, Modifier) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    ExpressiveScaffold(
        contentWindowInsets = contentWindowInsets ?: materialScaffoldEdgeToEdgeInsets(),
        snackbarHost = snackbarHost,
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        topBar = {
            LargeFlexibleTopAppBar(
                title = {
                    Column {
                        Text(text = title, maxLines = 1)
                        if (!subtitle.isNullOrBlank()) {
                            Text(
                                text = subtitle,
                                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                            )
                        }
                    }
                },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(
                            onClick = onBack,
                            colors = materialChromeIconButtonColors(),
                        ) {
                            Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = null)
                        }
                    }
                },
                actions = {
                    if (hasPopupMenuItems(overflowGroups)) {
                        PopupMenuIconButton(
                            icon = Icons.Default.MoreVert,
                            contentDescription = null,
                            groups = overflowGroups,
                        )
                    }
                    actions.forEach { action ->
                        IconButton(
                            onClick = action.onClick,
                            colors = when (action.style) {
                                DetailToolbarActionStyle.Accent -> materialAccentIconButtonColors()
                                DetailToolbarActionStyle.Default -> materialChromeIconButtonColors()
                            },
                        ) {
                            Icon(
                                imageVector = action.icon,
                                contentDescription = action.contentDescription,
                            )
                        }
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
private fun DetailPageBodyMaterial(
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
private fun DetailPageHostMiuix(
    title: String,
    subtitle: String?,
    onBack: (() -> Unit)?,
    actions: List<DetailToolbarAction>,
    overflowGroups: List<PopupMenuGroup>,
    contentWindowInsets: WindowInsets?,
    content: @Composable (PaddingValues, Modifier) -> Unit,
) {
    val scrollBehavior = MiuixScrollBehavior()
    val topBar: @Composable () -> Unit = {
        MiuixTopAppBar(
            title = title,
            subtitle = subtitle.orEmpty(),
            color = MiuixTheme.colorScheme.surface,
            titleColor = MiuixTheme.colorScheme.onSurface,
            navigationIcon = {
                if (onBack != null) {
                    MiuixIconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = null,
                            tint = MiuixTheme.colorScheme.onSurface,
                        )
                    }
                }
            },
            actions = {
                if (hasPopupMenuItems(overflowGroups)) {
                    PopupMenuIconButton(
                        icon = Icons.Default.MoreVert,
                        contentDescription = null,
                        groups = overflowGroups,
                    )
                }
                actions.forEach { action ->
                    MiuixIconButton(onClick = action.onClick) {
                        Icon(
                            imageVector = action.icon,
                            contentDescription = action.contentDescription,
                            tint = MiuixTheme.colorScheme.onSurface,
                        )
                    }
                }
            },
            scrollBehavior = scrollBehavior,
        )
    }

    if (contentWindowInsets != null) {
        MiuixScaffold(
            contentWindowInsets = contentWindowInsets,
            topBar = topBar,
        ) { paddingValues ->
            content(paddingValues, Modifier.nestedScroll(scrollBehavior.nestedScrollConnection))
        }
    } else {
        MiuixScaffold(
            topBar = topBar,
        ) { paddingValues ->
            content(paddingValues, Modifier.nestedScroll(scrollBehavior.nestedScrollConnection))
        }
    }
}

@Composable
private fun DetailPageBodyMiuix(
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

private fun List<DetailSettingsOverflowAction>.toOverflowGroups(): List<PopupMenuGroup> {
    if (isEmpty()) return emptyList()
    return listOf(
        PopupMenuGroup(
            items = map { action ->
                PopupMenuItem(
                    label = action.label,
                    onClick = action.onClick,
                )
            },
        ),
    )
}
