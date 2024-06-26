package com.pmdweather.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.pmdweather.MainActivity;
import com.pmdweather.api.Weather;
import com.pmdweather.db.WeatherDAO;

import java.util.Arrays;

public class DatabaseService extends Service {
    
    private WeatherDAO weatherDAO;
    @Override
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    public void onCreate(){
        super.onCreate();
        weatherDAO = new WeatherDAO(this);
        weatherDAO.open();
        IntentFilter filter = new IntentFilter("com.pmdweather.HISTORY_UPDATE");
        registerReceiver(updateHistoryReceiver, filter);
    }

    private final BroadcastReceiver updateHistoryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Weather weather = (Weather) intent.getSerializableExtra(MainActivity.EXTRA_HISTORY_WEATHER_DATA);
            String cityName = intent.getStringExtra(MainActivity.EXTRA_CITY_NAME);
            if (weather != null){

                if (weather != null && cityName != null) {
                    if (weather.getDaily() != null && weather.getHourly() != null) {
                        storeWeatherData(weather,cityName);
                        System.out.println("Weather data saved for city: " + cityName);
                    } else if (weather.getDaily() != null){
                        System.out.println( "Weather weekly is null");
                    }
                    else if (weather.getHourly() != null){
                        System.out.println( "Weather hourly is null");
                    }
                } else {
                    System.out.println("Weather data or city name is null");
                }

            }
        }
    };
    
    private void storeWeatherData(Weather weather,String cityName){
        System.out.println("storing ");
        weatherDAO.insertWeather(weather,cityName);
        System.out.println("weather stored");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        weatherDAO.close();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(updateHistoryReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
