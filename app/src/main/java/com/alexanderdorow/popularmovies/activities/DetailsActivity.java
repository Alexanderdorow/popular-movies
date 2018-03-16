package com.alexanderdorow.popularmovies.activities;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alexanderdorow.popularmovies.R;
import com.alexanderdorow.popularmovies.adapter.ReviewsAdapter;
import com.alexanderdorow.popularmovies.adapter.TrailersAdapter;
import com.alexanderdorow.popularmovies.api.MovieApi;
import com.alexanderdorow.popularmovies.api.dto.MovieItemDto;
import com.alexanderdorow.popularmovies.api.dto.MovieReviewDto;
import com.alexanderdorow.popularmovies.api.dto.MovieTrailerDto;
import com.alexanderdorow.popularmovies.api.dto.RequestReview;
import com.alexanderdorow.popularmovies.api.dto.RequestTrailer;
import com.alexanderdorow.popularmovies.data.entry.MovieEntry;
import com.alexanderdorow.popularmovies.utilities.GlideUtils;
import com.alexanderdorow.popularmovies.utilities.RetrofitUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsActivity extends AppCompatActivity implements TrailersAdapter.OnTrailerClickedListener {

    @BindView(R.id.rb_vote_average)
    RatingBar movieVoteAverage;
    @BindView(R.id.iv_poster)
    ImageView moviePoster;
    @BindView(R.id.tv_movie_title)
    TextView movieTitle;
    @BindView(R.id.tv_overview)
    TextView movieOverview;
    @BindView(R.id.tv_released_date)
    TextView movieReleasedDate;
    @BindView(R.id.rv_trailers)
    RecyclerView trailerList;
    @BindView(R.id.rv_reviews)
    RecyclerView reviewList;

    public static final String YOUTUBE_BASE_URL = "http://www.youtube.com/watch?v=%s";
    public static final String TRAILER_MESSAGE = "Hey see this movie trailer: \n." + YOUTUBE_BASE_URL;
    public static final String TEXT_PLAIN = "text/plain";
    public static final String EXTRA_MOVIE_DATA = "EXTRA_MOVIE_DATA";
    private ReviewsAdapter reviewsAdapter;
    private TrailersAdapter trailersAdapter;
    private MovieItemDto movie;
    private MovieApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        LinearLayoutManager trailerManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        LinearLayoutManager reviewManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        trailersAdapter = new TrailersAdapter(this);
        trailerList.setLayoutManager(trailerManager);
        trailerList.setNestedScrollingEnabled(false);
        trailerList.setAdapter(trailersAdapter);
        reviewsAdapter = new ReviewsAdapter();
        reviewList.setLayoutManager(reviewManager);
        reviewList.setNestedScrollingEnabled(false);
        reviewList.setAdapter(reviewsAdapter);
        api = RetrofitUtils.getMovieApi();
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
        GlideUtils.showFadedImage(DetailsActivity.this, GlideUtils.getImageUrl(movie.getPosterPath()), moviePoster);
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
                    movie.setFavorite(true);
                    item.setIcon(R.drawable.ic_favorite_selected);
                    saveMovieOnDatabase();
                } else {
                    movie.setFavorite(false);
                    deleteMovieOnDatabase();
                    item.setIcon(R.drawable.ic_favorite);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getMovieExtraInfo() {
        api.getMovieReviewInfo(movie.getId(), MovieApi.API_KEY).enqueue(new Callback<RequestReview>() {
            @Override
            public void onResponse(Call<RequestReview> call, Response<RequestReview> response) {
                RequestReview requestReview = response.body();
                if (requestReview == null) {
                    return;
                }
                List<MovieReviewDto> data = requestReview.getData();
                if (data != null) {
                    reviewsAdapter.setItems(data);
                }
            }

            @Override
            public void onFailure(Call<RequestReview> call, Throwable t) {

            }
        });
        api.getMovieTrailerInfo(movie.getId(), MovieApi.API_KEY).enqueue(new Callback<RequestTrailer>() {
            @Override
            public void onResponse(Call<RequestTrailer> call, Response<RequestTrailer> response) {
                RequestTrailer requestTrailer = response.body();
                if (requestTrailer == null) {
                    return;
                }
                List<MovieTrailerDto> data = requestTrailer.getData();
                if (data != null) {
                    trailersAdapter.setItems(data);
                }
            }

            @Override
            public void onFailure(Call<RequestTrailer> call, Throwable t) {

            }
        });
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
