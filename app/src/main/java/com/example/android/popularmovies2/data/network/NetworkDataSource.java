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


    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static NetworkDataSource sInstance;
    // please acquire your API KEY from https://www.themoviedb.org/ then:
    // user your API key in the project's gradle.properties file: MY_TMDB_API_KEY="<your API KEY>"
//    private static Retrofit retrofit;

    private static String BASE_URL = "https://api.themoviedb.org/3/movie/";
    String MY_TMDB_API_KEY = BuildConfig.TMDB_API_KEY;
    private static final String POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";
    private MutableLiveData<List<Movie>> popularMovies;
    private MutableLiveData<List<Movie>> topRatedMovies;
    private List<Movie> dowonloadedMovies;
    private Retrofit retrofit;

    @Inject
    NetworkDataSource(Retrofit retrofit) {
        /*Create handle for the RetrofitInstance interface*/
//        retrofit = RetrofitClientInstance.getRetrofitInstance();
        this.retrofit = retrofit;
        popularMovies = new MutableLiveData<>();
        topRatedMovies = new MutableLiveData<>();
        dowonloadedMovies = new ArrayList<>();
    }

    /*  */

    /**
     * Get the singleton for this class
     *//*
    public static NetworkDataSource getInstance(Application application) {
        Timber.tag("MyApp").d("NetworkDataSource: getting the network data source");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new NetworkDataSource(application);
                Timber.tag("MyApp").d("NetworkDataSource: Made new Network Data source");
            }
        }
        return sInstance;
    }*/
    private void getMoviesByPath(String path) {

        MovieApi movieApi = retrofit.create(MovieApi.class);
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
                Timber.tag("MyApp").d("onResponse() called with: call = [" + call + "], response = [" + response + "]");
                MoviesList moviesLists = response.body();
                switch (path) {
                    case POPULAR:
                        dowonloadedMovies = moviesLists.getMovies();
                        Timber.tag("MyApp").d("NetworkDataSource: getPopularMovies from Retrofit");
                        popularMovies.postValue(dowonloadedMovies);
                        break;
                    case TOP_RATED:
                        dowonloadedMovies = moviesLists.getMovies();
                        Timber.tag("MyApp").d("NetworkDataSource: getTopRatedMovies from Retrofit");
                        topRatedMovies.postValue(dowonloadedMovies);
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
