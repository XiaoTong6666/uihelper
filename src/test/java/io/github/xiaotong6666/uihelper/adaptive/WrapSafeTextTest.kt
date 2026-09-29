package io.github.xiaotong6666.uihelper.adaptive

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import java.util.Locale

class WrapSafeTextTest {
    @Test fun preservesOriginalTechnicalValueWhenVisualBreaksAreRemoved() {
        val value = "a_long/package.name:abc1234567890"
        val displayed = value.withWrapOpportunities(Locale.US)
        assertEquals(value, displayed.replace("\u200B", ""))
    }

    @Test fun neverBreaksInsideSurrogatePairs() {
        val value = "12345678901😀23456789012😀34567890"
        val displayed = value.withWrapOpportunities(Locale.US)
        assertEquals(value, displayed.replace("\u200B", ""))
        assertFalse(displayed.contains("\uD83D\u200B\uDE00"))
    }

    @Test fun neverBreaksInsideEmojiZwJCluster() {
        val value = "12345678901👩‍💻abcdefghijkl"
        val displayed = value.withWrapOpportunities(Locale.US)
        assertEquals(value, displayed.replace("\u200B", ""))
        assertFalse(displayed.contains("👩\u200B‍"))
        assertFalse(displayed.contains("‍\u200B💻"))
    }
}
