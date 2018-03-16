package com.alexanderdorow.popularmovies.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alexanderdorow.popularmovies.R;
import com.alexanderdorow.popularmovies.adapter.viewholder.MovieViewHolder;
import com.alexanderdorow.popularmovies.api.dto.MovieItemDto;

import java.util.ArrayList;
import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MovieViewHolder> {


    private final OnMovieItemSelected onMovieItemSelected;
    private List<MovieItemDto> movies;

    public ArrayList<MovieItemDto> getItems() {
        return new ArrayList<>(movies);
    }

    public interface OnMovieItemSelected {
        void onMovieSelected(MovieItemDto movie);
    }

    public MoviesAdapter(OnMovieItemSelected onMovieItemSelected) {
        this.onMovieItemSelected = onMovieItemSelected;
        movies = new ArrayList<>();
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.movie_grid_item, parent, false);
        return new MovieViewHolder(view, onMovieItemSelected);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bind(movies.get(position));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void setItems(List<MovieItemDto> data) {
        this.movies = data;
        notifyDataSetChanged();
    }

    public void addItems(List<MovieItemDto> data) {
        int positionStart = this.movies.size() - 1;
        this.movies.addAll(data);
        int positionEnd = this.movies.size() - 1;
        notifyItemRangeChanged(positionStart, positionEnd);
    }

}
