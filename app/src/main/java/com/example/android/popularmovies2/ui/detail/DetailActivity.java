/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */

package com.example.android.popularmovies2.ui.detail;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.android.popularmovies2.AppExecutors;
import com.example.android.popularmovies2.Constants;
import com.example.android.popularmovies2.R;
import com.example.android.popularmovies2.data.model.Movie;
import com.example.android.popularmovies2.data.model.Review;
import com.example.android.popularmovies2.data.model.Trailer;
import com.example.android.popularmovies2.databinding.ActivityDetailBinding;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static com.example.android.popularmovies2.ui.list.MainActivity.MOVIE_OBJECT;
import static com.example.android.popularmovies2.ui.list.MovieAdapter.buildBackdropImageUrl;
import static com.example.android.popularmovies2.ui.list.MovieAdapter.buildPosterImageUrl;

public class DetailActivity extends AppCompatActivity {

    private final List<Review> reviewList = new ArrayList<>();
    private final List<Trailer> trailersList = new ArrayList<>();
    DetailViewModel detailViewModel;
    private ActivityDetailBinding activityDetailBinding;
    private String movieId;
    private ReviewsAdapter reviewsAdapter;
    private Movie detailMovie;
    private TrailersAdapter trailersAdapter;
    private boolean isFavorite;
    private int iconEmpty;
    private int iconFull;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDetailBinding = ActivityDetailBinding.inflate(getLayoutInflater());
        View view = activityDetailBinding.getRoot();
        setContentView(view);
        Intent extraIntent = getIntent();
        if (extraIntent != null) {
            if (extraIntent.hasExtra(MOVIE_OBJECT)) {
                detailMovie = extraIntent.getParcelableExtra(MOVIE_OBJECT);
            }
        }

        activityDetailBinding.setMovie(detailMovie);
        iconEmpty = R.drawable.ic_favorite_empty;
        iconFull = R.drawable.ic_favorite_full;
        loadPosterAndBackdropImages();
        setTitle(detailMovie.getOriginalTitle());
        movieId = String.valueOf(detailMovie.getMovieId());
        setReviewsRecyclerView();
        setTrailersRecyclerView();
        setupViewModel();
        likeDislikeMovie();
    }

    private void likeDislikeMovie() {
        activityDetailBinding.like.setOnClickListener(v -> AppExecutors.getInstance().diskIO().execute(() -> {
            if (isFavorite) {
                Timber.tag(Constants.TAG).d("DetailActivity: likeDislikeMovie() called , detailMovie = %s ", detailMovie);
                detailViewModel.deleteFavoriteMovie(detailMovie);
            } else {
                detailViewModel.insertFavoriteMovie(detailMovie);
                Timber.tag(Constants.TAG).d("DetailActivity: likeDislikeMovie() called ");
            }
        }));
        activityDetailBinding.executePendingBindings();
    }

    private void setupViewModel() {
        DetailViewModelFactory factory = new DetailViewModelFactory(this.getApplication(), movieId, detailMovie);

        detailViewModel = new ViewModelProvider(this, factory).get(DetailViewModel.class);
        detailViewModel.getReviewsByMovieId().observe(this, reviews -> {
            Timber.tag(Constants.TAG).d("DetailActivity: onChanged() called with: reviews empty = [" + reviews.isEmpty() + "]");
            reviewsAdapter.setReviewsData(reviews);
            if (reviews.size() < 1) {
                activityDetailBinding.reviewsTitle.setVisibility(View.GONE);
            } else {
                activityDetailBinding.reviewsTitle.setVisibility(View.VISIBLE);
            }
        });

        detailViewModel.getTrailersByMovieId().observe(this, trailers -> {
            Timber.tag(Constants.TAG).d("DetailActivity: onChanged() called with: trailers empty = [" + trailers.isEmpty() + "]");
            trailersAdapter.setTrailersData(trailers);
            if (trailers.size() < 1) {
                activityDetailBinding.trailersTitle.setVisibility(View.GONE);
            } else {
                activityDetailBinding.trailersTitle.setVisibility(View.VISIBLE);
            }
        });

        detailViewModel.getFavoriteMovieById().observe(this, movie -> {
            if (movie != null) {
                if (movie.equals(detailMovie)) {
                    isFavorite = true;
                    activityDetailBinding.like.setImageResource(iconFull);
                } else {
                    isFavorite = false;
                    activityDetailBinding.like.setImageResource(iconEmpty);
                }
            } else {
                isFavorite = false;
                activityDetailBinding.like.setImageResource(iconEmpty);
            }

        });
    }

    private void setReviewsRecyclerView() {
        activityDetailBinding.reviewsView.setVisibility(View.VISIBLE);
        activityDetailBinding.reviewsView.setHasFixedSize(true);
        reviewsAdapter = new ReviewsAdapter(reviewList);
        activityDetailBinding.reviewsView.setAdapter(reviewsAdapter);
    }

    private void setTrailersRecyclerView() {
        activityDetailBinding.trailersView.setVisibility(View.VISIBLE);
        activityDetailBinding.trailersView.setHasFixedSize(true);
        trailersAdapter = new TrailersAdapter(trailersList);
        activityDetailBinding.trailersView.setAdapter(trailersAdapter);
    }


    // TODO: continue with com/example/android/popularmovies2/ui/BindingAdapters.java:31
    private void loadPosterAndBackdropImages() {
        Glide.with(this)
                .load(buildPosterImageUrl(detailMovie.getPosterPath()))
                .into(activityDetailBinding.imageviewPoster);

        Glide.with(this)
                .load(buildBackdropImageUrl(detailMovie.getBackdropPath()))
                .into(activityDetailBinding.imageviewBackdrop);
    }
}
