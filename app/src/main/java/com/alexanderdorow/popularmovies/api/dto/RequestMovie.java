
package com.alexanderdorow.popularmovies.api.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RequestMovie {

    @SerializedName("total_pages")
    @Expose
    private int totalPages;
    @SerializedName("results")
    @Expose
    private List<MovieItemDto> data;

    public RequestMovie() {
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public void setData(List<MovieItemDto> data) {
        this.data = data;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public List<MovieItemDto> getData() {
        return data;
    }

}
