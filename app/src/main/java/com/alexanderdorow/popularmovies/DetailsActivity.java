package com.alexanderdorow.popularmovies;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.alexanderdorow.popularmovies.utilities.GlideUtils;
import com.alexanderdorow.popularmovies.utilities.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;

public class DetailsActivity extends AppCompatActivity {

    private String movieId;
    private RatingBar movieVoteAverage;
    private ImageView moviePoster;
    private TextView movieTitle;
    private TextView movieOverview;
    private TextView movieReleasedDate;
    private TextView movieTime;
    private ProgressBar loading;
    private LinearLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        movieId = getIntent().getStringExtra(Intent.EXTRA_UID);
        movieVoteAverage = findViewById(R.id.rb_vote_average);
        moviePoster = findViewById(R.id.iv_poster);
        movieTitle = findViewById(R.id.tv_movie_title);
        movieOverview = findViewById(R.id.tv_overview);
        movieReleasedDate = findViewById(R.id.tv_released_date);
        movieTime = findViewById(R.id.tv_time);
        loading = findViewById(R.id.loading);
        rootLayout = findViewById(R.id.root_layout);
        loadMovieData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadMovieData() {
        new RequestMovieDataTask().execute();
    }

    private void showLoading(boolean show) {
        rootLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        loading.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private class RequestMovieDataTask extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading(true);
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            URL url = NetworkUtils.buildUrl(movieId, null);
            JSONObject object = null;
            try {
                String responseFromUrl = NetworkUtils.getResponseFromUrl(url);
                object = new JSONObject(responseFromUrl);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return object;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            showLoading(false);
            if (jsonObject == null) {
                NetworkUtils.showNetworkError(R.string.error_message_singular, R.string.ops,
                        DetailsActivity.this, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                loadMovieData();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                onBackPressed();
                            }
                        }, new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                onBackPressed();
                            }
                        });
                return;
            }
            try {
                movieTitle.setText(jsonObject.getString("title"));
                movieReleasedDate.setText(jsonObject.getString("release_date").substring(0, 4));
                movieVoteAverage.setRating(Float.parseFloat(jsonObject.getString("vote_average")) / 2);
                movieOverview.setText(jsonObject.getString("overview"));
                movieTime.setText(String.format(Locale.getDefault(), "%d min", jsonObject.getInt("runtime")));
                String url = String.format(NetworkUtils.BASE_IMAGE_PATH, jsonObject.getString("poster_path"));
                GlideUtils.showFadedImage(DetailsActivity.this, url, moviePoster);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
