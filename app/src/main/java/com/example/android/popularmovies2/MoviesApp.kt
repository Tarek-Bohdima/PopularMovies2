/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */
package com.example.android.popularmovies2

import android.app.Application
import com.example.android.popularmovies2.di.component.DaggerMovieComponent
import com.example.android.popularmovies2.di.component.MovieComponent
import com.example.android.popularmovies2.di.module.ContextModule
import com.example.android.popularmovies2.di.module.NetworkModule
import com.example.android.popularmovies2.di.module.OkHttpClientModule
import timber.log.Timber

class MoviesApp : Application() {
    lateinit var movieComponent: MovieComponent
        private set

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        movieComponent = DaggerMovieComponent.builder()
            .networkModule(NetworkModule(BASE_URL))
            .contextModule(ContextModule(this))
            .okHttpClientModule(OkHttpClientModule())
            .build()
    }

    private companion object {
        const val BASE_URL = "https://api.themoviedb.org/3/movie/"
    }
}
