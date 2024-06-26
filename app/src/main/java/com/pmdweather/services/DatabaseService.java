package com.pmdweather.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.pmdweather.api.Weather;
import com.pmdweather.db.WeatherDAO;

public class DatabaseService extends Service {
    
    private WeatherDAO weatherDAO;
    @Override
    public void onCreate(){
        super.onCreate();
        weatherDAO = new WeatherDAO(this);
        weatherDAO.open();
        IntentFilter filter = new IntentFilter("com.pmdweather.WEATHER_UPDATE");
        LocalBroadcastManager.getInstance(this).registerReceiver(weatherUpdateReceiver, filter);
        System.out.println("Database Started");
    }

    private final BroadcastReceiver weatherUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Weather weather = (Weather) intent.getSerializableExtra(ApiService.EXTRA_WEATHER_DATA);
            if (weather != null){
                storeWeatherData(weather);
            }
        }
    };
    
    private void storeWeatherData(Weather weather){
        weatherDAO.insertWeather(weather);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        weatherDAO.close();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(weatherUpdateReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
