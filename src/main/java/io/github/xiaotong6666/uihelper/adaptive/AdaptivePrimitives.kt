/*
 * Copyright (C) 2026 XiaoTong6666
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

@file:Suppress("ktlint:standard:function-naming")

package io.github.xiaotong6666.uihelper.adaptive

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.xiaotong6666.uihelper.mode.LocalUiMode
import io.github.xiaotong6666.uihelper.mode.UiMode
import io.github.xiaotong6666.uihelper.miuix.primitive.resolveMiuixIcon
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.ExpandLess
import top.yukonga.miuix.kmp.icon.extended.ExpandMore
import top.yukonga.miuix.kmp.squircle.squircleBackground
import top.yukonga.miuix.kmp.squircle.squircleClip
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.basic.Button as MiuixButton
import top.yukonga.miuix.kmp.basic.ButtonDefaults as MiuixButtonDefaults
import top.yukonga.miuix.kmp.basic.Card as MiuixCard
import top.yukonga.miuix.kmp.basic.CardDefaults as MiuixCardDefaults
import top.yukonga.miuix.kmp.basic.CircularProgressIndicator as MiuixCircularProgressIndicator
import top.yukonga.miuix.kmp.basic.HorizontalDivider as MiuixHorizontalDivider
import top.yukonga.miuix.kmp.basic.Icon as MiuixIcon
import top.yukonga.miuix.kmp.basic.IconButton as MiuixIconButton
import top.yukonga.miuix.kmp.basic.InfiniteProgressIndicator as MiuixInfiniteProgressIndicator
import top.yukonga.miuix.kmp.basic.Text as MiuixText
import top.yukonga.miuix.kmp.basic.TextButton as MiuixTextButton
import top.yukonga.miuix.kmp.basic.VerticalDivider as MiuixVerticalDivider

/**
 * Small escape hatch for genuinely app-specific structural differences.
 *
 * Prefer a semantic adaptive component whenever both skins represent the same UI concept.
 */
@Composable
fun AdaptiveContent(
    material: @Composable () -> Unit,
    miuix: @Composable () -> Unit,
) {
    when (LocalUiMode.current) {
        UiMode.Material -> material()
        UiMode.Miuix -> miuix()
    }
}

/** Native text widget without inserting extra wrap opportunities. */
@Composable
fun AdaptiveText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle,
    color: Color,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
) {
    when (LocalUiMode.current) {
        UiMode.Material -> Text(
            text = text,
            modifier = modifier,
            style = style,
            color = color,
            maxLines = maxLines,
            overflow = overflow,
            softWrap = softWrap,
        )

        UiMode.Miuix -> MiuixText(
            text = text,
            modifier = modifier,
            style = style,
            color = color,
            maxLines = maxLines,
            overflow = overflow,
            softWrap = softWrap,
        )
    }
}

@Composable
@ReadOnlyComposable
fun <T> adaptiveValue(material: T, miuix: T): T = when (LocalUiMode.current) {
    UiMode.Material -> material
    UiMode.Miuix -> miuix
}

@Composable
@ReadOnlyComposable
fun adaptivePrimaryColor(): Color = when (LocalUiMode.current) {
    UiMode.Material -> MaterialTheme.colorScheme.primary
    UiMode.Miuix -> MiuixTheme.colorScheme.primary
}

@Composable
@ReadOnlyComposable
fun adaptiveErrorColor(): Color = when (LocalUiMode.current) {
    UiMode.Material -> MaterialTheme.colorScheme.error
    UiMode.Miuix -> MiuixTheme.colorScheme.error
}

@Composable
@ReadOnlyComposable
fun adaptiveOnSurfaceColor(): Color = when (LocalUiMode.current) {
    UiMode.Material -> MaterialTheme.colorScheme.onSurface
    UiMode.Miuix -> MiuixTheme.colorScheme.onSurface
}

@Composable
@ReadOnlyComposable
fun adaptiveSecondaryTextColor(): Color = when (LocalUiMode.current) {
    UiMode.Material -> MaterialTheme.colorScheme.onSurfaceVariant
    UiMode.Miuix -> MiuixTheme.colorScheme.onSurfaceVariantSummary
}

@Composable
@ReadOnlyComposable
fun adaptiveActionContentColor(): Color = when (LocalUiMode.current) {
    UiMode.Material -> MaterialTheme.colorScheme.onSurfaceVariant
    UiMode.Miuix -> MiuixTheme.colorScheme.onSurfaceVariantActions
}

@Composable
@ReadOnlyComposable
fun adaptiveContainerContentColor(): Color = when (LocalUiMode.current) {
    UiMode.Material -> MaterialTheme.colorScheme.onSurface
    UiMode.Miuix -> MiuixTheme.colorScheme.onSurfaceContainer
}

