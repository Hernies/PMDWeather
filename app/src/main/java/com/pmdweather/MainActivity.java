package com.pmdweather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.Manifest;
import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.pmdweather.api.Weather;
import com.pmdweather.services.ApiService;
import com.pmdweather.services.NotificationService;


import java.io.IOException;
import java.util.List;
import java.util.Locale;

import java.time.LocalTime;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private Integer time;
    private Weather weatherData;
    private String cityName;
    private Double latitude;
    private Double longitude;
    private Double ogLat;
    private Double ogLon;
    private boolean acceptLocationUpdates=true;
    public static final String ACTION_HISTORY_UPDATE = "com.pmdweather.HISTORY_UPDATE";
    public static final String EXTRA_LATITUDE = "com.pmdweather.EXTRA_LATITUDE";
    public static final String EXTRA_LONGITUDE = "com.pmdweather.EXTRA_LONGITUDE";

    public static final String ACTION_LOCATION_UPDATE = "com.pmdweather.LOCATION_UPDATE_MAIN";
    public static final String EXTRA_HISTORY_WEATHER_DATA = "com.pmdweather.HISTORY_WEATHER_DATA";
    public static final String EXTRA_CITY_NAME = "com.pmdweater.HISTORY_CITY_NAME";
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
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
        if (!checkLocationPermissions()) {
            requestLocationPermission();
            System.out.println("services not started, insufficient permissions");
        } else {
            System.out.println("starting services");
            ((WeatherApp) getApplication()).startServices();
            IntentFilter locationFilter = new IntentFilter("com.pmdweather.LOCATION_UPDATE");
            registerReceiver(locationUpdateReceiver, locationFilter);
            IntentFilter weatherFilter = new IntentFilter("com.pmdweather.WEATHER_UPDATE");
            registerReceiver(weatherUpdateReceiver, weatherFilter);
            IntentFilter citySelFilter = new IntentFilter("com.pmdweather.EXPLORE");
            registerReceiver(locationCitySel,citySelFilter);
        }
       //comprobar permisos
       if (!checkNotificationPermission()){
            requestNotificationPermission();
       } else {
            int[] programadas = {8,12,16,20};
            for(int hora: programadas){
                scheduleNotification(hora);
            }
       }
        // set background to the time
        setBackgroundTime();

        ImageButton buttonHistory = findViewById(R.id.buttonHistory);
        buttonHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent history = new Intent(MainActivity.this, HistoryActivity.class);
                history.putExtra("CITY_NAME", cityName);
                startActivity(history);
            }
        });
        ImageButton citySelection = findViewById(R.id.selectNameCity);
        citySelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cityname = new Intent(MainActivity.this, CitySelectionActivity.class);;
                cityname.putExtra("CITY_NAME", returnCityName(ogLat,ogLon));
                acceptLocationUpdates=false;
                startActivity(cityname);
            }
        });
    }

//////// GESTION DE PERMISOS
     private boolean checkNotificationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestNotificationPermission() {
        ActivityResultLauncher<String[]> notificationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                    Boolean postNotificationsGranted = result.getOrDefault(Manifest.permission.POST_NOTIFICATIONS, false);
                    if (postNotificationsGranted != null && postNotificationsGranted) {
                        System.out.println("POST_NOTIFICATIONS Granted");
                    } else {
                        Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
                    }
                });
        notificationPermissionRequest.launch(new String[]{
                Manifest.permission.POST_NOTIFICATIONS
        });
    }


    private boolean checkLocationPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
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
////////

