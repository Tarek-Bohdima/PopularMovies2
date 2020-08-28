/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */

package com.example.android.popularmovies2.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.android.popularmovies2.data.model.Movie;
import com.example.android.popularmovies2.data.network.NetworkDataSource;

import java.util.List;

import timber.log.Timber;

public class AppRepository {
    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static AppRepository sInstance;

    private NetworkDataSource networkDataSource;

    private AppRepository(Application application) {
        networkDataSource = NetworkDataSource.getInstance(application);
    }

    public synchronized static AppRepository getInstance(Application application) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Timber.tag("MyApp").d("AppRepository: getInstance: Creating new repository instance");
                sInstance = new AppRepository(application);
            }
        }
        return sInstance;
    }


    public LiveData<List<Movie>> getPopularMovies() {
        Timber.tag("MyApp").d("AppRepository: getPopularMovies");
        return networkDataSource.getPopularMoviesLiveData();
    }

    public LiveData<List<Movie>> getTopRatedMovies() {
        Timber.tag("MyApp").d("AppRepository: getTopRatedMovies");
        return networkDataSource.getTopRatedMoviesLiveData();
    }
}
