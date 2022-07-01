package com.example.movielibrary.movie;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MovieDao {

    @Query("select * from movies")
    LiveData<List<Movie>> getAllMovie();

    @Query("select * from movies where movieTitle=:name")
    List<Movie> getMovie(String name);

    @Insert
    void addMovie(Movie movie);

    @Query("delete from movies where movieTitle=:name")
    void deleteMovie(String name);

    @Query("delete from movies")
    void deleteAllMovies();

    @Query("delete from movies where movieYear=:year")
    void deleteAllMoviesByYear(int year);
}
