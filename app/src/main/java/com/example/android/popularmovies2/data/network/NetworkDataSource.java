/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */

package com.example.android.popularmovies2.data.network;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.android.popularmovies2.BuildConfig;
import com.example.android.popularmovies2.data.model.Movie;
import com.example.android.popularmovies2.data.model.MoviesList;
import com.example.android.popularmovies2.di.scopes.ApplicationScope;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import timber.log.Timber;

@ApplicationScope
public class NetworkDataSource {

    private static final String POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";
    String MY_TMDB_API_KEY = BuildConfig.TMDB_API_KEY;
    private MutableLiveData<List<Movie>> popularMovies;
    private MutableLiveData<List<Movie>> topRatedMovies;
    private List<Movie> downloadedMovies;
    private Retrofit retrofit;

    @Inject
    NetworkDataSource(Retrofit retrofit) {

        this.retrofit = retrofit;
        popularMovies = new MutableLiveData<>();
        topRatedMovies = new MutableLiveData<>();
        downloadedMovies = new ArrayList<>();
    }

    private void getMoviesByPath(String path) {

        MovieApi movieApi = retrofit.create(MovieApi.class);
        Call<MoviesList> callMoviesByPath = movieApi.getMoviesByPath(path, MY_TMDB_API_KEY);
        callMoviesByPath.enqueue(new Callback<MoviesList>() {
            @Override
            public void onResponse(Call<MoviesList> call, Response<MoviesList> response) {
                if (!response.isSuccessful()) {
                    // parse the response body …
                    APIError error = ErrorUtils.parseError(response);
                    Timber.tag("MyApp").d("NetworkDataSource: onResponse: %s", error.message());
                }

                Timber.tag("MyApp").d("onResponse() called with: call = [" + call + "], response = [" + response + "]");
                MoviesList moviesLists = response.body();
                switch (path) {
                    case POPULAR:
                        downloadedMovies = moviesLists.getMovies();
                        Timber.tag("MyApp").d("NetworkDataSource: getPopularMovies from Retrofit");
                        popularMovies.postValue(downloadedMovies);
                        break;
                    case TOP_RATED:
                        downloadedMovies = moviesLists.getMovies();
                        Timber.tag("MyApp").d("NetworkDataSource: getTopRatedMovies from Retrofit");
                        topRatedMovies.postValue(downloadedMovies);
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
