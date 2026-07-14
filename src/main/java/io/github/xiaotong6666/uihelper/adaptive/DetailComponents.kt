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

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.xiaotong6666.uihelper.material.primitive.DetailGroupMaterial
import io.github.xiaotong6666.uihelper.material.primitive.GroupedSurfaceMaterial
import io.github.xiaotong6666.uihelper.material.primitive.SettingsTextAreaItemMaterial
import io.github.xiaotong6666.uihelper.miuix.primitive.DetailGroupMiuix
import io.github.xiaotong6666.uihelper.miuix.primitive.GroupedSurfaceMiuix
import io.github.xiaotong6666.uihelper.miuix.primitive.SettingsTextAreaItemMiuix
import io.github.xiaotong6666.uihelper.mode.LocalUiMode
import io.github.xiaotong6666.uihelper.mode.UiMode

class DetailGroupScope {
    internal val items = mutableListOf<@Composable () -> Unit>()

    fun item(content: @Composable () -> Unit) {
        items += content
    }
}

@Composable
fun DetailGroup(
    title: String = "",
    description: String? = null,
    modifier: Modifier = Modifier,
    content: DetailGroupScope.() -> Unit,
) {
    val items = DetailGroupScope().apply(content).items
    when (LocalUiMode.current) {
        UiMode.Miuix -> DetailGroupMiuix(title, description, modifier, items)
        UiMode.Material -> DetailGroupMaterial(title, description, modifier, items)
    }
}

@Composable
fun GroupedSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> GroupedSurfaceMiuix(modifier, content)
        UiMode.Material -> GroupedSurfaceMaterial(modifier, content)
    }
}

@Composable
fun SettingsTextAreaItem(
    value: String,
    onValueChange: (String) -> Unit,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    minLines: Int = 5,
    maxLines: Int = 8,
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> SettingsTextAreaItemMiuix(value, onValueChange, title, description, modifier, minLines, maxLines)
        UiMode.Material -> SettingsTextAreaItemMaterial(value, onValueChange, title, description, modifier, minLines, maxLines)
    }
}
