
package com.alexanderdorow.popularmovies.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieRequest {

    @SerializedName("total_pages")
    @Expose
    private int totalPages;
    @SerializedName("results")
    @Expose
    private List<MovieItemDto> movieItemDtos;

    public MovieRequest() {
    }


    public int getTotalPages() {
        return totalPages;
    }

    public List<MovieItemDto> getMovieItemDtos() {
        return movieItemDtos;
    }

}
