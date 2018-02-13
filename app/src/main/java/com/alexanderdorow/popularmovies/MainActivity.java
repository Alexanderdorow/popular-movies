package com.alexanderdorow.popularmovies;

import android.content.DialogInterface;
import android.content.Intent;
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

import com.alexanderdorow.popularmovies.adapter.MoviesAdapter;
import com.alexanderdorow.popularmovies.asynctask.FetchMovieDataTask;
import com.alexanderdorow.popularmovies.dto.MovieItemDto;
import com.alexanderdorow.popularmovies.dto.MovieRequest;
import com.alexanderdorow.popularmovies.utilities.DialogUtils;
import com.alexanderdorow.popularmovies.utilities.NetworkUtils;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.OnMovieItemSelected, FetchMovieDataTask.ProgressListener, View.OnClickListener {

    private RecyclerView movieList;
    private MoviesAdapter adapter;
    private ProgressBar loading;
    private TextView error;
    private int page = 1;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private int totalPages = 1;
    private boolean filmsLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                        switch (which) {
                            case 0:
                                new FetchMovieDataTask(MainActivity.this).execute(NetworkUtils.POPULAR, String.valueOf(page));
                                break;
                            case 1:
                                new FetchMovieDataTask(MainActivity.this).execute(NetworkUtils.TOP_RATED, String.valueOf(page));
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
    public void onPostExecute(MovieRequest movieRequest) {
        if (movieRequest == null || movieRequest.getMovieItemDtos() == null) {
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
                            error.setVisibility(View.VISIBLE);
                            loading.setVisibility(View.GONE);
                        }
                    });
            return;
        }

        totalPages = movieRequest.getTotalPages();

        if (page == 1) {
            adapter.setItems(movieRequest.getMovieItemDtos());
        } else {
            adapter.addItems(movieRequest.getMovieItemDtos());
        }

        movieList.setVisibility(View.VISIBLE);
        loading.setVisibility(View.GONE);
        page++;
        filmsLoaded = true;
    }
}
