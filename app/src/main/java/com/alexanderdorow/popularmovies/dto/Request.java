
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
    private List<T> movieItemDtos;

    public Request() {
    }


    public int getTotalPages() {
        return totalPages;
    }

    public List<T> getMovieItemDtos() {
        return movieItemDtos;
    }

}
