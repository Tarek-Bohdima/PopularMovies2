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
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.popularmovies2.data.model.Trailer;
import com.example.android.popularmovies2.databinding.TrailerItemBinding;

import java.util.List;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailerViewHolder> {

    TrailerItemBinding trailerItemBinding;
    private List<Trailer> trailers;
    private Context context;

    public TrailersAdapter(List<Trailer> trailers) {
        this.trailers = trailers;
    }

    @NonNull
    @Override

    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        trailerItemBinding = TrailerItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TrailerViewHolder(trailerItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {
        Trailer trailer = trailers.get(position);
        holder.bind(trailer);
    }

    @Override
    public int getItemCount() {
        return trailers == null ? 0 : trailers.size();
    }

    public void setTrailersData(List<Trailer> trailersData) {
        trailers = trailersData;
        notifyDataSetChanged();
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

    public class TrailerViewHolder extends RecyclerView.ViewHolder {
        TrailerItemBinding trailerItemBinding;

        public TrailerViewHolder(TrailerItemBinding trailerItemBinding) {
            super(trailerItemBinding.getRoot());
            this.trailerItemBinding = trailerItemBinding;
        }

        public void bind(Trailer trailer) {
            String key = trailer.getKey();
            trailerItemBinding.trailerTitle.setText(trailer.getName());
            trailerItemBinding.trailerTitle.setOnClickListener(v -> watchYoutubeVideo(context, key));
            trailerItemBinding.executePendingBindings();
        }
    }
}
