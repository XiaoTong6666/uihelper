package io.github.xiaotong6666.uihelper.chrome

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PagerSwipeExclusionTest {
    @Test
    fun `overlapping touch owners do not release each other`() {
        val state = PagerSwipeExclusionState()
        assertFalse(state.isBlocked)
        state.begin()
        state.begin()
        assertTrue(state.isBlocked)
        state.end()
        assertTrue(state.isBlocked)
        state.end()
        assertFalse(state.isBlocked)
        state.end()
        assertFalse(state.isBlocked)
    }
}
