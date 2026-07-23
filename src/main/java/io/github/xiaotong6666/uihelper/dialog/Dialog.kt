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

package io.github.xiaotong6666.uihelper.dialog

import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import io.github.xiaotong6666.uihelper.mode.LocalUiMode
import io.github.xiaotong6666.uihelper.mode.UiMode
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import kotlin.coroutines.resume

data class ConfirmDialogVisuals(
    val title: String,
    val content: String? = null,
    val isMarkdown: Boolean = false,
    val isHtml: Boolean = false,
    val confirm: String? = null,
    val dismiss: String? = null,
) {
    companion object {
        val Empty = ConfirmDialogVisuals(title = "")
    }
}

typealias NullableCallback = (() -> Unit)?
typealias ConfirmDialogMessageContent = @Composable (ConfirmDialogVisuals) -> Unit

interface DialogHandle {
    val isShown: Boolean
    val dialogType: String
    fun show()
    fun hide()
}

interface LoadingDialogHandle : DialogHandle {
    suspend fun <R> withLoading(message: String? = null, block: suspend () -> R): R
    fun showLoading(message: String? = null)
}

sealed interface ConfirmResult {
    data object Confirmed : ConfirmResult
    data object Canceled : ConfirmResult
}

interface ConfirmDialogHandle : DialogHandle {
    val visuals: ConfirmDialogVisuals

    fun showConfirm(
        title: String,
        content: String? = null,
        markdown: Boolean = false,
        html: Boolean = false,
        confirm: String? = null,
        dismiss: String? = null,
    )

    suspend fun awaitConfirm(
        title: String,
        content: String? = null,
        markdown: Boolean = false,
        html: Boolean = false,
        confirm: String? = null,
        dismiss: String? = null,
    ): ConfirmResult
}

interface ConfirmCallback {
    val onConfirm: NullableCallback
    val onDismiss: NullableCallback

    val isEmpty: Boolean
        get() = onConfirm == null && onDismiss == null

    companion object {
        operator fun invoke(
            onConfirmProvider: () -> NullableCallback,
            onDismissProvider: () -> NullableCallback,
        ): ConfirmCallback = object : ConfirmCallback {
            override val onConfirm: NullableCallback
                get() = onConfirmProvider()
            override val onDismiss: NullableCallback
                get() = onDismissProvider()
        }
    }
}

private abstract class DialogHandleBase(
    protected val visible: MutableState<Boolean>,
    protected val coroutineScope: CoroutineScope,
) : DialogHandle {
    override val isShown: Boolean
        get() = visible.value

    override fun show() {
        visible.value = true
    }

    final override fun hide() {
        visible.value = false
    }
}

private class LoadingDialogHandleImpl(
    visible: MutableState<Boolean>,
    private val message: MutableState<String?>,
    coroutineScope: CoroutineScope,
) : DialogHandleBase(visible, coroutineScope),
    LoadingDialogHandle {
    override suspend fun <R> withLoading(message: String?, block: suspend () -> R): R = coroutineScope.async {
        try {
            this@LoadingDialogHandleImpl.message.value = message
            visible.value = true
            block()
        } finally {
            visible.value = false
            this@LoadingDialogHandleImpl.message.value = null
        }
    }.await()

    override fun showLoading(message: String?) {
        this.message.value = message
        show()
    }

    override val dialogType: String
        get() = "LoadingDialog"
}

private object DefaultConfirmDialogMessageRenderer {
    private val markdownParser: Parser by lazy { Parser.builder().build() }
    private val htmlRenderer: HtmlRenderer by lazy { HtmlRenderer.builder().build() }

    fun renderMarkdown(markdown: String): String = htmlRenderer.render(markdownParser.parse(markdown))
}

@Composable
internal fun DefaultConfirmDialogMessage(visuals: ConfirmDialogVisuals) {
    val content = visuals.content ?: return
    when {
        visuals.isMarkdown -> HtmlDialogMessage(DefaultConfirmDialogMessageRenderer.renderMarkdown(content))
        visuals.isHtml -> HtmlDialogMessage(content)
        else -> PlainDialogMessage(content)
    }
}

