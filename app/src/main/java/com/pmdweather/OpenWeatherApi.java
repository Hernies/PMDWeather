package com.pmdweather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeatherApi {

    @GET("v1/forecast")
    Call<Weather> getCurrentWeatherData(
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            @Query("current") String current
    );

    @GET("v1/forecast")
    Call<Weather> getHourlyWeatherData(
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            @Query("start_date") String start_date,
            @Query("start_date") String end_date,
            @Query("hourly") String current
    );
}

