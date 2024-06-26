package com.pmdweather.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.pmdweather.api.WeatherCall;
import com.pmdweather.api.Weather;

import java.time.LocalDate;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class ApiService extends Service{
    public static final String ACTION_WEATHER_UPDATE = "com.pmdweather.WEATHER_UPDATE";
    public static final String EXTRA_WEATHER_DATA = "com.pmdweather.WEATHER_DATA";

    private WeatherCall weatherCaller;


    @Override
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    public void onCreate(){
        super.onCreate();
        weatherCaller = new WeatherCall();
        IntentFilter filter = new IntentFilter("com.pmdweather.LOCATION_UPDATE");
        registerReceiver(locationUpdateReceiver, filter);
        System.out.println("api Started");
    }
    private final BroadcastReceiver locationUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            double latitude = intent.getDoubleExtra("latitude", 0.0);
            double longitude = intent.getDoubleExtra("longitude", 0.0);
            fetchWeatherData(latitude, longitude);
            
        }
    };

    private void fetchWeatherData(double latitude, double longitude) {
        Call<Weather> call = weatherCaller.getAllWeatherData(latitude, longitude, 7,0 );
        call.enqueue(new Callback<Weather>() {
            @Override
            public void onResponse(Call<Weather> call, Response<Weather> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Weather weather = response.body();
                    //broadcast body to all pages that want it
                    sendWeatherUpdateBroadcast(weather);
                } else {
                    System.out.println("api failed to fetch weekly data");
                }
            }

            @Override
            public void onFailure(Call<Weather> call, Throwable t) {
                t.printStackTrace();
            }
        });


    }


    private void sendWeatherUpdateBroadcast(Weather weather) {
        Intent intent = new Intent(ACTION_WEATHER_UPDATE);
        intent.putExtra(EXTRA_WEATHER_DATA, weather);
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(locationUpdateReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
