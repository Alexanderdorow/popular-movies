
package com.alexanderdorow.popularmovies.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MovieItemDto implements Parcelable {

    @SerializedName("id")
    @Expose
    private String id;
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
    private boolean isFavorite;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(float voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public static Creator<MovieItemDto> getCREATOR() {
        return CREATOR;
    }

    public MovieItemDto() {
    }

    protected MovieItemDto(Parcel in) {
        id = in.readString();
        voteAverage = in.readFloat();
        title = in.readString();
        posterPath = in.readString();
        overview = in.readString();
        releaseDate = in.readString();
        isFavorite = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeFloat(voteAverage);
        dest.writeString(title);
        dest.writeString(posterPath);
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeByte((byte) (isFavorite ? 1 : 0));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MovieItemDto> CREATOR = new Parcelable.Creator<MovieItemDto>() {
        @Override
        public MovieItemDto createFromParcel(Parcel in) {
            return new MovieItemDto(in);
        }

        @Override
        public MovieItemDto[] newArray(int size) {
            return new MovieItemDto[size];
        }
    };
}