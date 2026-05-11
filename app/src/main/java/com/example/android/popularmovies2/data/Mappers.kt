/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */
package com.example.android.popularmovies2.data

import com.example.android.popularmovies2.data.local.MovieEntity
import com.example.android.popularmovies2.data.model.Movie
import com.example.android.popularmovies2.data.model.MovieDto

/**
 * Single-source mappers between the three Movie shapes:
 *
 *  - [MovieDto] (`@Serializable`, wire) — what `NetworkDataSource` receives.
 *  - [MovieEntity] (`@Entity`, Room) — what `LocalDataSource` reads / writes.
 *  - [Movie] (`@Parcelize`, domain) — what ViewModels / UI hold.
 *
 * Domain code never sees `MovieDto` or `MovieEntity`. The repository takes / returns
 * `Movie`; data sources translate at the edge.
 */
internal fun MovieDto.toDomain(): Movie = Movie(
    movieId = tmdbId,
    originalTitle = originalTitle.orEmpty(),
    posterPath = posterPath.orEmpty(),
    backdropPath = backdropPath.orEmpty(),
    overview = overview.orEmpty(),
    voteAverage = voteAverage ?: 0.0,
    releaseDate = releaseDate.orEmpty(),
    isFavorite = false,
)

internal fun MovieEntity.toDomain(): Movie = Movie(
    movieId = tmdbId,
    originalTitle = originalTitle,
    posterPath = posterPath,
    backdropPath = backdropPath,
    overview = overview,
    voteAverage = voteAverage,
    releaseDate = releaseDate,
    isFavorite = true,
)

internal fun Movie.toEntity(): MovieEntity = MovieEntity(
    id = 0L,
    tmdbId = movieId,
    originalTitle = originalTitle,
    posterPath = posterPath,
    backdropPath = backdropPath,
    overview = overview,
    voteAverage = voteAverage,
    releaseDate = releaseDate,
)