//////// RECIBIDORES DE DATOS (ubicacion y datos metereologicos)
private final BroadcastReceiver locationUpdateReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (acceptLocationUpdates) {
            latitude = intent.getDoubleExtra("latitude", 0.0);
            longitude = intent.getDoubleExtra("longitude", 0.0);
            ogLat = latitude;
            ogLon = longitude;
            getCityName(latitude, longitude);
            System.out.println("latitude " + latitude);
            System.out.println("longitude " + longitude);
            Intent apiRequest = new Intent(ACTION_LOCATION_UPDATE);
            apiRequest.putExtra(EXTRA_LATITUDE, latitude);
            apiRequest.putExtra(EXTRA_LONGITUDE, longitude);
            sendBroadcast(apiRequest);
        }
    }
};
    private final BroadcastReceiver locationCitySel= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            double newLatitude = intent.getDoubleExtra("LATITUDE", 0.0);
            double newLongitude = intent.getDoubleExtra("LONGITUDE", 0.0);
            if (newLongitude != 0.0 && newLatitude != 0.0){
                latitude = newLatitude;
                longitude = newLongitude;
            } else if (ogLat== newLatitude && ogLon == newLongitude ) {
                acceptLocationUpdates=true;
            }
            getCityName(latitude, longitude);
            System.out.println("citySEllatitude " + latitude);
            System.out.println("citySEllongitude " + longitude);
            Intent apiRequest = new Intent(ACTION_LOCATION_UPDATE);
            apiRequest.putExtra(EXTRA_LATITUDE,latitude);
            apiRequest.putExtra(EXTRA_LONGITUDE,longitude);
            sendBroadcast(apiRequest);
        }
    };
    private final BroadcastReceiver weatherUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ApiService.ACTION_WEATHER_UPDATE.equals(intent.getAction())) {
                Weather weather = (Weather) intent.getSerializableExtra(ApiService.EXTRA_WEATHER_DATA);
                if (weather != null) {
                    // Update UI with weather data
                    weatherData = weather;
                    setValuesforPage();
                    if (weather != null && cityName != null) {
                        Intent history = new Intent(ACTION_HISTORY_UPDATE);
                        history.putExtra(EXTRA_HISTORY_WEATHER_DATA, weather);
                        history.putExtra(EXTRA_CITY_NAME, cityName);
                        sendBroadcast(history);
                    }
                }
            }
        }
    };

    private void scheduleNotification(int hour) {
        Intent intent = new Intent(this, NotificationService.class);
        intent.putExtra("title", "PMDWeather");
        intent.putExtra("message", "check your weather predictions for "+cityName);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour); 
        calendar.set(Calendar.MINUTE, 0); //

        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }
////////

//////// METER DATOS EN LA PAGINA
@SuppressLint("DefaultLocale")
private void setValuesforPage() {
    // Clear values first

    // Clear city name
    TextView cityNameElement = findViewById(R.id.cityNameTextView);
    cityNameElement.setText("");

    // Clear weather icon
    ImageView weatherImageView = findViewById(R.id.weatherImageView);
    weatherImageView.setImageDrawable(null);

    // Clear temperature info
    TextView temperatureTextView = findViewById(R.id.temperatureTextView);
    temperatureTextView.setText("");

    // Clear additional info
    TextView humidityTextView = findViewById(R.id.additionalInfoTextView);
    humidityTextView.setText("");

    // Clear hourly forecast container
    LinearLayout hourlyForecastContainer = findViewById(R.id.hourlyForecastContainer);
    hourlyForecastContainer.removeAllViews();

    // Set new values

    // Set city name
    cityNameElement.setText(cityName);

    // Set weather icon
    int weathercode = weatherData.getCurrent().getWeatherCode();
    setImageFromImageCode(weathercode, weatherImageView);

    // Set temperature info
    double temperature = weatherData.getCurrent().getApparentTemperature();
    temperatureTextView.setText(String.format("%.1f", temperature)+"°C");

    // Set additional info
    humidityTextView.setText(String.valueOf(weatherData.getCurrent().getRelativeHumidity2m()));

    // Set hourly forecast data
    for (int i = 0; i < weatherData.getHourly().getTime().size(); i++) {
        LinearLayout hourlyLayout = new LinearLayout(this);
        hourlyLayout.setOrientation(LinearLayout.VERTICAL);

        // Text view for time
        TextView timeScroll = new TextView(this);
        timeScroll.setText(weatherData.getHourly().getTime().get(i)+":00");
        timeScroll.setTextSize(16);
        timeScroll.setGravity(Gravity.CENTER);

        // Create the ImageView
        ImageView weatherIconScroll = new ImageView(this);
        setImageFromImageCode(weatherData.getHourly().getWeatherCode().get(i), weatherIconScroll);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(70, 70);
        imageParams.gravity = Gravity.CENTER;
        weatherIconScroll.setLayoutParams(imageParams);

        // Temperature
        TextView temperatureScroll = new TextView(this);
        temperatureScroll.setText(String.format("%.1f", weatherData.getHourly().getApparentTemperature().get(i))+"°C");
        temperatureScroll.setTextSize(16);
        temperatureScroll.setGravity(Gravity.CENTER);

        // Add elements to layout
        hourlyLayout.addView(timeScroll);
        hourlyLayout.addView(weatherIconScroll);
        hourlyLayout.addView(temperatureScroll);

        // Add layout to container
        hourlyForecastContainer.addView(hourlyLayout);
    }
}

