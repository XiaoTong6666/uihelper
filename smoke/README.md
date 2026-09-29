# Independent consumer smoke test

This is a separate Android/Gradle project. It consumes uihelper through its public
Maven coordinates with includeBuild substitution; it does not read Duck's
version catalog or import Duck symbols.

From the uihelper root:

    ./gradlew -p smoke :consumer:compileDebugKotlin

The dependency coordinate automatically uses `git rev-list --count HEAD` from
the parent uihelper checkout. For a binary AAR test, publish with a unique
local version, remove the includeBuild line, add mavenLocal() to the smoke
settings repositories, and pass the same `-Puihelper.version=...` to the
consumer build. No editing of its build.gradle.kts is necessary. A fresh
online resolution may be needed for a new Maven publication; an uncached BOM
POM cannot be resolved with --offline on first use.
