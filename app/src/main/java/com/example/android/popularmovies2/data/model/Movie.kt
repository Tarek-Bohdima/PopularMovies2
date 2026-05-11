/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */
package com.example.android.popularmovies2.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * UI/ViewModel-facing domain type for a movie. Carries no Room or kotlinx.serialization
 * annotations — those live on `MovieEntity` (Room) and `MovieDto` (wire), with the
 * mapping in `data.Mappers`.
 *
 * `movieId` is TMDb's id (the API's `"id"` field). It is **not** a Room primary key —
 * the favorites table has its own auto-generated primary key plus a unique `tmdb_id`
 * column.
 */
@Parcelize
data class Movie(
    val movieId: Int,
    val originalTitle: String,
    val posterPath: String,
    val backdropPath: String,
    val overview: String,
    val voteAverage: Double,
    val releaseDate: String,
    val isFavorite: Boolean = false,
) : Parcelable
