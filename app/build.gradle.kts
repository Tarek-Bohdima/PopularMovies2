plugins {
    alias(libs.plugins.android.application)
}

// Sourced from ~/.gradle/gradle.properties (local) or ORG_GRADLE_PROJECT_myTMDBApiKey (CI).
// Surrounding double-quotes are required: the value is interpolated verbatim into a Java
// string literal by `buildConfigField` below. Will be replaced with the typed BuildConfigField
// API in a follow-up issue.
val myTMDBApiKey: String by project

android {
    namespace = "com.example.android.popularmovies2"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.android.popularmovies2"
        minSdk = 19
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    buildTypes.all {
        buildConfigField("String", "TMDB_API_KEY", myTMDBApiKey)
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        buildConfig = true
        dataBinding = true
        viewBinding = true
    }

    lint {
        // Timber 4.7.1's WrongTimberUsageDetector calls Context.getMainProject() which is
        // forbidden during module analysis on AGP 7.x — produces LintError, not a real issue.
        // Glide 4.11.0's NotificationTarget triggers NotificationPermission on targetSdk 33,
        // but we don't use NotificationTarget. Both libraries are slated for replacement
        // (Timber → structured logging, Glide → Coil) so we don't bump them just for lint.
        disable += setOf("LintError", "NotificationPermission")
    }
}

dependencies {
    implementation(libs.jetbrains.annotations)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.coordinatorlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.google.material)

    // Lifecycle (legacy — slated for removal alongside the Coroutines/Flow migration)
    implementation(libs.bundles.lifecycle.legacy)

    // Retrofit / OkHttp
    implementation(libs.bundles.retrofit)

    // RxJava (legacy — slated for removal alongside the Coroutines/Flow migration)
    implementation(libs.bundles.rxjava.legacy)

    // Room
    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.androidx.room.compiler)

    // Dagger (legacy — slated for replacement by Hilt)
    implementation(libs.dagger)
    annotationProcessor(libs.dagger.compiler)

    // Glide (legacy — slated for replacement by Coil)
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)

    implementation(libs.timber)
    implementation(libs.expandable.textview)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}
