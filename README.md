# uihelper

`uihelper` is an Android UI framework for settings-oriented and utility-style apps.

Its design goal is:

- write business pages once
- switch between `Material` and `Miuix` skins behind one abstraction layer
- keep project-specific widgets and business models out of the framework

This module is intended to be extractable and reusable in other projects.

## Reuse

`uihelper` is a standalone Android library project. It requires `compileSdk 37`, `minSdk 29`, Java/JVM 17 bytecode, and the Kotlin Compose compiler plugin. A JDK 21 build runtime is fine; the consumer does not have to match it. Its `rememberNavigator` uses a JVM-17-safe saver equivalent to MIUIX Nav's, because MIUIX Nav 0.9.4's own `rememberNavBackStack` is an inline JVM-21 API. This retains back-stack save/restore and allows JVM-17 consumers to call the public inline helper.

### MIUIX blur on API 29–32

The upstream `miuix-blur-android` 0.9.4 AAR declares `minSdk 33` even though the rest of the MIUIX UI used here supports older Android versions. `uihelper` includes a scoped `tools:overrideLibrary` for that artifact in its own manifest, so consumers can retain `minSdk 29` without adding a second manifest override.

Actual backdrop blur is **only enabled when MIUIX RuntimeShader is supported** (Android 13 / API 33 or later). API 29–32 retain their MIUIX surfaces, navigation, cards and animations, but use opaque chrome rather than a transparent bar without a rendered blur. Keep the capability check in `miuix/effect/Blur.kt` in sync with the MIUIX blur implementation when upgrading the dependency. This manifest override is specific to the guarded MIUIX 0.9.4 blur integration, not a general permission to run arbitrary newer-API libraries on older devices.

JVM-17 consumers can also use this library's reified `rememberNavigator` helper; do not call upstream MIUIX Nav's JVM-21 inline helper directly from a JVM-17 module. In projects with strict module dependency boundaries, consume the published AAR from a UI module (or classify the included module explicitly); do not add this UI dependency to a headless SDK module.

For active development, add this repository as a Git submodule or sibling directory, then include the **independent build** in the consumer's `settings.gradle.kts`. This preserves uihelper's own version catalog and plugin versions:

```kotlin
includeBuild("uihelper")
```

Consume it using the matching published coordinates (Gradle composite-build substitution):

```kotlin
dependencies {
    implementation("io.github.xiaotong6666:uihelper:<git-commit-count>")
}
```

Do **not** use `include(":uihelper")` with the current standalone build: it merges the consumer's `libs` catalog with uihelper's catalog and can silently resolve a different toolchain. Both projects must be configured for compatible Compose / Android APIs. Add a separate version-catalog mapping if the consumer uses aliases.

For a local binary dependency, publish the release AAR from this directory:

```bash
./gradlew publishReleasePublicationToMavenLocal
# For an unpublished smoke version that cannot overwrite a released commit-count version:
./gradlew publishReleasePublicationToMavenLocal -Puihelper.version=local-smoke
```

Then add `mavenLocal()` to the consumer's repositories and use:

```kotlin
implementation("io.github.xiaotong6666:uihelper:<git-commit-count>")
```

The default published version is `git rev-list --count HEAD` from the `uihelper` repository. Override it with `-Puihelper.version=...` for a uniquely named unpublished build; this override does not change any committed release. **Changes in a Git submodule worktree are not fetched by another project until committed and the consumer updates its submodule SHA.**

### Consumer API contracts

- `AdaptiveTheme` accepts the app's optional `materialColorScheme` and `themeController`. A consumer controls branding; uihelper only provides standalone defaults.
- `Navigator<T>` uses typed mutation methods and allows duplicate routes with `push`. Use `pushSingleTop` or `pushUnique` explicitly when appropriate. Define a serializable route hierarchy when using `rememberNavigator`; do not mutate its public `backStack` except to integrate it with a host.
- `AdaptiveNavigationShell` defaults to first-page Back and responsive navigation rail, but callers can configure `backBehavior`, `onBackRequested`, `navigationRail`, and `swipeNavigationEnabled`. Each `NavigationShellItem` may also supply app-owned `leadingContent` and `trailingContent`; MIUIX scrollable bars additionally support `largeTitleLeadingContent`, rendered alongside the expanded title and handed off to the compact leading slot on collapse. A short `compactTopBarTitle` can be set for large accessibility text without changing the expanded MIUIX title. None of these slots requires a particular app's logo or link.
- `rememberExpandableSectionState(identity = ...)` accepts a stable business ID, **never localized display text**. The default is positional state; when used in reorderable lists, wrap the call in a stable Compose `key(id)` or a keyed lazy item.
- `LabeledValueLayout` in `Auto` mode queries child intrinsic widths; use `mode = LabeledValueMode.Stacked` for children without intrinsic measurement support.
- `WrapSafeText` inserts visual U+200B breaks. Accessibility receives the original text, but selection may contain U+200B; explicit copy actions must use the original model value.
- Domain-shaped `HomeStatusCard` and `UpdatePromptDialogMiuix` are optional recipes, not required shell primitives. Release notes, status meanings, actions, and translations belong to the consumer.
- `StatusHeroCardMiuix` accepts optional, app-owned `metaContent` and `actionContent` slots. Both live inside one status hero, with a large decorative glyph clipped at the bottom/right card edges while its interior remains visible; metadata is restricted to the left column. The original `footer` slot remains supported for existing consumers. Interactive cards use native Tilt and visible pressed indication; uihelper does not know about build details, export, or any other app domain.

### Verification

