package io.github.xiaotong6666.uihelper.miuix.primitive

import org.junit.Assert.assertEquals
import org.junit.Test

class GroupedCardItemsMiuixTest {
    @Test
    fun singleRowGetsAllOuterCorners() {
        assertEquals(GroupedCardPosition.Single, groupedCardPosition(index = 0, size = 1))
    }

    @Test
    fun multipleRowsPreserveContinuousEdges() {
        assertEquals(GroupedCardPosition.First, groupedCardPosition(index = 0, size = 4))
        assertEquals(GroupedCardPosition.Middle, groupedCardPosition(index = 1, size = 4))
        assertEquals(GroupedCardPosition.Middle, groupedCardPosition(index = 2, size = 4))
        assertEquals(GroupedCardPosition.Last, groupedCardPosition(index = 3, size = 4))
    }

    @Test(expected = IllegalArgumentException::class)
    fun invalidRowPositionFailsFast() {
        groupedCardPosition(index = 2, size = 2)
    }
}
