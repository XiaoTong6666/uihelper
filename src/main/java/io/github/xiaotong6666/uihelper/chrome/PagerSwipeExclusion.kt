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

package io.github.xiaotong6666.uihelper.chrome

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.input.pointer.util.addPointerInputChange
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

private sealed interface PagerMotionEvent {
    data class Delta(val x: Float) : PagerMotionEvent
    data class Finish(val velocityX: Float) : PagerMotionEvent
}

/** Registry of root-coordinate regions whose gestures belong to child content, not the shell pager. */
internal class PagerSwipeExclusionRegistry {
    private val regions = linkedMapOf<Any, Rect>()
    private val ownerPages = mutableStateMapOf<Any, Int>()

    fun attach(owner: Any, page: Int) {
        if (ownerPages[owner] != page) ownerPages[owner] = page
    }

    fun update(owner: Any, page: Int, bounds: Rect) {
        attach(owner, page)
        regions[owner] = bounds
    }

    fun remove(owner: Any) {
        regions.remove(owner)
        ownerPages.remove(owner)
    }

    fun hasRegions(page: Int): Boolean = ownerPages.values.any { it == page }

    fun contains(page: Int, positionInRoot: Offset): Boolean = regions.any { (owner, bounds) ->
        ownerPages[owner] == page && positionInRoot in bounds
    }

}

internal val LocalPagerSwipeExclusionRegistry = compositionLocalOf<PagerSwipeExclusionRegistry?> { null }
internal val LocalPagerSwipeExclusionPage = compositionLocalOf<Int?> { null }

/**
 * Marks a chart/map/canvas region as owning every gesture that starts inside its laid-out bounds.
 * The parent shell still allows horizontal tab swipes everywhere else.
 *
 * Unlike child-side pointer consumption, this only registers geometry. The shell performs the
 * arbitration before its own pager drag recognizer can latch the pointer, so custom `pointerInput`
 * and `transformable` children keep their full pan/pinch streams.
 */
@Composable
fun pagerSwipeExclusion(): Modifier {
    val registry = LocalPagerSwipeExclusionRegistry.current ?: return Modifier
    val page = LocalPagerSwipeExclusionPage.current ?: return Modifier
    val owner = remember { Any() }
    DisposableEffect(registry, owner, page) {
        // Register during composition, before the first layout pass. This disables the shell's
        // native/custom pager recognizers before an immediately-started gesture can race the
        // first onGloballyPositioned callback. The exact hit rectangle is filled in by layout.
        registry.attach(owner, page)
        onDispose { registry.remove(owner) }
    }
    return Modifier.onGloballyPositioned { coordinates ->
        registry.update(owner, page, coordinates.boundsInRoot())
    }
}

