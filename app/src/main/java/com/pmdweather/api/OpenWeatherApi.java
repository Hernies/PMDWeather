package com.pmdweather.api;

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
            @Query("hourly") String hourly
    );

    @GET("v1/forecast")
    Call<Weather> getWeeklyWeatherData(
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            @Query("daily") String daily
    );

    @GET("v1/forecast")
    Call<Weather> getAllWeatherData(
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            @Query("forecast_days") int forecast_days,
            @Query("past_days") int past_days,
            @Query("current") String current,
            @Query("hourly") String hourly,
            @Query("daily") String daily
    );
}

