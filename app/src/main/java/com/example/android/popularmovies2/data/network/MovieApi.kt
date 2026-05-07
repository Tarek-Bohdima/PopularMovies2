/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */
package com.example.android.popularmovies2.data.network

import com.example.android.popularmovies2.data.model.MoviesList
import com.example.android.popularmovies2.data.model.ReviewsList
import com.example.android.popularmovies2.data.model.TrailerList
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApi {
    @GET(PATH)
    fun getMoviesByPath(
        @Path("path") path: String,
        @Query(API_PARAMETER) key: String,
    ): Single<MoviesList>

    @GET("{id}/$REVIEWS")
    fun getReviews(
        @Path("id") movieId: String,
        @Query(API_PARAMETER) key: String,
    ): Single<ReviewsList>

    @GET("{id}/$VIDEOS")
    fun getTrailers(
        @Path("id") movieId: String,
        @Query(API_PARAMETER) key: String,
    ): Single<TrailerList>

    companion object {
        const val API_PARAMETER = "api_key"
        const val PATH = "{path}"
        const val REVIEWS = "reviews"
        const val VIDEOS = "videos"
    }
}
