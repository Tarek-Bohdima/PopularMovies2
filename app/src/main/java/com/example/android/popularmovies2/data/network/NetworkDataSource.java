/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */

package com.example.android.popularmovies2.data.network;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.android.popularmovies2.BuildConfig;
import com.example.android.popularmovies2.Constants;
import com.example.android.popularmovies2.data.model.Movie;
import com.example.android.popularmovies2.data.model.MoviesList;
import com.example.android.popularmovies2.data.model.Review;
import com.example.android.popularmovies2.data.model.ReviewsList;
import com.example.android.popularmovies2.data.model.Trailer;
import com.example.android.popularmovies2.data.model.TrailerList;
import com.example.android.popularmovies2.di.scopes.ApplicationScope;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Response;
import retrofit2.Retrofit;
import timber.log.Timber;

@ApplicationScope
public class NetworkDataSource {

    private static final String POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";
    private final MutableLiveData<List<Movie>> popularMovies;
    private final MutableLiveData<List<Movie>> topRatedMovies;
    private final MutableLiveData<List<Review>> reviewsLiveData;
    private final MutableLiveData<List<Trailer>> trailersLiveData;
    private final Retrofit retrofit;
    private final CompositeDisposable compositeDisposable;
    String MY_TMDB_API_KEY = BuildConfig.TMDB_API_KEY;
    private Integer movieId;
    private MovieApi movieApi;

    @Inject
    NetworkDataSource(Retrofit retrofit) {

        this.retrofit = retrofit;
        popularMovies = new MutableLiveData<>();
        topRatedMovies = new MutableLiveData<>();
        reviewsLiveData = new MutableLiveData<>();
        trailersLiveData = new MutableLiveData<>();
        compositeDisposable = new CompositeDisposable();
    }

    private void getMoviesByPath(String path) {


        movieApi = retrofit.create(MovieApi.class);
        Single<MoviesList> moviesListSingle = movieApi.getMoviesByPath(path, MY_TMDB_API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        switch (path) {
            case POPULAR:
                compositeDisposable.add(moviesListSingle
                        .subscribe(o -> popularMovies.postValue(o.getMovies()),
                                e -> Timber.tag(Constants.TAG)
                                        .d("NetworkDataSource: getMoviesByPath() called with: error = [" + e.getMessage() + "]")));
                break;
            case TOP_RATED:
                compositeDisposable.add(moviesListSingle
                        .subscribe(o -> topRatedMovies.postValue(o.getMovies()),
                                e -> Timber.tag(Constants.TAG)
                                        .d("NetworkDataSource: getMoviesByPath() called with: error = [" + e.getMessage() + "]")));
        }
    }

    private void getReviewsByMovie(String movieId) {
        Single<ReviewsList> reviewsListSingle = movieApi.getReviews(movieId, MY_TMDB_API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        compositeDisposable.add(reviewsListSingle
                .subscribe(o -> {
                            List<Review> reviews = null;
                            if (o != null) {
                                reviews = o.getReviews();
                            }
                            reviewsLiveData.postValue(reviews);
                        },
                        e -> Timber.tag(Constants.TAG).d("NetWorkDataSource: getReviewsByMovie() error: [" + e.getMessage() + "]")));
    }

    private void getTrailersByMovie(String movieId) {
        Single<TrailerList> trailerListSingle = movieApi.getTrailers(movieId, MY_TMDB_API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        compositeDisposable.add(trailerListSingle
                .subscribe(o -> {
                            List<Trailer> trailers = null;
                            if (o != null) {
                                trailers = o.getTrailers();
                            }
                            trailersLiveData.postValue(trailers);
                        },
                        e -> Timber.tag(Constants.TAG).d("NetworkDataSource: getTrailersByMovie() error = [" + e.getMessage() + "]")));
    }

    private void parseTrailers(Response<TrailerList> response) {
        TrailerList trailerList = response.body();
        List<Trailer> trailers = null;
        if (trailerList != null) {
            trailers = trailerList.getTrailers();
        } else {
            Timber.tag(Constants.TAG).d("parseTrailers() called with: response = [" + response + "]");
            // TODO: do something when there are no trailers
        }
//        trailersLiveData.setValue(trailers); // TODO: parse the list here or on DetailViewModel?
    }

    public LiveData<List<Movie>> getPopularMoviesLiveData() {
        getMoviesByPath(POPULAR);
        Timber.tag(Constants.TAG).d("NetworkDataSource: return PopularMovies LiveData");
        return popularMovies;
    }

    public LiveData<List<Movie>> getTopRatedMoviesLiveData() {

        getMoviesByPath(TOP_RATED);
        Timber.tag(Constants.TAG).d("NetworkDataSource: return TopRatedMovies LiveData");
        return topRatedMovies;
    }

    public LiveData<List<Review>> getReviewsLiveDataByMovieId(String movieId) {
        getReviewsByMovie(movieId);
        Timber.tag(Constants.TAG).d("NetWorkDataSource: getReviewsLiveDataByMovieId() called with: movieId = [" + movieId + "]");
        return reviewsLiveData;
    }

    public LiveData<List<Trailer>> getTrailersLiveDataByMovieId(String movieId) {
        getTrailersByMovie(movieId);
        Timber.tag(Constants.TAG).d("NetWorkDataSource: getTrailersLiveDataByMovieId() called with: movieId = [" + movieId + "]");
        return trailersLiveData;
    }

    public void clearDisposables() {
        // Using clear will clear all, but can accept new disposable
        compositeDisposable.clear();
        Timber.tag(Constants.TAG).d("NetworkDataSource: clearDisposables() called");
    }
}
