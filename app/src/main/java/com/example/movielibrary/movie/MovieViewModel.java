package com.example.movielibrary.movie;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class MovieViewModel extends AndroidViewModel {
    private MovieRepository mRepository;
    private LiveData<List<Movie>> mAllMovies;
    public MovieViewModel(@NonNull Application application) {
        super(application);
        mRepository = new MovieRepository(application);
        mAllMovies = mRepository.getAllMovies();
    }
    public LiveData<List<Movie>> getAllItems() {
        return mAllMovies;
    }
    public void insert(Movie movie) {
        mRepository.insert(movie);
    }
    public void deleteAll() {
        mRepository.deleteAll();
    }
    public void deleteAllByYear(int year){
        mRepository.deleteAllByYear(year);
    }
}
