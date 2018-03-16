package com.alexanderdorow.popularmovies.utilities;

import com.alexanderdorow.popularmovies.BuildConfig;
import com.alexanderdorow.popularmovies.api.MovieApi;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitUtils {

    private static MovieApi api;

    public static MovieApi getMovieApi() {
        if (api == null) {
            api = buildRetrofitApi();
        }
        return api;
    }

    private static MovieApi buildRetrofitApi() {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BuildConfig.SERVER_URL)
                .client(buildOkHttpClient())
                .build()
                .create(MovieApi.class);
    }

    private static OkHttpClient buildOkHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
    }

}
