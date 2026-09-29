package io.github.xiaotong6666.uihelper.navigation3

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import top.yukonga.miuix.kmp.nav.core.NavBackStack
import top.yukonga.miuix.kmp.nav.core.NavKey
import top.yukonga.miuix.kmp.nav.core.navBackStackOf

class Navigator(
    val backStack: NavBackStack,
) {

    fun push(key: NavKey) {
        if (key !in backStack) {
            backStack.add(key)
        }
    }

    fun pop() {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
        }
    }

    fun replace(key: NavKey) {
        if (backStack.isNotEmpty()) {
            backStack[backStack.lastIndex] = key
        } else {
            backStack.add(key)
        }
    }

    fun replaceAll(keys: List<NavKey>) {
        if (keys.isEmpty()) return
        backStack.clear()
        backStack.addAll(keys)
    }

    fun popUntil(predicate: (NavKey) -> Boolean) {
        while (backStack.size > 1 && !predicate(backStack.last())) {
            backStack.removeAt(backStack.lastIndex)
        }
    }

    fun current(): NavKey? = backStack.lastOrNull()

    fun backStackSize(): Int = backStack.size
}

@Composable
inline fun <reified T : NavKey> rememberNavigator(startRoute: T): Navigator {
    // MIUIX Nav's rememberNavBackStack is inline JVM-21 bytecode. Capture the
    // same closed-polymorphic serializer here so JVM-17 consumers can inline
    // this public API without losing process-death stack restoration.
    val saver = remember { navigatorBackStackSaver(serializer<List<T>>()) }
    val backStack = rememberSaveable(saver = saver) { navBackStackOf(startRoute) }
    return remember(backStack) {
        Navigator(backStack)
    }
}

@PublishedApi
internal val navigatorBackStackJson: Json = Json { ignoreUnknownKeys = true }

@PublishedApi
internal fun <T : NavKey> navigatorBackStackSaver(elementsSerializer: KSerializer<List<T>>): Saver<NavBackStack, String> = Saver(
    save = { stack ->
        @Suppress("UNCHECKED_CAST")
        navigatorBackStackJson.encodeToString(elementsSerializer, stack.toList() as List<T>)
    },
    restore = { encoded ->
        val decoded: List<NavKey> = navigatorBackStackJson.decodeFromString(elementsSerializer, encoded)
        navBackStackOf(*decoded.toTypedArray())
    },
)

val LocalNavigator = staticCompositionLocalOf<Navigator> {
    error("LocalNavigator not provided")
}