@Composable
fun AdaptiveIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
) {
    when (LocalUiMode.current) {
        UiMode.Material -> Icon(imageVector, contentDescription, modifier, tint)
        UiMode.Miuix -> MiuixIcon(resolveMiuixIcon(imageVector), contentDescription, modifier, tint)
    }
}

@Composable
fun AdaptiveIcon(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
) {
    // MIUIX and Material both ultimately consume a Compose Painter. Keeping the dispatch here
    // prevents feature code from depending on the concrete icon widget family.
    when (LocalUiMode.current) {
        UiMode.Material -> Icon(painter, contentDescription, modifier, tint)
        UiMode.Miuix -> MiuixIcon(painter, contentDescription, modifier, tint)
    }
}

@Composable
fun AdaptiveFilledIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    materialContainerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    materialContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    miuixContainerColor: Color = MiuixTheme.colorScheme.surfaceContainerHigh,
    miuixContentColor: Color = MiuixTheme.colorScheme.onSurface,
    content: @Composable () -> Unit,
) {
    when (LocalUiMode.current) {
        UiMode.Material -> FilledIconButton(
            onClick = onClick,
            modifier = modifier,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = materialContainerColor,
                contentColor = materialContentColor,
            ),
            content = content,
        )

        UiMode.Miuix -> MiuixIconButton(
            onClick = onClick,
            modifier = modifier,
            backgroundColor = miuixContainerColor,
        ) {
            CompositionLocalProvider(LocalContentColor provides miuixContentColor) { content() }
        }
    }
}

@Composable
fun AdaptiveButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    prominent: Boolean = false,
    materialColors: ButtonColors = ButtonDefaults.buttonColors(),
    materialContentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit,
) {
    when (LocalUiMode.current) {
        UiMode.Material -> Button(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            colors = materialColors,
            contentPadding = materialContentPadding,
            content = content,
        )

        UiMode.Miuix -> MiuixButton(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            colors = if (prominent) {
                MiuixButtonDefaults.buttonColorsPrimary()
            } else {
                MiuixButtonDefaults.buttonColors()
            },
            content = content,
        )
    }
}

@Composable
fun AdaptiveTonalButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    when (LocalUiMode.current) {
        UiMode.Material -> FilledTonalButton(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            content = content,
        )

        UiMode.Miuix -> MiuixButton(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            colors = MiuixButtonDefaults.buttonColorsPrimary(),
            content = content,
        )
    }
}

@Composable
fun AdaptiveTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    when (LocalUiMode.current) {
        UiMode.Material -> TextButton(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
        ) {
            WrapSafeText(text = text)
        }

        UiMode.Miuix -> MiuixTextButton(
            text = text,
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
        )
    }
}

/**
 * Native card container with caller-owned visual tokens.
 * Project palettes stay in the project; widget-family selection stays here.
 */
@Composable
fun AdaptiveCardSurface(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    materialShape: Shape = MaterialTheme.shapes.large,
    materialContainerColor: Color = Color.Unspecified,
    miuixCornerRadius: Dp? = null,
    miuixContainerColor: Color = Color.Unspecified,
    content: @Composable ColumnScope.() -> Unit,
) {
    when (LocalUiMode.current) {
        UiMode.Material -> {
            val colors = if (materialContainerColor == Color.Unspecified) {
                CardDefaults.cardColors()
            } else {
                CardDefaults.cardColors(containerColor = materialContainerColor)
            }
            Card(modifier = modifier, shape = materialShape, colors = colors) {
                Column(modifier = Modifier.padding(contentPadding), content = content)
            }
        }

        UiMode.Miuix -> {
            val colors = if (miuixContainerColor == Color.Unspecified) {
                MiuixCardDefaults.defaultColors()
            } else {
                MiuixCardDefaults.defaultColors(color = miuixContainerColor)
            }
            if (miuixCornerRadius == null) {
                MiuixCard(
                    modifier = modifier,
                    insideMargin = PaddingValues(0.dp),
                    colors = colors,
                ) {
                    Column(modifier = Modifier.padding(contentPadding), content = content)
                }
            } else {
                MiuixCard(
                    modifier = modifier,
                    cornerRadius = miuixCornerRadius,
                    insideMargin = PaddingValues(0.dp),
                    colors = colors,
                ) {
                    Column(modifier = Modifier.padding(contentPadding), content = content)
                }
            }
        }
    }
}

@Composable
fun Modifier.adaptiveSurfaceBackground(
    materialColor: Color,
    materialShape: Shape,
    miuixColor: Color,
    miuixCornerRadius: Dp,
): Modifier = when (LocalUiMode.current) {
    UiMode.Material -> background(materialColor, materialShape)
    UiMode.Miuix -> squircleBackground(miuixColor, miuixCornerRadius)
}

