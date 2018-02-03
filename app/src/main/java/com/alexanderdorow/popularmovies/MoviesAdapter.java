package com.alexanderdorow.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alexanderdorow.popularmovies.dto.MovieItemDto;
import com.alexanderdorow.popularmovies.utilities.GlideUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander on 02/02/2018.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {


    private final OnMovieItemSelected onMovieItemSelected;
    private List<MovieItemDto> movies;

    public interface OnMovieItemSelected {
        void onMovieSelected(String movieId);
    }

    MoviesAdapter(OnMovieItemSelected onMovieItemSelected) {
        this.onMovieItemSelected = onMovieItemSelected;
        movies = new ArrayList<>();
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.movie_grid_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bind(position);
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

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView movieTitle;
        private ImageView movieThumbnail;
        private String movieId;

        MovieViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            movieTitle = itemView.findViewById(R.id.tv_movie_title);
            movieThumbnail = itemView.findViewById(R.id.iv_movie_thumbnail);
        }

        void bind(int position) {
            MovieItemDto movie = movies.get(position);
            movieId = movie.getId();
            movieTitle.setText(movie.getTitle());
            GlideUtils.showFadedImage(itemView.getContext(), movie.getThumbnailUrl(), movieThumbnail);
        }

        @Override
        public void onClick(View view) {
            onMovieItemSelected.onMovieSelected(movieId);
        }
    }

}
