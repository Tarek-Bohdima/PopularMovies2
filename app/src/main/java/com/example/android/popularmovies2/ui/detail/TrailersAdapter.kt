/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */
package com.example.android.popularmovies2.ui.detail

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android.popularmovies2.data.model.Trailer
import com.example.android.popularmovies2.databinding.TrailerItemBinding

class TrailersAdapter(
    private var trailers: List<Trailer>?,
) : RecyclerView.Adapter<TrailersAdapter.TrailerViewHolder>() {
    private var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrailerViewHolder {
        context = parent.context
        val itemBinding = TrailerItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return TrailerViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: TrailerViewHolder, position: Int) {
        holder.bind(trailers!![position])
    }

    override fun getItemCount(): Int = trailers?.size ?: 0

    fun setTrailersData(trailersData: List<Trailer>?) {
        trailers = trailersData
        notifyDataSetChanged()
    }

    // credits to: https://stackoverflow.com/a/12439378/8899344
    private fun watchYoutubeVideo(context: Context, key: String?) {
        val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$key"))
        val webIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=$key"))
        try {
            context.startActivity(appIntent)
        } catch (ex: ActivityNotFoundException) {
            context.startActivity(webIntent)
        }
    }

    inner class TrailerViewHolder(
        private val itemBinding: TrailerItemBinding,
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(trailer: Trailer) {
            val key = trailer.key
            itemBinding.trailerTitle.text = trailer.name
            itemBinding.trailerTitle.setOnClickListener {
                context?.let { watchYoutubeVideo(it, key) }
            }
            itemBinding.executePendingBindings()
        }
    }
}
