package io.github.xiaotong6666.uihelper.adaptive

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class NativeSettingsSegmentedGroupTest {
    @Test
    fun allVisibleRowsAreCountedIncludingTrailingNavigation() {
        val scope = NativeSettingsGroupScope().apply {
            item(key = "crl") {}
            item(key = "location") {}
            item(key = "fsnotify") {}
            item(key = "permission-owner") {}
            item(key = "manage-permissions") {}
        }

        val positions = scope.visiblePositions()
        assertEquals(
            listOf(
                NativeSettingsGroupPosition(0, 5),
                NativeSettingsGroupPosition(1, 5),
                NativeSettingsGroupPosition(2, 5),
                NativeSettingsGroupPosition(3, 5),
                NativeSettingsGroupPosition(4, 5),
            ),
            positions.map { it.second },
        )
        assertEquals("manage-permissions", positions.last().first.key)
    }

    @Test
    fun hiddenRowsDoNotAffectSegmentCorners() {
        val scope = NativeSettingsGroupScope().apply {
            item(key = "crl") {}
            item(key = "location", visible = false) {}
            item(key = "fsnotify") {}
            item(key = "manage-permissions") {}
        }

        assertEquals(listOf("crl", "fsnotify", "manage-permissions"), scope.visiblePositions().map { it.first.key })
        assertEquals(
            listOf(
                NativeSettingsGroupPosition(0, 3),
                NativeSettingsGroupPosition(1, 3),
                NativeSettingsGroupPosition(2, 3),
            ),
            scope.visiblePositions().map { it.second },
        )
    }

    @Test
    fun loneVisibleRowGetsSingleItemGeometry() {
        val scope = NativeSettingsGroupScope().apply {
            item(key = "hidden", visible = false) {}
            item(key = "sole") {}
        }
        assertEquals(listOf(NativeSettingsGroupPosition(0, 1)), scope.visiblePositions().map { it.second })
    }

    @Test
    fun duplicateKeysFailBeforeComposition() {
        assertThrows(IllegalArgumentException::class.java) {
            NativeSettingsGroupScope().apply {
                item(key = "duplicate") {}
                item(key = "duplicate") {}
            }
        }
    }
}
