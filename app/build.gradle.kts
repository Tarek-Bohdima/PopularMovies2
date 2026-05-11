plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kover)
}

// Sourced from ~/.gradle/gradle.properties (local) or ORG_GRADLE_PROJECT_myTMDBApiKey (CI).
// Surrounding double-quotes are required: the value is interpolated verbatim into a Java
// string literal by `buildConfigField` below. Will be replaced with the typed BuildConfigField
// API in a follow-up issue.
val myTMDBApiKey: String by project

android {
    namespace = "com.example.android.popularmovies2"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.android.popularmovies2"
        minSdk = 26
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
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

    testOptions {
        unitTests.isReturnDefaultValues = true
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
    kapt(libs.androidx.room.compiler)

    // Dagger (legacy — slated for replacement by Hilt)
    implementation(libs.dagger)
    kapt(libs.dagger.compiler)

    // Glide (legacy — slated for replacement by Coil)
    implementation(libs.glide)
    kapt(libs.glide.compiler)

    implementation(libs.timber)
    implementation(libs.expandable.textview)

    testImplementation(libs.junit)
    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}

kover {
    reports {
        filters {
            excludes {
                // Generated code (Dagger, Room, DataBinding, Glide, R, BuildConfig, Parcelize)
                classes(
                    "*_Factory",
                    "*_Factory\$*",
                    "*_MembersInjector",
                    "*_MembersInjector\$*",
                    "*_Provide*Factory",
                    "*_Provide*Factory\$*",
                    "Dagger*",
                    "Dagger*\$*",
                    "*_Impl",
                    "*_Impl\$*",
                    "*Binding",
                    "*Binding\$*",
                    "*BindingImpl",
                    "*BindingImpl\$*",
                    "*.BR",
                    "*.R",
                    "*.R\$*",
                    "*.BuildConfig",
                    "*.databinding.*",
                    "*.DataBinderMapperImpl",
                    "*.DataBindingTriggerClass",
                    "com.example.android.popularmovies2.DataBinderMapperImpl",
                    "com.example.android.popularmovies2.DataBindingTriggerClass",
                    "*.GlideApp",
                    "*.GlideOptions",
                    "*.GlideOptions\$*",
                    "*.GlideRequest",
                    "*.GlideRequest\$*",
                    "*.GlideRequests",
                    "*.GeneratedAppGlideModuleImpl",
                    "*.GeneratedRequestManagerFactory",
                )
                packages(
                    "com.example.android.popularmovies2.generated.callback",
                    "com.bumptech.glide",
                )
                // Hand-written code that's tied to Android framework / DI graph and not
                // realistically JVM-unit-testable. Compose migration deletes most of these.
                packages(
                    "com.example.android.popularmovies2.di.*",
                    "com.example.android.popularmovies2.data.model",
                )
                classes(
                    "com.example.android.popularmovies2.MoviesApp",
                    "com.example.android.popularmovies2.CustomGlideModule",
                    "com.example.android.popularmovies2.AppExecutors",
                    "com.example.android.popularmovies2.AppExecutors\$*",
                    "com.example.android.popularmovies2.Constants",
                    "com.example.android.popularmovies2.data.network.MovieApi",
                    "com.example.android.popularmovies2.data.network.MovieApi\$*",
                    "com.example.android.popularmovies2.data.local.MovieDao",
                    "com.example.android.popularmovies2.data.local.AppDatabase",
                    "com.example.android.popularmovies2.data.local.AppDatabase\$*",
                    "com.example.android.popularmovies2.ui.BindingAdapters*",
                    "com.example.android.popularmovies2.ui.list.MainActivity",
                    "com.example.android.popularmovies2.ui.list.MainActivity\$*",
                    "com.example.android.popularmovies2.ui.list.MainViewModelFactory",
                    "com.example.android.popularmovies2.ui.list.MovieAdapter",
                    "com.example.android.popularmovies2.ui.list.MovieAdapter\$*",
                    "com.example.android.popularmovies2.ui.detail.DetailActivity",
                    "com.example.android.popularmovies2.ui.detail.DetailActivity\$*",
                    "com.example.android.popularmovies2.ui.detail.DetailViewModelFactory",
                    "com.example.android.popularmovies2.ui.detail.ReviewsAdapter",
                    "com.example.android.popularmovies2.ui.detail.ReviewsAdapter\$*",
                    "com.example.android.popularmovies2.ui.detail.TrailersAdapter",
                    "com.example.android.popularmovies2.ui.detail.TrailersAdapter\$*",
                )
            }
        }
        verify {
            rule {
                bound {
                    minValue = 60
                    coverageUnits = kotlinx.kover.gradle.plugin.dsl.CoverageUnit.LINE
                }
            }
        }
    }
}
