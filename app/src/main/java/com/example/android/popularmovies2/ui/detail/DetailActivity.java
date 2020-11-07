/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */

package com.example.android.popularmovies2.ui.detail;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent extraIntent = getIntent();
        if (extraIntent != null) {
            if (extraIntent.hasExtra(MOVIE_OBJECT)) {
                detailMovie = extraIntent.getParcelableExtra(MOVIE_OBJECT);
            }
        }

        activityDetailBinding.setMovie(detailMovie);
        iconEmpty = R.drawable.ic_favorite_empty;
        iconFull = R.drawable.ic_favorite_full;
        setTitle(detailMovie.getOriginalTitle());
        movieId = String.valueOf(detailMovie.getMovieId());
        setReviewsRecyclerView();
        setTrailersRecyclerView();
        setupViewModel();
        toggleLikeMovie();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleLikeMovie() {
        activityDetailBinding.like.setOnClickListener(v -> AppExecutors.getInstance().diskIO().execute(() -> {
            if (isFavorite) {
                Timber.tag(Constants.TAG).d("DetailActivity: likeDislikeMovie() called , detailMovie = %s ", detailMovie);
                detailViewModel.deleteFavoriteMovie(detailMovie);
            } else {
                detailViewModel.insertFavoriteMovie(detailMovie);
                Timber.tag(Constants.TAG).d("DetailActivity: likeDislikeMovie() called ");
            }
        }));
    }

    private void setupViewModel() {
        DetailViewModelFactory factory = new DetailViewModelFactory(this.getApplication(), movieId);

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
}
