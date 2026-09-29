package io.github.xiaotong6666.uihelper.adaptive

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class LabeledValueMode { Auto, Stacked }

/** Inline label/value when they fit; otherwise stack without constraining long values. */
@Composable
fun LabeledValueLayout(
    label: @Composable () -> Unit,
    value: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    horizontalGap: Dp = 16.dp,
    verticalGap: Dp = 2.dp,
    mode: LabeledValueMode = LabeledValueMode.Auto,
) {
    Layout(contents = listOf(label, value), modifier = modifier.fillMaxWidth()) { (labels, values), constraints ->
        val labelMeasurable = labels.single()
        val valueMeasurable = values.single()
        val gap = horizontalGap.roundToPx()
        // Intrinsic queries are only valid for children supporting intrinsic measurement.
        // Stacked skips them entirely (e.g. nested custom Layout or lazy content).
        val labelWidth = if (mode == LabeledValueMode.Auto) labelMeasurable.maxIntrinsicWidth(Constraints.Infinity) else 0
        val valueWidth = if (mode == LabeledValueMode.Auto) valueMeasurable.maxIntrinsicWidth(Constraints.Infinity) else 0
        val width = if (constraints.hasBoundedWidth) constraints.maxWidth else {
            (labelWidth.toLong() + gap + valueWidth).coerceAtMost(Constraints.Infinity.toLong()).toInt()
        }
        if (mode == LabeledValueMode.Auto && labelWidth.toLong() + gap + valueWidth <= width.toLong()) {
            val valuePlaceable = valueMeasurable.measure(Constraints(maxWidth = width))
            val labelPlaceable = labelMeasurable.measure(Constraints(maxWidth = (width - valuePlaceable.width - gap).coerceAtLeast(0)))
            val height = maxOf(labelPlaceable.height, valuePlaceable.height)
            layout(width, height) {
                labelPlaceable.placeRelative(0, (height - labelPlaceable.height) / 2)
                valuePlaceable.placeRelative(width - valuePlaceable.width, (height - valuePlaceable.height) / 2)
            }
        } else {
            val labelPlaceable = labelMeasurable.measure(Constraints(maxWidth = width))
            val valuePlaceable = valueMeasurable.measure(Constraints(maxWidth = width))
            val spacing = verticalGap.roundToPx()
            layout(width, labelPlaceable.height + spacing + valuePlaceable.height) {
                labelPlaceable.placeRelative(0, 0)
                valuePlaceable.placeRelative(0, labelPlaceable.height + spacing)
            }
        }
    }
}