@Composable
private fun PlainDialogMessage(content: String) {
    val contentColor = LocalContentColor.current
    val textStyle = LocalTextStyle.current
    BasicText(
        text = content,
        style = textStyle.merge(TextStyle(color = contentColor)),
    )
}

@Composable
private fun HtmlDialogMessage(html: String) {
    val context = LocalContext.current
    val contentColor = LocalContentColor.current
    val textStyle = LocalTextStyle.current
    val fontSizeSp = if (textStyle.fontSize.isSpecified) textStyle.fontSize.value else 16f
    val htmlText = remember(html) {
        HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT)
    }

    AndroidView(
        factory = {
            TextView(context).apply {
                movementMethod = LinkMovementMethod.getInstance()
                linksClickable = true
            }
        },
        update = { textView ->
            textView.text = htmlText
            textView.setTextColor(contentColor.toArgb())
            textView.textSize = fontSizeSp
            if (textStyle.fontFamily == FontFamily.Monospace) {
                textView.typeface = android.graphics.Typeface.MONOSPACE
            }
        },
    )
}

private class ConfirmDialogHandleImpl(
    visibleState: MutableState<Boolean>,
    coroutineScope: CoroutineScope,
    private val callback: ConfirmCallback,
    initialVisuals: ConfirmDialogVisuals = ConfirmDialogVisuals.Empty,
    private val resultFlow: ReceiveChannel<ConfirmResult>,
) : DialogHandleBase(visibleState, coroutineScope),
    ConfirmDialogHandle {
    private val visualsState = mutableStateOf(initialVisuals)
    override val visuals: ConfirmDialogVisuals
        get() = visualsState.value

    private var awaitContinuation: CancellableContinuation<ConfirmResult>? = null

    init {
        coroutineScope.launch {
            resultFlow.consumeAsFlow().collectLatest { result ->
                awaitContinuation?.let {
                    awaitContinuation = null
                    if (it.isActive) {
                        it.resume(result)
                    }
                }
                hide()
                when (result) {
                    ConfirmResult.Confirmed -> callback.onConfirm?.invoke()
                    ConfirmResult.Canceled -> callback.onDismiss?.invoke()
                }
            }
        }
    }

    private suspend fun awaitResult(): ConfirmResult = suspendCancellableCoroutine { continuation ->
        awaitContinuation = continuation
        if (callback.isEmpty) {
            continuation.invokeOnCancellation {
                visible.value = false
            }
        }
    }

    override fun showConfirm(
        title: String,
        content: String?,
        markdown: Boolean,
        html: Boolean,
        confirm: String?,
        dismiss: String?,
    ) {
        coroutineScope.launch {
            updateVisuals(
                ConfirmDialogVisuals(
                    title = title,
                    content = content,
                    isMarkdown = markdown,
                    isHtml = html,
                    confirm = confirm,
                    dismiss = dismiss,
                ),
            )
            show()
        }
    }

    override suspend fun awaitConfirm(
        title: String,
        content: String?,
        markdown: Boolean,
        html: Boolean,
        confirm: String?,
        dismiss: String?,
    ): ConfirmResult {
        coroutineScope.launch {
            updateVisuals(
                ConfirmDialogVisuals(
                    title = title,
                    content = content,
                    isMarkdown = markdown,
                    isHtml = html,
                    confirm = confirm,
                    dismiss = dismiss,
                ),
            )
            show()
        }
        return awaitResult()
    }

    override fun show() {
        check(visuals != ConfirmDialogVisuals.Empty) {
            "cannot show confirm dialog with empty visuals"
        }
        super.show()
    }

    override val dialogType: String
        get() = "ConfirmDialog"

    private fun updateVisuals(visuals: ConfirmDialogVisuals) {
        visualsState.value = visuals
    }

    companion object {
        fun saver(
            visible: MutableState<Boolean>,
            coroutineScope: CoroutineScope,
            callback: ConfirmCallback,
            resultChannel: ReceiveChannel<ConfirmResult>,
        ): Saver<ConfirmDialogHandle, Any> = listSaver(
            save = {
                listOf(
                    it.visuals.title,
                    it.visuals.content,
                    it.visuals.isMarkdown,
                    it.visuals.isHtml,
                    it.visuals.confirm,
                    it.visuals.dismiss,
                )
            },
            restore = { saved ->
                ConfirmDialogHandleImpl(
                    visibleState = visible,
                    coroutineScope = coroutineScope,
                    callback = callback,
                    initialVisuals = ConfirmDialogVisuals(
                        title = saved[0] as String,
                        content = saved[1] as String?,
                        isMarkdown = saved[2] as Boolean,
                        isHtml = saved[3] as Boolean,
                        confirm = saved[4] as String?,
                        dismiss = saved[5] as String?,
                    ),
                    resultFlow = resultChannel,
                )
            },
        )
    }
}

