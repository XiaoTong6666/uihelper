/*
 * Copyright (C) 2026 XiaoTong6666
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
@file:Suppress("ktlint:standard:function-naming")
package io.github.xiaotong6666.uihelper.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.xiaotong6666.uihelper.mode.LocalUiMode
import io.github.xiaotong6666.uihelper.mode.UiMode

/** Cross-skin update prompt. Fetching, downloading and version semantics remain caller-owned. */
@Composable
fun UpdatePromptDialog(
    show: Boolean,
    title: String,
    summary: String,
    metadata: List<UpdatePromptMetadata>,
    changesTitle: String,
    changes: List<UpdatePromptChange>,
    dismissLabel: String,
    confirmLabel: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    confirmEnabled: Boolean = true,
    moreChangesLabel: String? = null,
    viewAllLabel: String? = null,
    onViewAll: (() -> Unit)? = null,
    onDismissFinished: (() -> Unit)? = null,
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> UpdatePromptDialogMiuix(
            show = show,
            title = title,
            summary = summary,
            metadata = metadata,
            changesTitle = changesTitle,
            changes = changes,
            dismissLabel = dismissLabel,
            confirmLabel = confirmLabel,
            onDismiss = onDismiss,
            onConfirm = onConfirm,
            modifier = modifier,
            confirmEnabled = confirmEnabled,
            moreChangesLabel = moreChangesLabel,
            viewAllLabel = viewAllLabel,
            onViewAll = onViewAll,
            onDismissFinished = onDismissFinished,
        )

        UiMode.Material -> UpdatePromptDialogMaterial(
            show = show,
            title = title,
            summary = summary,
            metadata = metadata,
            changesTitle = changesTitle,
            changes = changes,
            dismissLabel = dismissLabel,
            confirmLabel = confirmLabel,
            onDismiss = onDismiss,
            onConfirm = onConfirm,
            modifier = modifier,
            confirmEnabled = confirmEnabled,
            moreChangesLabel = moreChangesLabel,
            viewAllLabel = viewAllLabel,
            onViewAll = onViewAll,
        )
    }
}
