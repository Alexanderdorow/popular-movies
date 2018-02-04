
package com.alexanderdorow.popularmovies.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MovieItemDto implements Parcelable {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("vote_average")
    @Expose
    private float voteAverage;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("poster_path")
    @Expose
    private String posterPath;
    @SerializedName("overview")
    @Expose
    private String overview;
    @SerializedName("release_date")
    @Expose
    private String releaseDate;

    public final static Creator<MovieItemDto> CREATOR = new Creator<MovieItemDto>() {

        public MovieItemDto createFromParcel(Parcel in) {
            return new MovieItemDto(in);
        }

        public MovieItemDto[] newArray(int size) {
            return (new MovieItemDto[size]);
        }

    };

    private MovieItemDto(Parcel in) {
        this.id = in.readInt();
        this.voteAverage = in.readFloat();
        this.title = in.readString();
        this.posterPath = in.readString();
        this.overview = in.readString();
        this.releaseDate = in.readString();
    }

    public int getId() {
        return id;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(voteAverage);
        dest.writeValue(title);
        dest.writeValue(posterPath);
        dest.writeValue(overview);
        dest.writeValue(releaseDate);
    }

    public int describeContents() {
        return 0;
    }

}
