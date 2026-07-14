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

package io.github.xiaotong6666.uihelper.miuix.primitive

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.preference.OverlayDropdownPreference
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun SettingsGroupMiuix(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        insideMargin = PaddingValues(0.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth(), content = content)
    }
}

@Composable
fun SettingsGroupHeaderMiuix(text: String) {
    Box(modifier = Modifier.padding(bottom = 4.dp)) { SmallTitle(text) }
}

@Composable
fun SettingsToggleItemMiuix(checked: Boolean, title: String, description: String, icon: ImageVector? = null, onToggle: () -> Unit) {
    SwitchPreference(
        title = title,
        summary = description,
        startAction = {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 6.dp),
                    tint = MiuixTheme.colorScheme.onBackground,
                )
            }
        },
        checked = checked,
        onCheckedChange = { onToggle() },
    )
}

@Composable
fun SettingsInfoItemMiuix(title: String, value: String, icon: ImageVector? = null) {
    SettingsItemSurfaceMiuix {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.weight(1f)) {
                Text(text = title, style = MiuixTheme.textStyles.footnote2, color = MiuixTheme.colorScheme.onSurfaceVariantSummary)
                Text(text = value, style = MiuixTheme.textStyles.body1, color = MiuixTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
fun SettingsNavigationItemMiuix(
    title: String,
    description: String,
    icon: ImageVector? = null,
    onClick: () -> Unit,
) {
    ArrowPreference(
        title = title,
        summary = description,
        startAction = {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 6.dp),
                    tint = MiuixTheme.colorScheme.onBackground,
                )
            }
        },
        onClick = onClick,
    )
}

@Composable
fun SettingsDropdownItemMiuix(
    title: String,
    description: String,
    items: List<String>,
    selectedIndex: Int,
    icon: ImageVector? = null,
    onItemSelected: (Int) -> Unit,
) {
    OverlayDropdownPreference(
        title = title,
        summary = description,
        items = items,
        startAction = {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 6.dp),
                    tint = MiuixTheme.colorScheme.onBackground,
                )
            }
        },
        selectedIndex = selectedIndex,
        onSelectedIndexChange = onItemSelected,
    )
}

@Composable
fun SettingsGroupDividerMiuix() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(1.dp)
            .background(MiuixTheme.colorScheme.outline.copy(alpha = 0.28f)),
    )
}

@Composable
private fun SettingsItemSurfaceMiuix(onClick: (() -> Unit)? = null, content: @Composable () -> Unit) {
    if (onClick != null) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = onClick,
            colors = CardDefaults.defaultColors(color = Color.Transparent, contentColor = MiuixTheme.colorScheme.onSurface),
            insideMargin = PaddingValues(0.dp),
        ) { Column(modifier = Modifier.fillMaxWidth()) { content() } }
    } else {
        Box(modifier = Modifier.fillMaxWidth()) { content() }
    }
}
