package io.github.xiaotong6666.uihelper.chrome

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity

/**
 * A pager may keep multiple pages composed, but only the visibly active page may drive its
 * shared collapsing chrome. Ignore incoming and outgoing pages during horizontal transitions.
 * This preserves the same top-bar geometry until the new page actually owns the viewport.
 */
internal class ActivePageNestedScrollConnection(
    private val delegate: NestedScrollConnection,
    private val isActive: () -> Boolean,
) : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset =
        if (isActive()) delegate.onPreScroll(available, source) else Offset.Zero

    override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset =
        if (isActive()) delegate.onPostScroll(consumed, available, source) else Offset.Zero

    override suspend fun onPreFling(available: Velocity): Velocity =
        if (isActive()) delegate.onPreFling(available) else Velocity.Zero

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity =
        if (isActive()) delegate.onPostFling(consumed, available) else Velocity.Zero
}

internal fun isActivePageScrollOwner(page: Int, visiblePage: Int, isPagerScrolling: Boolean): Boolean =
    !isPagerScrolling && page == visiblePage
