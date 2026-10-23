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

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector

/** UI-only, skin-independent data shared by MIUIX and Material Expressive detail layouts. */
@Immutable
data class DetailMetric(val value: String, val label: String)

@Immutable
data class DetailTextRow(
    val title: String,
    val summary: String? = null,
    /** Optional stable identity; duplicate titles are otherwise disambiguated by position. */
    val key: String? = null,
)

@Immutable
data class DetailField(val label: String, val value: String)

data class DetailSection(
    val title: String,
    val icon: ImageVector,
    val fields: List<DetailField>,
)
