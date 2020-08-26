/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */

package com.example.android.popularmovies2.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

// implementation of Parcelable credits go to : https://stackoverflow.com/a/23647471/8899344
@Entity(tableName = "movies")
public class Movie implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    private int movieId;

    @SerializedName("original_title")
    private String originalTitle;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("backdrop_path")
    private String backdropPath;

    private String overview;

    @SerializedName("vote_average")
    private double voteAverage;

    @SerializedName("release_date")
    private String releaseDate;

//    @ColumnInfo(name = "is_top_rated")
//    private boolean isTopRated;
//
//    @ColumnInfo(name = "is_popular")
//    private boolean isPopular;

    @ColumnInfo(name = "is_favorite")
    private boolean isFavorite;


    public Movie(int movieId, String originalTitle, String posterPath, String backdropPath,
                 String overview, double voteAverage, String releaseDate) {
        this.movieId = movieId;
        this.originalTitle = originalTitle;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
    }

    private Movie(Parcel in) {
        movieId = in.readInt();
        originalTitle = in.readString();
        posterPath = in.readString();
        backdropPath = in.readString();
        overview = in.readString();
        voteAverage = in.readDouble();
        releaseDate = in.readString();
//        isTopRated = in.readByte() != 0;  //myBoolean == true if byte != 0 credit https://stackoverflow.com/a/7089687/8899344
//        isPopular = in.readByte() != 0;
        isFavorite = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(movieId);
        dest.writeString(originalTitle);
        dest.writeString(posterPath);
        dest.writeString(backdropPath);
        dest.writeString(overview);
        dest.writeDouble(voteAverage);
        dest.writeString(releaseDate);
//        dest.writeByte((byte) (isTopRated ? 1 : 0));  //if myBoolean == true, byte == 1
//        dest.writeByte((byte) (isPopular ? 1 : 0));
        dest.writeByte((byte) (isFavorite ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

//    public boolean isTopRated() {
//        return isTopRated;
//    }
//
//    public void setTopRated(boolean topRated) {
//        isTopRated = topRated;
//    }
//
//    public boolean isPopular() {
//        return isPopular;
//    }
//
//    public void setPopular(boolean popular) {
//        isPopular = popular;
//    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public void setVoteAverage(double voteAverage) {

        this.voteAverage = voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "movieId=" + movieId +
                ", originalTitle='" + originalTitle + '\'' +
//                ", isTopRated=" + isTopRated +
//                ", isPopular=" + isPopular +
                ", isFavorite=" + isFavorite +
                '}';
    }
}
