package com.alexanderdorow.popularmovies.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alexanderdorow.popularmovies.R;
import com.alexanderdorow.popularmovies.adapter.MoviesAdapter;
import com.alexanderdorow.popularmovies.api.MovieApi;
import com.alexanderdorow.popularmovies.api.dto.MovieItemDto;
import com.alexanderdorow.popularmovies.api.dto.RequestMovie;
import com.alexanderdorow.popularmovies.data.entry.MovieEntry;
import com.alexanderdorow.popularmovies.utilities.DialogUtils;
import com.alexanderdorow.popularmovies.utilities.RetrofitUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.OnMovieItemSelected, Callback<RequestMovie> {

    @BindView(R.id.rv_movie_list)
    RecyclerView movieList;
    @BindView(R.id.loading)
    ProgressBar loading;
    @BindView(R.id.tv_error)
    TextView error;

    private MoviesAdapter adapter;
    private int page = 1;
    private int visibleThreshold = 5;
    private int totalItemCount;
    private int lastVisibleItem;
    private int totalPages = 1;
    private boolean filmsLoaded;
    private MovieApi api;

    public static final String[] MOVIE_PROJECTION = {
            MovieEntry._ID,
            MovieEntry.COLUMN_TITLE,
            MovieEntry.COLUMN_POSTER_PATH,
            MovieEntry.COLUMN_OVERVIEW,
            MovieEntry.COLUMN_RELEASE_DATE,
            MovieEntry.COLUMN_VOTE_AVG
    };
    private int category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        api = RetrofitUtils.getMovieApi();
        adapter = new MoviesAdapter(this);
        movieList.setAdapter(adapter);
        GridLayoutManager gridLayoutManager;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridLayoutManager = new GridLayoutManager(this, 1, GridLayout.HORIZONTAL, false);
        } else {
            gridLayoutManager = new GridLayoutManager(this, 2, GridLayout.VERTICAL, false);
        }
        movieList.setLayoutManager(gridLayoutManager);
        movieList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (category == 2) {
                    return;
                }
                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                totalItemCount = layoutManager.getItemCount();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                if (totalItemCount <= (lastVisibleItem + visibleThreshold) && filmsLoaded) {
                    loadMovieData();
                }
            }
        });

        loadMovieData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (category == 2) {
            getMoviesFromDatabase();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_filter:
                openFilterDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onPreExecuteRequest() {
        filmsLoaded = false;
        if (page == 1) {
            movieList.setVisibility(View.GONE);
            error.setVisibility(View.GONE);
            loading.setVisibility(View.VISIBLE);
        }
    }

    private void openFilterDialog() {
        AlertDialog builder = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.filter))
                .setItems(R.array.filter_options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        page = 1;
                        category = which;
                         switch (category) {
                            case 0:
                                onPreExecuteRequest();
                                api.getAllMovies(MovieApi.POPULAR, MovieApi.API_KEY, page).enqueue(MainActivity.this);
                                break;
                            case 1:
                                onPreExecuteRequest();
                                api.getAllMovies(MovieApi.TOP_RATED, MovieApi.API_KEY, page).enqueue(MainActivity.this);
                                break;
                            case 2:
                                getMoviesFromDatabase();
                                break;
                        }
                    }
                }).create();
        builder.show();
    }

    private void loadMovieData() {
        onPreExecuteRequest();
        if (page > totalPages) {
            return;
        }
        api.getAllMovies(MovieApi.POPULAR, MovieApi.API_KEY, page).enqueue(MainActivity.this);
    }

    @Override
    public void onMovieSelected(MovieItemDto movie) {
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        intent.putExtra(DetailsActivity.EXTRA_MOVIE_DATA, movie);
        startActivity(intent);
    }

    @OnClick(R.id.tv_error)
    public void onErrorClick(View view) {
        loadMovieData();
    }

    public void getMoviesFromDatabase() {
        loading.setVisibility(View.VISIBLE);
        adapter.setItems(new ArrayList<MovieItemDto>());
        Cursor cursor = getContentResolver().query(MovieEntry.CONTENT_URI, MOVIE_PROJECTION, null, null, null);
        if (cursor == null || cursor.getCount() == 0) {
            error.setText(R.string.no_movies_on_database_error);
            error.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
            return;
        }
        List<MovieItemDto> moviesFromDatabase = new ArrayList<>();
        while (cursor.moveToNext()) {
            int index = -1;
            MovieItemDto movieItemDto = new MovieItemDto();
            movieItemDto.setId(cursor.getString(++index));
            movieItemDto.setTitle(cursor.getString(++index));
            movieItemDto.setPosterPath(cursor.getString(++index));
            movieItemDto.setOverview(cursor.getString(++index));
            movieItemDto.setReleaseDate(cursor.getString(++index));
            movieItemDto.setVoteAverage(cursor.getFloat(++index));
            movieItemDto.setFavorite(true);
            moviesFromDatabase.add(movieItemDto);
        }
        cursor.close();
        adapter.setItems(moviesFromDatabase);
        loading.setVisibility(View.INVISIBLE);
    }


    private void showErrorDialog() {
        DialogUtils.showNetworkError(R.string.error_message_all, R.string.ops,
                MainActivity.this,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        loadMovieData();
                    }
                },
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        if (adapter.getItemCount() > 0) {
                            return;
                        }
                        error.setText(R.string.error_message_all);
                        error.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void onResponse(Call<RequestMovie> call, Response<RequestMovie> response) {
        RequestMovie movieRequest = response.body();
        if (movieRequest == null || movieRequest.getData() == null) {
            showErrorDialog();
            return;
        }

        totalPages = movieRequest.getTotalPages();

        if (page == 1) {
            adapter.setItems(movieRequest.getData());
        } else {
            adapter.addItems(movieRequest.getData());
        }

        movieList.setVisibility(View.VISIBLE);
        loading.setVisibility(View.GONE);
        page++;
        filmsLoaded = true;

    }

    @Override
    public void onFailure(Call<RequestMovie> call, Throwable t) {
        showErrorDialog();
    }
}
