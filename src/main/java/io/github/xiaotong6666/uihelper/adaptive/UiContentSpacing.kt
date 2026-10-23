/*
 * Copyright (C) 2026 XiaoTong6666
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.xiaotong6666.uihelper.adaptive

import androidx.compose.ui.unit.dp

/**
 * Per-screen spacing presets for consumers that need to preserve their existing dimensions.
 * These values intentionally do not enforce a uniform spacing scale across screens.
 */
object UiContentSpacing {
    val MiuixHorizontal = 12.dp
    val MaterialHorizontal = 16.dp
    val PageTop = 12.dp
    val DashboardItemGapMiuix = 12.dp
    val DashboardItemGapMaterial = 16.dp
    val DashboardBottom = 16.dp
    val SettingsItemGap = 6.dp
    val SettingsBottom = 24.dp
    val DetailItemGap = 12.dp
    val DetailBottom = 20.dp
}
