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
        // Timber is slated for replacement, so we don't bump just for lint.
        disable += setOf("LintError")
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

    // Lifecycle (KTX — LiveData artifact still legacy; runtime + viewmodel KTX are target-stack)
    implementation(libs.bundles.lifecycle)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Retrofit / OkHttp
    implementation(libs.bundles.retrofit)

    // Room
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)

    // Dagger (legacy — slated for replacement by Hilt)
    implementation(libs.dagger)
    kapt(libs.dagger.compiler)

    // Image loading
    implementation(libs.coil)

    implementation(libs.timber)
    implementation(libs.expandable.textview)

    testImplementation(libs.junit)
    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}

kover {
    reports {
        filters {
            excludes {
                // Generated code (Dagger, Room, DataBinding, R, BuildConfig, Parcelize)
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
                )
                packages(
                    "com.example.android.popularmovies2.generated.callback",
                )
                // Hand-written code that's tied to Android framework / DI graph and not
                // realistically JVM-unit-testable. Compose migration deletes most of these.
                packages(
                    "com.example.android.popularmovies2.di.*",
                    "com.example.android.popularmovies2.data.model",
                )
                classes(
                    "com.example.android.popularmovies2.MoviesApp",
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
