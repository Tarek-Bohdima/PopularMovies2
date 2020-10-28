/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */

package com.example.android.popularmovies2.ui.detail;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.android.popularmovies2.Constants;
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

    private final Context context = DetailActivity.this;
    private ActivityDetailBinding activityDetailBinding;
    private String movieId;
    private ReviewsAdapter reviewsAdapter;
    private final List<Review> reviewList = new ArrayList<>();
    private Movie detailMovie;
    private final List<Trailer> trailersList = new ArrayList<>();
    private TrailersAdapter trailersAdapter;

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

        loadPosterAndBackdropImages();

        setViews();

        setTitle(detailMovie.getOriginalTitle());

        movieId = String.valueOf(detailMovie.getMovieId());

        setReviewsRecyclerView();
        setTrailersRecyclerView();

        setupViewModel();

    }

    private void setupViewModel() {
        DetailViewModelFactory factory = new DetailViewModelFactory(this.getApplication(), movieId);
        DetailViewModel detailViewModel = new ViewModelProvider(this, factory).get(DetailViewModel.class);

        detailViewModel.getReviewsByMovieId().observe(this, new Observer<List<Review>>() {
            @Override
            public void onChanged(List<Review> reviews) {
                Timber.tag(Constants.TAG).d("DetailActivity: onChanged() called with: reviews empty = [" + reviews.isEmpty() + "]");
                reviewsAdapter.setReviewsData(reviews);
                if (reviews.size() < 1) {
                    activityDetailBinding.reviewsTitle.setVisibility(View.GONE);
                } else {
                    activityDetailBinding.reviewsTitle.setVisibility(View.VISIBLE);
                }
            }
        });

        detailViewModel.getTrailersByMovieId().observe(this, new Observer<List<Trailer>>() {
            @Override
            public void onChanged(List<Trailer> trailers) {
                Timber.tag(Constants.TAG).d("DetailActivity: onChanged() called with: trailers empty = [" + trailers.isEmpty() + "]");
                trailersAdapter.setTrailersData(trailers);
                if (trailers.size() < 1) {
                    activityDetailBinding.trailersTitle.setVisibility(View.GONE);
                } else {
                    activityDetailBinding.trailersTitle.setVisibility(View.VISIBLE);
                }
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


    private void loadPosterAndBackdropImages() {
        Glide.with(context)
                .load(buildPosterImageUrl(detailMovie.getPosterPath()))
                .into(activityDetailBinding.imageviewPoster);

        Glide.with(context)
                .load(buildBackdropImageUrl(detailMovie.getBackdropPath()))
                .into(activityDetailBinding.imageviewBackdrop);
    }

    private void setViews() {
        activityDetailBinding.originalTitle.setText(detailMovie.getOriginalTitle());
        activityDetailBinding.releaseDate.setText(detailMovie.getReleaseDate());
        activityDetailBinding.userRating.setText(String.valueOf(detailMovie.getVoteAverage()));
        activityDetailBinding.synopsisText.setText(detailMovie.getOverview());
        activityDetailBinding.synopsisText.setMovementMethod(new ScrollingMovementMethod());
        activityDetailBinding.synopsisText.getScrollBarDefaultDelayBeforeFade();
    }

    private void getTrailersOnMovie() {
       /* Call<TrailerList> callTrailersByMovieId = movieApi.getTrailers(movieId, MY_TMDB_API_KEY);
        callTrailersByMovieId.enqueue(new Callback<TrailerList>() {
            @Override
            public void onResponse(Call<TrailerList> call, Response<TrailerList> response) {
                if (!response.isSuccessful()) {
                    *//* TODO notify user about response error in UI *//*
                    // parse the response body …
                    APIError error = ErrorUtils.parseError(response);
                    Timber.tag(Constants.TAG).d("onResponse: %s", response.code());
                    Toast.makeText(DetailActivity.this, "OnResponse " + error.message(), Toast.LENGTH_LONG).show();
                }

                TrailerList trailerList = response.body();
                List<Trailer> trailers = null;
                if (trailerList != null) {
                    trailers = trailerList.getTrailers();
                }


                String key = "dr2dnVLJmyY";
                ArrayList<String> keys = new ArrayList<>();
                if (trailers != null && !trailers.isEmpty()) {
                    for (int i = 0; i < trailers.size(); i++) {
                        key = trailers.get(i).getKey();
                        keys.add(i, key);
                    }
//                    key = trailers.get(0).getKey();
                }

                //credits to: https://stackoverflow.com/a/12439378/8899344
                watchYoutubeVideo(context, key);

            }

            @Override
            public void onFailure(Call<TrailerList> call, Throwable t) {
                Timber.tag(Constants.TAG).d("onFailure: %s", t.getMessage());
                Toast.makeText(DetailActivity.this, "onFailure: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });*/
    }

    //credits to: https://stackoverflow.com/a/12439378/8899344
    private void watchYoutubeVideo(Context context, String key) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + key));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }
}
