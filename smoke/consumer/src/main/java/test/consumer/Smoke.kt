@file:Suppress("ktlint:standard:function-naming")

package test.consumer

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.github.xiaotong6666.uihelper.adaptive.AdaptiveClickableCard
import io.github.xiaotong6666.uihelper.adaptive.AdaptiveIconLabelChip
import io.github.xiaotong6666.uihelper.adaptive.AdaptiveLabelChip
import io.github.xiaotong6666.uihelper.adaptive.AdaptiveMetricChip
import io.github.xiaotong6666.uihelper.adaptive.AdaptiveSummaryCard
import io.github.xiaotong6666.uihelper.adaptive.AdaptiveSurfaceTone
import io.github.xiaotong6666.uihelper.adaptive.AdaptiveTonalSurface
import io.github.xiaotong6666.uihelper.adaptive.ExpandableSectionBody
import io.github.xiaotong6666.uihelper.adaptive.LabeledValueLayout
import io.github.xiaotong6666.uihelper.adaptive.LabeledValueMode
import io.github.xiaotong6666.uihelper.adaptive.WrapSafeText
import io.github.xiaotong6666.uihelper.adaptive.rememberExpandableSectionState
import io.github.xiaotong6666.uihelper.chrome.AdaptiveNavigationShell
import io.github.xiaotong6666.uihelper.chrome.NavigationShellBackBehavior
import io.github.xiaotong6666.uihelper.chrome.NavigationShellItem
import io.github.xiaotong6666.uihelper.common.StatusTag
import io.github.xiaotong6666.uihelper.dialog.AdaptiveDecisionDialog
import io.github.xiaotong6666.uihelper.dialog.AdaptiveDetailsDialog
import io.github.xiaotong6666.uihelper.miuix.primitive.StatusHeroCardMiuix
import io.github.xiaotong6666.uihelper.mode.AdaptiveTheme
import io.github.xiaotong6666.uihelper.mode.UiMode
import io.github.xiaotong6666.uihelper.navigation3.rememberNavigator
import kotlinx.serialization.Serializable
import top.yukonga.miuix.kmp.nav.core.NavKey

@Serializable
sealed interface DemoRoute : NavKey {
    @Serializable data object Home : DemoRoute

    @Serializable data object Details : DemoRoute
}

@Composable
fun ReusableConsumer() {
    val routes = rememberNavigator<DemoRoute>(DemoRoute.Home)
    AdaptiveTheme(uiMode = UiMode.Material, darkTheme = false, materialColorScheme = lightColorScheme()) {
        val expanded = rememberExpandableSectionState(identity = "stable-section")
        AdaptiveNavigationShell(
            items = listOf(
                NavigationShellItem(
                    title = "Home",
                    icon = Icons.Rounded.Home,
                    compactTopBarTitle = "Home",
                    leadingContent = { Text("L") },
                    largeTitleLeadingContent = { Text("Brand") },
                    trailingContent = { Text("Action") },
                ),
            ),
            selectedIndex = 0,
            onSelectedIndexChange = {},
            navigationRail = false,
            swipeNavigationEnabled = false,
            backBehavior = NavigationShellBackBehavior.Disabled,
        ) { _, _, _, _ ->
            Column {
                WrapSafeText("very_long_identifier_abcdefghijklmnopqrstuvwxyz")
                LabeledValueLayout(
                    label = { Text("Label") },
                    value = { Text("Value") },
                    mode = LabeledValueMode.Stacked,
                )
                StatusTag("Ready", backgroundColor = Color.Green, contentColor = Color.Black)
                Button(onClick = expanded::toggle) { Text("Toggle details") }
                Button(onClick = { routes.pushSingleTop(DemoRoute.Details) }) { Text("Open details") }
                ExpandableSectionBody(expanded = expanded.expanded) { Text("More") }
                AdaptiveSummaryCard(
                    icon = Icons.Rounded.Home,
                    title = "Inventory",
                    summary = "Reusable summary",
                    trailingText = "3",
                )
                AdaptiveClickableCard(onClick = {}) {
                    Text("Clickable adaptive card")
                }
                AdaptiveTonalSurface(tone = AdaptiveSurfaceTone.Highest) {
                    AdaptiveMetricChip(Icons.Rounded.Home, "Items", "3")
                    AdaptiveIconLabelChip(Icons.Rounded.Home, "Category")
                    AdaptiveLabelChip("Label")
                }
                StatusHeroCardMiuix(
                    title = "Status",
                    summary = "Consumer-provided summary",
                    icon = Icons.Rounded.Home,
                    containerColor = Color.DarkGray,
                    accentColor = Color.LightGray,
                    onClick = {},
                    metaContent = { Text("Consumer-owned metadata") },
                    actionContent = { Text("Consumer-owned action") },
                )
            }
        }
        AdaptiveDecisionDialog(
            show = false,
            title = "Decision",
            message = "Choose one",
            confirmLabel = "Yes",
            dismissLabel = "No",
            onConfirm = {},
            onDismissButton = {},
            onDismissRequest = {},
        )
        AdaptiveDetailsDialog(
            show = false,
            title = "Details",
            summary = "Reusable details shell",
            icon = Icons.Rounded.Home,
            closeLabel = "Close",
            onDismiss = {},
        ) {
            Text("Consumer-owned body")
        }
    }
}
