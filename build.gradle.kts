// Top-level build file. Plugin versions are pulled from gradle/libs.versions.toml.
// Plugins are declared `apply false` here and applied in the relevant module.

plugins {
    alias(libs.plugins.android.application) apply false
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