@Composable
fun rememberLoadingDialog(): LoadingDialogHandle {
    val visible = remember { mutableStateOf(false) }
    val message = remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    when (LocalUiMode.current) {
        UiMode.Miuix -> LoadingDialogMiuix(visible, message)
        UiMode.Material -> LoadingDialogMaterial(visible, message)
    }

    return remember {
        LoadingDialogHandleImpl(visible, message, coroutineScope)
    }
}

@Composable
fun rememberConfirmCallback(
    onConfirm: NullableCallback,
    onDismiss: NullableCallback,
): ConfirmCallback {
    val currentOnConfirm by rememberUpdatedState(onConfirm)
    val currentOnDismiss by rememberUpdatedState(onDismiss)
    return remember {
        ConfirmCallback({ currentOnConfirm }, { currentOnDismiss })
    }
}

@Composable
fun rememberConfirmDialog(
    onConfirm: NullableCallback = null,
    onDismiss: NullableCallback = null,
    messageContent: ConfirmDialogMessageContent? = null,
): ConfirmDialogHandle = rememberConfirmDialog(
    callback = rememberConfirmCallback(onConfirm, onDismiss),
    messageContent = messageContent,
)

@Composable
fun rememberConfirmDialog(
    callback: ConfirmCallback,
    messageContent: ConfirmDialogMessageContent? = null,
): ConfirmDialogHandle = rememberConfirmDialog(
    visuals = ConfirmDialogVisuals.Empty,
    callback = callback,
    messageContent = messageContent,
)

@Composable
private fun rememberConfirmDialog(
    visuals: ConfirmDialogVisuals,
    callback: ConfirmCallback,
    messageContent: ConfirmDialogMessageContent?,
): ConfirmDialogHandle {
    val visible = rememberSaveable { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val resultChannel = remember { Channel<ConfirmResult>() }

    val handle = rememberSaveable(
        saver = ConfirmDialogHandleImpl.saver(visible, coroutineScope, callback, resultChannel),
    ) {
        ConfirmDialogHandleImpl(
            visibleState = visible,
            coroutineScope = coroutineScope,
            callback = callback,
            initialVisuals = visuals,
            resultFlow = resultChannel,
        )
    }

    when (LocalUiMode.current) {
        UiMode.Miuix -> ConfirmDialogMiuix(
            visuals = handle.visuals,
            confirm = { coroutineScope.launch { resultChannel.send(ConfirmResult.Confirmed) } },
            dismiss = { coroutineScope.launch { resultChannel.send(ConfirmResult.Canceled) } },
            showDialog = visible,
            messageContent = messageContent,
        )

        UiMode.Material -> ConfirmDialogMaterial(
            visuals = handle.visuals,
            confirm = { coroutineScope.launch { resultChannel.send(ConfirmResult.Confirmed) } },
            dismiss = { coroutineScope.launch { resultChannel.send(ConfirmResult.Canceled) } },
            showDialog = visible,
            messageContent = messageContent,
        )
    }

    return handle
}
