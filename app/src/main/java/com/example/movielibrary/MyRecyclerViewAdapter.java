package com.example.movielibrary;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movielibrary.movie.Movie;
import com.example.movielibrary.movie.MovieViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    List<Movie> movies = new ArrayList<>();

    private MovieViewModel mMovieViewModel;

    public MyRecyclerViewAdapter(MovieViewModel mMovieViewModel){
        this.mMovieViewModel = mMovieViewModel;
    }

    public void setMovies(List<Movie> movies){
        this.movies = movies;
    }

    @NonNull
    @Override
    public MyRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_main, parent, false); //CardView inflated as RecyclerView list item
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyRecyclerViewAdapter.ViewHolder holder, int position) {
        Movie movie = movies.get(position);

        holder.movieNumberText.setText(movie.getId() + "");
        holder.titleText.setText(movie.getTitle());
        holder.yearText.setText(movie.getYear() + "");
        holder.countryText.setText(movie.getCountry());
        holder.genreText.setText(movie.getGenre());
        holder.costText.setText(movie.getCost() + "");
        holder.keywordsText.setText(movie.getKeywords());

        final int fPosition = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Snackbar.make(view, "Item at position " + fPosition + " was clicked!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                String toastMessage = String.format("Movie No. %d with Title: %s is selected.", holder.getAdapterPosition()+1, movie.getTitle());
                Toast.makeText(view.getContext(), toastMessage, Toast.LENGTH_SHORT).show();

                // Extra Task
                mMovieViewModel.deleteAllByYear(movie.getYear());
            }
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View itemView;
        public TextView movieNumberText;
        public TextView titleText;
        public TextView yearText;
        public TextView countryText;
        public TextView genreText;
        public TextView costText;
        public TextView keywordsText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // itemView is the cardView
            this.itemView = itemView;
            movieNumberText = itemView.findViewById(R.id.cardMovieId);
            titleText = itemView.findViewById(R.id.cardTitle);
            yearText = itemView.findViewById(R.id.cardYear);
            countryText = itemView.findViewById(R.id.cardCountry);
            genreText = itemView.findViewById(R.id.cardGenre);
            costText = itemView.findViewById(R.id.cardCost);
            keywordsText = itemView.findViewById(R.id.cardKeywords);
        }
    }
}
