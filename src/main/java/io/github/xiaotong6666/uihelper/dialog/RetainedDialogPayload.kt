/*
 * Copyright (C) 2026 XiaoTong6666
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package io.github.xiaotong6666.uihelper.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import io.github.xiaotong6666.uihelper.mode.LocalUiMode
import io.github.xiaotong6666.uihelper.mode.UiMode

@Stable
class RetainedDialogPayload<T : Any> internal constructor(
    val value: T?,
    val onDismissFinished: () -> Unit,
)

/**
 * Keeps the last non-null dialog payload alive while a skin with an exit animation is dismissing.
 *
 * The caller still owns visibility and business state. Material drops the payload immediately by
 * default because its platform dialog has no retained hide transition here; MIUIX keeps it until
 * [RetainedDialogPayload.onDismissFinished].
 */
@Composable
fun <T : Any> rememberRetainedDialogPayload(
    value: T?,
    retainDuringExit: Boolean = LocalUiMode.current == UiMode.Miuix,
): RetainedDialogPayload<T> {
    var retained by remember { mutableStateOf<T?>(value) }
    val currentValue by rememberUpdatedState(value)
    val currentRetainDuringExit by rememberUpdatedState(retainDuringExit)

    SideEffect {
        when {
            value != null -> retained = value
            !retainDuringExit -> retained = null
        }
    }

    val displayed = value ?: retained.takeIf { retainDuringExit }
    val onDismissFinished = remember {
        {
            if (currentValue == null || !currentRetainDuringExit) {
                retained = null
            }
        }
    }
    return RetainedDialogPayload(
        value = displayed,
        onDismissFinished = onDismissFinished,
    )
}
