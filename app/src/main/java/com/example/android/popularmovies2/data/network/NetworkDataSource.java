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
    String MY_TMDB_API_KEY = BuildConfig.TMDB_API_KEY;
    private MutableLiveData<List<Movie>> popularMovies;
    private MutableLiveData<List<Movie>> topRatedMovies;
    private Integer movieId;
    private MutableLiveData<List<Review>> reviewsLiveData;
    private MutableLiveData<List<Trailer>> trailersLiveData;
    private Retrofit retrofit;
    private CompositeDisposable compositeDisposable;
    private MovieApi movieApi;

    @Inject
    NetworkDataSource(Retrofit retrofit) {

        this.retrofit = retrofit;
        popularMovies = new MutableLiveData<>();
        topRatedMovies = new MutableLiveData<>();
        reviewsLiveData = new MutableLiveData<>();
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
                                        .d("getMoviesByPath() called with: error = [" + e.getMessage() + "]")));
                break;
            case TOP_RATED:
                compositeDisposable.add(moviesListSingle
                        .subscribe(o -> topRatedMovies.postValue(o.getMovies()),
                                e -> Timber.tag(Constants.TAG)
                                        .d("getMoviesByPath() called with: error = [" + e.getMessage() + "]")));
        }
    }

    private void getReviewsByMovie(String movieId) {
        Single<ReviewsList> reviewsListSingle = movieApi.getReviews(movieId, MY_TMDB_API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        // TODO: just starting
        compositeDisposable.add(reviewsListSingle
                .subscribe(o -> reviewsLiveData.postValue(o.getReviews()),
                        e -> Timber.tag(Constants.TAG).d("NetWorkDataSource: getReviewsByMovie() error: [" + e.getMessage() + "]")));

       /* Call<ReviewsList> callReviewsByMovieId = movieApi.getReviews(movieId, MY_TMDB_API_KEY);
        callReviewsByMovieId.enqueue(new Callback<ReviewsList>() {
            @Override
            public void onResponse(Call<ReviewsList> call, Response<ReviewsList> response) {
                if (!response.isSuccessful()) {
                    *//* TODO notify user about response error in UI *//*
                    // parse the response body …
                    APIError error = ErrorUtils.parseError(response);
                    Timber.tag(Constants.TAG).d("NetworkDataSource: Reviews: onResponse: %s", error.message());
                }
                parseReviews(response);
            }

            @Override
            public void onFailure(Call<ReviewsList> call, Throwable t) {
                Timber.tag(Constants.TAG).d("getReviewsByMovie: onFailure() called with: call = [" + call + "], t = [" + t + "]");
            }
        });*/
    }

    private void parseReviews(Response<ReviewsList> response) {
        ReviewsList reviewsList = response.body();
        List<Review> reviews = null;
        if (reviewsList != null) {
            reviews = reviewsList.getReviews();
        } else {
            Timber.tag(Constants.TAG).d("parseReviews() called with: response = [" + response + "]");
            // TODO: Do or pass something when there are no Reviews
        }
        reviewsLiveData.postValue(reviews); // TODO: parse the list on DetailViewModel or here?!
    }

    private void getTrailersByMovie(String movieId) {
      /*  Call<TrailerList> callTrailersByMovieId = movieApi.getTrailers(movieId, MY_TMDB_API_KEY);
        callTrailersByMovieId.enqueue(new Callback<TrailerList>() {
            @Override
            public void onResponse(Call<TrailerList> call, Response<TrailerList> response) {
                if (!response.isSuccessful()) {
                    APIError error = ErrorUtils.parseError(response);
                    Timber.tag(Constants.TAG).d("NetworkDataSource: Trailers: onResponse: %s", error.message());
                }
//                parseTrailers(response);
            }

            @Override
            public void onFailure(Call<TrailerList> call, Throwable t) {
                Timber.tag(Constants.TAG).d("NetWorkDataSource:  getTrailersByMovie: onFailure() called with: call = [" + call + "], t = [" + t + "]");
            }
        });*/
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
