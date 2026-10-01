@file:Suppress("ktlint:standard:function-naming")

package io.github.xiaotong6666.uihelper.adaptive

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize
import io.github.xiaotong6666.uihelper.mode.LocalUiMode
import io.github.xiaotong6666.uihelper.mode.UiMode
import top.yukonga.miuix.kmp.anim.folmeSpring

/** Saveable positional state. [identity] is business identity, never display text; changing it resets state. */
@Composable
fun rememberExpandableSectionState(identity: Any? = Unit, initiallyExpanded: Boolean = false): ExpandableSectionState {
    var expanded by rememberSaveable(identity) { mutableStateOf(initiallyExpanded) }
    return ExpandableSectionState(expanded) { expanded = it }
}

class ExpandableSectionState internal constructor(
    val expanded: Boolean,
    private val onExpandedChange: (Boolean) -> Unit,
) {
    fun toggle() = onExpandedChange(!expanded)
    fun setExpanded(value: Boolean) = onExpandedChange(value)
}

/** Native MIUIX/Material expansion motion; header and content styling stay with the caller. */
@Composable
fun ExpandableSectionBody(
    expanded: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val miuix = LocalUiMode.current == UiMode.Miuix
    AnimatedVisibility(
        visible = expanded,
        modifier = modifier,
        enter = if (miuix) {
            expandVertically(animationSpec = folmeSpring(damping = 1f, response = 0.32f, visibilityThreshold = IntSize.VisibilityThreshold)) +
                fadeIn(animationSpec = folmeSpring(damping = 1f, response = 0.28f))
        } else {
            expandVertically(animationSpec = spring(visibilityThreshold = IntSize.VisibilityThreshold)) + fadeIn()
        },
        exit = if (miuix) {
            shrinkVertically(animationSpec = folmeSpring(damping = 1f, response = 0.28f, visibilityThreshold = IntSize.VisibilityThreshold)) +
                fadeOut(animationSpec = folmeSpring(damping = 1f, response = 0.20f))
        } else {
            shrinkVertically(animationSpec = spring(visibilityThreshold = IntSize.VisibilityThreshold)) + fadeOut()
        },
    ) { Column(content = content) }
}
