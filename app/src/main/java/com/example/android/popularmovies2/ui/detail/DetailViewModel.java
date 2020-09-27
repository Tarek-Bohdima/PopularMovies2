/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */

package com.example.android.popularmovies2.ui.detail;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.android.popularmovies2.BuildConfig;
import com.example.android.popularmovies2.MoviesApp;
import com.example.android.popularmovies2.data.network.MovieApi;

import retrofit2.Retrofit;
import timber.log.Timber;

public class DetailViewModel extends AndroidViewModel {

    String MY_TMDB_API_KEY = BuildConfig.TMDB_API_KEY;
    String movieId;
    private Retrofit retrofit;

    public DetailViewModel(@NonNull Application application) {
        super(application);
        Timber.tag("MyApp").d("DetailViewModel: get AppRepository instance");

        retrofit = ((MoviesApp) getApplication()).getMovieComponent().getRetrofit();
    }

    public void getTrailersOnMovie() {
        MovieApi movieApi = retrofit.create(MovieApi.class);
//        Call<TrailerList> callTrailersByMovieId = movieApi.getTrailers()

    }

}
