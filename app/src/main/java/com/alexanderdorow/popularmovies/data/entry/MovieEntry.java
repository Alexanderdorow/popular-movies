package com.alexanderdorow.popularmovies.data.entry;

import android.net.Uri;
import android.provider.BaseColumns;

import com.alexanderdorow.popularmovies.data.MovieContract;

public final class MovieEntry implements BaseColumns {
    public static Uri CONTENT_URI = MovieContract.BASE_CONTENT_URI.buildUpon()
            .appendPath(MovieContract.PATH_MOVIE)
            .build();

    public static String TABLE_NAME = "movie";
    public static String COLUMN_VOTE_AVG = "voteAverage";
    public static String COLUMN_TITLE = "title";
    public static String COLUMN_POSTER_PATH = "poster_path";
    public static String COLUMN_OVERVIEW = "overview";
    public static String COLUMN_RELEASE_DATE = "release_date";

    public static String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + _ID + " INTEGER PRIMARY KEY NOT NULL, "
            + COLUMN_VOTE_AVG + " REAL NOT NULL, "
            + COLUMN_TITLE + " TEXT NOT NULL, "
            + COLUMN_POSTER_PATH + " TEXT, "
            + COLUMN_OVERVIEW + " TEXT DEFAULT '', "
            + COLUMN_RELEASE_DATE + " TEXT DEFAULT '');";
}
