/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */

package com.example.android.popularmovies2.ui.detail;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.android.popularmovies2.MoviesApp;
import com.example.android.popularmovies2.data.AppRepository;
import com.example.android.popularmovies2.data.model.Review;
import com.example.android.popularmovies2.data.model.Trailer;

import java.util.List;

public class DetailViewModel extends ViewModel {

    private LiveData<List<Review>> reviews;
    private LiveData<List<Trailer>> trailers;


    public DetailViewModel(Application application, String movieId) {
        AppRepository appRepository = ((MoviesApp) application).getMovieComponent().getAppRepository();
        reviews = appRepository.getReviewsByMovieId(movieId);
        trailers = appRepository.getTrailersByMovieId(movieId);
    }

    public LiveData<List<Review>> getReviewsByMovieId(String movieId) {
        return reviews;
    }

    public LiveData<List<Trailer>> getTrailersByMovieId(String movieId) {
        return trailers;
    }
}
