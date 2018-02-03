package com.alexanderdorow.popularmovies.utilities;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import com.alexanderdorow.popularmovies.R;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class NetworkUtils {

    private static final String API_TOKEN = "INSERT YOUR API TOKEN HERE";
    private static final String MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3/movie";
    private static final String API_KEY_PARAM = "api_key";
    private static final String PAGE_PARAM = "page";
    public static final String BASE_IMAGE_PATH = "https://image.tmdb.org/t/p/w500/%s";
    private static final OkHttpClient client = new OkHttpClient();

    public static URL buildUrl(String prefix, @Nullable Integer page) {
        Uri.Builder builder = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                .appendPath(prefix)
                .appendQueryParameter(API_KEY_PARAM, API_TOKEN);
        if (page != null) {
            builder.appendQueryParameter(PAGE_PARAM, String.valueOf(page));
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

    public static void showNetworkError(int message, int title, Context context,
                                        DialogInterface.OnClickListener onClickListener,
                                        DialogInterface.OnClickListener cancelClick,
                                        DialogInterface.OnCancelListener cancelListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.try_again, onClickListener);
        if (cancelClick != null) {
            builder.setNegativeButton(R.string.cancel, cancelClick);
        }
        if (cancelListener != null) {
            builder.setOnCancelListener(cancelListener);
        }

        builder.show();
    }
}
