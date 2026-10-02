/*
 * Copyright (C) 2026 XiaoTong6666
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package io.github.xiaotong6666.uihelper.navigation3

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import top.yukonga.miuix.kmp.nav.transition.NavSwipeDirection

/** Physical swipe direction that corresponds to logical Back in the current layout direction. */
@Composable
@ReadOnlyComposable
fun logicalBackSwipeDirection(): NavSwipeDirection = if (LocalLayoutDirection.current == LayoutDirection.Rtl) {
    NavSwipeDirection.RightToLeft
} else {
    NavSwipeDirection.LeftToRight
}
