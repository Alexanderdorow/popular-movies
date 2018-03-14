package com.alexanderdorow.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.alexanderdorow.popularmovies.adapter.ReviewsAdapter;
import com.alexanderdorow.popularmovies.adapter.TrailersAdapter;
import com.alexanderdorow.popularmovies.asynctask.FetchMovieReviewInfoTask;
import com.alexanderdorow.popularmovies.asynctask.FetchMovieTrailerInfoTask;
import com.alexanderdorow.popularmovies.dto.MovieItemDto;
import com.alexanderdorow.popularmovies.dto.MovieReviewDto;
import com.alexanderdorow.popularmovies.dto.MovieTrailerDto;
import com.alexanderdorow.popularmovies.dto.Request;
import com.alexanderdorow.popularmovies.utilities.GlideUtils;
import com.alexanderdorow.popularmovies.utilities.NetworkUtils;

import java.util.List;

public class DetailsActivity extends AppCompatActivity implements TrailersAdapter.OnTrailerClickedListener {


    public static final String YOUTUBE_BASE_URL = "http://www.youtube.com/watch?v=%s";
    public static final String REVIEWS_PATH = "reviews";
    public static final String VIDEOS_PATH = "videos";
    private ReviewsAdapter reviewsAdapter;
    private TrailersAdapter trailersAdapter;

    public static final String EXTRA_MOVIE_DATA = "EXTRA_MOVIE_DATA";
    private RatingBar movieVoteAverage;
    private ImageView moviePoster;
    private TextView movieTitle;
    private TextView movieOverview;
    private TextView movieReleasedDate;
    private MovieItemDto movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        trailersAdapter = new TrailersAdapter(this);
        reviewsAdapter = new ReviewsAdapter();
        movieVoteAverage = findViewById(R.id.rb_vote_average);
        moviePoster = findViewById(R.id.iv_poster);
        movieTitle = findViewById(R.id.tv_movie_title);
        movieOverview = findViewById(R.id.tv_overview);
        movieReleasedDate = findViewById(R.id.tv_released_date);
        RecyclerView trailerList = findViewById(R.id.rv_trailers);
        RecyclerView reviewList = findViewById(R.id.rv_reviews);
        LinearLayoutManager trailerManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        trailerList.setLayoutManager(trailerManager);
        trailerList.setHasFixedSize(true);
        trailerList.setAdapter(trailersAdapter);
        LinearLayoutManager reviewManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        reviewList.setLayoutManager(reviewManager);
        reviewList.setHasFixedSize(true);
        reviewList.setAdapter(reviewsAdapter);
        loadMovieData();
    }

    private void loadMovieData() {
        movie = getIntent().getParcelableExtra(EXTRA_MOVIE_DATA);
        movieTitle.setText(movie.getTitle());
        movieReleasedDate.setText(movie.getReleaseDate().substring(0, 4));
        movieVoteAverage.setRating(movie.getVoteAverage() / 2);
        movieOverview.setText(movie.getOverview());
        GlideUtils.showFadedImage(DetailsActivity.this, NetworkUtils.getImageUrl(movie.getPosterPath()), moviePoster);
        getMovieExtraInfo();
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

    public void getMovieExtraInfo() {
        new FetchMovieReviewInfoTask(new FetchMovieReviewInfoTask.ProgressListener() {
            @Override
            public void onPostExecute(Request<MovieReviewDto> movieRequest) {
                List<MovieReviewDto> data = movieRequest.getData();
                if (data != null) {
                    reviewsAdapter.setItems(data);
                    Log.i("", "");
                }
            }
        }).execute(REVIEWS_PATH, movie.getId());
        new FetchMovieTrailerInfoTask(new FetchMovieTrailerInfoTask.ProgressListener() {
            @Override
            public void onPostExecute(Request<MovieTrailerDto> movieRequest) {
                List<MovieTrailerDto> data = movieRequest.getData();
                if (data != null) {
                    trailersAdapter.setItems(data);
                }
            }
        }).execute(VIDEOS_PATH, movie.getId());
    }

    @Override
    public void onTrailerClick(MovieTrailerDto dto) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(YOUTUBE_BASE_URL, dto.getKey()))));
    }
}
