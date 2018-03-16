
package com.alexanderdorow.popularmovies.api.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RequestTrailer {

    @SerializedName("total_pages")
    @Expose
    private int totalPages;
    @SerializedName("results")
    @Expose
    private List<MovieTrailerDto> data;

    public RequestTrailer() {
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public void setData(List<MovieTrailerDto> data) {
        this.data = data;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public List<MovieTrailerDto> getData() {
        return data;
    }

}
