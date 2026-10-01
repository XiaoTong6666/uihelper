@file:Suppress("ktlint:standard:function-naming")

package io.github.xiaotong6666.uihelper.adaptive

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import io.github.xiaotong6666.uihelper.mode.LocalUiMode
import io.github.xiaotong6666.uihelper.mode.UiMode
import java.text.BreakIterator
import java.util.Locale
import top.yukonga.miuix.kmp.basic.Text as MiuixText

/** Text that offers visual breaks in long identifiers. Copy canonical data from the source string,
 * not the displayed selection (which may contain U+200B); accessibility exposes the source. */
@Composable
fun WrapSafeText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    val locale = currentLocale()
    val safeText = remember(text, locale) { text.withWrapOpportunities(locale) }
    val accessibleModifier = modifier.semantics { this.text = AnnotatedString(text) }
    val resolvedStyle = if (textAlign == null) style else style.copy(textAlign = textAlign)
    if (LocalUiMode.current == UiMode.Miuix) {
        MiuixText(
            text = safeText,
            modifier = accessibleModifier,
            style = resolvedStyle,
            color = if (color == Color.Unspecified) LocalContentColor.current else color,
            maxLines = maxLines,
            overflow = overflow,
        )
    } else {
        Text(
            text = safeText,
            modifier = accessibleModifier,
            style = resolvedStyle,
            color = color,
            maxLines = maxLines,
            overflow = overflow,
        )
    }
}

/** Break only between grapheme clusters: never insert U+200B within surrogate/emoji sequences. */
internal fun String.withWrapOpportunities(locale: Locale = Locale.getDefault()): String {
    if (length <= 12) return this
    val builder = StringBuilder(length + length / 12)
    var uninterruptedCount = 0
    val boundaries = BreakIterator.getCharacterInstance(locale).apply { setText(this@withWrapOpportunities) }
    var from = boundaries.first()
    while (true) {
        val to = boundaries.next()
        if (to == BreakIterator.DONE) break
        val cluster = substring(from, to)
        builder.append(cluster)
        if (cluster.any(Char::isWhitespace) || (cluster.length == 1 && cluster[0] in "-_/\\.:,;|+@#")) {
            if (!cluster.any(Char::isWhitespace)) builder.append('\u200B')
            uninterruptedCount = 0
        } else if (++uninterruptedCount >= 12) {
            builder.append('\u200B')
            uninterruptedCount = 0
        }
        from = to
    }
    return builder.toString()
}