Run uihelper's contract tests, independent Release AAR build, and its **separate**
consumer source build (which does not import Duck or share Duck's catalog):

```bash
./gradlew :testDebugUnitTest :assembleRelease
./gradlew -p smoke :consumer:compileDebugKotlin
```

The independent consumer source lives under `smoke/` and exercises typed routes,
theme injection, shell policies and the adaptive components. See `smoke/README.md`
for binary AAR verification. Runtime UI/gesture behavior still requires device tests.

## Scope

`uihelper` should own:

- UI mode and skin runtime
- app chrome and shared shell behavior
- reusable adaptive UI components
- skin-specific primitive implementations
- optional Android-app extensions that are still generic enough to reuse

`uihelper` should not own:

- app-specific data models
- business page semantics
- feature-specific widgets
- project-specific actions, routes, or search state machines

Examples of code that does **not** belong in `uihelper`:

- `AppInfo`
- `GroupedApps`
- `SearchStatus` tied to one feature flow
- `ConfigPageOverflowAction` tied to one app's config page
- app list rows, config detail cards, or any widget named after one project's domain nouns

## Package Contract

### Public API

These packages are the supported surface for feature and page code.

- `io.github.xiaotong6666.uihelper.adaptive`
  - semantic cross-skin components
  - preferred import target for business pages
  - `WrapSafeText` for long identifiers, paths and other unbroken text in either skin
  - `rememberExpandableSectionState` and `ExpandableSectionBody` for generic saveable disclosure state and skin-specific expansion motion; callers own headers, badges, and business meaning
  - `LabeledValueLayout` for responsive inline-or-stacked label/value placement without business-specific row models
- `io.github.xiaotong6666.uihelper.common`
  - small reusable UI helpers shared across skins
- `io.github.xiaotong6666.uihelper.model`
  - generic UI models and enums used by adaptive components
- `io.github.xiaotong6666.uihelper.mode`
  - UI mode runtime such as `UiMode` and `LocalUiMode`
- `io.github.xiaotong6666.uihelper.chrome`
  - shell and nested-scroll integration used by app-level containers
  - includes the public dual-skin host API for top-level navigation shells
  - public entry points should be shell-style APIs such as `AdaptiveNavigationShell`, `PageHost`, and `PageChrome`
- `io.github.xiaotong6666.uihelper.dialog`
  - reusable loading and confirm dialog handles plus dual-skin presenters
- `io.github.xiaotong6666.uihelper.navigation3`
  - reusable navigator helpers
- `io.github.xiaotong6666.uihelper.popup`
  - dual-skin popup and menu models used by app chrome and settings surfaces
- `io.github.xiaotong6666.uihelper.extensions.androidapp`
  - optional Android-app-specific helpers that remain generic enough to reuse

### Implementation Layers

These packages are framework internals and should normally only be used by `uihelper` itself or by app-specific skin widgets.

- `io.github.xiaotong6666.uihelper.material.scaffold`
- `io.github.xiaotong6666.uihelper.material.primitive`
- `io.github.xiaotong6666.uihelper.miuix.primitive`

Within `chrome`, shell internals such as composition locals and host state objects should stay internal to the module even though the package itself is public.

Rules:

- business pages should not import these packages directly
- if a feature page needs something from here more than once, add or extend an adaptive/common API instead
- app-specific Material or Miuix widgets may depend on these packages when they are intentionally skin-specific

## Dependency Direction

The intended dependency flow is:

`feature page -> adaptive/common/model -> material primitive or miuix primitive`

Not this:

`feature page -> material primitive`

And never this:

`uihelper -> app project feature code`

## Placement Rules

When deciding whether code belongs in `uihelper`, use this test:

> If the project name and business data changed, would this code still make sense?

If yes, it can belong in `uihelper`.

If no, keep it in the app project.

More specific guidance:

- put semantic cross-skin APIs in `adaptive`
- put tiny reusable visual helpers in `common`
- put generic presentation models in `model`
- put Material-only building blocks in `material.primitive`
- put Miuix-only building blocks in `miuix.primitive`
- put app-shell behavior in `chrome`
- put Android `PackageManager` or app-icon helpers in `extensions.androidapp` only if they stay project-agnostic

## Expected Usage

Business page code should look like this:

```kotlin
import io.github.xiaotong6666.uihelper.adaptive.SectionCard
import io.github.xiaotong6666.uihelper.adaptive.SectionTitle
import io.github.xiaotong6666.uihelper.adaptive.SettingsToggleItem
import io.github.xiaotong6666.uihelper.model.SectionTitleStyle
```

Not like this:

```kotlin
import io.github.xiaotong6666.uihelper.material.primitive.SettingsToggleItemMaterial
import io.github.xiaotong6666.uihelper.miuix.primitive.SettingsToggleItemMiuix
```

App shell code may use runtime and chrome packages directly:

```kotlin
import io.github.xiaotong6666.uihelper.chrome.AdaptiveNavigationShell
import io.github.xiaotong6666.uihelper.chrome.PageChrome
import io.github.xiaotong6666.uihelper.mode.UiMode
```

## Maintenance Rules

Before adding new code to `uihelper`:

1. Decide whether it is public API, implementation layer, or app-specific code.
2. Prefer extending `adaptive` over exposing a new skin-specific primitive to business pages.
3. Reject project-specific nouns in framework packages.
4. Keep `material/primitive` and `miuix/primitive` behavior aligned under one semantic contract.

If a future change makes a business page import `material.primitive` or `miuix.primitive` directly, treat that as architectural debt and either:

- lift the abstraction into `adaptive`, or
- keep the logic inside an app-specific skin widget layer
