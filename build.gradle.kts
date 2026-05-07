// Top-level build file. Plugin versions are pulled from gradle/libs.versions.toml.
// Plugins are declared `apply false` here and applied in the relevant module.

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
