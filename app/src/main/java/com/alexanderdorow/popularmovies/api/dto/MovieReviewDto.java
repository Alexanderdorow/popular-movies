package com.alexanderdorow.popularmovies.api.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MovieReviewDto implements Parcelable {

    @SerializedName("author")
    @Expose
    private String author;
    @SerializedName("content")
    @Expose
    private String content;

    public MovieReviewDto() {
    }

    public MovieReviewDto(Parcel in) {
        author = in.readString();
        content = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(author);
        parcel.writeString(content);
    }

    public static final Parcelable.Creator<MovieReviewDto> CREATOR = new Parcelable.Creator<MovieReviewDto>() {
        @Override
        public MovieReviewDto createFromParcel(Parcel in) {
            return new MovieReviewDto(in);
        }

        @Override
        public MovieReviewDto[] newArray(int size) {
            return new MovieReviewDto[size];
        }
    };
}
