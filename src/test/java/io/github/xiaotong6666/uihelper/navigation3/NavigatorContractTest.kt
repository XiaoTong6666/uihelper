package io.github.xiaotong6666.uihelper.navigation3

import androidx.compose.runtime.saveable.SaverScope
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import top.yukonga.miuix.kmp.nav.core.NavKey
import top.yukonga.miuix.kmp.nav.core.navBackStackOf

class NavigatorContractTest {
    @Serializable
    private sealed interface Route : NavKey {
        @Serializable @SerialName("start") data object Start : Route
        @Serializable @SerialName("details") data object Details : Route
    }

    @Test fun duplicatePolicyIsExplicit() {
        val navigator = Navigator<Route>(navBackStackOf(Route.Start))
        navigator.push(Route.Details)
        navigator.push(Route.Details)
        assertEquals(3, navigator.backStackSize())
        navigator.pushSingleTop(Route.Details)
        assertEquals(3, navigator.backStackSize())
        navigator.pushUnique(Route.Start)
        assertEquals(3, navigator.backStackSize())
        navigator.pop()
        assertEquals(Route.Details, navigator.current())
    }

    @Test fun saveRestorePreservesRouteVariantsAndDuplicates() {
        val navigator = Navigator<Route>(navBackStackOf(Route.Start))
        navigator.push(Route.Details)
        navigator.push(Route.Details)
        val saver = navigatorBackStackSaver(ListSerializer(Route.serializer()))
        val scope = object : SaverScope { override fun canBeSaved(value: Any): Boolean = true }
        val encoded = with(saver) { scope.save(navigator.backStack) }
        assertNotNull(encoded)
        val restored = saver.restore(requireNotNull(encoded))
        assertEquals(navigator.backStack.toList(), restored?.toList())
    }
}
