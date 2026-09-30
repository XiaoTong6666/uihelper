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
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults.flingBehavior
import androidx.compose.foundation.pager.PagerDefaults.pageNestedScrollConnection
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
import androidx.compose.runtime.Stable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.res.stringResource
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
import top.yukonga.miuix.kmp.basic.Text as MiuixText
import top.yukonga.miuix.kmp.basic.TopAppBarDefaults as MiuixTopAppBarDefaults
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
import kotlin.math.roundToInt
import top.yukonga.miuix.kmp.basic.FloatingNavigationBar as MiuixFloatingNavigationBar
import top.yukonga.miuix.kmp.basic.FloatingNavigationBarItem as MiuixFloatingNavigationBarItem
import top.yukonga.miuix.kmp.basic.NavigationBar as MiuixNavigationBar
import top.yukonga.miuix.kmp.basic.NavigationBarItem as MiuixNavigationBarItem
import top.yukonga.miuix.kmp.basic.NavigationRail as MiuixNavigationRail
import top.yukonga.miuix.kmp.basic.NavigationRailItem as MiuixNavigationRailItem

@Stable
data class NavigationShellAction(
    val icon: ImageVector,
    val contentDescription: String? = null,
    val onClick: () -> Unit,
)

@Stable
data class NavigationShellItem(
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon,
    val topBarTitle: String = title,
    val action: NavigationShellAction? = null,
    /** Optional short MIUIX collapsed title; expanded and Material titles stay [topBarTitle]. */
    val compactTopBarTitle: String = topBarTitle,
    /** Optional app-owned leading content in the native top bar (e.g. a brand glyph). */
    val leadingContent: (@Composable () -> Unit)? = null,
    /** Optional glyph next to expanded MIUIX title; pair with [leadingContent] for collapsed state. */
    val largeTitleLeadingContent: (@Composable () -> Unit)? = null,
    /** Optional app-owned action(s). Overrides [action] when supplied. */
    val trailingContent: (@Composable RowScope.() -> Unit)? = null,
    /** Custom Material title slot, e.g. a brand icon next to the title text. */
    val materialTitleContent: (@Composable () -> Unit)? = null,
)

enum class NavigationShellTopBarMode {
    Scrollable,
    Collapsed,
}

/** Controls which tab, if any, handles Back. Disabled lets the parent route host handle it. */
enum class NavigationShellBackBehavior { FirstPage, PreviousPage, Disabled }

