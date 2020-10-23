/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */

package com.example.android.popularmovies2.data.network;

import com.example.android.popularmovies2.data.model.MoviesList;
import com.example.android.popularmovies2.data.model.ReviewsList;
import com.example.android.popularmovies2.data.model.TrailerList;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieApi {

    String API_PARAMETER = "api_key";
    String PATH = "{path}";
    String REVIEWS = "reviews";
    String VIDEOS = "videos";

    @GET(PATH)
    Single<MoviesList> getMoviesByPath(@Path("path") String path,
                                       @Query(API_PARAMETER) String key);

    @GET("{id}/" + REVIEWS)
    Single<ReviewsList> getReviews(@Path("id") String movieId,
                                   @Query(API_PARAMETER) String key);

    @GET("{id}/" + VIDEOS)
    Single<TrailerList> getTrailers(@Path("id") String movieId,
                                    @Query(API_PARAMETER) String key);
}
