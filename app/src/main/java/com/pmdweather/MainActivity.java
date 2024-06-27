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


public class MainActivity extends AppCompatActivity {
    private Integer time;
    private Weather weatherData;
    private String cityName;

    public static final String ACTION_HISTORY_UPDATE = "com.pmdweather.HISTORY_UPDATE";

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
        } 

        else {
            System.out.println("starting services");
            ((WeatherApp) getApplication()).startServices();
            IntentFilter locationFilter = new IntentFilter("com.pmdweather.LOCATION_UPDATE");
            registerReceiver(locationUpdateReceiver, locationFilter);
            IntentFilter weatherFilter = new IntentFilter("com.pmdweather.WEATHER_UPDATE");
            registerReceiver(weatherUpdateReceiver, weatherFilter);
        }
        // set background to the time
        setBackgroundTime();

        ImageButton buttonHistory = findViewById(R.id.buttonHistory);
        buttonHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });

        // Mostar notificaciones
        showTestNotification();
    }

    //////// GESTION DE PERMISOS
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
            double latitude = intent.getDoubleExtra("latitude", 0.0);
            double longitude = intent.getDoubleExtra("longitude", 0.0);
            getCityName(latitude, longitude);
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
////////

    //////// METER DATOS EN LA PAGINA
    // todo introducir datos a elementos xml
    @SuppressLint("DefaultLocale")
    private void setValuesforPage() {
        // seleccionamos el elemento de texto del xml
        TextView cityNameElement = findViewById(R.id.cityNameTextView);
        // introducimos el nombre de la ciudad (guardado en el string cityNam)
        cityNameElement.setText(cityName);

        // continua con el resto de elementos de la pagina
        // empieza por los datos de temperatura y los iconos
        // los datos estan en el objeto weather (es accesible dentro de la clase)
        // para extraer algun dato haz por ejemplo:
        // weather.getCurrent().getWeatherCode()

        // Icono Weather
        System.out.println("weatherCode: " + weatherData.getCurrent().getWeatherCode());
        int weathercode = weatherData.getCurrent().getWeatherCode();
        setImageFromImageCode(weathercode);


        // Info temperatura
        TextView temperatureTextView = findViewById(R.id.temperatureTextView);
        double temperature = weatherData.getCurrent().getApparentTemperature();
        temperatureTextView.setText(String.format("%.2f", temperature));

        // Info Adiccional
        TextView humidityTextView = findViewById(R.id.additionalInfoTextView);
        humidityTextView.setText(String.valueOf(weatherData.getCurrent().getRelativeHumidity2m()));


        // HorizontalScrollView y su contenedor
        LinearLayout hourlyForecastContainer = findViewById(R.id.hourlyForecastContainer);

        // Agregamos datos dinamicamente
        for (int i = 0; i < weatherData.getHourly().getTime().size(); i++) {
            LinearLayout hourlyLayout = new LinearLayout(this);
            hourlyLayout.setOrientation(LinearLayout.VERTICAL);
            hourlyLayout.setPadding(10, 10, 10, 10);

            // Text view para la hora
            TextView timeScroll = new TextView(this);
            timeScroll.setText(weatherData.getHourly().getTime().get(i));
            timeScroll.setTextSize(16);
            timeScroll.setGravity(Gravity.CENTER);

            // Crear el imageView
            ImageView weatherIconScroll = new ImageView(this);
            weatherIconScroll.setBackground(ContextCompat.getDrawable(this, R.drawable.background_evening)); // No se setear esto

            // Temperatura
            TextView temperatureScroll = new TextView(this);
            temperatureScroll.setText(String.format("%.2f", weatherData.getHourly().getApparentTemperature().get(i)));
            temperatureScroll.setTextSize(16);
            temperatureScroll.setGravity(Gravity.CENTER);

            //añadir elementos al layout
            hourlyLayout.addView(timeScroll);
            hourlyLayout.addView(weatherIconScroll);
            hourlyLayout.addView(temperatureScroll);

            //añadir layout al contenedor
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
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    private void setImageFromImageCode(int imagecode) {
        ImageView weatherImageView = findViewById(R.id.weatherImageView);
        Drawable weatherImage = null;
        if (imagecode == 0) {
            if (time == 2) {
                weatherImage = ContextCompat.getDrawable(this, R.drawable.clear_night);
                weatherImageView.setBackground(weatherImage);
            } else {
                weatherImage = ContextCompat.getDrawable(this, R.drawable.clear_day);
                weatherImageView.setBackground(weatherImage);
            }
        } else if (imagecode >0 && imagecode <= 3) {
            if (time == 2) {
                weatherImage = ContextCompat.getDrawable(this, R.drawable.night_rain);
                weatherImageView.setBackground(weatherImage);
            } else {
                weatherImage = ContextCompat.getDrawable(this, R.drawable.day_rain_option_2);
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

    private void showTestNotification() {
        NotificationService notificationService = new NotificationService();
        notificationService.showNotification("Test Notification", "This is a test notification");
    }

    private void setDailyWeatherNotifications(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        int [] hours = {6,9, 12, 15,18, 21,00};

        for(int hour: hours){
           //Implementar la logica de la mierda esta, ahora vuelvo
        }
    }
////////
}

