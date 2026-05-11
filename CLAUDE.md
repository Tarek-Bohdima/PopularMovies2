# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

> **Documentation continuity rule.** This file is the cross-account anchor. Every working preference, decision, or convention agreed in conversation MUST land here so a cold session in any Claude Code account can pick up where the previous one left off. When the user states a new rule, update this file in the same turn — do not rely on per-account memory alone.

---

## 1. Project context

Android app — Udacity Android Developer Nanodegree "Popular Movies" Stage 2 submission. Browses popular/top-rated movies from TMDb, plus a local "favorites" collection with trailers and reviews on the detail screen. Every legacy source file carries an Udacity-honor-code header stating the code is reference-only; preserve that header on edits to existing files. New files may use a simpler license header or none.

**Stack state.** The data, DI, imaging, and JSON layers have all been migrated to the §2 target stack (Kotlin + Coroutines/Flow + Hilt + Coil + kotlinx.serialization on AGP 9 / Gradle Kotlin DSL with a version catalog). What's still legacy is **the UI layer**: XML layouts, DataBinding/ViewBinding, two Activities. The Compose + Navigation-Compose migration is the last big lane. Until that lands, keep writing new UI in a Compose-friendly shape (`StateFlow<UiState>`, hoisted state) so the eventual swap is mechanical.

---

## 2. Target stack (Google-recommended, agreed conventions)

| Concern | Target | Status |
|---|---|---|
| Language | **Kotlin (idiomatic)** | Done — pre-modernization (Java already converted). |
| UI | **Jetpack Compose** | **Pending** — still XML + DataBinding/ViewBinding. |
| Architecture | **MVVM + UDF** (immutable `UiState` data class, `StateFlow` from `ViewModel`, state hoisted into Composables) | Partial — VMs expose `StateFlow<T>`; the single immutable-`UiState` consolidation per screen is still pending. |
| Async | **Coroutines + Flow / StateFlow** — no `Single`/`Observable`, no `LiveData` | Done — #20. |
| DI | **Hilt** | Done — #21. |
| Annotation processing | **KSP** | Pending — currently kapt for both Hilt and Room. |
| Build | **Gradle Kotlin DSL** + **`gradle/libs.versions.toml`** version catalog + convention plugins in `build-logic/` | Partial — Gradle Kotlin DSL ✓, version catalog ✓; convention plugins / `build-logic/` still pending. |
| Coverage | **Kover** | Done (≥60% line gate). |
| Format | **Spotless** (with ktlint) | Pending. |
| Static analysis | **Detekt** | Pending. |
| Image loading | **Coil** (Compose-native, behind `ImageLoader` interface) | Done — #23. |
| Navigation | **Navigation-Compose** (or Navigation 3 when stable — see android/skills `navigation-3`) | Pending — comes with the Compose migration. |
| Local persistence (structured) | Room — exposes Flows, not LiveData | Done — #20. |
| Local persistence (key/value) | **DataStore** (Preferences DataStore for simple key/value, Proto DataStore for typed). **Forbidden: `SharedPreferences`, `PreferenceManager`, `getDefaultSharedPreferences`, anything in `androidx.preference:*`.** | N/A — no key/value usage in the app yet. Rule stays in force for any future settings screen. |
| Networking | Retrofit + OkHttp + **kotlinx.serialization** + coroutine `suspend` functions | Done — #20 (`suspend`) + #24 (kotlinx). |
| JSON | **kotlinx.serialization** (compile-time, no reflection). If Moshi is unavoidable in some module, use **Moshi codegen** (`com.squareup.moshi:moshi-kotlin-codegen` via KSP) — never the reflective `KotlinJsonAdapterFactory`. | Done — #24. |
| Connectivity | `NetworkMonitor` interface backed by `ConnectivityManager.NetworkCallback` (`Flow<Boolean>`); collected in VMs via `stateIn`; no synchronous `isNetworkConnected(context)` polling. | Done — #18. |
| Launcher SDK floor | `minSdk = 26` (adaptive-icon-only delivery; no `mipmap-*dpi/` PNGs). | Done — #17. |

