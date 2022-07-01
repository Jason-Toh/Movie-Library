package com.example.movielibrary;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movielibrary.movie.Movie;
import com.example.movielibrary.movie.MovieViewModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MovieListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    MyRecyclerViewAdapter adapter;

    ArrayList<Movie> movies = new ArrayList<>();
    Gson gson = new Gson();

    private MovieViewModel mMovieViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view_main);

        getGsonData();

        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mMovieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);
        adapter = new MyRecyclerViewAdapter(mMovieViewModel);
        recyclerView.setAdapter(adapter);

        mMovieViewModel.getAllItems().observe(this, newData -> {
            adapter.setMovies(newData);
            adapter.notifyDataSetChanged();
        });
    }

    public void getGsonData(){
        SharedPreferences sP = getSharedPreferences("f1",0);
        String movieStr = sP.getString("KEY_GSON","");
        Type type = new TypeToken<ArrayList<Movie>>(){}.getType();
        movies = gson.fromJson(movieStr, type);
    }

    public void goBackToPreviousActivity(View view){
        finish();
    }
}