////////


    //////// FUNCIONES AUXILIARES PARA LA PAGINA PRINCIPAL
    // Establece el nombre de la ciudad una vez se tiene la ubicacion en el string cityName
    private void getCityName(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                cityName = addresses.get(0).getLocality();
                System.out.println(cityName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String returnCityName(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String cityName="";
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                 cityName = addresses.get(0).getLocality();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;
    }

    //Establece el fondo de pantalla en funcion del momento del dia que sea
    private void setBackgroundTime() {
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
            time = 0;
        } else if (current.isAfter(afternoonStart) && current.isBefore(nightStart)) {
            //Afternoon
            background = ContextCompat.getDrawable(this, R.drawable.background_evening);
            backgroundImageView.setBackground(background);
            time = 1;
        } else {
            //Night
            background = ContextCompat.getDrawable(this, R.drawable.background_night);
            backgroundImageView.setBackground(background);
            time = 2;
        }
    }

    // establece el icono en concordancia con el weathercode recibido y al momento del dia que sea
    private void setImageFromImageCode(int imagecode, ImageView imageView) {
        Drawable weatherImage = null;
        if (imagecode == 0) {
            if (time == 2) {
                weatherImage = ContextCompat.getDrawable(this, R.drawable.clear_night);
                imageView.setBackground(weatherImage);
            } else {
                weatherImage = ContextCompat.getDrawable(this, R.drawable.clear_day);
                imageView.setBackground(weatherImage);
            }
        } else if (imagecode >0 && imagecode <= 3) {
            if (time == 2) {
                weatherImage = ContextCompat.getDrawable(this, R.drawable.night_rain);
                imageView.setBackground(weatherImage);
            } else {
                weatherImage = ContextCompat.getDrawable(this, R.drawable.day_rain_option_2);
                imageView.setBackground(weatherImage);
            }
        } else if (imagecode > 44 && imagecode <= 48) {
            if (time == 2) {
                weatherImage = ContextCompat.getDrawable(this, R.drawable.night_rain);
                imageView.setBackground(weatherImage);
            } else {
                weatherImage = ContextCompat.getDrawable(this, R.drawable.day_rain_option_2);
                imageView.setBackground(weatherImage);
            }
        } else if (imagecode > 50 && imagecode <= 65) {
            if (time == 2) {
                weatherImage = ContextCompat.getDrawable(this, R.drawable.fog);
                imageView.setBackground(weatherImage);
            } else {
                weatherImage = ContextCompat.getDrawable(this, R.drawable.fog_option_2);
                imageView.setBackground(weatherImage);
            }
        } else if (imagecode > 79 && imagecode <= 82) {
            if (time == 2) {
                weatherImage = ContextCompat.getDrawable(this, R.drawable.night_rain);
                imageView.setBackground(weatherImage);
            } else {
                weatherImage = ContextCompat.getDrawable(this, R.drawable.day_rain_option_2);
                imageView.setBackground(weatherImage);
            }
        } else if (imagecode > 94 && imagecode <= 99) {
            if (time == 2) {
                weatherImage = ContextCompat.getDrawable(this, R.drawable.night_rain);
                imageView.setBackground(weatherImage);
            } else {
                weatherImage = ContextCompat.getDrawable(this, R.drawable.day_rain_option_2);
                imageView.setBackground(weatherImage);
            }
        }
    }

   
////////
}