/** A stable one-line native measurement slot; app-owned expanded titles are rendered on top. */
private const val StableLargeTitleMeasureText = "\u00A0"

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
    navigationRail: Boolean? = null,
    swipeNavigationEnabled: Boolean = true,
    backBehavior: NavigationShellBackBehavior = NavigationShellBackBehavior.FirstPage,
    onBackRequested: ((currentPage: Int) -> Unit)? = null,
    content: @Composable (pageIndex: Int, contentPadding: PaddingValues, isCurrentPage: Boolean, pageModifier: Modifier) -> Unit,
) {
    if (items.isEmpty()) return

    val coercedSelectedIndex = selectedIndex.coerceIn(0, items.lastIndex)
    val useNavigationRail = navigationRail ?: shouldShowSplitPane()
    val pagerMode = PagerInterceptionMode.CrossAxisInterceptor
    val pagerState = rememberPagerState(initialPage = coercedSelectedIndex, pageCount = { items.size })
    val pagerSwipeExclusions = remember { PagerSwipeExclusionRegistry() }
    val shellPagerState = rememberNavigationShellPagerState(
        pagerState = pagerState,
        animatePageChanges = !useNavigationRail,
    )
    val settledPage = pagerState.settledPage
    // A page with interactive canvases/maps uses uihelper's region-aware shell drag instead of
    // either HorizontalPager's native drag or MIUIX's pagerGestureOverride. This makes ownership
    // a down-time geometry decision: a gesture that starts in an exclusion never enters a pager
    // recognizer at all, while the rest of the page keeps normal horizontal tab navigation.
    val pageHasSwipeExclusions = pagerSwipeExclusions.hasRegions(settledPage)
    val useCustomPagerGestures = swipeNavigationEnabled && !pageHasSwipeExclusions
    val interceptPagerGestures = useCustomPagerGestures && pagerMode == PagerInterceptionMode.CrossAxisInterceptor
    // FuseHide's pager fix (d0d727e): chrome must follow the page that is
    // physically visible, never the optimistic destination set by a tab click.
    // A tap may start its spring several frames before the pager actually moves.
    // The fractional pager offset changes every animation frame. Derived state
    // invalidates the chrome only when the *integer* visible page changes.
    val activePageIndex by remember(pagerState, items.size) {
        derivedStateOf {
            visiblePagerPage(
                currentPage = pagerState.currentPage,
                currentPageOffsetFraction = pagerState.currentPageOffsetFraction,
                settledPage = pagerState.settledPage,
                isScrollInProgress = pagerState.isScrollInProgress,
                pageCount = items.size,
            )
        }
    }
    val activeItem = items[activePageIndex]
    val appChromeState = rememberAppChromeState()
    val chromeSpec = appChromeState.spec
    // Vertical events from the outgoing and incoming page must not compete for
    // the one shared title while the gesture belongs to the horizontal pager.
    val canScrollMiuixChrome = remember(pagerState) { { !pagerState.isScrollInProgress } }
    val miuixScrollBehavior = MiuixScrollBehavior(canScroll = canScrollMiuixChrome)
    val isTopBarScrollable = topBarMode == NavigationShellTopBarMode.Scrollable
    var contentReady by remember { mutableStateOf(false) }
    var navigationRailExpanded by rememberSaveable { mutableStateOf(false) }
    val onPageSelected: (Int) -> Unit = { index ->
        if (pagerState.isScrollInProgress || settledPage != index) {
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
        isBackEnabled = activePageIndex != 0 && (backBehavior != NavigationShellBackBehavior.Disabled || onBackRequested != null),
        onBackCompleted = {
            if (onBackRequested != null) onBackRequested(activePageIndex)
            else when (backBehavior) {
                NavigationShellBackBehavior.FirstPage -> onPageSelected(0)
                NavigationShellBackBehavior.PreviousPage -> onPageSelected(activePageIndex - 1)
                NavigationShellBackBehavior.Disabled -> Unit
            }
        },
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
                                    val largeLeading = activeItem.largeTitleLeadingContent
                                    Box(Modifier.fillMaxWidth().wrapContentHeight().clipToBounds()) {
                                        top.yukonga.miuix.kmp.basic.TopAppBar(
                                            title = activeItem.compactTopBarTitle,
                                            // The native bar measures its large title to compute heightOffsetLimit.
                                            // All tabs must have the same one-line measure or switching from a
                                            // wrapped title to a short one changes the shared collapse geometry.
                                            // The actual title (and optional glyph) is drawn in the overlay below.
                                            largeTitle = StableLargeTitleMeasureText,
                                            color = miuixChromeColor(blurActive),
                                            titleColor = MiuixTheme.colorScheme.onSurface,
                                            // MIUIX applies this *on both sides* to the compact title as well.
                                            // Reserving the expanded glyph here incorrectly ellipsizes a title
                                            // that would fit between the navigation and action icons.
                                            titlePadding = MiuixTopAppBarDefaults.TitlePadding,
                                            navigationIcon = {
                                                Box(
                                                    modifier = if (largeLeading != null) Modifier.graphicsLayer {
                                                        alpha = (miuixScrollBehavior.state.collapsedFraction * 3f).coerceIn(0f, 1f)
                                                    } else Modifier,
                                                ) { activeItem.leadingContent?.invoke() }
                                            },
                                            actions = {
                                                if (activeItem.trailingContent != null) {
                                                    activeItem.trailingContent.invoke(this)
                                                } else activeItem.action?.let { item ->
                                                    top.yukonga.miuix.kmp.basic.IconButton(onClick = item.onClick) {
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
                                        // A single-line, asymmetrically spaced expanded title: only the
                                        // leading glyph uses width; the right-hand text area remains available.
                                        // This also keeps native heightOffsetLimit identical across tab titles.
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .windowInsetsPadding(WindowInsets.statusBars.only(WindowInsetsSides.Top))
                                                .padding(top = MiuixTopAppBarDefaults.CollapsedHeight)
                                                .offset { IntOffset(0, miuixScrollBehavior.state.heightOffset.roundToInt()) }
                                                .graphicsLayer {
                                                    alpha = (1f - miuixScrollBehavior.state.collapsedFraction * 3f).coerceIn(0f, 1f)
                                                }
                                                .padding(horizontal = MiuixTopAppBarDefaults.TitlePadding),
                                            horizontalArrangement = Arrangement.spacedBy(if (largeLeading != null) 10.dp else 0.dp),
                                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                                        ) {
                                            largeLeading?.invoke()
                                            MiuixText(
                                                text = activeItem.topBarTitle,
                                                modifier = Modifier.weight(1f, fill = false),
                                                color = MiuixTheme.colorScheme.onSurface,
                                                fontSize = MiuixTheme.textStyles.title1.fontSize,
                                                fontWeight = FontWeight.Normal,
                                                maxLines = 1,
                                                softWrap = false,
                                                overflow = TextOverflow.Ellipsis,
                                            )
                                        }
                                    }
                                } else {
                                    top.yukonga.miuix.kmp.basic.SmallTopAppBar(
                                        title = activeItem.topBarTitle,
                                        color = miuixChromeColor(blurActive),
                                        navigationIcon = { activeItem.leadingContent?.invoke() },
                                        actions = {
                                            if (activeItem.trailingContent != null) {
                                                activeItem.trailingContent.invoke(this)
                                            } else activeItem.action?.let { item ->
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
                    LocalPagerSwipeExclusionRegistry provides pagerSwipeExclusions,
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
                                expandContentDescription = stringResource(io.github.xiaotong6666.uihelper.R.string.uihelper_expand_navigation),
                                collapseContentDescription = stringResource(io.github.xiaotong6666.uihelper.R.string.uihelper_collapse_navigation),
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
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .pagerSwipeExclusionHost(
                                    registry = pagerSwipeExclusions,
                                    page = settledPage,
                                    pagerState = pagerState,
                                    enabled = swipeNavigationEnabled && pageHasSwipeExclusions,
                                    settleAnimationSpec = PagerNavigationSpringSpec,
                                ),
                        ) {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .pagerGestureOverride(
                                        pagerState = pagerState,
                                        mode = pagerMode,
                                        enabled = useCustomPagerGestures,
                                    ),
                                beyondViewportPageCount = if (contentReady) minOf(3, items.lastIndex) else 0,
                                overscrollEffect = null,
                                userScrollEnabled = swipeNavigationEnabled && !pageHasSwipeExclusions && !interceptPagerGestures,
                                pageNestedScrollConnection = if (interceptPagerGestures) {
                                    PagerGestureNestedScrollConnection
                                } else {
                                    pageNestedScrollConnection(
                                        state = pagerState,
                                        orientation = Orientation.Horizontal,
                                    )
                                },
                                flingBehavior = flingBehavior(
                                    state = pagerState,
                                    snapAnimationSpec = PagerNavigationSpringSpec,
                                ),
                            ) { page ->
                                if (!contentReady && page != settledPage) {
                                    return@HorizontalPager
                                }
                                val pageConnection = remember(page, pagerState, miuixScrollBehavior, items.size) {
                                    ActivePageNestedScrollConnection(miuixScrollBehavior.nestedScrollConnection) {
                                        isActivePageScrollOwner(
                                            page = page,
                                            visiblePage = visiblePagerPage(
                                                currentPage = pagerState.currentPage,
                                                currentPageOffsetFraction = pagerState.currentPageOffsetFraction,
                                                settledPage = pagerState.settledPage,
                                                isScrollInProgress = pagerState.isScrollInProgress,
                                                pageCount = items.size,
                                            ),
                                            isPagerScrolling = pagerState.isScrollInProgress,
                                        )
                                    }
                                }
                                val pageHost = remember(pageConnection, isTopBarScrollable) {
                                    if (isTopBarScrollable) PageHostHandle(
                                        nestedScrollConnection = pageConnection,
                                        collapsedFractionProvider = { miuixScrollBehavior.state.collapsedFraction },
                                    ) else PageHostHandle(collapsedFractionProvider = { 1f })
                                }
                                val pageModifier = if (chromeSpec.consumeOuterScroll || !isTopBarScrollable) {
                                    Modifier
                                } else {
                                    Modifier.nestedScroll(pageConnection)
                                }
                                CompositionLocalProvider(
                                    LocalMiuixNestedScrollConnection provides pageConnection.takeIf { isTopBarScrollable },
                                    LocalPageHostHandle provides pageHost,
                                    LocalPagerSwipeExclusionPage provides page,
                                ) {
                                    content(
                                        page,
                                        paddingValues,
                                        page == activePageIndex,
                                        pageModifier,
                                    )
                                }
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
                            title = { activeItem.materialTitleContent?.invoke() ?: Text(text = activeItem.topBarTitle) },
                            navigationIcon = { if (activeItem.materialTitleContent == null) activeItem.leadingContent?.invoke() },
                            actions = {
                                if (activeItem.trailingContent != null) {
                                    activeItem.trailingContent.invoke(this)
                                } else activeItem.action?.let { item ->
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
                            title = { activeItem.materialTitleContent?.invoke() ?: Text(text = activeItem.topBarTitle) },
                            navigationIcon = { if (activeItem.materialTitleContent == null) activeItem.leadingContent?.invoke() },
                            actions = {
                                if (activeItem.trailingContent != null) {
                                    activeItem.trailingContent.invoke(this)
                                } else activeItem.action?.let { item ->
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
                    LocalPagerSwipeExclusionRegistry provides pagerSwipeExclusions,
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
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .pagerSwipeExclusionHost(
                                    registry = pagerSwipeExclusions,
                                    page = settledPage,
                                    pagerState = pagerState,
                                    enabled = swipeNavigationEnabled && pageHasSwipeExclusions,
                                    settleAnimationSpec = PagerNavigationSpringSpec,
                                ),
                        ) {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .pagerGestureOverride(
                                        pagerState = pagerState,
                                        mode = pagerMode,
                                        enabled = useCustomPagerGestures,
                                    ),
                                beyondViewportPageCount = if (contentReady) minOf(3, items.lastIndex) else 0,
                                overscrollEffect = null,
                                userScrollEnabled = swipeNavigationEnabled && !pageHasSwipeExclusions && !interceptPagerGestures,
                                pageNestedScrollConnection = if (interceptPagerGestures) {
                                    PagerGestureNestedScrollConnection
                                } else {
                                    pageNestedScrollConnection(
                                        state = pagerState,
                                        orientation = Orientation.Horizontal,
                                    )
                                },
                                flingBehavior = flingBehavior(
                                    state = pagerState,
                                    snapAnimationSpec = PagerNavigationSpringSpec,
                                ),
                            ) { page ->
                                if (!contentReady && page != settledPage) {
                                    return@HorizontalPager
                                }
                                CompositionLocalProvider(LocalPagerSwipeExclusionPage provides page) {
                                    content(
                                        page,
                                        paddingValues,
                                        page == activePageIndex,
                                        Modifier,
                                    )
                                }
                            }
                            chromeSpec.overlayContent?.invoke(paddingValues)
                        }
                    }
                }
            }
        }
    }
}
