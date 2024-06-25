package com.pmdweather;

import android.app.Application;
import android.content.Intent;
import com.pmdweather.services.ApiService;
import com.pmdweather.services.DatabaseService;
import com.pmdweather.services.LocationService;


// La clase principal de nuestra app, inicia todos los servicios de fondo
public class WeatherApp extends Application {
        public void onCreate() {
            super.onCreate();
        }
        
    public void startServices(){
            startLocationService();
            startApiService();
            startDataBaseService();
    }
    private void startApiService(){
        Intent intent = new Intent (this, ApiService.class);
        startService(intent);
    }
    private void startDataBaseService(){
        Intent intent = new Intent (this, DatabaseService.class);
        startService(intent);
    }
    private void startLocationService() {
        Intent intent = new Intent(this, LocationService.class);
        startService(intent);
    }


}
