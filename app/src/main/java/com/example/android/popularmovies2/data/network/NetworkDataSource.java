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
import com.example.android.popularmovies2.data.model.Review;
import com.example.android.popularmovies2.data.model.ReviewsList;
import com.example.android.popularmovies2.data.model.Trailer;
import com.example.android.popularmovies2.data.model.TrailerList;
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
    private Integer movieId;
    private MutableLiveData<List<Review>> reviewsLiveData;
    private MutableLiveData<List<Trailer>> trailersLiveData;
    private Retrofit retrofit;
    private MovieApi movieApi;

    @Inject
    NetworkDataSource(Retrofit retrofit) {

        this.retrofit = retrofit;
        popularMovies = new MutableLiveData<>();
        topRatedMovies = new MutableLiveData<>();
        downloadedMovies = new ArrayList<>();
        reviewsLiveData = new MutableLiveData<>();
    }

    private void getMoviesByPath(String path) {

        movieApi = retrofit.create(MovieApi.class);
        Call<MoviesList> callMoviesByPath = movieApi.getMoviesByPath(path, MY_TMDB_API_KEY);
        callMoviesByPath.enqueue(new Callback<MoviesList>() {
            @Override
            public void onResponse(Call<MoviesList> call, Response<MoviesList> response) {
                if (!response.isSuccessful()) {
                    // parse the response body …
                    APIError error = ErrorUtils.parseError(response);
                    Timber.tag("MyApp").d("NetworkDataSource: onResponse: %s", error.message());
                }
                parseMovies(call, response, path);
            }

            @Override
            public void onFailure(Call<MoviesList> call, Throwable t) {
                Timber.tag("MyApp").d("onFailure: %s", t.getMessage());

            }
        });
    }

    private void parseMovies(Call<MoviesList> call, Response<MoviesList> response, String path) {
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

    private void getReviewsByMovie(String movieId) {
        Call<ReviewsList> callReviewsByMovieId = movieApi.getReviews(movieId, MY_TMDB_API_KEY);
        callReviewsByMovieId.enqueue(new Callback<ReviewsList>() {
            @Override
            public void onResponse(Call<ReviewsList> call, Response<ReviewsList> response) {
                if (!response.isSuccessful()) {
                    /* TODO notify user about response error in UI */
                    // parse the response body …
                    APIError error = ErrorUtils.parseError(response);
                    Timber.tag("MyApp").d("NetworkDataSource: Reviews: onResponse: %s", error.message());
                }
                parseReviews(response);
            }

            @Override
            public void onFailure(Call<ReviewsList> call, Throwable t) {
                Timber.tag("MyApp").d("NetWorkDataSource: getReviewsByMovie: onFailure() called with: call = [" + call + "], t = [" + t + "]");
            }
        });
    }

    private void parseReviews(Response<ReviewsList> response) {
        ReviewsList reviewsList = response.body();
        List<Review> reviews = null;
        if (reviewsList != null) {
            reviews = reviewsList.getReviews();
        } else {
            Timber.tag("MyApp").d("parseReviews() called with: response = [" + response + "]");
            // TODO: Do or pass something when there are no Reviews
        }
        reviewsLiveData.setValue(reviews); // TODO: parse the list on DetailViewModel or here?!
    }

    private void getTrailersByMovie(String movieId) {
        Call<TrailerList> callTrailersByMovieId = movieApi.getTrailers(movieId, MY_TMDB_API_KEY);
        callTrailersByMovieId.enqueue(new Callback<TrailerList>() {
            @Override
            public void onResponse(Call<TrailerList> call, Response<TrailerList> response) {
                if (!response.isSuccessful()) {
                    APIError error = ErrorUtils.parseError(response);
                    Timber.tag("MyApp").d("NetworkDataSource: Trailers: onResponse: %s", error.message());
                }
//                parseTrailers(response);
            }

            @Override
            public void onFailure(Call<TrailerList> call, Throwable t) {
                Timber.tag("MyApp").d("NetWorkDataSource:  getTrailersByMovie: onFailure() called with: call = [" + call + "], t = [" + t + "]");
            }
        });
    }

    private void parseTrailers(Response<TrailerList> response) {
        TrailerList trailerList = response.body();
        List<Trailer> trailers = null;
        if (trailerList != null) {
            trailers = trailerList.getTrailers();
        } else {
            Timber.tag("MyApp").d("parseTrailers() called with: response = [" + response + "]");
            // TODO: do something when there are no trailers
        }
//        trailersLiveData.setValue(trailers); // TODO: parse the list here or on DetailViewModel?
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

    public LiveData<List<Review>> getReviewsLiveDataByMovieId(String movieId) {
        getReviewsByMovie(movieId);
        Timber.tag("MyApp").d("NetWorkDataSource: getReviewsLiveDataByMovieId() called with: movieId = [" + movieId + "]");
        return reviewsLiveData;
    }

    public LiveData<List<Trailer>> getTrailersLiveDataByMovieId(String movieId) {
        getTrailersByMovie(movieId);
        Timber.tag("MyApp").d("NetWorkDataSource: getTrailersLiveDataByMovieId() called with: movieId = [" + movieId + "]");
        return trailersLiveData;
    }
}
