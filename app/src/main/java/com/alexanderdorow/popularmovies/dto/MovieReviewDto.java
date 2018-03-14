package com.alexanderdorow.popularmovies.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MovieReviewDto {

    @SerializedName("author")
    @Expose
    private String author;
    @SerializedName("content")
    @Expose
    private String content;

    public MovieReviewDto() {
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
