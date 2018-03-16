package com.alexanderdorow.popularmovies.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alexanderdorow.popularmovies.R;
import com.alexanderdorow.popularmovies.adapter.TrailersAdapter;
import com.alexanderdorow.popularmovies.api.dto.MovieTrailerDto;

public class MovieTrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private MovieTrailerDto trailer;
    private TrailersAdapter.OnTrailerClickedListener onTrailerClickedListener;

    public MovieTrailerViewHolder(View itemView) {
        super(itemView);
    }

    public void bind(MovieTrailerDto trailer, TrailersAdapter.OnTrailerClickedListener onTrailerClickedListener) {
        this.trailer = trailer;
        this.onTrailerClickedListener = onTrailerClickedListener;
        this.itemView.setOnClickListener(this);
        TextView movieTrailerName = itemView.findViewById(R.id.tv_trailer_name);
        movieTrailerName.setText(trailer.getName());
    }

    @Override
    public void onClick(View view) {
        onTrailerClickedListener.onTrailerClick(trailer);
    }
}