package com.alexanderdorow.popularmovies.asynctask;

import android.os.AsyncTask;

import com.alexanderdorow.popularmovies.dto.MovieItemDto;
import com.alexanderdorow.popularmovies.dto.Request;
import com.alexanderdorow.popularmovies.utilities.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;

public class FetchMovieDataTask extends AsyncTask<String, Void, Request<MovieItemDto>> {

    public interface ProgressListener {

        void onPreExecute();

        void onPostExecute(Request<MovieItemDto> movieRequest);

    }

    private final ProgressListener progressListener;

    public FetchMovieDataTask(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressListener.onPreExecute();
    }

    //
    @Override
    protected Request<MovieItemDto> doInBackground(String... strings) {
        URL url = NetworkUtils.buildUrl(strings[0], strings[1], null);
        Request<MovieItemDto> movieRequest = null;
        try {
            String responseFromUrl = NetworkUtils.getResponseFromUrl(url);
            Type type = new TypeToken<Request<MovieItemDto>>() {
            }.getType();

            movieRequest = new Gson().fromJson(responseFromUrl, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return movieRequest;
    }

    @Override
    protected void onPostExecute(final Request<MovieItemDto> movieRequest) {
        super.onPostExecute(movieRequest);
        progressListener.onPostExecute(movieRequest);

    }
}
