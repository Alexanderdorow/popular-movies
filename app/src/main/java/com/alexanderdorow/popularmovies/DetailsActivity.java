package com.alexanderdorow.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.alexanderdorow.popularmovies.dto.MovieItemDto;
import com.alexanderdorow.popularmovies.utilities.GlideUtils;
import com.alexanderdorow.popularmovies.utilities.NetworkUtils;

public class DetailsActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE_DATA = "EXTRA_MOVIE_DATA";
    private RatingBar movieVoteAverage;
    private ImageView moviePoster;
    private TextView movieTitle;
    private TextView movieOverview;
    private TextView movieReleasedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        movieVoteAverage = findViewById(R.id.rb_vote_average);
        moviePoster = findViewById(R.id.iv_poster);
        movieTitle = findViewById(R.id.tv_movie_title);
        movieOverview = findViewById(R.id.tv_overview);
        movieReleasedDate = findViewById(R.id.tv_released_date);
        loadMovieData();
    }

    private void loadMovieData() {
        MovieItemDto movie = getIntent().getParcelableExtra(EXTRA_MOVIE_DATA);
        movieTitle.setText(movie.getTitle());
        movieReleasedDate.setText(movie.getReleaseDate().substring(0, 4));
        movieVoteAverage.setRating(movie.getVoteAverage() / 2);
        movieOverview.setText(movie.getOverview());
        GlideUtils.showFadedImage(DetailsActivity.this, NetworkUtils.getImageUrl(movie.getPosterPath()), moviePoster);
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

}
