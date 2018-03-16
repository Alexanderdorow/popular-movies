package com.alexanderdorow.popularmovies.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alexanderdorow.popularmovies.R;
import com.alexanderdorow.popularmovies.adapter.MoviesAdapter;
import com.alexanderdorow.popularmovies.api.dto.MovieItemDto;
import com.alexanderdorow.popularmovies.utilities.GlideUtils;

public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final MoviesAdapter.OnMovieItemSelected onMovieItemSelected;
    private TextView movieTitle;
    private ImageView movieThumbnail;
    private MovieItemDto movie;

    public MovieViewHolder(View itemView, MoviesAdapter.OnMovieItemSelected onMovieItemSelected) {
        super(itemView);
        itemView.setOnClickListener(this);
        this.onMovieItemSelected = onMovieItemSelected;
        movieTitle = itemView.findViewById(R.id.tv_movie_title);
        movieThumbnail = itemView.findViewById(R.id.iv_movie_thumbnail);
    }

    public void bind(final MovieItemDto movie) {
        this.movie = movie;
        movieTitle.setText(movie.getTitle());
        //prevent to the recyclerView check my checkbox automatically
        GlideUtils.showFadedImage(itemView.getContext(), GlideUtils.getImageUrl(movie.getPosterPath()), movieThumbnail);
    }

    @Override
    public void onClick(View view) {
        onMovieItemSelected.onMovieSelected(movie);
    }
}