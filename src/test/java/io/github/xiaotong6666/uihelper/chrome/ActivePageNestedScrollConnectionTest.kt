package io.github.xiaotong6666.uihelper.chrome

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ActivePageNestedScrollConnectionTest {
    @Test fun onlyVisibleSettledPageMayChangeSharedChrome() {
        assertTrue(isActivePageScrollOwner(page = 0, visiblePage = 0, isPagerScrolling = false))
        assertFalse(isActivePageScrollOwner(page = 1, visiblePage = 0, isPagerScrolling = false))
        assertFalse(isActivePageScrollOwner(page = 0, visiblePage = 0, isPagerScrolling = true))
        assertFalse(isActivePageScrollOwner(page = 1, visiblePage = 1, isPagerScrolling = true))
        assertTrue(isActivePageScrollOwner(page = 1, visiblePage = 1, isPagerScrolling = false))
    }

    @Test fun offscreenScrollAndDelayedCallbacksCannotMoveChrome() {
        var active = false
        var callbacks = 0
        val delegate = object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                callbacks++
                return available
            }

            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                callbacks++
                return available
            }
        }
        val connection = ActivePageNestedScrollConnection(delegate) { active }
        val delta = Offset(0f, -50f)
        assertEquals(Offset.Zero, connection.onPreScroll(delta, NestedScrollSource.UserInput))
        assertEquals(Offset.Zero, connection.onPostScroll(delta, delta, NestedScrollSource.UserInput))
        assertEquals(0, callbacks)
        active = true
        assertEquals(delta, connection.onPreScroll(delta, NestedScrollSource.UserInput))
        assertEquals(delta, connection.onPostScroll(delta, delta, NestedScrollSource.UserInput))
        assertEquals(2, callbacks)
    }
}
