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

package io.github.xiaotong6666.uihelper.material

import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

@Immutable
data class MaterialSurfaceLadder(
    val page: Color,
    val chrome: Color,
    val grouped: Color,
    val groupedSelected: Color,
    val groupedSelectedContent: Color,
    val input: Color,
    val accent: Color,
    val accentContent: Color,
)

@Composable
fun materialSurfaceLadder(): MaterialSurfaceLadder {
    val colorScheme = MaterialTheme.colorScheme
    return remember(colorScheme) {
        MaterialSurfaceLadder(
            page = colorScheme.surfaceContainer,
            chrome = colorScheme.surfaceContainerHighest,
            grouped = colorScheme.surfaceBright,
            groupedSelected = colorScheme.secondaryContainer,
            groupedSelectedContent = colorScheme.onSecondaryContainer,
            input = colorScheme.surfaceContainerHigh,
            accent = colorScheme.primaryContainer,
            accentContent = colorScheme.onPrimaryContainer,
        )
    }
}

@Composable
fun materialChromeIconButtonColors(): IconButtonColors {
    val surfaces = materialSurfaceLadder()
    return IconButtonDefaults.iconButtonColors(
        containerColor = surfaces.chrome,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
fun materialAccentIconButtonColors(): IconButtonColors {
    val surfaces = materialSurfaceLadder()
    return IconButtonDefaults.iconButtonColors(
        containerColor = surfaces.accent,
        contentColor = surfaces.accentContent,
    )
}
