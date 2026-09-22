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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults.flingBehavior
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.WideNavigationRail
import androidx.compose.material3.WideNavigationRailDefaults
import androidx.compose.material3.WideNavigationRailItem
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import io.github.xiaotong6666.uihelper.adaptive.shouldShowSplitPane
import io.github.xiaotong6666.uihelper.material.materialChromeIconButtonColors
import io.github.xiaotong6666.uihelper.material.materialSurfaceLadder
import io.github.xiaotong6666.uihelper.material.scaffold.ExpressiveScaffold
import io.github.xiaotong6666.uihelper.material.scaffold.expressiveTopAppBarColors
import io.github.xiaotong6666.uihelper.material.scaffold.materialScaffoldEdgeToEdgeInsets
import io.github.xiaotong6666.uihelper.material.scaffold.materialTopBarEdgeToEdgeInsets
import io.github.xiaotong6666.uihelper.miuix.effect.LocalMiuixBlurActive
import io.github.xiaotong6666.uihelper.miuix.effect.LocalMiuixBlurBackdrop
import io.github.xiaotong6666.uihelper.miuix.effect.MiuixBlurredChrome
import io.github.xiaotong6666.uihelper.miuix.effect.miuixChromeColor
import io.github.xiaotong6666.uihelper.miuix.effect.rememberMiuixBlurBackdrop
import io.github.xiaotong6666.uihelper.mode.LocalUiMode
import io.github.xiaotong6666.uihelper.mode.UiMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.NavigationRailValue
import top.yukonga.miuix.kmp.basic.rememberNavigationRailState
import top.yukonga.miuix.kmp.blur.layerBackdrop
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtils.Companion.MiuixPopupHost
import top.yukonga.miuix.kmp.utils.PagerGestureNestedScrollConnection
import top.yukonga.miuix.kmp.utils.PagerInterceptionMode
import top.yukonga.miuix.kmp.utils.PagerNavigationSpringSpec
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.pagerGestureOverride
import top.yukonga.miuix.kmp.utils.springAnimateToPage
import top.yukonga.miuix.kmp.basic.FloatingNavigationBar as MiuixFloatingNavigationBar
import top.yukonga.miuix.kmp.basic.FloatingNavigationBarItem as MiuixFloatingNavigationBarItem
import top.yukonga.miuix.kmp.basic.NavigationBar as MiuixNavigationBar
import top.yukonga.miuix.kmp.basic.NavigationBarItem as MiuixNavigationBarItem
import top.yukonga.miuix.kmp.basic.NavigationRail as MiuixNavigationRail
import top.yukonga.miuix.kmp.basic.NavigationRailItem as MiuixNavigationRailItem

@Immutable
data class NavigationShellAction(
    val icon: ImageVector,
    val contentDescription: String? = null,
    val onClick: () -> Unit,
)

@Immutable
data class NavigationShellItem(
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon,
    val action: NavigationShellAction? = null,
)

enum class NavigationShellTopBarMode {
    Scrollable,
    Collapsed,
}

private class NavigationShellPagerState(
    val pagerState: PagerState,
    private val coroutineScope: CoroutineScope,
    private val animatePageChanges: Boolean,
) {
    var selectedPage by mutableIntStateOf(pagerState.currentPage)
        private set

    var isNavigating by mutableStateOf(false)
        private set

    private var navJob: Job? = null

    fun animateToPage(targetIndex: Int) {
        if (targetIndex == selectedPage) return

        navJob?.cancel()
        selectedPage = targetIndex
        isNavigating = true

        navJob = coroutineScope.launch {
            val myJob = coroutineContext.job
            try {
                if (animatePageChanges) {
                    pagerState.springAnimateToPage(targetIndex)
                } else {
                    pagerState.scrollToPage(targetIndex)
                }
            } finally {
                if (navJob == myJob) {
                    isNavigating = false
                    if (pagerState.settledPage != targetIndex) {
                        selectedPage = pagerState.settledPage
                    }
                }
            }
        }
    }

    fun syncPage() {
        if (!isNavigating && selectedPage != pagerState.currentPage) {
            selectedPage = pagerState.currentPage
        }
    }

    suspend fun cancelNavigation() {
        navJob?.cancelAndJoin()
        navJob = null
        isNavigating = false
        selectedPage = pagerState.settledPage
    }
}

