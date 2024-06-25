    package com.pmdweather;

    import android.content.BroadcastReceiver;
    import android.content.Context;
    import android.content.Intent;
    import android.content.pm.PackageManager;
    import android.graphics.drawable.Drawable;
    import android.location.LocationListener;
    import android.location.LocationManager;
    import android.os.Bundle;

    import android.Manifest;
    import android.annotation.SuppressLint;
    import android.location.Address;
    import android.location.Geocoder;
    import android.location.Location;
    import android.util.Log;
    import android.view.View;
    import android.widget.ImageView;
    import android.widget.TextView;
    import android.widget.Toast;


    import androidx.activity.EdgeToEdge;
    import androidx.annotation.NonNull;
    import androidx.activity.result.ActivityResultLauncher;
    import androidx.activity.result.contract.ActivityResultContracts;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.app.ActivityCompat;
    import androidx.core.content.ContextCompat;
    import androidx.core.graphics.Insets;
    import androidx.core.view.ViewCompat;
    import androidx.core.view.WindowInsetsCompat;

    import com.pmdweather.api.Weather;
    import com.pmdweather.db.WeatherDAO;
    import com.pmdweather.services.ApiService;
    import com.pmdweather.WeatherApp;

    import java.io.IOException;
    import java.time.LocalDate;
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
        
        private final LocalDate today = LocalDate.now();

        private Weather weatherData;
//        private WeatherService weatherService;
        private WeatherDAO weatherDAO;

        private BroadcastReceiver weatherUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (ApiService.ACTION_WEATHER_UPDATE.equals(intent.getAction())) {
                    Weather weather = (Weather) intent.getSerializableExtra(ApiService.EXTRA_WEATHER_DATA);
                    if (weather != null) {
                        // Update UI with weather data
                        weatherData = weather;
                    }
                }
            }
        };
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
            if(!checkLocationPermissions()){
                requestLocationPermission();
            } else {
                ((WeatherApp) getApplication()).startServices();
            }
            
            cityNameTextView = findViewById(R.id.cityNameTextView);
            // set background to the time
            setBackgroundTime();


        }

        private boolean checkLocationPermissions() {
            return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }

        private void requestLocationPermission(){
            ActivityResultLauncher<String[]> locationPermissionRequest =
                    registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                        Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                        Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);

                        if (fineLocationGranted != null && fineLocationGranted) {
                            System.out.println("Fine Location Granted");
                        } else if (coarseLocationGranted != null && coarseLocationGranted) {
                            // Only approximate location access granted.
                            System.out.println("Coarse Location Granted");
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
        // establece el icono en concordancia con el weathercode recibido
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

