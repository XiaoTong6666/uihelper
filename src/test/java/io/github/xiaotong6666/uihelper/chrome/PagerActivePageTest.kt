package io.github.xiaotong6666.uihelper.chrome

import org.junit.Assert.assertEquals
import org.junit.Test

class PagerActivePageTest {
    @Test fun navigationDoesNotPrematurelySwitchChromeBeforePagerMoves() {
        // A user has requested page 1, but the pager still displays page 0.
        assertEquals(0, visiblePagerPage(0, 0f, 0, false, 2))
    }

    @Test fun titleAndCurrentPageFollowTheSamePhysicalSwipeProgress() {
        assertEquals(0, visiblePagerPage(0, 0.49f, 0, true, 2))
        assertEquals(1, visiblePagerPage(1, -0.49f, 0, true, 2))
        assertEquals(1, visiblePagerPage(1, 0f, 0, true, 2))
        assertEquals(0, visiblePagerPage(0, 0f, 0, false, 2)) // canceled gesture
        assertEquals(1, visiblePagerPage(1, 0f, 1, false, 2)) // completed gesture
    }

    @Test fun boundsAreSafeForTabsAndWideRails() {
        assertEquals(0, visiblePagerPage(-1, 0f, -1, false, 4))
        assertEquals(3, visiblePagerPage(4, 0f, 4, true, 4))
        assertEquals(0, visiblePagerPage(0, 0f, 0, false, 0))
    }
}
