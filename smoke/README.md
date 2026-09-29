# Independent consumer smoke test

This is a separate Android/Gradle project. It consumes uihelper through its public
Maven coordinates with includeBuild substitution; it does not read Duck's
version catalog or import Duck symbols.

From the uihelper root:

    ./gradlew -p smoke :consumer:compileDebugKotlin

For a binary AAR test, publish with a unique local version and use that version
in smoke/consumer/build.gradle.kts; remove the includeBuild line and
add mavenLocal() to the smoke settings repositories. A fresh online resolution
may be needed for a new Maven publication; an uncached BOM POM cannot be
resolved with --offline on first use.