@Composable
private fun rememberNavigationShellPagerState(
    pagerState: PagerState,
    animatePageChanges: Boolean,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
): NavigationShellPagerState = remember(pagerState, coroutineScope, animatePageChanges) {
    NavigationShellPagerState(pagerState, coroutineScope, animatePageChanges)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AdaptiveNavigationShell(
    items: List<NavigationShellItem>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    topBarMode: NavigationShellTopBarMode = NavigationShellTopBarMode.Scrollable,
    enableMiuixBlur: Boolean = false,
    enableMiuixFloatingBottomBar: Boolean = false,
    content: @Composable (pageIndex: Int, contentPadding: PaddingValues, isCurrentPage: Boolean, pageModifier: Modifier) -> Unit,
) {
    if (items.isEmpty()) return

    val coercedSelectedIndex = selectedIndex.coerceIn(0, items.lastIndex)
    val useNavigationRail = shouldShowSplitPane()
    val pagerMode = PagerInterceptionMode.CrossAxisInterceptor
    val interceptPagerGestures = pagerMode == PagerInterceptionMode.CrossAxisInterceptor
    val pagerState = rememberPagerState(initialPage = coercedSelectedIndex, pageCount = { items.size })
    val shellPagerState = rememberNavigationShellPagerState(
        pagerState = pagerState,
        animatePageChanges = !useNavigationRail,
    )
    val settledPage = pagerState.settledPage
    val activePageIndex = shellPagerState.selectedPage.coerceIn(0, items.lastIndex)
    val activeItem = items[activePageIndex]
    val appChromeState = rememberAppChromeState()
    val chromeSpec = appChromeState.spec
    val miuixScrollBehavior = MiuixScrollBehavior()
    val isTopBarScrollable = topBarMode == NavigationShellTopBarMode.Scrollable
    var contentReady by remember { mutableStateOf(false) }
    var navigationRailExpanded by rememberSaveable { mutableStateOf(false) }
    val onPageSelected: (Int) -> Unit = { index ->
        if (shellPagerState.selectedPage != index) {
            shellPagerState.animateToPage(index)
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        shellPagerState.syncPage()
    }

    LaunchedEffect(settledPage) {
        if (selectedIndex != settledPage) {
            onSelectedIndexChange(settledPage)
        }
    }

    LaunchedEffect(selectedIndex) {
        val targetIndex = selectedIndex.coerceIn(0, items.lastIndex)
        if (!shellPagerState.isNavigating && targetIndex != shellPagerState.selectedPage) {
            shellPagerState.animateToPage(targetIndex)
        }
    }

    LaunchedEffect(pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress && shellPagerState.isNavigating) {
            shellPagerState.cancelNavigation()
        }
    }

    LaunchedEffect(Unit) {
        withFrameNanos { }
        contentReady = true
    }

    val navigationEventState = rememberNavigationEventState(NavigationEventInfo.None)
    NavigationBackHandler(
        state = navigationEventState,
        isBackEnabled = activePageIndex != 0,
        onBackCompleted = { onPageSelected(0) },
    )

    when (LocalUiMode.current) {
        UiMode.Miuix -> {
            val blurBackdrop = rememberMiuixBlurBackdrop(enableMiuixBlur)
            val blurActive = blurBackdrop != null
            val pageHostHandle = remember(miuixScrollBehavior, isTopBarScrollable) {
                if (isTopBarScrollable) {
                    PageHostHandle(
                        nestedScrollConnection = miuixScrollBehavior.nestedScrollConnection,
                        collapsedFractionProvider = { miuixScrollBehavior.state.collapsedFraction },
                    )
                } else {
                    PageHostHandle(collapsedFractionProvider = { 1f })
                }
            }
            top.yukonga.miuix.kmp.basic.Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .then(modifier),
                topBar = {
                    CompositionLocalProvider(LocalMiuixBlurActive provides blurActive) {
                        MiuixBlurredChrome(backdrop = blurBackdrop) {
                            val defaultTopBar: ComposableContent = {
                                if (isTopBarScrollable) {
                                    top.yukonga.miuix.kmp.basic.TopAppBar(
                                        title = activeItem.title,
                                        color = miuixChromeColor(blurActive),
                                        titleColor = MiuixTheme.colorScheme.onSurface,
                                        actions = {
                                            activeItem.action?.let { item ->
                                                top.yukonga.miuix.kmp.basic.IconButton(
                                                    onClick = item.onClick,
                                                ) {
                                                    Icon(
                                                        imageVector = item.icon,
                                                        contentDescription = item.contentDescription,
                                                        tint = MiuixTheme.colorScheme.onSurface,
                                                    )
                                                }
                                            }
                                        },
                                        scrollBehavior = miuixScrollBehavior,
                                    )
                                } else {
                                    top.yukonga.miuix.kmp.basic.SmallTopAppBar(
                                        title = activeItem.title,
                                        color = miuixChromeColor(blurActive),
                                        actions = {
                                            activeItem.action?.let { item ->
                                                top.yukonga.miuix.kmp.basic.IconButton(
                                                    onClick = item.onClick,
                                                ) {
                                                    Icon(
                                                        imageVector = item.icon,
                                                        contentDescription = item.contentDescription,
                                                        tint = MiuixTheme.colorScheme.onSurface,
                                                    )
                                                }
                                            }
                                        },
                                    )
                                }
                            }
                            val miuixTopBar = chromeSpec.miuixTopBar
                            val miuixTopBarWrapper = chromeSpec.miuixTopBarWrapper
                            when {
                                miuixTopBar != null -> miuixTopBar()
                                miuixTopBarWrapper != null -> miuixTopBarWrapper(defaultTopBar)
                                else -> defaultTopBar()
                            }
                        }
                    }
                },
                popupHost = chromeSpec.miuixPopupHost ?: { MiuixPopupHost() },
                bottomBar = if (chromeSpec.hideBottomBar || useNavigationRail) {
                    {}
                } else {
                    {
                        if (enableMiuixFloatingBottomBar) {
                            MiuixFloatingNavigationBar {
                                items.forEachIndexed { index, item ->
                                    MiuixFloatingNavigationBarItem(
                                        selected = activePageIndex == index,
                                        onClick = { onPageSelected(index) },
                                        icon = item.icon,
                                        label = item.title,
                                    )
                                }
                            }
                        } else {
                            MiuixBlurredChrome(backdrop = blurBackdrop) {
                                MiuixNavigationBar(
                                    color = miuixChromeColor(blurActive),
                                ) {
                                    items.forEachIndexed { index, item ->
                                        MiuixNavigationBarItem(
                                            selected = activePageIndex == index,
                                            onClick = { onPageSelected(index) },
                                            icon = item.icon,
                                            label = item.title,
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
            ) { paddingValues ->
                CompositionLocalProvider(
                    LocalAppChromeState provides appChromeState,
                    LocalPageHostHandle provides pageHostHandle,
                    LocalMiuixBlurActive provides blurActive,
                    LocalMiuixBlurBackdrop provides blurBackdrop,
                    LocalMiuixNestedScrollConnection provides miuixScrollBehavior.nestedScrollConnection.takeIf { isTopBarScrollable },
                    LocalMiuixCollapsedFractionProvider provides {
                        if (isTopBarScrollable) miuixScrollBehavior.state.collapsedFraction else 1f
                    },
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .then(
                                if (blurBackdrop != null && !chromeSpec.consumeOuterScroll) {
                                    Modifier.layerBackdrop(blurBackdrop)
                                } else {
                                    Modifier
                                },
                            ),
                    ) {
                        if (useNavigationRail && !chromeSpec.hideBottomBar) {
                            val railState = rememberNavigationRailState(
                                initialValue = if (navigationRailExpanded) {
                                    NavigationRailValue.Expanded
                                } else {
                                    NavigationRailValue.Collapsed
                                },
                            )
                            LaunchedEffect(railState.currentValue) {
                                navigationRailExpanded = railState.isExpanded
                            }
                            MiuixNavigationRail(
                                modifier = Modifier.fillMaxHeight(),
                                state = railState,
                                color = MiuixTheme.colorScheme.surface,
                                expandContentDescription = "Expand navigation",
                                collapseContentDescription = "Collapse navigation",
                            ) {
                                items.forEachIndexed { index, item ->
                                    MiuixNavigationRailItem(
                                        selected = activePageIndex == index,
                                        onClick = { onPageSelected(index) },
                                        icon = item.icon,
                                        label = item.title,
                                    )
                                }
                            }
                        }
                        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .pagerGestureOverride(
                                        pagerState = pagerState,
                                        mode = pagerMode,
                                        enabled = true,
                                    ),
                                beyondViewportPageCount = if (contentReady) minOf(3, items.lastIndex) else 0,
                                overscrollEffect = null,
                                userScrollEnabled = !interceptPagerGestures,
                                pageNestedScrollConnection = PagerGestureNestedScrollConnection,
                                flingBehavior = flingBehavior(
                                    state = pagerState,
                                    snapAnimationSpec = PagerNavigationSpringSpec,
                                ),
                            ) { page ->
                                if (!contentReady && page != settledPage) {
                                    return@HorizontalPager
                                }
                                val pageModifier = if (chromeSpec.consumeOuterScroll || !isTopBarScrollable) {
                                    Modifier
                                } else {
                                    Modifier.nestedScroll(miuixScrollBehavior.nestedScrollConnection)
                                }

                                content(
                                    page,
                                    paddingValues,
                                    page == settledPage,
                                    pageModifier,
                                )
                            }
                            chromeSpec.overlayContent?.invoke(paddingValues)
                        }
                    }
                }
            }
        }

        UiMode.Material -> {
            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
            val surfaces = materialSurfaceLadder()
            val pageHostHandle = remember(scrollBehavior, isTopBarScrollable) {
                if (isTopBarScrollable) {
                    PageHostHandle(
                        nestedScrollConnection = scrollBehavior.nestedScrollConnection,
                        collapsedFractionProvider = { scrollBehavior.state.collapsedFraction },
                    )
                } else {
                    PageHostHandle(collapsedFractionProvider = { 1f })
                }
            }
            ExpressiveScaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .then(modifier)
                    .then(
                        if (chromeSpec.consumeOuterScroll || !isTopBarScrollable) {
                            Modifier
                        } else {
                            Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                        },
                    ),
                contentWindowInsets = materialScaffoldEdgeToEdgeInsets(),
                topBar = chromeSpec.materialTopBar ?: {
                    if (isTopBarScrollable) {
                        LargeFlexibleTopAppBar(
                            title = { Text(text = activeItem.title) },
                            actions = {
                                activeItem.action?.let { item ->
                                    IconButton(
                                        onClick = item.onClick,
                                        colors = materialChromeIconButtonColors(),
                                    ) {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = item.contentDescription,
                                        )
                                    }
                                }
                            },
                            colors = expressiveTopAppBarColors(),
                            scrollBehavior = scrollBehavior,
                            windowInsets = materialTopBarEdgeToEdgeInsets(),
                        )
                    } else {
                        TopAppBar(
                            title = { Text(text = activeItem.title) },
                            actions = {
                                activeItem.action?.let { item ->
                                    IconButton(
                                        onClick = item.onClick,
                                        colors = materialChromeIconButtonColors(),
                                    ) {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = item.contentDescription,
                                        )
                                    }
                                }
                            },
                            colors = expressiveTopAppBarColors(),
                            windowInsets = materialTopBarEdgeToEdgeInsets(),
                        )
                    }
                },
                bottomBar = if (chromeSpec.hideBottomBar || useNavigationRail) {
                    {}
                } else {
                    {
                        ShortNavigationBar(
                            containerColor = surfaces.page,
                            windowInsets = WindowInsets.systemBars.union(WindowInsets.displayCutout).only(
                                WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom,
                            ),
                        ) {
                            items.forEachIndexed { index, item ->
                                val selected = activePageIndex == index
                                ShortNavigationBarItem(
                                    selected = selected,
                                    onClick = { onPageSelected(index) },
                                    icon = {
                                        Icon(
                                            imageVector = if (selected) item.selectedIcon else item.icon,
                                            contentDescription = item.title,
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = item.title,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    },
                                )
                            }
                        }
                    }
                },
            ) { paddingValues ->
                CompositionLocalProvider(
                    LocalAppChromeState provides appChromeState,
                    LocalPageHostHandle provides pageHostHandle,
                    LocalMaterialNestedScrollConnection provides scrollBehavior.nestedScrollConnection.takeIf { isTopBarScrollable },
                ) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        if (useNavigationRail && !chromeSpec.hideBottomBar) {
                            val railState = rememberWideNavigationRailState(
                                initialValue = if (navigationRailExpanded) {
                                    WideNavigationRailValue.Expanded
                                } else {
                                    WideNavigationRailValue.Collapsed
                                },
                            )
                            val railScope = rememberCoroutineScope()
                            val railExpanded = railState.targetValue == WideNavigationRailValue.Expanded
                            LaunchedEffect(railState.targetValue) {
                                navigationRailExpanded =
                                    railState.targetValue == WideNavigationRailValue.Expanded
                            }
                            WideNavigationRail(
                                modifier = Modifier.fillMaxHeight(),
                                state = railState,
                                colors = WideNavigationRailDefaults.colors().copy(
                                    containerColor = surfaces.page,
                                    contentColor = MaterialTheme.colorScheme.onSurface,
                                ),
                                windowInsets = WindowInsets.systemBars.union(WindowInsets.displayCutout).only(
                                    WindowInsetsSides.Start + WindowInsetsSides.Vertical,
                                ),
                                contentPadding = PaddingValues(vertical = 20.dp),
                                header = {
                                    IconButton(
                                        modifier = Modifier.padding(start = 24.dp),
                                        onClick = {
                                            railScope.launch {
                                                if (railExpanded) {
                                                    railState.collapse()
                                                } else {
                                                    railState.expand()
                                                }
                                            }
                                        },
                                    ) {
                                        Icon(
                                            imageVector = if (railExpanded) {
                                                Icons.AutoMirrored.Filled.MenuOpen
                                            } else {
                                                Icons.Filled.Menu
                                            },
                                            contentDescription = null,
                                        )
                                    }
                                },
                            ) {
                                items.forEachIndexed { index, item ->
                                    val selected = activePageIndex == index
                                    WideNavigationRailItem(
                                        railExpanded = railExpanded,
                                        selected = selected,
                                        onClick = { onPageSelected(index) },
                                        icon = {
                                            Icon(
                                                imageVector = if (selected) item.selectedIcon else item.icon,
                                                contentDescription = item.title,
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = item.title,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                            )
                                        },
                                    )
                                }
                            }
                        }
                        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .pagerGestureOverride(
                                        pagerState = pagerState,
                                        mode = pagerMode,
                                        enabled = true,
                                    ),
                                beyondViewportPageCount = if (contentReady) minOf(3, items.lastIndex) else 0,
                                overscrollEffect = null,
                                userScrollEnabled = !interceptPagerGestures,
                                pageNestedScrollConnection = PagerGestureNestedScrollConnection,
                                flingBehavior = flingBehavior(
                                    state = pagerState,
                                    snapAnimationSpec = PagerNavigationSpringSpec,
                                ),
                            ) { page ->
                                if (!contentReady && page != settledPage) {
                                    return@HorizontalPager
                                }
                                content(
                                    page,
                                    paddingValues,
                                    page == settledPage,
                                    Modifier,
                                )
                            }
                            chromeSpec.overlayContent?.invoke(paddingValues)
                        }
                    }
                }
            }
        }
    }
}
