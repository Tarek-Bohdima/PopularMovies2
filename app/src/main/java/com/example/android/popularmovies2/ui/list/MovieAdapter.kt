/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */
package com.example.android.popularmovies2.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.android.popularmovies2.R
import com.example.android.popularmovies2.data.model.Movie
import com.example.android.popularmovies2.databinding.MovieListItemBinding

class MovieAdapter(
    private var moviesArrayList: List<Movie>?,
    private val movieItemClickListener: MovieItemClickListener,
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val itemBinding: MovieListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.movie_list_item,
            parent,
            false,
        )
        return MovieViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(moviesArrayList!![position], movieItemClickListener)
    }

    override fun getItemCount(): Int = moviesArrayList?.size ?: 0

    fun setMovieData(movieData: List<Movie>?) {
        moviesArrayList = movieData
        notifyDataSetChanged()
    }

    interface MovieItemClickListener {
        fun onMovieItemClicked(movie: Movie)
    }

    inner class MovieViewHolder(
        private val itemBinding: MovieListItemBinding,
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(currentMovie: Movie, movieItemClickListener: MovieItemClickListener) {
            itemBinding.movie = currentMovie
            itemBinding.movieItemClick = movieItemClickListener
            itemBinding.executePendingBindings()
        }
    }
}
