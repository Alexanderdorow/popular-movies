
package com.alexanderdorow.popularmovies.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Request<T> {

    @SerializedName("total_pages")
    @Expose
    private int totalPages;
    @SerializedName("results")
    @Expose
    private List<T> data;

    public Request() {
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public List<T> getData() {
        return data;
    }

}
