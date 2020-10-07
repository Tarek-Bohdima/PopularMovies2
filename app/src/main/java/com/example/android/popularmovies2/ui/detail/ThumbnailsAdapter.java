/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */

package com.example.android.popularmovies2.ui.detail;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ThumbnailsAdapter extends RecyclerView.Adapter<ThumbnailsAdapter.ThumbnailsVH> {
    private Context context;
    private List<Integer> thumbs;

    @NonNull
    @Override
    public ThumbnailsVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ThumbnailsVH holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ThumbnailsVH extends RecyclerView.ViewHolder {

        public ThumbnailsVH(@NonNull View itemView) {
            super(itemView);
        }
    }
}
