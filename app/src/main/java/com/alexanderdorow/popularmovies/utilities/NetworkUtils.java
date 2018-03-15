package com.alexanderdorow.popularmovies.utilities;

import android.net.Uri;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class NetworkUtils {

    private static final String API_TOKEN = "insert your api key here";
    private static final String MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3/movie";
    private static final String API_KEY_PARAM = "api_key";
    private static final String PAGE_PARAM = "page";
    private static final String BASE_IMAGE_PATH = "https://image.tmdb.org/t/p/w500/%s";
    private static final OkHttpClient client = new OkHttpClient();
    public static final String POPULAR = "popular";
    public static final String TOP_RATED = "top_rated";

    public static URL buildUrl(String prefix, @Nullable String page, @Nullable String id) {
        Uri.Builder builder = Uri.parse(MOVIE_DB_BASE_URL).buildUpon();
        if(id != null){
            builder.appendPath(id);
        }
        builder.appendPath(prefix)
                .appendQueryParameter(API_KEY_PARAM, API_TOKEN);
        if (page != null) {
            builder.appendQueryParameter(PAGE_PARAM, page);
        }
        Uri uri = builder.build();
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }


    public static String getResponseFromUrl(URL url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        ResponseBody body = response.body();
        return body == null ? null : body.string();
    }

    public static String getImageUrl(String posterPath) {
        return String.format(BASE_IMAGE_PATH, posterPath);
    }
}
