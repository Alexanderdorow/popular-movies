package com.alexanderdorow.popularmovies.data;

import android.net.Uri;

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.alexanderdorow.popularmovies.data";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIE = "movie";
    public static final String PATH_REVIEW = "review";
    public static final String PATH_TRAILER = "trailer";
}
