/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */

package com.example.android.popularmovies2.ui.list;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.android.popularmovies2.MoviesApp;
import com.example.android.popularmovies2.data.AppRepository;
import com.example.android.popularmovies2.data.model.Movie;

import java.util.List;

public class MainActivityViewModel extends AndroidViewModel {

    private static final String TAG = MainActivityViewModel.class.getSimpleName();

    MutableLiveData<List<Movie>> popularMovies1 = new MutableLiveData<>();
    MutableLiveData<List<Movie>> topMovies1 = new MutableLiveData<>();

    public MainActivityViewModel(@NonNull Application application) {
        super(application);

        AppRepository appRepository = ((MoviesApp) getApplication()).getAppRepository();
        Log.d(TAG, "MainActivityViewModel: get AppRepository instance");


        popularMovies1.setValue(appRepository.getPopularMovies());
                
        topMovies1.setValue(appRepository.getTopRatedMovies());

    }

    public LiveData<List<Movie>> getPopularMovies() {
        return popularMovies1;
    }

    public LiveData<List<Movie>> getTopRatedMovies() {
        return topMovies1;
    }
}
