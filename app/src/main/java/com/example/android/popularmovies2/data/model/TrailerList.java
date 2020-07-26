/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */

package com.example.android.popularmovies2.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrailerList {

    @SerializedName("id")
    private Integer movieId;

    @SerializedName("results")
    private List<Trailer> trailers = null;

    public Integer getMovieId() {
        return movieId;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }

    // use this to get list of trailers.
    public List<Trailer> getTrailers() {
        return trailers;
    }

    public void setTrailers(List<Trailer> trailers) {
        this.trailers = trailers;
    }
}
