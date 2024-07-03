package com.pmdweather.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.pmdweather.api.Weather;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WeatherDAO {
    private SQLiteDatabase database;
    private WeatherDatabaseHelper dbHelper;
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public WeatherDAO(Context context) {
        dbHelper = new WeatherDatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
        Log.d("WeatherDAO", "Database opened");
    }

    public void close() {
        dbHelper.close();
        Log.d("WeatherDAO", "Database closed");
    }

    public Response executeRequest(Request request) {
        Response response = new Response();
        long cityId = getCityId(request.getCityName());
        if (cityId == -1) {
            Log.d("WeatherDAO", "City not found");
            return response; // Return empty response if city not found
        }

        Date startDate = request.getDate();
        int numWeeks = request.getNumWeeks();
        Date endDate = addWeeksToDate(startDate, numWeeks);

        // Query for daily data
        Response.Daily daily = queryDailyData(cityId, startDate, endDate);
        response.setDaily(daily);

        return response;
    }

    private long getCityId(String cityName) {
        Cursor cursor = database.query(
                WeatherDatabaseHelper.TABLE_CITIES,
                new String[]{WeatherDatabaseHelper.COLUMN_CITY_ID},
                WeatherDatabaseHelper.COLUMN_CITY_NAME + " = ?",
                new String[]{cityName},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") long cityId = cursor.getLong(cursor.getColumnIndex(WeatherDatabaseHelper.COLUMN_CITY_ID));
            cursor.close();
            return cityId;
        }

        return -1;
    }

    private Date addWeeksToDate(Date date, int weeks) {
        long msPerWeek = 1000L * 60 * 60 * 24 * 7;
        return new Date(date.getTime() + (weeks * msPerWeek));
    }

    private Response.Daily queryDailyData(long cityId, Date startDate, Date endDate) {
        Response.Daily daily = new Response.Daily();

        List<String> timeList = new ArrayList<>();
        List<Integer> weatherCodeList = new ArrayList<>();
        List<Double> temperatureMaxList = new ArrayList<>();
        List<Double> temperatureMinList = new ArrayList<>();
        List<Double> apparentTemperatureMaxList = new ArrayList<>();
        List<Double> apparentTemperatureMinList = new ArrayList<>();

        String start = dateFormat.format(startDate);
        String end = dateFormat.format(endDate);

        Cursor cursor = database.query(
                WeatherDatabaseHelper.TABLE_WEEKLY,
                new String[]{WeatherDatabaseHelper.COLUMN_WEEKLY_DATETIME, WeatherDatabaseHelper.COLUMN_WEEKLY_ID},
                WeatherDatabaseHelper.COLUMN_WEEKLY_CITY_ID + " = ? AND " + WeatherDatabaseHelper.COLUMN_WEEKLY_DATETIME + " BETWEEN ? AND ?", //<----- HERE
                new String[]{String.valueOf(cityId), start, end},
                null, null, WeatherDatabaseHelper.COLUMN_WEEKLY_DATETIME + " ASC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String datetime = cursor.getString(cursor.getColumnIndex(WeatherDatabaseHelper.COLUMN_WEEKLY_DATETIME));
                @SuppressLint("Range") long weeklyId = cursor.getLong(cursor.getColumnIndex(WeatherDatabaseHelper.COLUMN_WEEKLY_ID));

                timeList.add(datetime);

                Cursor dataCursor = database.query(
                        WeatherDatabaseHelper.TABLE_WEEKLY_VALUES,
                        new String[]{
                                WeatherDatabaseHelper.COLUMN_WEEKLY_VALUES_WEATHER_CODE,
                                WeatherDatabaseHelper.COLUMN_WEEKLY_VALUES_TEMPERATURE,
                                WeatherDatabaseHelper.COLUMN_WEEKLY_VALUES_APPARENT_TEMP
                        },
                        WeatherDatabaseHelper.COLUMN_WEEKLY_VALUES_WEEKLY_ID + " = ?",
                        new String[]{String.valueOf(weeklyId)},
                        null, null, null);

                if (dataCursor != null && dataCursor.moveToFirst()) {
                    @SuppressLint("Range") int weatherCode = dataCursor.getInt(dataCursor.getColumnIndex(WeatherDatabaseHelper.COLUMN_WEEKLY_VALUES_WEATHER_CODE));
                    @SuppressLint("Range") double temperature = dataCursor.getDouble(dataCursor.getColumnIndex(WeatherDatabaseHelper.COLUMN_WEEKLY_VALUES_TEMPERATURE));
                    @SuppressLint("Range") double apparentTemp = dataCursor.getDouble(dataCursor.getColumnIndex(WeatherDatabaseHelper.COLUMN_WEEKLY_VALUES_APPARENT_TEMP));

                    weatherCodeList.add(weatherCode);
                    temperatureMaxList.add(temperature);
                    temperatureMinList.add(temperature);
                    apparentTemperatureMaxList.add(apparentTemp);
                    apparentTemperatureMinList.add(apparentTemp);

                    dataCursor.close();
                }
            }
            cursor.close();
        }

        daily.setTime(timeList);
        daily.setWeatherCode(weatherCodeList);
        daily.setTemperature2mMax(temperatureMaxList);
        daily.setTemperature2mMin(temperatureMinList);
        daily.setApparentTemperatureMax(apparentTemperatureMaxList);
        daily.setApparentTemperatureMin(apparentTemperatureMinList);

        return daily;
    }


    public void insertWeather(Weather weather, String cityName) {
        long cityId = getCityId(cityName);

        if (cityId == -1) {
            // Insert the city
            cityId = addCity(cityName);

            // Insert hourly and weekly weather data
            insertHourlyWeatherData(cityId, weather.getHourly());
            insertWeeklyWeatherData(cityId, weather.getDaily());
        } else {
            if (!isHourlyDataExists(cityId, weather.getHourly().getTime().get(0))) {
                insertHourlyWeatherData(cityId, weather.getHourly());
            }

            if (!isWeeklyDataExists(cityId, weather.getDaily().getTime().get(0))) {
                insertWeeklyWeatherData(cityId, weather.getDaily());
            }
        }
    }

    private boolean isHourlyDataExists(long cityId, String datetime) {
        Cursor cursor = database.query(
                WeatherDatabaseHelper.TABLE_HOURLY,
                new String[]{WeatherDatabaseHelper.COLUMN_HOURLY_ID},
                WeatherDatabaseHelper.COLUMN_HOURLY_CITY_ID + " = ? AND " + WeatherDatabaseHelper.COLUMN_HOURLY_DATETIME + " = ?",
                new String[]{String.valueOf(cityId), datetime},
                null, null, null);

        boolean exists = cursor != null && cursor.moveToFirst();
        if (cursor != null) cursor.close();
        return exists;
    }

    private boolean isWeeklyDataExists(long cityId, String datetime) {
        Cursor cursor = database.query(
                WeatherDatabaseHelper.TABLE_WEEKLY,
                new String[]{WeatherDatabaseHelper.COLUMN_WEEKLY_ID},
                WeatherDatabaseHelper.COLUMN_WEEKLY_CITY_ID + " = ? AND " + WeatherDatabaseHelper.COLUMN_WEEKLY_DATETIME + " = ?",
                new String[]{String.valueOf(cityId), datetime},
                null, null, null);

        boolean exists = cursor != null && cursor.moveToFirst();
        if (cursor != null) cursor.close();
        return exists;
    }

    private long addCity(String cityName) {
        ContentValues values = new ContentValues();
        values.put(WeatherDatabaseHelper.COLUMN_CITY_NAME, cityName);
        return database.insertWithOnConflict(WeatherDatabaseHelper.TABLE_CITIES, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    private void insertHourlyWeatherData(long cityId, Weather.Hourly hourly) {
        database.beginTransaction();
        try {
            for (int i = 0; i < hourly.getTime().size(); i++) {
                ContentValues hourlyValues = new ContentValues();
                hourlyValues.put(WeatherDatabaseHelper.COLUMN_HOURLY_CITY_ID, cityId);
                hourlyValues.put(WeatherDatabaseHelper.COLUMN_HOURLY_DATETIME, hourly.getTime().get(i));

                long hourlyId = database.insert(WeatherDatabaseHelper.TABLE_HOURLY, null, hourlyValues);

                ContentValues hourlyDataValues = new ContentValues();
                hourlyDataValues.put(WeatherDatabaseHelper.COLUMN_HOURLY_VALUES_WEATHER_CODE, hourly.getWeatherCode().get(i));
                hourlyDataValues.put(WeatherDatabaseHelper.COLUMN_HOURLY_VALUES_TEMPERATURE, hourly.getTemperature2m().get(i));
                hourlyDataValues.put(WeatherDatabaseHelper.COLUMN_HOURLY_VALUES_APPARENT_TEMP, hourly.getApparentTemperature().get(i));
                hourlyDataValues.put(WeatherDatabaseHelper.COLUMN_HOURLY_VALUES_HUMIDITY, hourly.getRelativeHumidity2m().get(i));
                hourlyDataValues.put(WeatherDatabaseHelper.COLUMN_HOURLY_VALUES_HOURLY_ID, hourlyId);

                database.insert(WeatherDatabaseHelper.TABLE_HOURLY_VALUES, null, hourlyDataValues);
            }

            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    private void insertWeeklyWeatherData(long cityId, Weather.Daily daily) {
        database.beginTransaction();
        try {
            for (int i = 0; i < daily.getTime().size(); i++) {
                ContentValues weeklyValues = new ContentValues();
                weeklyValues.put(WeatherDatabaseHelper.COLUMN_WEEKLY_CITY_ID, cityId);
                weeklyValues.put(WeatherDatabaseHelper.COLUMN_WEEKLY_DATETIME, daily.getTime().get(i));

                long weeklyId = database.insert(WeatherDatabaseHelper.TABLE_WEEKLY, null, weeklyValues);

                ContentValues weeklyDataValues = new ContentValues();
                weeklyDataValues.put(WeatherDatabaseHelper.COLUMN_WEEKLY_VALUES_WEATHER_CODE, daily.getWeatherCode().get(i));
                weeklyDataValues.put(WeatherDatabaseHelper.COLUMN_WEEKLY_VALUES_TEMPERATURE, (daily.getTemperature2mMax().get(i) + daily.getTemperature2mMin().get(i)) / 2);
                weeklyDataValues.put(WeatherDatabaseHelper.COLUMN_WEEKLY_VALUES_APPARENT_TEMP, (daily.getApparentTemperatureMax().get(i) + daily.getApparentTemperatureMin().get(i)) / 2);
                weeklyDataValues.put(WeatherDatabaseHelper.COLUMN_WEEKLY_VALUES_WEEKLY_ID, weeklyId);

                database.insert(WeatherDatabaseHelper.TABLE_WEEKLY_VALUES, null, weeklyDataValues);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }
}
