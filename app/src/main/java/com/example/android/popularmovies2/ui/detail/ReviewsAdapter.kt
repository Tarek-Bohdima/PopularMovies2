/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */
package com.example.android.popularmovies2.ui.detail

import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android.popularmovies2.data.model.Review
import com.example.android.popularmovies2.databinding.ReviewItemBinding

class ReviewsAdapter(
    private var reviews: List<Review>?,
) : RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder>() {
    private val collapsedStatus = SparseBooleanArray()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val itemBinding = ReviewItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return ReviewViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(reviews!![position])
    }

    override fun getItemCount(): Int = reviews?.size ?: 0

    fun setReviewsData(reviewsData: List<Review>?) {
        reviews = reviewsData
        notifyDataSetChanged()
    }

    inner class ReviewViewHolder(
        private val itemBinding: ReviewItemBinding,
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(review: Review) {
            itemBinding.authorTextView.text = review.author
            itemBinding.expandTextView.setText(review.content, collapsedStatus, adapterPosition)
            itemBinding.urlTextView.text = review.reviewUrl
            itemBinding.expandTextView.setOnExpandStateChangeListener { _, _ -> }
            itemBinding.executePendingBindings()
        }
    }
}
