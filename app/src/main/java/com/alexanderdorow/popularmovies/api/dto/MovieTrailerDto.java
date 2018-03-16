package com.alexanderdorow.popularmovies.api.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MovieTrailerDto implements Parcelable {

    @SerializedName("key")
    @Expose
    private String key;
    @SerializedName("name")
    @Expose
    private String name;

    public MovieTrailerDto() {
    }

    public MovieTrailerDto(String id, String name) {
        this.key = id;
        this.name = name;
    }

    protected MovieTrailerDto(Parcel in) {
        key = in.readString();
        name = in.readString();
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(name);
    }

    public static final Parcelable.Creator<MovieTrailerDto> CREATOR = new Parcelable.Creator<MovieTrailerDto>() {
        @Override
        public MovieTrailerDto createFromParcel(Parcel in) {
            return new MovieTrailerDto(in);
        }

        @Override
        public MovieTrailerDto[] newArray(int size) {
            return new MovieTrailerDto[size];
        }
    };

}
