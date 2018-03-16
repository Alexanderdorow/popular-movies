package com.alexanderdorow.popularmovies.api;

import com.alexanderdorow.popularmovies.api.dto.RequestMovie;
import com.alexanderdorow.popularmovies.api.dto.RequestReview;
import com.alexanderdorow.popularmovies.api.dto.RequestTrailer;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieApi {

    String POPULAR = "popular";
    String TOP_RATED = "top_rated";
    String API_KEY = "{{INSERT YOUR API KEY HERE}}";

    @GET("/3/movie/{category}")
    Call<RequestMovie> getAllMovies(@Path("category") String category, @Query("api_key") String apiKey, @Query("page") int page);

    @GET("/3/movie/{id}/videos")
    Call<RequestTrailer> getMovieTrailerInfo(@Path("id") String id, @Query("api_key") String apiKey);

    @GET("/3/movie/{id}/reviews")
    Call<RequestReview> getMovieReviewInfo(@Path("id") String id, @Query("api_key") String apiKey);


}
