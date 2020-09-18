/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */

package com.example.android.popularmovies2.ui.list;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.android.popularmovies2.MoviesApp;
import com.example.android.popularmovies2.data.AppRepository;
import com.example.android.popularmovies2.data.model.Movie;

import java.util.List;

import timber.log.Timber;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<Movie>> popularMoviesLiveData;
    private LiveData<List<Movie>> topRatedMoviesLiveData;

    public MainViewModel(@NonNull Application application) {
        super(application);
        AppRepository appRepository = ((MoviesApp) getApplication()).getMovieComponent().getAppRepository();
        Timber.tag("MyApp").d("MainViewModel: get AppRepository instance");

        popularMoviesLiveData = appRepository.getPopularMovies();
        topRatedMoviesLiveData = appRepository.getTopRatedMovies();
    }

    public LiveData<List<Movie>> getPopularMovies() {
        Timber.tag("MyApp").d("MainViewModel: getPopularMovies");
        return popularMoviesLiveData;
    }

    public LiveData<List<Movie>> getTopRatedMovies() {
        Timber.tag("MyApp").d("MainViewModel: getTopRatedMovies");
        return topRatedMoviesLiveData;
    }
}
