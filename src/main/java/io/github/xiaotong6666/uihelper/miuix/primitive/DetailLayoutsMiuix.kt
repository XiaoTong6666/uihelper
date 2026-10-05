/*
 * Copyright (C) 2026 XiaoTong6666
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

@file:Suppress("ktlint:standard:function-naming")

package io.github.xiaotong6666.uihelper.miuix.primitive

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.VerticalDivider
import top.yukonga.miuix.kmp.theme.MiuixTheme

data class MiuixMetricItem(
    val value: String,
    val label: String,
)

data class MiuixTextRow(
    val title: String,
    val summary: String? = null,
)

data class MiuixDetailField(
    val label: String,
    val value: String,
)

data class MiuixDetailSection(
    val title: String,
    val icon: ImageVector,
    val fields: List<MiuixDetailField>,
)

/**
 * MIUIX section label with the spacing used by compact inspector and inventory screens.
 *
 * The caller owns the section semantics and text. uihelper only standardizes the native title
 * component and its insets.
 */
@Composable
fun MiuixSectionTitle(
    text: String,
    modifier: Modifier = Modifier,
    topPadding: Dp = 4.dp,
    bottomPadding: Dp = 6.dp,
) {
    SmallTitle(
        text = text,
        modifier = modifier,
        insideMargin = PaddingValues(
            start = 16.dp,
            top = topPadding,
            end = 16.dp,
            bottom = bottomPadding,
        ),
    )
}

/**
 * Compact equal-width metric summary in one native MIUIX card.
 *
 * This is intentionally data-only: labels and values remain app-owned while the reusable layout
 * owns spacing, typography, and separators.
 */
@Composable
fun MiuixMetricSummaryCard(
    metrics: List<MiuixMetricItem>,
    modifier: Modifier = Modifier,
) {
    if (metrics.isEmpty()) return

    Card(
        modifier = modifier.fillMaxWidth(),
        insideMargin = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            metrics.forEachIndexed { index, metric ->
                if (index > 0) {
                    VerticalDivider(
                        modifier = Modifier
                            .height(38.dp)
                            .padding(horizontal = 16.dp),
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(1.dp),
                ) {
                    Text(
                        text = metric.value,
                        style = MiuixTheme.textStyles.headline1,
                        color = MiuixTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = metric.label,
                        style = MiuixTheme.textStyles.footnote1,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    )
                }
            }
        }
    }
}

/** A native MIUIX card containing simple title/summary rows. */
@Composable
fun MiuixTextRowsCard(
    rows: List<MiuixTextRow>,
    modifier: Modifier = Modifier,
    rowPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
) {
    if (rows.isEmpty()) return

    Card(
        modifier = modifier.fillMaxWidth(),
        insideMargin = PaddingValues(0.dp),
    ) {
        rows.forEach { row ->
            BasicComponent(
                insideMargin = rowPadding,
                title = row.title,
                summary = row.summary,
            )
        }
    }
}

/**
 * Read-only inspector card split into named sections.
 *
 * Each field reserves a stable label column and gives the remaining width to the value, which
 * keeps long identifiers readable without turning every field into a vertically stacked block.
 */
@Composable
fun MiuixDetailSectionsCard(
    sections: List<MiuixDetailSection>,
    modifier: Modifier = Modifier,
    labelWidth: Dp = 96.dp,
) {
    if (sections.isEmpty()) return

    Card(
        modifier = modifier.fillMaxWidth(),
        insideMargin = PaddingValues(0.dp),
    ) {
        sections.forEachIndexed { sectionIndex, section ->
            if (sectionIndex > 0) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp),
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 13.dp, end = 16.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = resolveMiuixIcon(section.icon),
                    contentDescription = null,
                    tint = MiuixTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = section.title,
                    style = MiuixTheme.textStyles.headline2.copy(fontWeight = FontWeight.Medium),
                    color = MiuixTheme.colorScheme.onSurface,
                )
            }
            section.fields.forEach { field ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = field.label,
                        modifier = Modifier.width(labelWidth),
                        style = MiuixTheme.textStyles.body2,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    )
                    Text(
                        text = field.value,
                        modifier = Modifier.weight(1f),
                        style = MiuixTheme.textStyles.body2,
                        color = MiuixTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

/**
 * Native MIUIX card with a standard title/summary header and optional caller-owned body.
 */
@Composable
fun MiuixHeaderContentCard(
    title: String,
    summary: String?,
    modifier: Modifier = Modifier,
    content: (@Composable ColumnScope.() -> Unit)? = null,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        insideMargin = PaddingValues(0.dp),
    ) {
        BasicComponent(
            insideMargin = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            title = title,
            summary = summary,
        )
        if (content != null) {
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            content()
        }
    }
}

/** Selectable inset text panel for reports, identifiers, and other read-only payloads. */
@Composable
fun MiuixInsetTextPanel(
    text: String,
    modifier: Modifier = Modifier,
    monospace: Boolean = false,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = 12.dp,
        insideMargin = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
        colors = CardDefaults.defaultColors(
            color = MiuixTheme.colorScheme.surfaceContainerHighest,
            contentColor = MiuixTheme.colorScheme.onSurface,
        ),
    ) {
        SelectionContainer {
            Text(
                text = text,
                modifier = Modifier.fillMaxWidth(),
                style = MiuixTheme.textStyles.body2.copy(
                    fontFamily = if (monospace) FontFamily.Monospace else FontFamily.Default,
                ),
                color = MiuixTheme.colorScheme.onSurface,
            )
        }
    }
}
