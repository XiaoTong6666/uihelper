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

package io.github.xiaotong6666.uihelper.mode

import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test
import top.yukonga.miuix.kmp.utils.MiuixOverscrollFactory

class AdaptiveThemeTest {
    @Test
    fun materialKeepsPlatformOverscrollFactory() {
        assertNull(
            adaptiveOverscrollFactory(
                uiMode = UiMode.Material,
                platformOverscrollFactory = null,
            ),
        )
    }

    @Test
    fun miuixAlwaysUsesMiuixOverscrollFactory() {
        assertSame(
            MiuixOverscrollFactory,
            adaptiveOverscrollFactory(
                uiMode = UiMode.Miuix,
                platformOverscrollFactory = null,
            ),
        )
    }
}
