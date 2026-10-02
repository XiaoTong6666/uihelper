package io.github.xiaotong6666.uihelper.miuix.primitive

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.SettingsSuggest
import androidx.compose.material.icons.rounded.Wifi
import org.junit.Assert.assertSame
import org.junit.Test
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back

class MiuixIconResolverTest {
    @Test fun mapsNavigationChromeToNativeMiuixGlyph() {
        assertSame(
            MiuixIcons.Back,
            resolveMiuixIcon(Icons.AutoMirrored.Rounded.ArrowBack),
        )
    }

    @Test fun preservesBusinessSemanticIcons() {
        assertSame(Icons.Rounded.AdminPanelSettings, resolveMiuixIcon(Icons.Rounded.AdminPanelSettings))
        assertSame(Icons.Rounded.SettingsSuggest, resolveMiuixIcon(Icons.Rounded.SettingsSuggest))
        assertSame(Icons.Rounded.Security, resolveMiuixIcon(Icons.Rounded.Security))
        assertSame(Icons.Rounded.Memory, resolveMiuixIcon(Icons.Rounded.Memory))
    }

    @Test fun preservesUnknownIconsInsteadOfFallingBackToInfo() {
        assertSame(Icons.Rounded.Wifi, resolveMiuixIcon(Icons.Rounded.Wifi))
    }
}