@Composable
fun Modifier.adaptiveSurfaceClip(
    materialShape: Shape,
    miuixCornerRadius: Dp,
): Modifier = when (LocalUiMode.current) {
    UiMode.Material -> clip(materialShape)
    UiMode.Miuix -> squircleClip(miuixCornerRadius)
}

@Composable
fun AdaptiveHorizontalDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = Dp.Hairline,
    materialThickness: Dp = thickness,
    miuixThickness: Dp = thickness,
    materialColor: Color = Color.Unspecified,
    miuixColor: Color = Color.Unspecified,
) {
    when (LocalUiMode.current) {
        UiMode.Material -> if (materialColor == Color.Unspecified) {
            HorizontalDivider(modifier = modifier, thickness = materialThickness)
        } else {
            HorizontalDivider(modifier = modifier, thickness = materialThickness, color = materialColor)
        }

        UiMode.Miuix -> if (miuixColor == Color.Unspecified) {
            MiuixHorizontalDivider(modifier = modifier, thickness = miuixThickness)
        } else {
            MiuixHorizontalDivider(modifier = modifier, thickness = miuixThickness, color = miuixColor)
        }
    }
}

@Composable
fun AdaptiveVerticalDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = Dp.Hairline,
    materialColor: Color = Color.Unspecified,
    miuixColor: Color = Color.Unspecified,
) {
    when (LocalUiMode.current) {
        UiMode.Material -> if (materialColor == Color.Unspecified) {
            VerticalDivider(modifier = modifier, thickness = thickness)
        } else {
            VerticalDivider(modifier = modifier, thickness = thickness, color = materialColor)
        }

        UiMode.Miuix -> if (miuixColor == Color.Unspecified) {
            MiuixVerticalDivider(modifier = modifier, thickness = thickness)
        } else {
            MiuixVerticalDivider(modifier = modifier, thickness = thickness, color = miuixColor)
        }
    }
}

@Composable
fun AdaptiveCircularProgressIndicator(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    materialSize: Dp = size,
    miuixSize: Dp = size,
    materialStrokeWidth: Dp = 2.5.dp,
) {
    when (LocalUiMode.current) {
        UiMode.Material -> CircularProgressIndicator(
            modifier = modifier.size(materialSize),
            strokeWidth = materialStrokeWidth,
        )

        UiMode.Miuix -> MiuixCircularProgressIndicator(modifier = modifier, size = miuixSize)
    }
}

/** Material circular spinner paired with MIUIX's orbiting-dot progress indicator. */
@Composable
fun AdaptiveInfiniteProgressIndicator(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    materialStrokeWidth: Dp = 2.5.dp,
) {
    when (LocalUiMode.current) {
        UiMode.Material -> CircularProgressIndicator(
            modifier = modifier.size(size),
            strokeWidth = materialStrokeWidth,
        )

        UiMode.Miuix -> MiuixInfiniteProgressIndicator(modifier = modifier, size = size)
    }
}

/** Native expand affordance with skin-appropriate motion. */
@Composable
fun AdaptiveExpandIcon(
    expanded: Boolean,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    materialTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    materialSize: Dp = 24.dp,
    materialAnimationSpec: FiniteAnimationSpec<Float> = tween(durationMillis = 180),
    miuixTint: Color = MiuixTheme.colorScheme.onSurfaceVariantActions,
    miuixSize: Dp = 20.dp,
) {
    when (LocalUiMode.current) {
        UiMode.Material -> {
            val rotation = animateFloatAsState(
                targetValue = if (expanded) 180f else 0f,
                animationSpec = materialAnimationSpec,
                label = "AdaptiveExpandIcon",
            )
            Icon(
                imageVector = Icons.Rounded.ExpandMore,
                contentDescription = contentDescription,
                modifier = modifier.size(materialSize).rotate(rotation.value),
                tint = materialTint,
            )
        }

        UiMode.Miuix -> {
            val progress = animateFloatAsState(
                targetValue = if (expanded) 1f else 0f,
                animationSpec = tween(durationMillis = 240),
                label = "MiuixExpandIcon",
            )
            val glyphBlend = ((progress.value - 0.38f) / 0.24f).coerceIn(0f, 1f)
            val rotation = progress.value * 180f

            Box(modifier = modifier.size(miuixSize)) {
                MiuixIcon(
                    imageVector = MiuixIcons.ExpandMore,
                    contentDescription = if (expanded) null else contentDescription,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            rotationZ = rotation
                            alpha = 1f - glyphBlend
                        },
                    tint = miuixTint,
                )
                MiuixIcon(
                    imageVector = MiuixIcons.ExpandLess,
                    contentDescription = if (expanded) contentDescription else null,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            rotationZ = rotation
                            alpha = glyphBlend
                        },
                    tint = miuixTint,
                )
            }
        }
    }
}
