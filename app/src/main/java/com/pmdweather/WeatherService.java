package com.pmdweather;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherService {

    private static final String BASE_URL = "https://api.open-meteo.com/";

    private final OpenWeatherApi openWeatherApi;

    public WeatherService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        openWeatherApi = retrofit.create(OpenWeatherApi.class);
    }

    public Call<Weather> getCurrentWeather(double latitude, double longitude) {
        String currentParams = "temperature_2m,relative_humidity_2m,apparent_temperature";
        return openWeatherApi.getCurrentWeatherData(latitude, longitude, currentParams);
    }
    public Call<Weather> getHourlyWeather(double latitude, double longitude, Date startDate, Date endDate){
        String hourlyParams = "temperature_2m,relative_humidity_2m,apparent_temperature,weather_code";
        return openWeatherApi.getHourlyWeatherData(latitude,longitude,startDate.toString(),endDate.toString(),hourlyParams);
    }

    public Call<Weather> getWeeklyWeather(double latitude, double longitude, Date startDate, Date endDate){
        String hourlyParams = "temperature_2m,relative_humidity_2m,apparent_temperature,weather_code";
        return openWeatherApi.getHourlyWeatherData(latitude,longitude,startDate.toString(),endDate.toString(),hourlyParams);
    }
}
