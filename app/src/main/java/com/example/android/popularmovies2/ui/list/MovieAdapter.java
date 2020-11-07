/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */

package com.example.android.popularmovies2.ui.list;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.popularmovies2.R;
import com.example.android.popularmovies2.data.model.Movie;
import com.example.android.popularmovies2.databinding.MovieListItemBinding;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private final MovieItemClickListener movieItemClickListener;
    private List<Movie> moviesArrayList;

    public MovieAdapter(List<Movie> moviesArrayList, MovieItemClickListener onClickListener) {
        this.moviesArrayList = moviesArrayList;
        this.movieItemClickListener = onClickListener;
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

        holder.bind(moviesArrayList.get(position), movieItemClickListener);
    }

    @Override
    public int getItemCount() {
        return moviesArrayList == null ? 0 : moviesArrayList.size();
    }

    public void setMovieData(List<Movie> movieData) {
        moviesArrayList = movieData;
        notifyDataSetChanged();
    }


    public interface MovieItemClickListener {
        void onMovieItemClicked(Movie movie);
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {

        final MovieListItemBinding itemBinding;

        MovieViewHolder(MovieListItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }

        public void bind(Movie currentMovie, MovieItemClickListener movieItemClickListener) {
            itemBinding.setMovie(currentMovie);
            itemBinding.setMovieItemClick(movieItemClickListener);
            //This is to force bindings to execute right away
            itemBinding.executePendingBindings();
        }
    }
}
