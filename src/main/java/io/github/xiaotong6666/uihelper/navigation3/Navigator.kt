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

/** Typed route mutations. The exposed back stack is for NavDisplay integration; mutate through this class. */
class Navigator<T : NavKey>(
    val backStack: NavBackStack,
) {
    /** Push a new entry, including a route already present elsewhere in the stack. */
    fun push(key: T) {
        backStack.add(key)
    }

    /** Avoid adding an identical top entry. */
    fun pushSingleTop(key: T) {
        if (backStack.lastOrNull() != key) backStack.add(key)
    }

    /** Opt-in global de-duplication for apps with singleton destinations. */
    fun pushUnique(key: T) {
        if (key !in backStack) backStack.add(key)
    }

    fun pop() {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
        }
    }

    fun replace(key: T) {
        if (backStack.isNotEmpty()) {
            backStack[backStack.lastIndex] = key
        } else {
            backStack.add(key)
        }
    }

    fun replaceAll(keys: List<T>) {
        if (keys.isEmpty()) return
        backStack.clear()
        backStack.addAll(keys)
    }

    fun popUntil(predicate: (T) -> Boolean) {
        @Suppress("UNCHECKED_CAST")
        while (backStack.size > 1 && !predicate(backStack.last() as T)) {
            backStack.removeAt(backStack.lastIndex)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun current(): T? = backStack.lastOrNull() as T?

    fun backStackSize(): Int = backStack.size
}

@Composable
inline fun <reified T : NavKey> rememberNavigator(startRoute: T): Navigator<T> {
    // MIUIX Nav's rememberNavBackStack is inline JVM-21 bytecode. Capture the
    // same closed-polymorphic serializer here so JVM-17 consumers can inline
    // this public API without losing process-death stack restoration.
    val saver = remember { navigatorBackStackSaver(serializer<List<T>>()) }
    val backStack = rememberSaveable(saver = saver) { navBackStackOf(startRoute) }
    return remember(backStack) {
        Navigator<T>(backStack)
    }
}

@PublishedApi
internal val navigatorBackStackJson: Json = Json { ignoreUnknownKeys = true }

@PublishedApi
internal fun <T : NavKey> navigatorBackStackSaver(elementsSerializer: KSerializer<List<T>>): Saver<NavBackStack, String> = Saver(
    save = { stack ->
        // Only routes of T may be inserted via Navigator's typed mutation methods.
        // A consumer directly mutating the exposed NavBackStack bypasses that contract.
        @Suppress("UNCHECKED_CAST")
        navigatorBackStackJson.encodeToString(elementsSerializer, stack.toList() as List<T>)
    },
    restore = { encoded ->
        val decoded: List<NavKey> = navigatorBackStackJson.decodeFromString(elementsSerializer, encoded)
        navBackStackOf(*decoded.toTypedArray())
    },
)

val LocalNavigator = staticCompositionLocalOf<Navigator<out NavKey>> {
    error("LocalNavigator not provided")
}
