/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */

package com.example.android.popularmovies2.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReviewsList {
    @SerializedName("id")
    private Integer movieId;

    @SerializedName("page")
    private Integer page;

    @SerializedName("results")
    private List<Review> reviews = null;

    @SerializedName("total_pages")
    private Integer totalPages;

    @SerializedName("total_results")
    private Integer totalReviews;

    public Integer getMovieId() {
        return movieId;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    // Use this to get list of reviews.
    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(Integer totalReviews) {
        this.totalReviews = totalReviews;
    }
}
