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
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.pmdweather.api.Weather;
import com.pmdweather.db.Request;
import com.pmdweather.db.Response;
import com.pmdweather.services.DatabaseService;

import java.util.Date;

public class HistoryActivity extends AppCompatActivity {
    public static final String ACTION_REQUEST_HISTORY = "com.pmdweather.REQUEST_HISTORY";
    public static final String EXTRA_REQUEST_BODY = "com.pmdweather.REQUEST_BODY";
    private Response historyWeather;
    private CalendarView calendarView;
    private EditText weeksEditText;
    private ListView historicoListView;
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
        historicoListView = findViewById(R.id.historicoListView);
        submit = findViewById(R.id.submitQuery);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedDate==0 && selectedWeeks!=null){
                    Toast.makeText(HistoryActivity.this, "Please select a Date On the calendar", Toast.LENGTH_SHORT).show();
                } else if (selectedDate!=0 && selectedWeeks==null) {
                    Toast.makeText(HistoryActivity.this, "Please Specify the number of weeks you want to request", Toast.LENGTH_SHORT).show();
                } else if (selectedDate == 0){
                    Toast.makeText(HistoryActivity.this, "Specify Date and Number of Weeks", Toast.LENGTH_SHORT).show();
                } else {
                    String cityName = getIntent().getStringExtra("CITY_NAME");
                    Request request = new Request(cityName,new Date(selectedDate),selectedWeeks);
                    sendRequest(request);
                    System.out.println("                request sent!!!!!!!!!!!!!!!!!!!!!!!!!");
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
                selectedDate = view.getDate();
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



    private void setActivityValues() {
        //todo aqui se ponen los datos al front

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

