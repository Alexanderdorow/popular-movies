package com.alexanderdorow.popularmovies;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alexanderdorow.popularmovies.adapter.MoviesAdapter;
import com.alexanderdorow.popularmovies.asynctask.FetchMovieDataTask;
import com.alexanderdorow.popularmovies.data.entry.MovieEntry;
import com.alexanderdorow.popularmovies.dto.MovieItemDto;
import com.alexanderdorow.popularmovies.dto.Request;
import com.alexanderdorow.popularmovies.utilities.DialogUtils;
import com.alexanderdorow.popularmovies.utilities.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.OnMovieItemSelected, FetchMovieDataTask.ProgressListener, View.OnClickListener {

    public static final String CURRENT_MOVIE_CATEGORY = "currentMovieCategory";
    public static final String OLD_LIST = "oldList";
    public static final String CURRENT_PAGE = "currentPage";
    private RecyclerView movieList;
    private MoviesAdapter adapter;
    private ProgressBar loading;
    private TextView error;
    private int page = 1;
    private int visibleThreshold = 5;
    private int totalItemCount;
    private int lastVisibleItem;
    private int totalPages = 1;
    private boolean filmsLoaded;
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
        Log.i("started", "");
        movieList = findViewById(R.id.rv_movie_list);
        loading = findViewById(R.id.loading);
        error = findViewById(R.id.tv_error);
        adapter = new MoviesAdapter(this);
        movieList.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayout.VERTICAL, false);
        movieList.setLayoutManager(gridLayoutManager);
        movieList.setHasFixedSize(true);
        error.setOnClickListener(this);
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
        if (savedInstanceState == null) {
            loadMovieData();
        } else {
            page = savedInstanceState.getInt(CURRENT_PAGE);
            category = savedInstanceState.getInt(CURRENT_MOVIE_CATEGORY);
            adapter.setItems(savedInstanceState.<MovieItemDto>getParcelableArrayList(OLD_LIST));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //REFRESH ITEMS WHEN USER BACK
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

    private void openFilterDialog() {
        AlertDialog builder = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.filter))
                .setItems(R.array.filter_options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        page = 1;
                        category = which;
                        switch (category) {
                            case 0:
                                new FetchMovieDataTask(MainActivity.this).execute(NetworkUtils.POPULAR, String.valueOf(page));
                                break;
                            case 1:
                                new FetchMovieDataTask(MainActivity.this).execute(NetworkUtils.TOP_RATED, String.valueOf(page));
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
        if (page > totalPages) {
            return;
        }

        new FetchMovieDataTask(this).execute(NetworkUtils.POPULAR, String.valueOf(page));
    }

    @Override
    public void onMovieSelected(MovieItemDto movie) {
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        intent.putExtra(DetailsActivity.EXTRA_MOVIE_DATA, movie);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        loadMovieData();
    }

    @Override
    public void onPreExecute() {
        filmsLoaded = false;
        if (page == 1) {
            movieList.setVisibility(View.GONE);
            error.setVisibility(View.GONE);
            loading.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPostExecute(Request<MovieItemDto> movieRequest) {
        if (movieRequest == null || movieRequest.getData() == null) {
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

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putInt(CURRENT_MOVIE_CATEGORY, category);
        outState.putParcelableArrayList(OLD_LIST, adapter.getItems());
        outState.putInt(CURRENT_PAGE, page);
        super.onSaveInstanceState(outState, outPersistentState);
    }
}
