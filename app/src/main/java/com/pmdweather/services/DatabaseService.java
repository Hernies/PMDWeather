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

import com.pmdweather.HistoryActivity;
import com.pmdweather.MainActivity;
import com.pmdweather.api.Weather;
import com.pmdweather.db.Response;
import com.pmdweather.db.WeatherDAO;
import com.pmdweather.db.Request;













import java.util.Arrays;

public class DatabaseService extends Service {

    public static final String ACTION_RESPONSE_HISTORY = "com.pmdweather.RESPONSE_HISTORY";
    public static final String EXTRA_RESPONSE_BODY = "com.pmdweather.RESPONSE_BODY";
    private WeatherDAO weatherDAO;
    @Override
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    public void onCreate(){
        super.onCreate();
        weatherDAO = new WeatherDAO(this);
        weatherDAO.open();
        IntentFilter filter = new IntentFilter("com.pmdweather.HISTORY_UPDATE");
        registerReceiver(updateHistoryReceiver, filter);
        IntentFilter send = new IntentFilter("com.pmdweather.REQUEST_HISTORY");
        registerReceiver(handleHistoryRequest,send);
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
                        // System.out.println("Weather data saved for city: " + cityName);
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
    
    private final BroadcastReceiver handleHistoryRequest = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(HistoryActivity.ACTION_REQUEST_HISTORY.equals(intent.getAction())){
                Request request = (Request) intent.getSerializableExtra(HistoryActivity.EXTRA_REQUEST_BODY);
                if (request != null){
                    //if the request isnt null, prepare the broadcast and send over to history activity
                    Intent response = new Intent(ACTION_RESPONSE_HISTORY);
                    response.putExtra(EXTRA_RESPONSE_BODY,executeRequest(request));
                    sendBroadcast(response);
                }
            }

        }
    };
    
    private void storeWeatherData(Weather weather,String cityName){
        weatherDAO.insertWeather(weather,cityName);
    }
    
    private Response executeRequest(Request request){
        return weatherDAO.executeRequest(request);
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
