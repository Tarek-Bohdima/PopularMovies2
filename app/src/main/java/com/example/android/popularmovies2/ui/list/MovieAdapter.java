/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */

package com.example.android.popularmovies2.ui.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.popularmovies2.R;
import com.example.android.popularmovies2.data.model.Movie;
import com.example.android.popularmovies2.databinding.MovieListItemBinding;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    final private MovieAdapterClickListener onClickListener;
    private List<Movie> moviesArrayList;

    public MovieAdapter(List<Movie> moviesArrayList, MovieAdapterClickListener onClickListener) {
        this.moviesArrayList = moviesArrayList;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        MovieListItemBinding itemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.movie_list_item,
                parent, false);

        return new MovieViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {

        holder.bind();
        Movie currentMovie = moviesArrayList.get(position);
        holder.itemBinding.setMovie(currentMovie);
    }

    @Override
    public int getItemCount() {
        return moviesArrayList == null ? 0 : moviesArrayList.size();
    }

    public void setMovieData(List<Movie> movieData) {
        moviesArrayList = movieData;
        notifyDataSetChanged();
    }

    public interface MovieAdapterClickListener {
        void onListItemClick(Movie currentMovie);
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final MovieListItemBinding itemBinding;

        MovieViewHolder(MovieListItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }

        public void bind() {
            itemBinding.posterImg.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            Movie currentMovie = moviesArrayList.get(clickedPosition);
            onClickListener.onListItemClick(currentMovie);
        }
    }
}
