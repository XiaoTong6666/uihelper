plugins {
    id("com.android.library") version "9.4.1"
    id("org.jetbrains.kotlin.plugin.compose") version "2.4.20"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.4.20"
}

// The smoke consumer is a separate Gradle build. Read the version from the uihelper
// checkout above its root, matching the library's own Maven publication version.
val uihelperVersion = providers.gradleProperty("uihelper.version").orElse(
    providers.exec {
        workingDir = rootProject.projectDir.parentFile
        commandLine("git", "rev-list", "--count", "HEAD")
    }.standardOutput.asText.map { it.trim() },
)

android {
    namespace = "test.consumer"
    compileSdk = 37
    defaultConfig { minSdk = 29 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin { compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17) } }
    buildFeatures { compose = true }
}
dependencies {
    implementation(platform("androidx.compose:compose-bom:2026.09.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.material3:material3:1.5.0-alpha29")
    implementation("io.github.xiaotong6666:uihelper:${uihelperVersion.get()}")
}
