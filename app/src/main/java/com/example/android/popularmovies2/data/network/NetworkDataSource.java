/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */

package com.example.android.popularmovies2.data.network;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.android.popularmovies2.AppExecutors;
import com.example.android.popularmovies2.BuildConfig;
import com.example.android.popularmovies2.data.model.Movie;
import com.example.android.popularmovies2.data.model.MoviesList;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class NetworkDataSource {


    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static NetworkDataSource sInstance;
    public MovieApi movieApi;
    // please acquire your API KEY from https://www.themoviedb.org/ then:
    // user your API key in the project's gradle.properties file: MY_TMDB_API_KEY="<your API KEY>"
    String MY_TMDB_API_KEY = BuildConfig.TMDB_API_KEY;
    private static final String POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";
    private MutableLiveData<List<Movie>> popularMovies = new MutableLiveData<>();
    private MutableLiveData<List<Movie>> topRatedMovies = new MutableLiveData<>();


    private AppExecutors executors;

    private NetworkDataSource(Application application) {

        /*Create handle for the RetrofitInstance interface*/
        movieApi = RetrofitClientInstance.getRetrofitInstance().create(MovieApi.class);
    }


    /**
     * Get the singleton for this class
     */
    public static NetworkDataSource getInstance(Application application) {
        Timber.tag("MyApp").d("NetworkDataSource: getting the network data source");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new NetworkDataSource(application);
                Timber.tag("MyApp").d("NetworkDataSource: Made new Network Data source");
            }
        }
        return sInstance;
    }

    private void getMoviesByPath(String path) {

        Call<MoviesList> callMoviesByPath = movieApi.getMoviesByPath(path, MY_TMDB_API_KEY);
        callMoviesByPath.enqueue(new Callback<MoviesList>() {
            @Override
            public void onResponse(Call<MoviesList> call, Response<MoviesList> response) {
                if (!response.isSuccessful()) {
                    /* TODO notify user about response error in UI */
                    // parse the response body …
                    APIError error = ErrorUtils.parseError(response);
                    Timber.tag("MyApp").d("NetworkDataSource: onResponse: %s", response.code());
                }
                MoviesList moviesLists = response.body();
                switch (path) {
                    case POPULAR:
                        Timber.tag("MyApp").d("NetworkDataSource: getPopularMovies from Retrofit");
                        popularMovies.setValue(moviesLists.getMovies());
                        break;
                    case TOP_RATED:
                        Timber.tag("MyApp").d("NetworkDataSource: getTopRatedMovies from Retrofit");
                        topRatedMovies.setValue(moviesLists.getMovies());
                }
            }

            @Override
            public void onFailure(Call<MoviesList> call, Throwable t) {
                Timber.tag("MyApp").d("onFailure: %s", t.getMessage());
            }
        });

    }

    public LiveData<List<Movie>> getPopularMoviesLiveData() {
        getMoviesByPath(POPULAR);
        Timber.tag("MyApp").d("NetworkDataSource: return PopularMovies LiveData");
        return popularMovies;
    }

    public LiveData<List<Movie>> getTopRatedMoviesLiveData() {
        getMoviesByPath(TOP_RATED);
        Timber.tag("MyApp").d("NetworkDataSource: return TopRatedMovies LiveData");
        return topRatedMovies;
    }
}
