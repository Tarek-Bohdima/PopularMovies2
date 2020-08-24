/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */

package com.example.android.popularmovies2.data.network;

import android.app.Application;
import android.util.Log;

import com.example.android.popularmovies2.AppExecutors;
import com.example.android.popularmovies2.BuildConfig;
import com.example.android.popularmovies2.data.model.Movie;
import com.example.android.popularmovies2.data.model.MoviesList;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetworkDataSource {
    private static final String TAG = NetworkDataSource.class.getSimpleName();

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static NetworkDataSource sInstance;
    public MovieApi movieApi;
    // please acquire your API KEY from https://www.themoviedb.org/ then:
    // user your API key in the project's gradle.properties file: MY_TMDB_API_KEY="<your API KEY>"
    String MY_TMDB_API_KEY = BuildConfig.TMDB_API_KEY;
    String BASE_URL = "http://api.themoviedb.org/3/movie/";
    private static final String POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";

    private List<Movie> downloadedMovies = new ArrayList<>();
    private AppExecutors executors;

    private NetworkDataSource(Application application) {

                /*Create handle for the RetrofitInstance interface*/
        movieApi = RetrofitClientInstance.getRetrofitInstance().create(MovieApi.class);
    }


    /**
     * Get the singleton for this class
     */
    public static NetworkDataSource getInstance(Application application) {
        Log.d(TAG, "getting the network data source");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new NetworkDataSource(application);
                Log.d(TAG, "Made new Network Data source");
            }
        }
        return sInstance;
    }

    private List<Movie> getMoviesByPath(String path) {
        downloadedMovies = new ArrayList<>();
        Call<MoviesList> callMoviesByPath = movieApi.getMoviesByPath(path, MY_TMDB_API_KEY);
        callMoviesByPath.enqueue(new Callback<MoviesList>() {
            @Override
            public void onResponse(Call<MoviesList> call, Response<MoviesList> response) {
                if (!response.isSuccessful()) {
                    /* TODO notify user about response error in UI */
                    // parse the response body …
                    APIError error = ErrorUtils.parseError(response);
                    Log.d(TAG, "onResponse: " + response.code());
                }
                MoviesList moviesLists = response.body();
                downloadedMovies = moviesLists.getMovies();
            }

            @Override
            public void onFailure(Call<MoviesList> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });

        return downloadedMovies;
    }

    public List<Movie> getPopularMoviesLiveData() {
        return getMoviesByPath(POPULAR);
    }

    public List<Movie> getTopRatedMoviesLiveData() {
        return getMoviesByPath(TOP_RATED);
    }
}
