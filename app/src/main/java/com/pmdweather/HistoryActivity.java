package com.pmdweather;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.pmdweather.api.Weather;
import com.pmdweather.db.Request;
import com.pmdweather.db.Response;
import com.pmdweather.services.DatabaseService;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    public static final String ACTION_REQUEST_HISTORY = "com.pmdweather.REQUEST_HISTORY";
    public static final String EXTRA_REQUEST_BODY = "com.pmdweather.REQUEST_BODY";
    private Response historyWeather;
    private CalendarView calendarView;
    private EditText weeksEditText;
    private ScrollView historicoScrollView;
    private long selectedDate = 0;
    private Integer selectedWeeks = null;

    private Button submit;

    @Override
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);
        IntentFilter filter = new IntentFilter("com.pmdweather.RESPONSE_HISTORY");
        registerReceiver(getHistory, filter);
        calendarView = findViewById(R.id.calendarView);
        weeksEditText = findViewById(R.id.weeksEditText);
        historicoScrollView = findViewById(R.id.historicoScrollView);

        submit = findViewById(R.id.submitQuery);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedDate==0 && selectedWeeks!=null){
                    Toast.makeText(HistoryActivity.this, "Please Select a Date On the calendar", Toast.LENGTH_SHORT).show();
                } else if (selectedDate!=0 && selectedWeeks==null) {
                    Toast.makeText(HistoryActivity.this, "Please Specify the number of weeks you want to request", Toast.LENGTH_SHORT).show();
                } else if (selectedDate == 0){
                    Toast.makeText(HistoryActivity.this, "Specify Date and Number of Weeks", Toast.LENGTH_SHORT).show();
                } else {
                    String cityName = getIntent().getStringExtra("CITY_NAME");
                    System.out.println("selected date: "+ new Date(selectedDate));
                    Request request = new Request(cityName,new Date(selectedDate),selectedWeeks);
                    sendRequest(request);
                }
            }
        });

        weeksEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    try {
                        int weeks = Integer.parseInt(s.toString());
                        // Do something with the number of weeks
                        selectedWeeks = weeks;
                        Toast.makeText(HistoryActivity.this, "Weeks: " + weeks, Toast.LENGTH_SHORT).show();
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        Toast.makeText(HistoryActivity.this, "Invalid number format", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        // Set a listener for the calendar view
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                selectedDate = calendar.getTimeInMillis();
                System.out.println("selected date: "+ new Date(selectedDate));
                Toast.makeText(HistoryActivity.this, "Date Selected: " + dayOfMonth + "/" + (month + 1) + "/" + year, Toast.LENGTH_SHORT).show();
            }
        });


    }


//////// CONSULTAS DE DATOS (datos del historial)
    private void sendRequest(Request request) {
        Intent intent = new Intent(ACTION_REQUEST_HISTORY);
        intent.putExtra(EXTRA_REQUEST_BODY, request);
        sendBroadcast(intent);
    }

    private final BroadcastReceiver getHistory = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DatabaseService.ACTION_RESPONSE_HISTORY.equals(intent.getAction())) {
                Response response = (Response) intent.getSerializableExtra(DatabaseService.EXTRA_RESPONSE_BODY);
                if (response != null && response.getDaily() != null) {
                    System.out.println("              RESPOMSE RECEIVED!!!!!");
                    historyWeather = response;
                    printResponse(response);
                    setActivityValues();
                } else {
                    System.out.println("response is or holds null values");
                }
            }
        }
    };
    private void printResponse(Response response) {
        Response.Daily daily = response.getDaily();
        if (daily != null) {
            System.out.println("Daily Weather Data:");
            for (int i = 0; i < daily.getTime().size(); i++) {
                System.out.println("Date: " + daily.getTime().get(i));
                System.out.println("Weather Code: " + daily.getWeatherCode().get(i));
                System.out.println("Max Temperature: " + daily.getTemperature2mMax().get(i));
                System.out.println("Min Temperature: " + daily.getTemperature2mMin().get(i));
                System.out.println("Max Apparent Temperature: " + daily.getApparentTemperatureMax().get(i));
                System.out.println("Min Apparent Temperature: " + daily.getApparentTemperatureMin().get(i));
            }
        } else {
            System.out.println("No daily weather data available.");
        }
    }
