package io.github.xiaotong6666.uihelper.chrome

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PagerSwipeExclusionTest {
    @Test
    fun `registry disables pager as soon as an exclusion enters composition`() {
        val registry = PagerSwipeExclusionRegistry()
        val wall = Any()

        registry.attach(wall, page = 1)

        assertTrue(registry.hasRegions(1))
        assertFalse(registry.hasRegions(0))
        // Before the first layout there is intentionally no hit rectangle yet.
        assertFalse(registry.contains(1, Offset(300f, 400f)))
    }

    @Test
    fun `registry follows region ownership bounds and removal`() {
        val registry = PagerSwipeExclusionRegistry()
        val wall = Any()
        val wordmark = Any()

        registry.update(wall, page = 1, Rect(100f, 200f, 500f, 600f))
        registry.update(wordmark, page = 1, Rect(100f, 700f, 500f, 900f))

        assertTrue(registry.hasRegions(1))
        assertFalse(registry.hasRegions(0))
        assertTrue(registry.contains(1, Offset(300f, 400f)))
        assertTrue(registry.contains(1, Offset(300f, 800f)))
        assertFalse(registry.contains(0, Offset(300f, 400f)))
        assertFalse(registry.contains(1, Offset(50f, 400f)))

        registry.remove(wall)
        assertFalse(registry.contains(1, Offset(300f, 400f)))
        assertTrue(registry.contains(1, Offset(300f, 800f)))
    }
}
