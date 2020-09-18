/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */

package com.example.android.popularmovies2;

import android.app.Application;

import com.example.android.popularmovies2.di.component.DaggerMovieComponent;
import com.example.android.popularmovies2.di.component.MovieComponent;
import com.example.android.popularmovies2.di.module.NetworkModule;

import timber.log.Timber;

public class MoviesApp extends Application {

    private static String BASE_URL = "https://api.themoviedb.org/3/movie/";
    private MovieComponent movieComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        movieComponent = DaggerMovieComponent.builder()
                .networkModule(new NetworkModule(BASE_URL))
                .build();
    }

    public MovieComponent getMovieComponent() {
        return movieComponent;
    }
    /*public AppRepository getAppRepository() {
        Timber.tag("MyApp").d("MoviesApp: getAppRepository");
        return AppRepository.getInstance(this);
    }*/
}
