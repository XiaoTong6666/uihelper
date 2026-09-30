/*
 * Copyright (C) 2026 XiaoTong6666
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
@file:Suppress("ktlint:standard:function-naming")
package io.github.xiaotong6666.uihelper.adaptive

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import io.github.xiaotong6666.uihelper.mode.LocalUiMode
import io.github.xiaotong6666.uihelper.mode.UiMode
import top.yukonga.miuix.kmp.theme.MiuixTheme

/** Preserve a project's Material typography while mapping the same semantic role to native MIUIX. */
@Composable
fun adaptiveTitleStyle(materialStyle: TextStyle): TextStyle = when (LocalUiMode.current) {
    UiMode.Material -> materialStyle
    UiMode.Miuix -> MiuixTheme.textStyles.title3
}

@Composable
fun adaptiveHeadlineStyle(materialStyle: TextStyle): TextStyle = when (LocalUiMode.current) {
    UiMode.Material -> materialStyle
    UiMode.Miuix -> MiuixTheme.textStyles.headline1
}

@Composable
fun adaptiveBodyStyle(materialStyle: TextStyle): TextStyle = when (LocalUiMode.current) {
    UiMode.Material -> materialStyle
    UiMode.Miuix -> MiuixTheme.textStyles.body2
}

@Composable
fun adaptiveFootnoteStyle(materialStyle: TextStyle): TextStyle = when (LocalUiMode.current) {
    UiMode.Material -> materialStyle
    UiMode.Miuix -> MiuixTheme.textStyles.footnote1
}
