# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

> **Documentation continuity rule.** This file is the cross-account anchor. Every working preference, decision, or convention agreed in conversation MUST land here so a cold session in any Claude Code account can pick up where the previous one left off. When the user states a new rule, update this file in the same turn — do not rely on per-account memory alone.

---

## 1. Project context

Android app — Udacity Android Developer Nanodegree "Popular Movies" Stage 2 submission. Browses popular/top-rated movies from TMDb, plus a local "favorites" collection with trailers and reviews on the detail screen. Every legacy source file carries an Udacity-honor-code header stating the code is reference-only; preserve that header on edits to existing files. New files may use a simpler license header or none.

**The repository is mid-modernization.** Legacy code is Java + Dagger 2 + RxJava 3 + LiveData + DataBinding + ViewBinding on AGP 4.1. The agreed target stack is below — write new code against the target, migrate legacy code task-by-task with a tracking issue per migration.

---

## 2. Target stack (Google-recommended, agreed conventions)

| Concern | Target | Replaces (legacy) |
|---|---|---|
| Language | **Kotlin (idiomatic)** | Java |
| UI | **Jetpack Compose** | XML layouts + DataBinding/ViewBinding |
| Architecture | **MVVM + UDF** (immutable `UiState` data class, `StateFlow` from `ViewModel`, state hoisted into Composables) | LiveData-driven Activities |
| Async | **Coroutines + Flow / StateFlow** — no `Single`/`Observable`, no `LiveData` | RxJava 3, LiveData |
| DI | **Hilt** | Dagger 2 (manual `@Component`) |
| Annotation processing | **KSP** | kapt / `annotationProcessor` |
| Build | **Gradle Kotlin DSL** + **`gradle/libs.versions.toml`** version catalog + convention plugins in `build-logic/` | Groovy `build.gradle` with hardcoded versions |
| Coverage | **Kover** | — |
| Format | **Spotless** (with ktlint) | — |
| Static analysis | **Detekt** | Android Lint only |
| Image loading | **Coil** (Compose-native) | Glide |
| Navigation | **Navigation-Compose** (or Navigation 3 when stable — see android/skills `navigation-3`) | Activity-to-Activity Intents |
| Local persistence (structured) | Room (keep) — but expose Flows, not LiveData | Room with LiveData DAO methods |
| Local persistence (key/value) | **DataStore** (Preferences DataStore for simple key/value, Proto DataStore for typed). **Forbidden: `SharedPreferences`, `PreferenceManager`, `getDefaultSharedPreferences`, anything in `androidx.preference:*`.** | SharedPreferences |
| Networking | Retrofit + OkHttp + **kotlinx.serialization** + coroutine `suspend` functions | Retrofit + Gson + RxJava `Single` |
| JSON | **kotlinx.serialization** (compile-time, no reflection). If Moshi is unavoidable in some module, use **Moshi codegen** (`com.squareup.moshi:moshi-kotlin-codegen` via KSP) — never the reflective `KotlinJsonAdapterFactory`. | Gson |

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
./gradlew :app:testDebugUnitTest --tests "com.example.android.popularmovies2.ExampleUnitTest.addition_isCorrect"   # single test
./gradlew connectedAndroidTest         # instrumented tests (needs device/emulator)
./gradlew lint                         # Android Lint
./gradlew clean
```

Once Detekt/Spotless/Kover are wired:

```bash
./gradlew detekt                       # static analysis
./gradlew spotlessCheck                # format check
./gradlew spotlessApply                # auto-fix formatting
./gradlew koverHtmlReport              # coverage HTML at app/build/reports/kover/
./gradlew koverVerify                  # fail build if coverage < threshold
```

---

## 6. Current SDK / toolchain

- `compileSdk` 34, `targetSdk` 33, `minSdk` 21
- Java 8 source/target (Kotlin `jvmTarget = "1.8"`)
- AGP 9.2.1, Gradle 9.4.1, Kotlin 2.2.10 (kapt still in use; KSP migration is a follow-up)
- Repos: `google()` + `mavenCentral()` (jcenter removed)
- Daemon JVM pinned to JDK 17 via `gradle/gradle-daemon-jvm.properties`; `org.gradle.toolchains.foojay-resolver-convention` 0.10.0 enabled in `settings.gradle.kts`
- DataBinding *and* ViewBinding both enabled (`app/build.gradle.kts:56-60`) — both slated for removal in the Compose migration
- Multidex dropped — `minSdk = 21` makes the 64K dex limit a non-issue
- ProGuard/R8 disabled in release (`isMinifyEnabled = false`)
- `gradle.properties` carries AGP-9-upgrade-utility stability flags pinning AGP-7-era behavior (`android.nonTransitiveRClass=false`, `android.newDsl=false`, etc.). These are deprecated and emit warnings; they should be flipped one at a time in follow-up issues.

Bumped together with AGP 9 to avoid Kotlin-2.2-metadata kapt failures: Dagger 2.50 → 2.56.2, Room 2.2.5 → 2.7.2, Glide 4.11.0 → 4.16.0. Each is still slated for replacement (Hilt / Coroutines+Flow / Coil) per §2.

---

## 7. Legacy architecture (what's there now)

MVVM + single-Repository, DI via Dagger 2 (no Hilt). Replace lane-by-lane.

```
Activity → ViewModel → AppRepository ──► NetworkDataSource (Retrofit+RxJava3 → TMDb)
                                    └─► LocalDataSource (Room: favorite_movies.db)
```

- One `@ApplicationScope` `MovieComponent` is built in `MoviesApp.onCreate()` and exposes only `getAppRepository()`.
- ViewModels are **not** injected — they grab the repository via `((MoviesApp) application).getMovieComponent().getAppRepository()`. After Hilt migration, this pattern disappears (`@HiltViewModel` + constructor injection).
- `AppRepository` returns `LiveData` for everything; network calls return `MutableLiveData` posted from RxJava `Single` subscriptions inside `NetworkDataSource`. Disposables are managed via `CompositeDisposable` cleared on `ViewModel.onCleared`. **Migration target:** repository methods become `suspend fun` / `Flow<T>`; cancellation comes free with the ViewModel's `viewModelScope`.
- Room writes are dispatched through `AppExecutors.getInstance().diskIO()` from the repository because DAO methods are not annotated as suspending. **Migration target:** DAO `suspend` + `Flow`; delete `AppExecutors`.
- `Movie` is **both** the Room `@Entity` (table `favorite_movies`) and the Gson DTO (`@SerializedName("id")` + `@PrimaryKey(autoGenerate = true)` on the same field). When migrating, **split** into `MovieEntity` (Room) and `MovieDto` (network) with a mapper.
- Two activities: `MainActivity` (RecyclerView grid, span 2 portrait / 4 landscape) and `DetailActivity`. Sort mode (`"Popular"`/`"Top Rated"`/`"Favorites"`) is a `String` saved via `onSaveInstanceState`. Both activities will be replaced by Compose screens hosted in a single `ComponentActivity` with `NavHost`.
- Image loading uses Glide via two `@BindingAdapter`s (`posterUrl`, `backDropUrl`). Replaced by `coil.compose.AsyncImage`.
- Logging: Timber, planted only in debug, with tag `Constants.TAG = "MyApp"`.

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
