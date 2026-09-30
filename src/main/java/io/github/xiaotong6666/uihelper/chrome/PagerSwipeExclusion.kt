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

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput

/** Guards the horizontal shell pager while an independently draggable child owns a gesture. */
internal class PagerSwipeExclusionState {
    private var activeGestures by mutableIntStateOf(0)
    val isBlocked: Boolean get() = activeGestures > 0

    fun begin() { activeGestures++ }
    fun end() { activeGestures = (activeGestures - 1).coerceAtLeast(0) }
}

internal val LocalPagerSwipeExclusionState = compositionLocalOf<PagerSwipeExclusionState?> { null }

/**
 * Apply to a chart, canvas, map or other independent gesture surface. A gesture beginning in
 * this region cannot navigate the parent pager, even when the content is at its pan boundary.
 * The child still receives the original touch stream for taps, pan and pinch-to-zoom. Outside
 * this region, the shell retains its normal swipe navigation. No-op outside a shell.
 */
@Composable
fun pagerSwipeExclusion(): Modifier {
    val state = LocalPagerSwipeExclusionState.current ?: return Modifier
    return Modifier.pointerInput(state) {
        awaitEachGesture {
            awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
            state.begin()
            try {
                do {
                    val event = awaitPointerEvent(pass = PointerEventPass.Final)
                } while (event.changes.any { it.pressed })
            } finally {
                state.end()
            }
        }
    }
}
