/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */
package com.example.android.popularmovies2.data.network

import com.example.android.popularmovies2.BuildConfig
import com.example.android.popularmovies2.data.model.Movie
import com.example.android.popularmovies2.data.model.Review
import com.example.android.popularmovies2.data.model.Trailer
import com.example.android.popularmovies2.data.toDomain
import javax.inject.Inject
import javax.inject.Singleton
import retrofit2.Retrofit

@Singleton
class NetworkDataSource @Inject constructor(
    retrofit: Retrofit,
) {
    private val apiKey: String = BuildConfig.TMDB_API_KEY
    private val movieApi: MovieApi = retrofit.create(MovieApi::class.java)

    suspend fun popularMovies(): List<Movie> =
        movieApi.getMoviesByPath(POPULAR, apiKey).results.map { it.toDomain() }

    suspend fun topRatedMovies(): List<Movie> =
        movieApi.getMoviesByPath(TOP_RATED, apiKey).results.map { it.toDomain() }

    suspend fun reviews(movieId: String): List<Review> =
        movieApi.getReviews(movieId, apiKey).results

    suspend fun trailers(movieId: String): List<Trailer> =
        movieApi.getTrailers(movieId, apiKey).results

    private companion object {
        const val POPULAR = "popular"
        const val TOP_RATED = "top_rated"
    }
}
