package com.pmdweather.api;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherCall {

    private static final String BASE_URL = "https://api.open-meteo.com/";

    private final OpenWeatherApi openWeatherApi;

    public WeatherCall() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        openWeatherApi = retrofit.create(OpenWeatherApi.class);
    }
    
    public Call<Weather> getAllWeatherData(double latitude, double longitude, int forecast_days, int past_days){
        String current = "temperature_2m,relative_humidity_2m,apparent_temperature";
        String hourly = "temperature_2m,relative_humidity_2m,apparent_temperature,weather_code";
        String weekly = "weather_code,temperature_2m_max,temperature_2m_min,apparent_temperature_max,apparent_temperature_min";
        return openWeatherApi.getAllWeatherData(latitude,longitude,current,hourly,weekly,forecast_days,past_days);
    }
}
