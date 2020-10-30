/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */

package com.example.android.popularmovies2.ui.detail;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.android.popularmovies2.Constants;
import com.example.android.popularmovies2.MoviesApp;
import com.example.android.popularmovies2.data.AppRepository;
import com.example.android.popularmovies2.data.model.Movie;
import com.example.android.popularmovies2.data.model.Review;
import com.example.android.popularmovies2.data.model.Trailer;

import java.util.List;

import timber.log.Timber;

public class DetailViewModel extends ViewModel {

    private final LiveData<List<Review>> reviews;
    private final LiveData<List<Trailer>> trailers;
    private final LiveData<Movie> favouriteMovie;


    public DetailViewModel(Application application, String movieId) {

        AppRepository appRepository = ((MoviesApp) application).getMovieComponent().getAppRepository();
        reviews = appRepository.getReviewsByMovieId(movieId);
        trailers = appRepository.getTrailersByMovieId(movieId);
        favouriteMovie = appRepository.getFavouriteMovieById(movieId);
    }

    public LiveData<List<Review>> getReviewsByMovieId() {
        Timber.tag(Constants.TAG).d("DetailViewModel: getReviewsByMovieId() called");
        return reviews;
    }

    public LiveData<List<Trailer>> getTrailersByMovieId() {
        Timber.tag(Constants.TAG).d("DetailViewModel: getTrailersByMovieId() called");
        return trailers;
    }

    public LiveData<Movie> getFavouriteMovieById() {
        Timber.tag(Constants.TAG).d("DetailViewModel: getFavouriteMovieById() called");
        return favouriteMovie;
    }
}
