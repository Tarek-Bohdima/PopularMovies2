/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */

package com.example.android.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private Context context;
    private ArrayList<Movie> moviesArrayList;
    private static final String BASE_IMAGE_URL = "https://image.tmdb.org/t/p/";
    final private MovieAdapterClickListener onClickListener;

    public interface MovieAdapterClickListener {
        void onListItemClick(Movie currentMovie);
    }

    MovieAdapter(Context context, ArrayList<Movie> moviesArrayList, MovieAdapterClickListener onClickListener) {
        this.context = context;
        this.moviesArrayList = moviesArrayList;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View itemViewHolder = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new MovieViewHolder(itemViewHolder);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        context = holder.movieListItem.getContext();
        Movie currentMovie = moviesArrayList.get(position);
        Glide.with(context)
                .load(buildPosterImageUrl(currentMovie.getPosterPath()))
                .placeholder(R.drawable.film_poster_placeholder)
                .centerCrop()
                .into(holder.movieListItem);

    }

    @Override
    public int getItemCount() {
       /* if (moviesArrayList == null) {
            return 0;
        }else {
            return moviesArrayList.size();
        }*/
        // OR better ternary operator https://www.tutorialspoint.com/Java-Ternary-Operator-Examples
        // https://docs.oracle.com/javase/tutorial/java/nutsandbolts/op2.html
        return moviesArrayList == null ? 0 : moviesArrayList.size();
    }

    void setMovieData(ArrayList<Movie> movieData) {
        moviesArrayList = movieData;
        notifyDataSetChanged();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView movieListItem;

        MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            movieListItem = itemView.findViewById(R.id.poster_img);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            Movie currentMovie = moviesArrayList.get(clickedPosition);
            onClickListener.onListItemClick(currentMovie);
        }
    }

    static String buildPosterImageUrl(String filepath) {
        return BASE_IMAGE_URL + "w185/" + filepath;
    }

    static String buildBackdropImageUrl(String filepath) {
        return BASE_IMAGE_URL + "w500" + filepath;
    }
}
