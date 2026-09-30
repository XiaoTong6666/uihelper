/*
 * Copyright (C) 2026 XiaoTong6666
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

@file:Suppress("ktlint:standard:function-naming")

package io.github.xiaotong6666.uihelper.adaptive

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import io.github.xiaotong6666.uihelper.miuix.primitive.StatusHeroCardMiuix
import io.github.xiaotong6666.uihelper.mode.LocalUiMode
import io.github.xiaotong6666.uihelper.mode.UiMode
import top.yukonga.miuix.kmp.theme.MiuixTheme

enum class StatusHeroTone {
    Danger,
    Warning,
    Success,
    Neutral,
}

/**
 * Status-first hero shared by utility apps. The app owns status semantics and slot content;
 * uihelper owns the native Material/MIUIX composition and tone mapping.
 */
@Composable
fun AdaptiveStatusHeroCard(
    title: String,
    summary: String,
    icon: ImageVector,
    tone: StatusHeroTone,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    metaContent: (@Composable ColumnScope.(contentColor: Color) -> Unit)? = null,
    actionContent: (@Composable ColumnScope.(contentColor: Color) -> Unit)? = null,
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> {
            val containerColor = when (tone) {
                StatusHeroTone.Danger -> MiuixTheme.colorScheme.errorContainer
                StatusHeroTone.Warning,
                StatusHeroTone.Success,
                -> lerp(MiuixTheme.colorScheme.surfaceContainer, accentColor, 0.15f)
                StatusHeroTone.Neutral -> MiuixTheme.colorScheme.surfaceContainerHighest
            }
            val resolvedAccent = if (tone == StatusHeroTone.Danger) MiuixTheme.colorScheme.error else accentColor
            val contentColor = MiuixTheme.colorScheme.onSurface
            StatusHeroCardMiuix(
                title = title,
                summary = summary,
                icon = icon,
                containerColor = containerColor,
                accentColor = resolvedAccent,
                modifier = modifier,
                onClick = onClick,
                metaContent = metaContent?.let { content -> { content(contentColor) } },
                actionContent = actionContent?.let { content -> { content(contentColor) } },
            )
        }
        UiMode.Material -> {
            val containerColor = when (tone) {
                StatusHeroTone.Danger -> MaterialTheme.colorScheme.errorContainer
                StatusHeroTone.Warning -> MaterialTheme.colorScheme.tertiaryContainer
                StatusHeroTone.Success -> MaterialTheme.colorScheme.secondaryContainer
                StatusHeroTone.Neutral -> MaterialTheme.colorScheme.surfaceContainerHigh
            }
            val contentColor = when (tone) {
                StatusHeroTone.Danger -> MaterialTheme.colorScheme.onErrorContainer
                StatusHeroTone.Warning -> MaterialTheme.colorScheme.onTertiaryContainer
                StatusHeroTone.Success -> MaterialTheme.colorScheme.onSecondaryContainer
                StatusHeroTone.Neutral -> MaterialTheme.colorScheme.onSurface
            }
            Surface(
                onClick = onClick ?: {},
                enabled = onClick != null,
                modifier = modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                color = containerColor,
                contentColor = contentColor,
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .background(contentColor.copy(alpha = 0.12f), CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(32.dp))
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleLargeEmphasized,
                                color = contentColor,
                            )
                            Text(
                                text = summary,
                                style = MaterialTheme.typography.bodyMedium,
                                color = contentColor.copy(alpha = 0.80f),
                            )
                        }
                    }
                    metaContent?.let { content ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 12.dp),
                        ) { content(contentColor) }
                    }
                    actionContent?.let { content ->
                        Column(modifier = Modifier.fillMaxWidth()) { content(contentColor) }
                    }
                }
            }
        }
    }
}
