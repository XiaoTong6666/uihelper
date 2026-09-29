package io.github.xiaotong6666.uihelper.chrome

import kotlin.math.roundToInt

/** The page actually occupying the viewport, independent of any requested destination. */
internal fun visiblePagerPage(
    currentPage: Int,
    currentPageOffsetFraction: Float,
    settledPage: Int,
    isScrollInProgress: Boolean,
    pageCount: Int,
): Int {
    if (pageCount <= 0) return 0
    return (
        if (isScrollInProgress) (currentPage + currentPageOffsetFraction).roundToInt()
        else settledPage
    ).coerceIn(0, pageCount - 1)
}
