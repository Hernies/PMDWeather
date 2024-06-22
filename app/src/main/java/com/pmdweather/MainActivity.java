    package com.pmdweather;

    import android.content.Context;
    import android.graphics.drawable.Drawable;
    import android.location.LocationListener;
    import android.location.LocationManager;
    import android.os.Bundle;

    import android.Manifest;
    import android.annotation.SuppressLint;
    import android.content.Context;
    import android.content.pm.PackageManager;
    import android.location.Address;
    import android.location.Geocoder;
    import android.location.Location;
    import android.location.LocationListener;
    import android.location.LocationManager;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.View;
    import android.widget.ImageView;
    import android.widget.TextView;
    import android.widget.Toast;


    import androidx.activity.EdgeToEdge;
    import androidx.annotation.NonNull;
    import androidx.core.app.ActivityCompat;
    import androidx.activity.result.ActivityResultLauncher;
    import androidx.activity.result.contract.ActivityResultContracts;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.content.ContextCompat;
    import androidx.core.graphics.Insets;
    import androidx.core.view.ViewCompat;
    import androidx.core.view.WindowInsetsCompat;

    import com.pmdweather.R;

    import java.io.IOException;
    import java.util.Date;
    import java.util.List;
    import java.util.Locale;
    import java.time.LocalTime;

    import retrofit2.Call;
    import retrofit2.Callback;
    import retrofit2.Response;


    public class MainActivity extends AppCompatActivity {

        private LocationManager locationManager;
        private LocationListener locationListener;
        private TextView cityNameTextView;
        private Double latitude;
        private Double longitude;
        
        private Integer time;
        
        private Date date;


        private WeatherService weatherService;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_main);
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
            cityNameTextView = findViewById(R.id.cityNameTextView);
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            weatherService = new WeatherService();
            requestLocationPermission();
            // set background to the time
            setBackgroundTime();


        }

        private void setBackgroundTime(){
            ImageView backgroundImageView = findViewById(R.id.backgroundImageView);
            Drawable background;
            LocalTime current = LocalTime.now();
            LocalTime morningStart = LocalTime.of(6, 0);  // 6:00 AM
            LocalTime afternoonStart = LocalTime.of(12, 0);  // 12:00 PM
            LocalTime nightStart = LocalTime.of(18, 0);  // 6:00 PM
            View rootView = getCurrentFocus();

            if (current.isAfter(morningStart) && current.isBefore(afternoonStart)) {
                //Morning
                background = ContextCompat.getDrawable(this, R.drawable.background_morning);
                backgroundImageView.setBackground(background);
                time=0;
            } else if (current.isAfter(afternoonStart) && current.isBefore(nightStart)) {
                //Afternoon
                background = ContextCompat.getDrawable(this, R.drawable.background_evening);
                backgroundImageView.setBackground(background);
                time=1;
            } else {
                //Night
                background = ContextCompat.getDrawable(this, R.drawable.background_night);
                backgroundImageView.setBackground(background);
                time=2;
            }

        }
        private void requestLocationPermission(){

            ActivityResultLauncher<String[]> locationPermissionRequest =
                    registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                        Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                        Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);

                        if (fineLocationGranted != null && fineLocationGranted) {
                            // Precise location access granted.
                            startLocationUpdates();
                        } else if (coarseLocationGranted != null && coarseLocationGranted) {
                            // Only approximate location access granted.
                            startLocationUpdates();
                        } else {
                            // No location access granted.
                            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
                        }
                    });

            locationPermissionRequest.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }

        @SuppressLint("MissingPermission")
        private void startLocationUpdates() {
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    Log.d("MainActivity", "Lat: " + latitude + " | Lon: " + longitude);

                    // Get the city name using Geocoder
                    getCityName(latitude, longitude);

                    // Call API to get current weather
                    apiCallCurrentWeather();
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}

                @Override
                public void onProviderEnabled(@NonNull String provider) {}

                @Override
                public void onProviderDisabled(@NonNull String provider) {}
            };

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

        private void getCityName(double latitude, double longitude) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    String cityName = addresses.get(0).getLocality();
                    cityNameTextView.setText(cityName);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        private void apiCallHourlyWeather(){
//            Call<Weather> call = weatherService.getHourlyWeather()
        }
        private void apiCallCurrentWeather() {
            Call<Weather> call = weatherService.getCurrentWeather(latitude, longitude);
            call.enqueue(new Callback<Weather>() {
                @Override
                public void onResponse(@NonNull Call<Weather> call, @NonNull Response<Weather> response) {
                    if (response.isSuccessful()) {
                        Weather weather = response.body();
                        if (weather != null) {

                            //setting temperature
                            TextView temperatureTextView = findViewById(R.id.temperatureTextView);
                            String temp = weather.getCurrent().getTemperature2m().toString()+weather.getCurrentUnits().getTemperature2m();
                            temperatureTextView.setText(temp);

                            // Setting humidity
                            TextView additionalInfoTextView = findViewById(R.id.additionalInfoTextView);
                            String humidity = "Humidity "+ weather.getCurrent().getRelativeHumidity2m()+weather.getCurrentUnits().getRelativeHumidity2m();
                            additionalInfoTextView.setText(humidity);
                            
                            // Set image from imagecode
                            setImageFromImageCode(weather.getCurrent().getImagecode());
                        }
                    } else {
                        Log.e("MainActivity", "Request failed with status code: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Weather> call, @NonNull Throwable t) {
                    t.printStackTrace();
                }
            });
            
        }
        private void setImageFromImageCode(int imagecode){
            ImageView weatherImageView = findViewById(R.id.weatherImageView);
            Drawable weatherImage;
            if (imagecode == 0) {
                if( time == 2){
                    weatherImage = ContextCompat.getDrawable(this, R.drawable.clear_night);
                    weatherImageView.setBackground(weatherImage);
                } else {
                    weatherImage = ContextCompat.getDrawable(this, R.drawable.clear_day);
                    weatherImageView.setBackground(weatherImage);
                }
                
            } else if (imagecode > 44 && imagecode <= 48) {
                if (time == 2) {
                    weatherImage = ContextCompat.getDrawable(this, R.drawable.night_rain);
                    weatherImageView.setBackground(weatherImage);
                } else {
                    weatherImage = ContextCompat.getDrawable(this, R.drawable.day_rain_option_2);
                    weatherImageView.setBackground(weatherImage);
                }
            } else if (imagecode > 50 && imagecode <= 65) {
                if (time == 2) {
                    weatherImage = ContextCompat.getDrawable(this, R.drawable.fog);
                    weatherImageView.setBackground(weatherImage);
                } else {
                    weatherImage = ContextCompat.getDrawable(this, R.drawable.fog_option_2);
                    weatherImageView.setBackground(weatherImage);
                }
            } else if (imagecode > 79 && imagecode <= 82) {
                if (time == 2) {
                    weatherImage = ContextCompat.getDrawable(this, R.drawable.night_rain);
                    weatherImageView.setBackground(weatherImage);
                } else {
                    weatherImage = ContextCompat.getDrawable(this, R.drawable.day_rain_option_2);
                    weatherImageView.setBackground(weatherImage);
                }
            } else if (imagecode > 94 && imagecode <= 99) {
                if (time == 2) {
                    weatherImage = ContextCompat.getDrawable(this, R.drawable.night_rain);
                    weatherImageView.setBackground(weatherImage);
                } else {
                    weatherImage = ContextCompat.getDrawable(this, R.drawable.day_rain_option_2);
                    weatherImageView.setBackground(weatherImage);
                }
            }

        }
    }

