package com.alexanderdorow.popularmovies;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
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

import com.alexanderdorow.popularmovies.dto.MovieItemDto;
import com.alexanderdorow.popularmovies.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.OnMovieItemSelected, View.OnClickListener {

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
                                new FetchMovieDataTask(getString(R.string.popular_prefix)).execute();
                                break;
                            case 1:
                                new FetchMovieDataTask(getString(R.string.top_rated_prefix)).execute();
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

        new FetchMovieDataTask("popular").execute();
    }

    @Override
    public void onMovieSelected(String movieId) {
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        intent.putExtra(Intent.EXTRA_UID, movieId);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        loadMovieData();
    }

    private class FetchMovieDataTask extends AsyncTask<Void, Void, List<MovieItemDto>> {

        private final String prefix;

        FetchMovieDataTask(String prefix) {
            this.prefix = prefix;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            filmsLoaded = false;
            if (page == 1) {
                movieList.setVisibility(View.GONE);
                error.setVisibility(View.GONE);
                loading.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<MovieItemDto> doInBackground(Void... voids) {
            URL url = NetworkUtils.buildUrl(prefix, page);
            List<MovieItemDto> movieList = new ArrayList<>();
            try {
                String responseFromUrl = NetworkUtils.getResponseFromUrl(url);
                JSONObject jsonObject = new JSONObject(responseFromUrl);
                totalPages = jsonObject.getInt("total_pages");

                JSONArray moviesJson = jsonObject.getJSONArray("results");
                for (int i = 0; i < moviesJson.length(); i++) {
                    JSONObject movie = moviesJson.getJSONObject(i);
                    MovieItemDto item = new MovieItemDto();
                    item.setId(movie.getString("id"));
                    item.setThumbnailUrl(String.format(NetworkUtils.BASE_IMAGE_PATH, movie.getString("poster_path")));
                    item.setTitle(movie.getString("original_title"));
                    movieList.add(item);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return movieList;
        }

        @Override
        protected void onPostExecute(final List<MovieItemDto> movieItemDtos) {
            super.onPostExecute(movieItemDtos);
            if (movieItemDtos.isEmpty() && page < totalPages + 1) {
                NetworkUtils.showNetworkError(R.string.error_message_all, R.string.ops,
                        MainActivity.this,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                loadMovieData();
                            }
                        },
                        null,
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

            if (page == 1) {
                adapter.setItems(movieItemDtos);
            } else {
                adapter.addItems(movieItemDtos);
            }

            movieList.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
            page++;
            filmsLoaded = true;
        }
    }
}
