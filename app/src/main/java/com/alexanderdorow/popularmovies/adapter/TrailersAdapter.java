package com.alexanderdorow.popularmovies.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alexanderdorow.popularmovies.R;
import com.alexanderdorow.popularmovies.adapter.viewholder.MovieTrailerViewHolder;
import com.alexanderdorow.popularmovies.api.dto.MovieTrailerDto;

import java.util.ArrayList;
import java.util.List;

public class TrailersAdapter extends RecyclerView.Adapter<MovieTrailerViewHolder> {


    private List<MovieTrailerDto> trailers;

    public interface OnTrailerClickedListener {
        void onTrailerClick(MovieTrailerDto dto);
    }

    private OnTrailerClickedListener onTrailerClickedListener;

    public TrailersAdapter(OnTrailerClickedListener onTrailerClickedListener) {
        trailers = new ArrayList<>();
        this.onTrailerClickedListener = onTrailerClickedListener;
    }

    @Override
    public MovieTrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.movie_video_item, parent, false);
        return new MovieTrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieTrailerViewHolder holder, int position) {
        holder.bind(trailers.get(position), onTrailerClickedListener);
    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }

    public void setItems(List<MovieTrailerDto> data) {
        this.trailers = data;
        notifyDataSetChanged();
    }

}
