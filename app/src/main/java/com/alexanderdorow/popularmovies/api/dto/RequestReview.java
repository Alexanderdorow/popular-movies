
package com.alexanderdorow.popularmovies.api.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RequestReview {

    @SerializedName("total_pages")
    @Expose
    private int totalPages;
    @SerializedName("results")
    @Expose
    private List<MovieReviewDto> data;

    public RequestReview() {
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public void setData(List<MovieReviewDto> data) {
        this.data = data;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public List<MovieReviewDto> getData() {
        return data;
    }

}
