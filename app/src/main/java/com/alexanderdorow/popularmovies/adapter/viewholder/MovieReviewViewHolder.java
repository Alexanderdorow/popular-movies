package com.alexanderdorow.popularmovies.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alexanderdorow.popularmovies.R;
import com.alexanderdorow.popularmovies.api.dto.MovieReviewDto;

public class MovieReviewViewHolder extends RecyclerView.ViewHolder {

    public MovieReviewViewHolder(View itemView) {
        super(itemView);
    }

    public void bind(MovieReviewDto review) {
        TextView authorTextView = itemView.findViewById(R.id.tv_review_author);
        TextView reviewTextView = itemView.findViewById(R.id.tv_review_review);
        authorTextView.setText(review.getAuthor());
        reviewTextView.setText(review.getContent());
    }

}