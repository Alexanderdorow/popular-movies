package com.alexanderdorow.popularmovies;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alexanderdorow.popularmovies.adapter.ReviewsAdapter;
import com.alexanderdorow.popularmovies.adapter.TrailersAdapter;
import com.alexanderdorow.popularmovies.asynctask.FetchMovieReviewInfoTask;
import com.alexanderdorow.popularmovies.asynctask.FetchMovieTrailerInfoTask;
import com.alexanderdorow.popularmovies.data.entry.MovieEntry;
import com.alexanderdorow.popularmovies.dto.MovieItemDto;
import com.alexanderdorow.popularmovies.dto.MovieReviewDto;
import com.alexanderdorow.popularmovies.dto.MovieTrailerDto;
import com.alexanderdorow.popularmovies.dto.Request;
import com.alexanderdorow.popularmovies.utilities.GlideUtils;
import com.alexanderdorow.popularmovies.utilities.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends AppCompatActivity implements TrailersAdapter.OnTrailerClickedListener {


    public static final String YOUTUBE_BASE_URL = "http://www.youtube.com/watch?v=%s";
    public static final String REVIEWS_PATH = "reviews";
    public static final String VIDEOS_PATH = "videos";
    public static final String TRAILER_MESSAGE = "Hey see this movie trailer: \n." + YOUTUBE_BASE_URL;
    public static final String TEXT_PLAIN = "text/plain";
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
        reviewList.setNestedScrollingEnabled(false);
        trailerList.setNestedScrollingEnabled(false);
        reviewList.setAdapter(reviewsAdapter);
        loadMovieData();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        MenuItem item = menu.findItem(R.id.action_favorite);
        item.setIcon(movie.isFavorite() ? R.drawable.ic_favorite_selected : R.drawable.ic_favorite);
        item.setChecked(movie.isFavorite());
        return super.onCreateOptionsMenu(menu);
    }

    private void saveMovieOnDatabase() {
        getContentResolver().bulkInsert(MovieEntry.CONTENT_URI, new ArrayList<ContentValues>() {{
            add(getContentValues(movie));
        }}.toArray(new ContentValues[1]));
        Toast.makeText(this, R.string.movie_saved, Toast.LENGTH_SHORT).show();
    }

    private ContentValues getContentValues(MovieItemDto movie) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieEntry._ID, movie.getId());
        contentValues.put(MovieEntry.COLUMN_TITLE, movie.getTitle());
        contentValues.put(MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
        contentValues.put(MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
        contentValues.put(MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        contentValues.put(MovieEntry.COLUMN_VOTE_AVG, movie.getVoteAverage());
        return contentValues;
    }

    private void deleteMovieOnDatabase() {
        getContentResolver().delete(MovieEntry.CONTENT_URI, "_id = ?", new String[]{movie.getId()});
        Toast.makeText(this, R.string.movie_removed, Toast.LENGTH_SHORT).show();
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
            case R.id.action_favorite:
                item.setChecked(!item.isChecked());
                if (item.isChecked()) {
                    item.setIcon(R.drawable.ic_favorite_selected);
                    saveMovieOnDatabase();
                } else {
                    deleteMovieOnDatabase();
                    item.setIcon(R.drawable.ic_favorite);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getMovieExtraInfo() {
        new FetchMovieReviewInfoTask(new FetchMovieReviewInfoTask.ProgressListener() {
            @Override
            public void onPostExecute(Request<MovieReviewDto> movieRequest) {
                if(movieRequest == null){
					return;
				}
				List<MovieReviewDto> data = movieRequest.getData();
                if (data != null) {
                    reviewsAdapter.setItems(data);
                }
            }
        }).execute(REVIEWS_PATH, movie.getId());
        new FetchMovieTrailerInfoTask(new FetchMovieTrailerInfoTask.ProgressListener() {
            @Override
            public void onPostExecute(Request<MovieTrailerDto> movieRequest) {
                if(movieRequest == null){
					return;
				}               
				List<MovieTrailerDto> data = movieRequest.getData();
                if (data != null) {
                    trailersAdapter.setItems(data);
                }
            }
        }).execute(VIDEOS_PATH, movie.getId());
    }

    @Override
    public void onTrailerClick(final MovieTrailerDto dto) {
        AlertDialog builder = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.choose))
                .setItems(R.array.trailer_options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(YOUTUBE_BASE_URL, dto.getKey()))));
                                break;
                            case 1:
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, String.format(TRAILER_MESSAGE, dto.getKey()));
                                sendIntent.setType(TEXT_PLAIN);
                                startActivity(sendIntent);
                                break;
                        }
                    }
                }).create();
        builder.show();
    }
}
