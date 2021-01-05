package com.example.myevstation_android.remote;

import android.app.DownloadManager;

import com.example.myevstation_android.model.Favorite;
import com.example.myevstation_android.model.Review;
import com.example.myevstation_android.model.Station;
import com.example.myevstation_android.model.User;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @GET("stations/")
    Call<List<Station>> getStation(@Query("lat") Double lat, @Query("lng") Double lng, @Query("filter1") Boolean filter1, @Query("filter2") Boolean filter2, @Query("filter4") Boolean filter4, @Query("filter7") Boolean filter7 );

    @POST("users/")
    Call<User> createUser(@Body RequestBody body);

    @GET("reviews/")
    Call<List<Review>> getReviews(@Query("statId") String stationId);

    @POST("reviews/")
    Call<Review>  createReview(@Body RequestBody body);

    @GET("users/")
    Call<User> loginUser(@Query("user_id") String userId, @Query("user_pw") String passwd);

    @GET("stations/")
    Call<List<Station>> getSearch(@Query("addr") String addr);
}