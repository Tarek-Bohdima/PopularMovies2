/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */

package com.example.android.popularmovies2.ui.detail;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.popularmovies2.data.model.Review;
import com.example.android.popularmovies2.databinding.ReviewItemBinding;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    ReviewItemBinding reviewItemBinding;
    private List<Review> reviews;

    public ReviewsAdapter(List<Review> reviews) {
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        reviewItemBinding = ReviewItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ReviewViewHolder(reviewItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.bind(review);
    }

    @Override
    public int getItemCount() {
        return reviews == null ? 0 : reviews.size();
    }

    public void setReviewsData(List<Review> reviewsData) {
        reviews = reviewsData;
        notifyDataSetChanged();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        ReviewItemBinding reviewItemBinding;

        public ReviewViewHolder(ReviewItemBinding reviewItemBinding) {
            super(reviewItemBinding.getRoot());
            this.reviewItemBinding = reviewItemBinding;
        }

        public void bind(Review review) {
            reviewItemBinding.authorTextView.setText(review.getAuthor());
            reviewItemBinding.reviewTextView.setText(review.getContent());
            reviewItemBinding.urlTextView.setText(review.getReviewUrl());
        }
    }
}