**Why MVVM+UDF over full MVI:** Compose's recomposition model already enforces unidirectional flow when state is a single immutable `UiState` exposed via `StateFlow`. This is the *Now in Android* official sample pattern. Full reducer/intent-class MVI is heavier and unnecessary at this app's scale; we can evolve to it if state grows complex enough to warrant it.

### XML policy

**Refrain from XML unless necessary.** Required XML survivors: `AndroidManifest.xml`, resource files Android can't read otherwise (`mipmap-*`, `xml/backup_descriptor`, `values/strings.xml` for translations). UI is Compose. Themes are `Theme.kt` Compose theming, not `themes.xml`. Adapters (`@BindingAdapter`) are deleted with DataBinding.

### Code-to-abstraction principle

Every cross-layer dependency is an **interface**, with the concrete implementation injected via Hilt. ViewModels depend on `MovieRepository` (interface), not `MovieRepositoryImpl`. Repositories depend on `RemoteDataSource` / `LocalDataSource` interfaces. Test doubles replace implementations without mocking frameworks where possible.

---

## 3. Required setup: TMDb API key

The build pulls `myTMDBApiKey` from Gradle properties and emits it as `BuildConfig.TMDB_API_KEY` (`app/build.gradle:31-41`). Without it, Gradle throws `InvalidUserDataException` at configuration time. Set it once in `~/.gradle/gradle.properties` (NOT the repo's `gradle.properties`):

```
myTMDBApiKey="<YOUR_TMDB_V3_KEY>"
```

The quotes are required — the value is interpolated into a Java string literal verbatim by `buildConfigField 'String', 'TMDB_API_KEY', myTMDBApiKey`.

### Secrets policy (strict)

- **No secrets in the repo. Ever.** Not in `gradle.properties`, not in `local.properties` (it's gitignored but treat it as a tripwire), not in source.
- When a build, CI, or runtime path needs a credential, the assistant **must prompt the user** to add it to GitHub Secrets (or local-only Gradle properties) before wiring the workflow — never commit a placeholder that looks like a real value.
- Required GitHub Secret today: **`TMDB_API_KEY`** (raw v3 key, no quotes). The CI workflow wraps it in quotes when exporting `ORG_GRADLE_PROJECT_myTMDBApiKey` so the legacy `buildConfigField` interpolation still works.

---

## 4. Workflow rules

### Tracking issues

**No PR without a tracking issue.** Every PR's description references its issue (`Closes #N` / `Fixes #N`). If an issue doesn't exist for the work, open one first or ask the user.

### Branching & commits

- **GitHub Flow.** Short-lived branch off `master` per issue, named `<type>/<issue-number>-<slug>` (e.g. `feat/42-compose-detail-screen`). Squash-merge on green CI.
- **Conventional Commits** for commit *and* squash-merge titles: `feat:`, `fix:`, `refactor:`, `chore:`, `docs:`, `test:`, `build:`, `ci:`, `perf:`. Body wraps at 72; references the issue.

### Tagging & releases

- Tags are **semver `vMAJOR.MINOR.PATCH`** matching the Android `versionName`. Bump `versionCode` monotonically.
- Tag on `master` after the release commit; create a GitHub Release with auto-generated notes. CI's release job (when added) builds a signed AAB on tag push.

---

## 5. Common commands

```bash
./gradlew assembleDebug                # build debug APK
./gradlew installDebug                 # build + install on connected device/emulator
./gradlew test                         # JVM unit tests (app/src/test)
./gradlew :app:testDebugUnitTest --tests "com.example.android.popularmovies2.data.AppRepositoryTest.clearDisposables_propagatesToNetwork"   # single test
./gradlew connectedAndroidTest         # instrumented tests (needs device/emulator)
./gradlew lint                         # Android Lint
./gradlew koverHtmlReport              # coverage HTML at app/build/reports/kover/html/
./gradlew koverVerify                  # fail build if coverage < threshold (60% line)
./gradlew clean
```

Once Detekt/Spotless are wired:

```bash
./gradlew detekt                       # static analysis
./gradlew spotlessCheck                # format check
./gradlew spotlessApply                # auto-fix formatting
```

### Coverage rules

- Kover plugin is applied in `app/build.gradle.kts` with a `koverVerify` rule pinning min line coverage to **60%**.
- Excluded from coverage: Hilt code-gen (`Hilt_*`, `*HiltModules*`, `*HiltComponents*`, `*_GeneratedInjector`, `dagger.hilt.internal.*`, `hilt_aggregated_deps`), Room/DataBinding/R/BuildConfig generated code, Activities, RecyclerView adapters, BindingAdapters, the `di.module` package, DTOs/entities in `data.model.*`, and the `ui.image` package (Coil delegate + interface).
- ViewModels take their dependencies via constructor injection (`MovieRepository`, `NetworkMonitor` — both interfaces) so they're JVM-unit-testable with simple mockito-kotlin `mock<MovieRepository>()` — no Robolectric, no Hilt test components.
- `DetailViewModel` uses `SavedStateHandle` for its `movieId` argument; tests construct `SavedStateHandle(mapOf(MainActivity.MOVIE_OBJECT to sampleMovie()))` directly.

---

## 6. Current SDK / toolchain

- `compileSdk` 34, `targetSdk` 33, `minSdk` 26
- Java 8 source/target (Kotlin `jvmTarget = "1.8"`)
- AGP 9.2.1, Gradle 9.4.1, Kotlin 2.2.10 (kapt for Hilt + Room — KSP migration is a follow-up)
- Repos: `google()` + `mavenCentral()` (jcenter removed)
- Daemon JVM pinned to JDK 17 via `gradle/gradle-daemon-jvm.properties`; `org.gradle.toolchains.foojay-resolver-convention` 0.10.0 enabled in `settings.gradle.kts`
- DataBinding *and* ViewBinding both enabled (`app/build.gradle.kts:56-60`) — both slated for removal in the Compose migration
- Multidex dropped — `minSdk = 26` makes the 64K dex limit a non-issue
- ProGuard/R8 disabled in release (`isMinifyEnabled = false`)
- `gradle.properties` carries AGP-9-upgrade-utility stability flags pinning AGP-7-era behavior (`android.nonTransitiveRClass=false`, `android.newDsl=false`, etc.). These are deprecated and emit warnings; they should be flipped one at a time in follow-up issues.

Current direct deps (canonical versions live in `gradle/libs.versions.toml`):

- **Hilt** 2.56.2 (DI; `@HiltAndroidApp`, `@HiltViewModel`, `@AndroidEntryPoint`, `@Binds`/`@Provides`/`@InstallIn(SingletonComponent::class)`)
- **Coroutines** `kotlinx-coroutines-android` 1.8.1 + `-test`
- **Lifecycle** 2.8.7 (`runtime-ktx` + `viewmodel-ktx`; LiveData artifact intentionally absent)
- **Retrofit** 2.11.0 + `converter-kotlinx-serialization` + `okhttp-logging-interceptor`
- **kotlinx.serialization** 1.7.3 (`Json { ignoreUnknownKeys = true; explicitNulls = false }`)
- **Room** 2.7.2 (DAO returns `Flow<…>`; writes are `suspend`)
- **Coil** 2.7.0 (hidden behind `ui.image.ImageLoader` interface)
- **Activity** 1.9.3 (for `by viewModels()` reaching Hilt's factory)
- **Timber** 4.7.1 (still on the legacy version because `WrongTimberUsageDetector` is `LintError`-disabled — bump deferred until a structured-logging migration)

---

## 7. Current architecture

MVVM + single-Repository, DI via **Hilt**. The data, DI, imaging, and JSON layers all sit on the target stack; only the UI layer (XML + Activities) is still legacy — see "Still to migrate" at the end of this section.

```
@AndroidEntryPoint Activity → @HiltViewModel ViewModel → MovieRepository (interface)
                                                          └─► MovieRepositoryImpl
                                                              ├─► NetworkDataSource (Retrofit suspend → TMDb)
                                                              └─► LocalDataSource (Room: favorite_movies.db, Flow + suspend)
```

- `MoviesApp` is `@HiltAndroidApp`. There is no manual `Component` build any more — Hilt manages `SingletonComponent` automatically. The legacy `di/component/`, `di/scopes/`, `di/qualifier/` packages and `ContextModule` are all deleted.
- Modules live in `di/module/` and use `@Module @InstallIn(SingletonComponent::class)`:
  - `NetworkModule` (Retrofit + `kotlinx.serialization.Json` + `converter-kotlinx-serialization`, `@Singleton`).
  - `OkHttpClientModule` (`@Singleton`).
  - `FavoriteDatabaseModule` (Room AppDatabase + MovieDao via `@ApplicationContext Context`).
  - `RepositoryModule` (`@Binds @Singleton bindMovieRepository(impl: MovieRepositoryImpl): MovieRepository`) — the headline abstraction-principle binding.
  - `NetworkMonitorModule` (`@Provides @Singleton networkMonitor(@ApplicationContext)` — builds the `ConnectivityManager` + `NetworkRequest` and returns `ConnectivityManagerNetworkMonitor`).
- ViewModels are constructor-injected (`@HiltViewModel class FooViewModel @Inject constructor(repository: MovieRepository, ...)`). Activities use `by viewModels<FooViewModel>()`. **No more `*ViewModelFactory` classes**, and no more `(application as MoviesApp).movieComponent.getAppRepository()` Demeter chains — both gone with #21.
- Per-screen runtime arguments (e.g. `DetailViewModel.movieId`) ride `SavedStateHandle`. `ComponentActivity.defaultViewModelCreationExtras` promotes Intent extras to the SavedStateHandle, so `DetailActivity` putting a `Movie` Parcelable under `MainActivity.MOVIE_OBJECT` is all the wiring needed — the VM reads it via `savedStateHandle.get<Movie>(MOVIE_OBJECT)`. **Never read `Intent` extras from inside a ViewModel directly** — go through `SavedStateHandle`.
- `MovieRepositoryImpl`: network reads are `suspend fun` returning `List<T>` (the unwrap-then-map from `MoviesList`/`ReviewsList`/`TrailerList` happens inside `NetworkDataSource`, which then maps `MovieDto → Movie` via `data/Mappers.kt`). Room reads are `Flow<List<Movie>>` / `Flow<Movie?>` (`LocalDataSource` maps `MovieEntity → Movie`). Writes are `suspend fun`. **No `LiveData`, no `CompositeDisposable`, no `clearDisposables`** — cancellation rides `viewModelScope`.
- ViewModels expose **`StateFlow<T>` only**. One-shot network results use `MutableStateFlow<T>` updated from `viewModelScope.launch { runCatching { … }.onSuccess { _state.value = it }.onFailure { Timber.w(…) } }`; reactive Room reads use `repository.flow.stateIn(viewModelScope, WhileSubscribed(5_000), initialValue)`. **Never re-introduce `LiveData` exposure.**
- Activities collect VM `StateFlow`s with `lifecycleScope.launch { repeatOnLifecycle(STARTED) { vm.flow.collect { … } } }`. **No `.observe(this)` callsites.** When a single drawer-style screen flips between multiple flow sources (see `MainActivity.bindMovies`), keep a `currentJob: Job?` field and cancel/replace it on path change.
- `Movie` is the **domain type** only — a `@Parcelize data class` in `data.model`. Room uses `MovieEntity` (`data.local`, `@Entity(tableName = "favorite_movies")` with an auto-generated PK + unique `tmdb_id` column). Network uses `MovieDto` (`data.model`, `@Serializable`). Mappers (`data/Mappers.kt`) translate at the data-source edge; **domain code never sees `MovieDto` or `MovieEntity`**. The favorite-by-id lookup queries `WHERE tmdb_id = :tmdbId`.
- **Comparing `Movie` instances: use `movieId` only, not `==`.** The same logical TMDb movie has `isFavorite = false` when it came from the network DTO mapper and `isFavorite = true` when it came from the Room entity mapper, so full data-class equality returns false for genuinely-equal entities. The fix in `DetailActivity` (post-#27) is the canonical example.
- Image loading: `ui.image.ImageLoader` interface + `CoilImageLoader` impl; `MoviesApp` implements `ImageLoaderHost` so the two `@BindingAdapter`s (`posterUrl`, `backDropUrl`) reach the loader via `(context.applicationContext as ImageLoaderHost).imageLoader.load(...)`. **Only `CoilImageLoader.kt` imports `coil.*`** — never reference Coil directly from UI / VM / adapter callsites. Compose migration swaps the impl for a Composable wrapper around `coil.compose.AsyncImage`.
- Logging: Timber, planted only in debug, with tag `Constants.TAG = "MyApp"`.
- Connectivity: `NetworkMonitor` (`data/network/NetworkMonitor.kt`) exposes `Flow<Boolean>` via `callbackFlow` + `ConnectivityManager.NetworkCallback`. Used by `MainViewModel.isOnline: StateFlow<Boolean>` (collected by `MainActivity` with `repeatOnLifecycle(STARTED)`). Primary constructor takes injected `ConnectivityManager?` + `NetworkRequest` (Hilt-provided in production via `NetworkMonitorModule`; tests pass mocks directly). **Never re-introduce synchronous `isNetworkConnected(context)` polling** — observe the flow.

### Still to migrate

- **UI → Compose + Navigation-Compose.** Two Activities (`MainActivity` RecyclerView grid + `DetailActivity`), `R.layout.*` XML, DataBinding `@BindingAdapter`s, ViewBinding flag. Sort mode (`"Popular"`/`"Top Rated"`/`"Favorites"`) is a `String` saved via `onSaveInstanceState` — that becomes `SavedStateHandle` / nav arg in the Compose VM. Both activities collapse into one `ComponentActivity` hosting a `NavHost`. Drop DataBinding + ViewBinding from `buildFeatures`. Use `coil.compose.AsyncImage` to retire the `@BindingAdapter` indirection.
- **UiState consolidation** per screen — fold the per-collection `StateFlow<List<Movie>>`s in `MainViewModel` and the `reviews` / `trailers` / `favoriteMovie` triple in `DetailViewModel` into a single immutable `UiState` data class per screen.
- **KSP** for both Hilt and Room compilers (currently kapt).
- **Spotless + Detekt** in the CI pipeline.
- **`build-logic/` convention plugins** to retire the repeated config across modules (currently a single-module app, but laying the foundation now keeps the eventual multi-module split mechanical).
- **AGP-7 stability flags** in `gradle.properties` (`android.nonTransitiveRClass=false`, `android.newDsl=false`, etc.) flipped on by one to clear the deprecation warnings.

---

## 8. CI

GitHub Actions at `.github/workflows/ci.yml` (added as part of the modernization). Runs on push to `master` and on every PR. Pipeline:

1. JDK 17 (Temurin)
2. Gradle cache restore
3. `./gradlew spotlessCheck detekt lint testDebugUnitTest assembleDebug` (plus `koverVerify` once thresholds are set)
4. Uploads test + lint reports as artifacts on failure

The TMDb key is injected as the `TMDB_API_KEY` GitHub secret and re-exported as `ORG_GRADLE_PROJECT_myTMDBApiKey` (with the surrounding quotes the legacy `buildConfigField` requires) in the workflow `env`. Do not log the key. Do not echo `gradle.properties` in CI logs.

---

## 9. Skills & external references the assistant should consult

- **`github.com/android/skills`** — Android-team skill packs. Use proactively for the matching task:
  - `migrate-xml-views-to-jetpack-compose` — for any XML layout → Compose migration (this repo has many).
  - `agp-9-upgrade` — for the AGP 4 → 9 jump.
  - `r8-analyzer` — when enabling R8 in release.
  - `edge-to-edge` — when adopting edge-to-edge in Compose.
  - `camera1-to-camerax`, `play-billing-library-version-upgrade`, `navigation-3` — situational.
- **"7 Gradle Kotlin DSL tricks for human-friendly builds"** (Modexa, Medium) — guiding patterns for the Gradle migration:
  1. Version catalog (`gradle/libs.versions.toml`) — references like `libs.androidx.compose.ui` not strings.
  2. Convention plugins in `build-logic/` (e.g. `popularmovies.android.application.gradle.kts`).
  3. Type-safe project accessors (`projects.feature.detail`).
  4. Configuration avoidance (`tasks.register` not `tasks.create`, lazy `Provider<T>`).
  5. Typed Gradle extensions over `extra`.
  6. Platform/BOM + bundles for coherent transitive versions.
  7. Small English-reading helpers (e.g. `Project.env(name)`).
- **Now in Android** sample (`github.com/android/nowinandroid`) — canonical reference for the target architecture (Hilt + Compose + Coroutines + Room/DataStore + multi-module + version catalog + convention plugins). When in doubt, mirror its conventions.
