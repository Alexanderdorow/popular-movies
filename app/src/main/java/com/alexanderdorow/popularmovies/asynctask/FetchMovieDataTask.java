package com.alexanderdorow.popularmovies.asynctask;

import android.os.AsyncTask;

import com.alexanderdorow.popularmovies.dto.MovieRequest;
import com.alexanderdorow.popularmovies.utilities.NetworkUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URL;

public class FetchMovieDataTask extends AsyncTask<String, Void, MovieRequest> {

    public interface ProgressListener {

        void onPreExecute();

        void onPostExecute(MovieRequest movieRequest);

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

    @Override
    protected MovieRequest doInBackground(String... strings) {
        URL url = NetworkUtils.buildUrl(strings[0], strings[1]);
        MovieRequest movieRequest = null;
        try {
            String responseFromUrl = NetworkUtils.getResponseFromUrl(url);
            movieRequest = new Gson().fromJson(responseFromUrl, MovieRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return movieRequest;
    }

    @Override
    protected void onPostExecute(final MovieRequest movieRequest) {
        super.onPostExecute(movieRequest);
        progressListener.onPostExecute(movieRequest);

    }
}
