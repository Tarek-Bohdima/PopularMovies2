/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */
package com.example.android.popularmovies2.ui.detail

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.android.popularmovies2.Constants
import com.example.android.popularmovies2.R
import com.example.android.popularmovies2.data.model.Movie
import com.example.android.popularmovies2.data.model.Review
import com.example.android.popularmovies2.data.model.Trailer
import com.example.android.popularmovies2.databinding.ActivityDetailBinding
import com.example.android.popularmovies2.ui.list.MainActivity.Companion.MOVIE_OBJECT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
    private val reviewList: List<Review> = ArrayList()
    private val trailersList: List<Trailer> = ArrayList()
    private val detailViewModel: DetailViewModel by viewModels()
    private lateinit var activityDetailBinding: ActivityDetailBinding
    private lateinit var reviewsAdapter: ReviewsAdapter
    private lateinit var trailersAdapter: TrailersAdapter
    private lateinit var detailMovie: Movie
    private var isFavorite = false
    private var iconEmpty = 0
    private var iconFull = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail)
        activityDetailBinding.lifecycleOwner = this
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        @Suppress("DEPRECATION")
        detailMovie = checkNotNull(intent?.getParcelableExtra<Movie>(MOVIE_OBJECT)) {
            "DetailActivity launched without a $MOVIE_OBJECT extra"
        }

        activityDetailBinding.movie = detailMovie
        iconEmpty = R.drawable.ic_favorite_empty
        iconFull = R.drawable.ic_favorite_full
        title = detailMovie.originalTitle
        setReviewsRecyclerView()
        setTrailersRecyclerView()
        setupViewModel()
        toggleLikeMovie()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            @Suppress("DEPRECATION")
            super.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun toggleLikeMovie() {
        activityDetailBinding.like.setOnClickListener {
            if (isFavorite) {
                Timber.tag(Constants.TAG)
                    .d("DetailActivity: likeDislikeMovie() called , detailMovie = %s ", detailMovie)
                detailViewModel.deleteFavoriteMovie(detailMovie)
            } else {
                detailViewModel.insertFavoriteMovie(detailMovie)
                Timber.tag(Constants.TAG).d("DetailActivity: likeDislikeMovie() called ")
            }
            activityDetailBinding.executePendingBindings()
        }
    }

    private fun setupViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                detailViewModel.reviews.collect { reviews ->
                    Timber.tag(Constants.TAG)
                        .d("DetailActivity: reviews collected, empty = [${reviews.isEmpty()}]")
                    reviewsAdapter.setReviewsData(reviews)
                    activityDetailBinding.reviewsTitle.visibility =
                        if (reviews.isEmpty()) View.GONE else View.VISIBLE
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                detailViewModel.trailers.collect { trailers ->
                    Timber.tag(Constants.TAG)
                        .d("DetailActivity: trailers collected, empty = [${trailers.isEmpty()}]")
                    trailersAdapter.setTrailersData(trailers)
                    activityDetailBinding.trailersTitle.visibility =
                        if (trailers.isEmpty()) View.GONE else View.VISIBLE
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                detailViewModel.favoriteMovie.collect { movie ->
                    if (movie != null && movie == detailMovie) {
                        isFavorite = true
                        activityDetailBinding.like.setImageResource(iconFull)
                    } else {
                        isFavorite = false
                        activityDetailBinding.like.setImageResource(iconEmpty)
                    }
                }
            }
        }
    }

    private fun setReviewsRecyclerView() {
        activityDetailBinding.reviewsView.visibility = View.VISIBLE
        activityDetailBinding.reviewsView.setHasFixedSize(true)
        reviewsAdapter = ReviewsAdapter(reviewList)
        activityDetailBinding.reviewsView.adapter = reviewsAdapter
    }

    private fun setTrailersRecyclerView() {
        activityDetailBinding.trailersView.visibility = View.VISIBLE
        activityDetailBinding.trailersView.setHasFixedSize(true)
        trailersAdapter = TrailersAdapter(trailersList)
        activityDetailBinding.trailersView.adapter = trailersAdapter
    }
}
