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

@file:Suppress("ktlint:standard:function-naming")

package io.github.xiaotong6666.uihelper.mode

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.OverscrollFactory
import androidx.compose.foundation.rememberPlatformOverscrollFactory
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.theme.LocalContentColor
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.ThemeController
import top.yukonga.miuix.kmp.utils.MiuixIndication

private data class AdaptiveOverscrollBaseline(
    val inherited: OverscrollFactory?,
    val platform: OverscrollFactory?,
)

private val LocalAdaptiveOverscrollBaseline = staticCompositionLocalOf<AdaptiveOverscrollBaseline?> { null }

@Composable
fun AdaptiveTheme(
    uiMode: UiMode,
    darkTheme: Boolean,
    materialColorScheme: ColorScheme? = null,
    themeController: ThemeController? = null,
    content: @Composable () -> Unit,
) {
    val colorSchemeMode = remember(darkTheme) {
        if (darkTheme) ColorSchemeMode.Dark else ColorSchemeMode.Light
    }
    val defaultMiuixController = remember(colorSchemeMode) {
        ThemeController(colorSchemeMode = colorSchemeMode)
    }

    val overscrollBaseline = AdaptiveOverscrollBaseline(
        inherited = LocalOverscrollFactory.current,
        platform = rememberPlatformOverscrollFactory(),
    )

    CompositionLocalProvider(
        LocalUiMode provides uiMode,
        LocalAdaptiveOverscrollBaseline provides overscrollBaseline,
    ) {
        MaterialTheme(colorScheme = materialColorScheme ?: if (darkTheme) darkColorScheme() else lightColorScheme()) {
            MiuixTheme(controller = themeController ?: defaultMiuixController) {
                CompositionLocalProvider(
                    LocalContentColor provides MiuixTheme.colorScheme.onBackground,
                    content = content,
                )
            }
        }
    }
}

/**
 * Maps the active MIUIX palette into Material color roles while preserving caller-owned fallback
 * roles from [baseScheme]. This is a compatibility bridge for shared Material components rendered
 * inside a MIUIX skin; it does not define an app's Material branding.
 */
@Composable
fun materialCompatibilityColorSchemeFromMiuix(baseScheme: ColorScheme): ColorScheme {
    val miuix = MiuixTheme.colorScheme
    return baseScheme.copy(
        primary = miuix.primary,
        onPrimary = miuix.onPrimary,
        primaryContainer = miuix.primaryContainer,
        onPrimaryContainer = miuix.onPrimaryContainer,
        inversePrimary = miuix.primaryVariant,
        secondary = miuix.secondary,
        onSecondary = miuix.onSecondary,
        secondaryContainer = miuix.secondaryContainer,
        onSecondaryContainer = miuix.onSecondaryContainer,
        tertiary = miuix.onTertiaryContainer,
        onTertiary = miuix.tertiaryContainer,
        tertiaryContainer = miuix.tertiaryContainer,
        onTertiaryContainer = miuix.onTertiaryContainer,
        error = miuix.error,
        onError = miuix.onError,
        errorContainer = miuix.errorContainer,
        onErrorContainer = miuix.onErrorContainer,
        background = miuix.background,
        onBackground = miuix.onBackground,
        surface = miuix.surface,
        onSurface = miuix.onSurface,
        surfaceVariant = miuix.surfaceVariant,
        onSurfaceVariant = miuix.onSurfaceVariantSummary,
        surfaceTint = miuix.primary,
        inverseSurface = miuix.onSurface,
        inverseOnSurface = miuix.surface,
        outline = miuix.outline,
        outlineVariant = miuix.dividerLine,
        scrim = miuix.windowDimming,
        surfaceBright = miuix.surfaceContainer,
        surfaceDim = miuix.surface,
        surfaceContainerLowest = miuix.surface,
        surfaceContainerLow = miuix.surfaceContainer,
        surfaceContainer = miuix.surfaceContainer,
        surfaceContainerHigh = miuix.surfaceContainerHigh,
        surfaceContainerHighest = miuix.surfaceContainerHighest,
    )
}

/**
 * Installs the skin-specific interaction locals at the final app-theme layer.
 *
 * Call this inside a consumer theme that may itself provide Material locals. This keeps MIUIX
 * indication and Material platform overscroll from being replaced by an app-owned Material theme.
 */
@Composable
fun AdaptiveInteractionRuntime(content: @Composable () -> Unit) {
    val uiMode = LocalUiMode.current
    val materialIndication = LocalIndication.current
    val fallbackPlatformOverscrollFactory = rememberPlatformOverscrollFactory()
    val currentOverscrollFactory = LocalOverscrollFactory.current
    val baseline = LocalAdaptiveOverscrollBaseline.current
    val platformOverscrollFactory = if (baseline != null) baseline.platform else fallbackPlatformOverscrollFactory
    val inheritedOverscrollFactory = if (baseline != null) baseline.inherited else currentOverscrollFactory
    val indicationColor = MiuixTheme.colorScheme.onBackground
    val miuixIndication = remember(indicationColor) {
        MiuixIndication(color = indicationColor)
    }

    CompositionLocalProvider(
        LocalIndication provides if (uiMode == UiMode.Miuix) miuixIndication else materialIndication,
        LocalOverscrollFactory provides if (uiMode == UiMode.Material) {
            platformOverscrollFactory
        } else {
            inheritedOverscrollFactory
        },
        content = content,
    )
}
