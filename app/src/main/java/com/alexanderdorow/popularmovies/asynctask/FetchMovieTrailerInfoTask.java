package com.alexanderdorow.popularmovies.asynctask;

import android.os.AsyncTask;

import com.alexanderdorow.popularmovies.dto.MovieTrailerDto;
import com.alexanderdorow.popularmovies.dto.Request;
import com.alexanderdorow.popularmovies.utilities.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;

public class FetchMovieTrailerInfoTask extends AsyncTask<String, Void, Request<MovieTrailerDto>> {

    public FetchMovieTrailerInfoTask(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public interface ProgressListener {

        void onPostExecute(Request<MovieTrailerDto> movieRequest);

    }

    private final ProgressListener progressListener;

    @Override
    protected Request<MovieTrailerDto> doInBackground(String... strings) {
        URL url = NetworkUtils.buildUrl(strings[0], "1", strings[1]);
        Request<MovieTrailerDto> movieInfoRequest = null;
        try {
            String responseFromUrl = NetworkUtils.getResponseFromUrl(url);
            Type type = new TypeToken<Request<MovieTrailerDto>>() {
            }.getType();
            movieInfoRequest = new Gson().fromJson(responseFromUrl, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return movieInfoRequest;
    }

    @Override
    protected void onPostExecute(Request<MovieTrailerDto> request) {
        progressListener.onPostExecute(request);
        super.onPostExecute(request);
    }
}
