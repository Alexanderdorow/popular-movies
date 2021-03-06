package com.alexanderdorow.popularmovies.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alexanderdorow.popularmovies.R;
import com.alexanderdorow.popularmovies.adapter.viewholder.MovieReviewViewHolder;
import com.alexanderdorow.popularmovies.api.dto.MovieReviewDto;

import java.util.ArrayList;
import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<MovieReviewViewHolder> {


    private List<MovieReviewDto> reviews;

    public ReviewsAdapter() {
        reviews = new ArrayList<>();
    }

    @Override
    public MovieReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.movie_review_item, parent, false);
        return new MovieReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieReviewViewHolder holder, int position) {
        holder.bind(reviews.get(position));
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public void setItems(List<MovieReviewDto> data) {
        this.reviews = data;
        notifyDataSetChanged();
    }

    public ArrayList<MovieReviewDto> getItems() {
        return new ArrayList<>(reviews);
    }
}
