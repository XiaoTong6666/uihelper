package io.github.xiaotong6666.uihelper.adaptive

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WindowSizeTest {
    @Test
    fun phonePortraitKeepsBottomNavigation() {
        assertFalse(shouldUseNavigationRail(widthDp = 599f, heightDp = 900f))
        assertFalse(shouldUseNavigationRail(widthDp = 600f, heightDp = 1000f))
    }

    @Test
    fun landscapeAndLargeWindowsKeepExistingNavigationRailThresholds() {
        assertTrue(shouldUseNavigationRail(widthDp = 600f, heightDp = 719f))
        assertFalse(shouldUseNavigationRail(widthDp = 600f, heightDp = 720f))
        assertTrue(shouldUseNavigationRail(widthDp = 840f, heightDp = 1400f))
    }
}
