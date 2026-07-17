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

package io.github.xiaotong6666.uihelper.adaptive

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.xiaotong6666.uihelper.mode.LocalUiMode
import io.github.xiaotong6666.uihelper.mode.UiMode
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun CompactActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onLongClick: (() -> Unit)? = null,
    containerColor: Color? = null,
    contentColor: Color? = null,
    minHeight: Dp = 35.dp,
    minWidth: Dp = 35.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
    content: @Composable RowScope.() -> Unit,
) {
    when (LocalUiMode.current) {
        UiMode.Material -> {
            val resolvedContainer = containerColor ?: MaterialTheme.colorScheme.secondaryContainer
            val resolvedContent = contentColor ?: contentColorFor(resolvedContainer)
            val interactionSource = remember { MutableInteractionSource() }
            Surface(
                modifier = modifier
                    .semantics { role = Role.Button }
                    .clip(RoundedCornerShape(999.dp))
                    .combinedClickable(
                        interactionSource = interactionSource,
                        indication = androidx.compose.foundation.LocalIndication.current,
                        enabled = enabled,
                        onClick = onClick,
                        onLongClick = onLongClick,
                    ),
                shape = RoundedCornerShape(999.dp),
                color = resolvedContainer,
                contentColor = resolvedContent,
            ) {
                Row(
                    modifier = Modifier
                        .defaultMinSize(minWidth = minWidth, minHeight = minHeight)
                        .padding(contentPadding),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    content = content,
                )
            }
        }

        UiMode.Miuix -> {
            val resolvedContainer = containerColor ?: MiuixTheme.colorScheme.secondaryContainer
            val resolvedContent = contentColor ?: MiuixTheme.colorScheme.onSurface
            val interactionSource = remember { MutableInteractionSource() }
            CompositionLocalProvider(LocalContentColor provides resolvedContent) {
                Row(
                    modifier = modifier
                        .defaultMinSize(minWidth = minWidth, minHeight = minHeight)
                        .clip(CircleShape)
                        .background(resolvedContainer)
                        .combinedClickable(
                            interactionSource = interactionSource,
                            indication = androidx.compose.foundation.LocalIndication.current,
                            enabled = enabled,
                            onClick = onClick,
                            onLongClick = onLongClick,
                        )
                        .padding(contentPadding),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    content = content,
                )
            }
        }
    }
}
