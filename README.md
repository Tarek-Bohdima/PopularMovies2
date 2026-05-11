# Popular Movies

A small Android app that browses popular and top-rated movies from [The Movie DB][tmdb], lets you read reviews and play trailers on the detail screen, and bookmark favorites locally — viewable offline.

> _This product uses the TMDb API but is not endorsed or certified by TMDb._

<p>
  <img src="ScreenShots/01-popular-portrait.png" width="220" alt="Popular grid">
  <img src="ScreenShots/04-detail-portrait.png" width="220" alt="Detail screen">
  <img src="ScreenShots/03-favorites-portrait.png" width="220" alt="Favorites grid">
</p>

## What it does

- **Browse** popular and top-rated movie posters in a grid (2 columns portrait, 4 landscape).
- **Open a detail screen** showing title, release date, vote, overview, trailers, and reviews.
- **Favorite** a movie with the heart icon. Favorites persist locally in Room and stay accessible while offline.
- **Reacts to connectivity changes live** — losing signal mid-session swaps to the offline UI within seconds; regaining signal auto-reloads the grid.

## Tech stack

Every cross-layer dependency is an interface; the concrete is bound in a Hilt module. ViewModels mock against the interface. See [CLAUDE.md](./CLAUDE.md) for the full decision set and conventions (including the Law-of-Demeter rule and the identity-vs-equality rule that informed the data-model split).

| Concern | Choice |
|---|---|
| Language / SDK | Kotlin 2.2 · `minSdk` 26 · `compileSdk` 34 |
| Build | Gradle 9.4 (Kotlin DSL) · version catalog (`gradle/libs.versions.toml`) · AGP 9.2 |
| DI | **Hilt** 2.56 |
| Async | **Coroutines + Flow / StateFlow** — no RxJava, no LiveData |
| Networking | Retrofit 2.11 `suspend` + OkHttp + `converter-kotlinx-serialization` |
| JSON | **kotlinx.serialization** 1.7 (compile-time, no reflection) |
| Local persistence | Room 2.7 — DAO reads return `Flow`, writes are `suspend` |
| Image loading | **Coil** 2.7, hidden behind a `ui.image.ImageLoader` interface |
| Connectivity | `NetworkMonitor` interface wrapping `ConnectivityManager.NetworkCallback` as `Flow<Boolean>` |
| Coverage | **Kover** (≥60% line gate in CI) |
| UI (last legacy lane) | XML layouts + DataBinding/ViewBinding + two Activities — Compose + Navigation-Compose migration pending |

## Architecture

```
@AndroidEntryPoint Activity
        │
        ▼  (by viewModels())
@HiltViewModel MainViewModel / DetailViewModel
        │
        ▼  (constructor injection)
MovieRepository  (interface)
        │
        ▼
MovieRepositoryImpl
        ├─► NetworkDataSource ──► Retrofit suspend  ──► TMDb API
        │                          (MovieDto → Movie at the data-source edge)
        └─► LocalDataSource  ──► Room Flow / suspend ──► favorite_movies.db
                                   (MovieEntity → Movie at the data-source edge)
```

`Movie` is a `@Parcelize` domain type with no Room or kotlinx.serialization annotations on it; `MovieEntity` and `MovieDto` are package-internal concerns mapped via `data/Mappers.kt`. The repository public surface speaks domain types only.

## Build

You need a TMDb v3 API key. Sign up at [themoviedb.org][tmdb-signup] (free) and grab a v3 key from your account's "API" page.

The build reads the key from a Gradle property called `myTMDBApiKey` and emits it as `BuildConfig.TMDB_API_KEY`. Add it **once** to your **user-level** Gradle properties — never to the repo's `gradle.properties`:

| OS | File |
|---|---|
| macOS / Linux | `~/.gradle/gradle.properties` |
| Windows | `%USERPROFILE%\.gradle\gradle.properties` |

```properties
myTMDBApiKey="<your-v3-key-here>"
```

The surrounding double-quotes are required — the value is interpolated verbatim into a Java string literal by `buildConfigField`.

Then:

```bash
./gradlew installDebug          # build + push to a connected device/emulator
./gradlew test                  # JVM unit tests
./gradlew koverHtmlReport       # coverage HTML at app/build/reports/kover/html/
./gradlew koverVerify           # fail if line coverage < 60%
```

For the full list of supported tasks, see [CLAUDE.md §5](./CLAUDE.md#5-common-commands).

## Project history

The original 2020 codebase was a Udacity Android Developer Nanodegree _Stage 2: Trailers, Reviews, and Favorites_ submission. It was Java + Dagger 2 + RxJava 3 + LiveData + DataBinding + Glide + Gson on AGP 4.1. Since then the data, DI, JSON, and imaging layers have all been migrated to the Google-recommended target stack listed above; the UI layer (XML + Activities) is the last lane, scheduled for the Compose migration.

Per Udacity Honor Code, the original submission files carry a header noting the code is reference-only. Preserve those headers when editing existing files. New files written during the modernization do not require it.

## Screenshots

### Portrait

| Popular | Top Rated | Favorites |
| --- | --- | --- |
| <img src="ScreenShots/01-popular-portrait.png" width="240"> | <img src="ScreenShots/02-top-rated-portrait.png" width="240"> | <img src="ScreenShots/03-favorites-portrait.png" width="240"> |

| Detail | Trailers |
| --- | --- |
| <img src="ScreenShots/04-detail-portrait.png" width="240"> | <img src="ScreenShots/05-trailers-portrait.png" width="240"> |

### Landscape

| Popular | Top Rated |
| --- | --- |
| <img src="ScreenShots/06-popular-landscape.png" width="480"> | <img src="ScreenShots/07-top-rated-landscape.png" width="480"> |

| Favorites | Detail |
| --- | --- |
| <img src="ScreenShots/08-favorites-landscape.png" width="480"> | <img src="ScreenShots/09-detail-landscape.png" width="480"> |

[tmdb]: https://www.themoviedb.org/
[tmdb-signup]: https://developers.themoviedb.org/3/getting-started/introduction
