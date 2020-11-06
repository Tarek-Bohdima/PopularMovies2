/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */

package com.example.android.popularmovies2.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.android.popularmovies2.data.model.Movie;

import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM favorite_movies")
    LiveData<List<Movie>> getAllMovies();

    @Query("SELECT * FROM favorite_movies where movieId = :id")
    LiveData<Movie> getMovieById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovie(Movie movie);

    @Delete
    void delete(Movie movie);

    @Query("DELETE FROM favorite_movies")
    void deleteAllMovies();

    @Update
    void update(Movie movie);

}
