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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.android.popularmovies2.BuildConfig;
import com.example.android.popularmovies2.R;
import com.example.android.popularmovies2.data.model.Movie;
import com.example.android.popularmovies2.data.model.Review;
import com.example.android.popularmovies2.data.model.ReviewsList;
import com.example.android.popularmovies2.data.model.Trailer;
import com.example.android.popularmovies2.data.model.TrailerList;
import com.example.android.popularmovies2.data.network.APIError;
import com.example.android.popularmovies2.data.network.ErrorUtils;
import com.example.android.popularmovies2.data.network.MovieApi;
import com.example.android.popularmovies2.ui.list.MovieAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.example.android.popularmovies2.ui.list.MainActivity.MOVIE_OBJECT;
import static com.example.android.popularmovies2.ui.list.MovieAdapter.buildBackdropImageUrl;

public class DetailActivity extends AppCompatActivity {

    private final Context context = DetailActivity.this;
    String MY_TMDB_API_KEY = BuildConfig.TMDB_API_KEY;
    TextView reviewsTV;
    String movieId;
    MovieApi movieApi;
    private Movie detailMovie;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ImageView poster = findViewById(R.id.imageview_poster);
        ImageView backdrop = findViewById(R.id.imageview_backdrop);
        TextView originalTitle = findViewById(R.id.original_title);
        TextView releaseDate = findViewById(R.id.release_date);
        TextView userRating = findViewById(R.id.user_rating);
        TextView synopsisText = findViewById(R.id.synopsis_text);

        reviewsTV = findViewById(R.id.review);

//        movieApi = RetrofitClientInstance.getRetrofitInstance().create(MovieApi.class);

        Intent extraIntent = getIntent();
        if (extraIntent != null) {
            if (extraIntent.hasExtra(MOVIE_OBJECT)) {
                detailMovie = extraIntent.getParcelableExtra(MOVIE_OBJECT);
            }
        }

        Glide.with(context)
                .load(MovieAdapter.buildPosterImageUrl(detailMovie.getPosterPath()))
                .into(poster);

        Glide.with(context)
                .load(buildBackdropImageUrl(detailMovie.getBackdropPath()))
                .into(backdrop);

        originalTitle.setText(detailMovie.getOriginalTitle());
        releaseDate.setText(detailMovie.getReleaseDate());
        userRating.setText(String.valueOf(detailMovie.getVoteAverage()));
        synopsisText.setText(detailMovie.getOverview());

        setTitle(detailMovie.getOriginalTitle());

        movieId = String.valueOf(detailMovie.getMovieId());

        //Experimental
        getReviewsOnMovie();
        getTrailersOnMovie();
    }

    private void getTrailersOnMovie() {
        Call<TrailerList> callTrailersByMovieId = movieApi.getTrailers(movieId, MY_TMDB_API_KEY);
        callTrailersByMovieId.enqueue(new Callback<TrailerList>() {
            @Override
            public void onResponse(Call<TrailerList> call, Response<TrailerList> response) {
                if (!response.isSuccessful()) {
                    /* TODO notify user about response error in UI */
                    // parse the response body …
                    APIError error = ErrorUtils.parseError(response);
                    Timber.tag("MyApp").d("onResponse: %s", response.code());
                    Toast.makeText(DetailActivity.this, "OnResponse " + error.message(), Toast.LENGTH_LONG).show();
                }

                TrailerList trailerList = response.body();
                List<Trailer> trailers = null;
                if (trailerList != null) {
                    trailers = trailerList.getTrailers();
                }

                String key = "dr2dnVLJmyY";
                if (trailers != null && !trailers.isEmpty()) {
                    key = trailers.get(0).getKey();
                }

                //credits to: https://stackoverflow.com/a/12439378/8899344
                watchYoutubeVideo(context, key);

            }

            @Override
            public void onFailure(Call<TrailerList> call, Throwable t) {
                Timber.tag("MyApp").d("onFailure: %s", t.getMessage());
                Toast.makeText(DetailActivity.this, "onFailure: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
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

    private void getReviewsOnMovie() {

        Call<ReviewsList> callReviewsByMovieId = movieApi.getReviews(movieId, MY_TMDB_API_KEY);
        callReviewsByMovieId.enqueue(new Callback<ReviewsList>() {
            @Override
            public void onResponse(Call<ReviewsList> call, Response<ReviewsList> response) {
                if (!response.isSuccessful()) {
                    /* TODO notify user about response error in UI */
                    // parse the response body …
                    APIError error = ErrorUtils.parseError(response);
                    Timber.tag("MyApp").d("onResponse: %s", response.code());
                    Toast.makeText(DetailActivity.this, "OnResponse " + error.message(), Toast.LENGTH_LONG).show();
                }
                ReviewsList reviewsList = response.body();
                List<Review> reviews = reviewsList.getReviews();
                String review = "No reviews yet";
                if (reviews.size() != 0) {
                    review = reviews.get(0).getContent();
                }
                reviewsTV.setText(review);

            }

            @Override
            public void onFailure(Call<ReviewsList> call, Throwable t) {
                Timber.tag("MyApp").d("onFailure: %s", t.getMessage());
                Toast.makeText(DetailActivity.this, "onFailure: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
