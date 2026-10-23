package io.github.xiaotong6666.uihelper.material.primitive

import io.github.xiaotong6666.uihelper.adaptive.DetailTextRow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class GroupedTextRowsMaterialTest {
    @Test
    fun stableIdentityIsIndependentOfListPosition() {
        val row = DetailTextRow(title = "Same name", key = "com.example.app")
        assertEquals("group:com.example.app", groupedTextRowKey("group", row, 0))
        assertEquals("group:com.example.app", groupedTextRowKey("group", row, 8))
    }

    @Test
    fun repeatedTitlesWithoutIdsHaveDistinctKeys() {
        val row = DetailTextRow(title = "Duplicate")
        assertNotEquals(groupedTextRowKey("group", row, 0), groupedTextRowKey("group", row, 1))
    }

    @Test
    fun groupNamespaceSeparatesDifferentCategories() {
        val row = DetailTextRow(title = "Same name", key = "com.example.app")
        assertNotEquals(groupedTextRowKey("root", row, 0), groupedTextRowKey("hook", row, 0))
    }
}