/** Parent-side pager drag used on pages that contain registered interactive regions. */
@Composable
internal fun Modifier.pagerSwipeExclusionHost(
    registry: PagerSwipeExclusionRegistry,
    page: Int,
    pagerState: PagerState,
    enabled: Boolean,
    settleAnimationSpec: AnimationSpec<Float> = spring(stiffness = Spring.StiffnessMediumLow),
): Modifier {
    val layoutDirection = LocalLayoutDirection.current
    var hostBounds = Rect.Zero
    return this
        .onGloballyPositioned {
            val next = it.boundsInRoot()
            if (next != hostBounds) hostBounds = next
        }
        .pointerInput(registry, page, pagerState, enabled, layoutDirection, settleAnimationSpec) {
            if (!enabled) return@pointerInput
            val minimumVelocity = 400.dp.toPx()
            coroutineScope {
                val gestureScope = this
                var motionJob: Job? = null
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Main)
                    val positionInRoot = hostBounds.topLeft + down.position
                    val excluded = registry.contains(page, positionInRoot)
                    if (excluded) return@awaitEachGesture

                    val velocityTracker = VelocityTracker().apply { addPointerInputChange(down) }
                    val touchSlop = viewConfiguration.touchSlop
                    var accumulated = Offset.Zero
                    var trackedId = down.id

                    while (true) {
                        val event = awaitPointerEvent(pass = PointerEventPass.Main)
                        val change = event.changes.firstOrNull { it.id == trackedId }
                            ?: event.changes.firstOrNull { it.pressed }
                            ?: return@awaitEachGesture
                        trackedId = change.id
                        velocityTracker.addPointerInputChange(change)
                        if (change.changedToUpIgnoreConsumed()) return@awaitEachGesture
                        if (change.isConsumed) return@awaitEachGesture

                        accumulated += change.positionChange()
                        if (accumulated.getDistance() < touchSlop) continue
                        if (abs(accumulated.x) <= abs(accumulated.y)) return@awaitEachGesture
                        change.consume()

                        // A new horizontal drag takes over from the currently displayed position.
                        // Only another horizontal pager gesture may interrupt this motion; a
                        // vertical child gesture must not cancel a pending snap and strand the
                        // pager between pages.
                        motionJob?.cancel()
                        motionJob = null

                        val directionSign = if (
                            (layoutDirection == LayoutDirection.Rtl) xor pagerState.layoutInfo.reverseLayout
                        ) 1f else -1f

                        val events = Channel<PagerMotionEvent>(Channel.UNLIMITED)
                        motionJob = gestureScope.launch(start = CoroutineStart.UNDISPATCHED) {
                            pagerState.scroll(MutatePriority.UserInput) {
                                for (motionEvent in events) {
                                    when (motionEvent) {
                                        is PagerMotionEvent.Delta -> scrollBy(motionEvent.x * directionSign)
                                        is PagerMotionEvent.Finish -> {
                                            val position = pagerState.currentPage + pagerState.currentPageOffsetFraction
                                            val signedVelocity = motionEvent.velocityX * directionSign
                                            val target = when {
                                                signedVelocity > minimumVelocity -> ceil(position).toInt()
                                                signedVelocity < -minimumVelocity -> floor(position).toInt()
                                                else -> position.roundToInt()
                                            }.coerceIn(0, pagerState.pageCount - 1)
                                            val pageSize = pagerState.layoutInfo.pageSize + pagerState.layoutInfo.pageSpacing
                                            val distance = pagerState.getOffsetDistanceInPages(target) * pageSize
                                            var previousValue = 0f
                                            animate(
                                                initialValue = 0f,
                                                targetValue = distance,
                                                initialVelocity = signedVelocity,
                                                animationSpec = settleAnimationSpec,
                                            ) { value, _ ->
                                                val delta = value - previousValue
                                                previousValue += scrollBy(delta)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        events.trySend(PagerMotionEvent.Delta(accumulated.x))
                        try {
                            while (true) {
                                val dragEvent = awaitPointerEvent(pass = PointerEventPass.Main)
                                val dragChange = dragEvent.changes.firstOrNull { it.id == trackedId }
                                    ?: dragEvent.changes.firstOrNull { it.pressed }
                                    ?: break
                                trackedId = dragChange.id
                                velocityTracker.addPointerInputChange(dragChange)
                                if (dragChange.changedToUpIgnoreConsumed()) break
                                val deltaX = dragChange.positionChange().x
                                dragChange.consume()
                                events.trySend(PagerMotionEvent.Delta(deltaX))
                            }
                        } finally {
                            // Keep the same PagerState.scroll mutation alive for the snap. This is
                            // important because settledPage is derived from isScrollInProgress;
                            // ending the drag mutation before starting the snap can temporarily
                            // make the destination look settled and tear down this host mid-snap.
                        }
                        val releaseVelocityX = velocityTracker.calculateVelocity().x
                        events.trySend(PagerMotionEvent.Finish(releaseVelocityX))
                        events.close()
                        return@awaitEachGesture
                    }
                }
            }
        }
}