////////



    @SuppressLint("DefaultLocale")
    private void setActivityValues() {
        LinearLayout historicContainer = findViewById(R.id.historicoListView);
        // Clear any existing views
        historicContainer.removeAllViews();
        // Check if there is any data to display
        Response.Daily daily = historyWeather.getDaily();
        if (daily.getTime().isEmpty()) {
            // Display a message indicating no data is available
            TextView noDataTextView = new TextView(this);
            noDataTextView.setText("No offline data available for the selected interval.");
            noDataTextView.setTextSize(16);
            noDataTextView.setGravity(Gravity.CENTER);
            historicContainer.addView(noDataTextView);
            return;
        }
        System.out.println("selected date: "+ new Date(selectedDate));
        // Loop through each day's data
        for (int i = 0; i < daily.getTime().size(); i++) {
            // Create a horizontal layout for each day's data
            LinearLayout historicLayout = new LinearLayout(this);
            historicLayout.setOrientation(LinearLayout.HORIZONTAL);
            historicLayout.setPadding(16, 16, 16, 16);

            // Date TextView
            TextView dateTextView = new TextView(this);
            dateTextView.setText(daily.getTime().get(i));
            dateTextView.setTextSize(16);
            dateTextView.setGravity(Gravity.CENTER);
            dateTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
            ));

            // Weather Icon
            ImageView weatherImageView = new ImageView(this);
            setImageFromImageCode(daily.getWeatherCode().get(i), weatherImageView);
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(70, 70);
            imageParams.gravity = Gravity.CENTER;
            weatherImageView.setLayoutParams(imageParams);

            // Max Temperature TextView
            TextView maxTempTextView = new TextView(this);
            maxTempTextView.setText(String.format("Max: %.2f", daily.getTemperature2mMax().get(i)));
            maxTempTextView.setTextSize(16);
            maxTempTextView.setGravity(Gravity.CENTER);
            maxTempTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
            ));

            // Min Temperature TextView
            TextView minTempTextView = new TextView(this);
            minTempTextView.setText(String.format("Min: %.2f", daily.getTemperature2mMin().get(i)));
            minTempTextView.setTextSize(16);
            minTempTextView.setGravity(Gravity.CENTER);
            minTempTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
            ));

            // Add all views to the horizontal layout
            historicLayout.addView(dateTextView);
            historicLayout.addView(weatherImageView);
            historicLayout.addView(maxTempTextView);
            historicLayout.addView(minTempTextView);

            // Add the horizontal layout to the container
            historicContainer.addView(historicLayout);
        }
    }







    //usa esta funcion para poner el icono del clima en el front
    private void setImageFromImageCode(int imagecode, ImageView imageView) {
        Drawable weatherImage = null;
        if (imagecode == 0) {
            weatherImage = ContextCompat.getDrawable(this, R.drawable.clear_day_black);
            imageView.setBackground(weatherImage);
        } else if (imagecode > 0 && imagecode <= 3) {
            weatherImage = ContextCompat.getDrawable(this, R.drawable.day_rain_option_2);
            imageView.setBackground(weatherImage);
        } else if (imagecode > 44 && imagecode <= 48) {
            weatherImage = ContextCompat.getDrawable(this, R.drawable.day_rain_option_2);
            imageView.setBackground(weatherImage);
        } else if (imagecode > 50 && imagecode <= 65) {
            weatherImage = ContextCompat.getDrawable(this, R.drawable.fog_option_2_black);
            imageView.setBackground(weatherImage);
        } else if (imagecode > 79 && imagecode <= 82) {
            weatherImage = ContextCompat.getDrawable(this, R.drawable.day_rain_option_2);
            imageView.setBackground(weatherImage);
        } else if (imagecode > 94 && imagecode <= 99) {
            weatherImage = ContextCompat.getDrawable(this, R.drawable.day_rain_option_2);
            imageView.setBackground(weatherImage);
        }
    }
}

