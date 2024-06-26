package com.pmdweather.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.pmdweather.api.Weather;

public class WeatherDAO {
    private SQLiteDatabase database;
    private WeatherDatabaseHelper dbHelper;

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

    public void insertWeather(Weather weather) {
        String cityName = getCityName(weather.getLatitude(), weather.getLongitude());

        if (cityName == null) {
            // Insert the city

            long cityId = addCity(getCityName(weather.getLatitude(), weather.getLatitude()));

            // Insert hourly and weekly weather data
            insertHourlyWeatherData(cityId, weather.getHourly());
            insertWeeklyWeatherData(cityId, weather.getWeekly());
        } else {
            long cityId = getCityId(cityName);

            if (!isHourlyDataExists(cityId, weather.getHourly().getTime().get(0))) {
                insertHourlyWeatherData(cityId, weather.getHourly());
            }

            if (!isWeeklyDataExists(cityId, weather.getWeekly().getTime().get(0))) {
                insertWeeklyWeatherData(cityId, weather.getWeekly());
            }
        }
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

    private String getCityName(double latitude, double longitude) {
        Cursor cursor = database.query(
                WeatherDatabaseHelper.TABLE_CITIES,
                new String[]{WeatherDatabaseHelper.COLUMN_CITY_NAME},
                "latitude = ? AND longitude = ?",
                new String[]{String.valueOf(latitude), String.valueOf(longitude)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") String cityName = cursor.getString(cursor.getColumnIndex(WeatherDatabaseHelper.COLUMN_CITY_NAME));
            cursor.close();
            return cityName;
        }

        return null;
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
            ContentValues hourlyValues = new ContentValues();
            hourlyValues.put(WeatherDatabaseHelper.COLUMN_HOURLY_CITY_ID, cityId);
            hourlyValues.put(WeatherDatabaseHelper.COLUMN_HOURLY_DATETIME, hourly.getTime().get(0)); // Assume Weather class has a method getHourlyDatetime()

            long hourlyId = database.insert(WeatherDatabaseHelper.TABLE_HOURLY, null, hourlyValues);

            for (int i=0; i<hourly.getTime().size(); i++) { // Assume Weather class has a method getHourlyData()
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

    private void insertWeeklyWeatherData(long cityId, Weather.Weekly weekly) {
        database.beginTransaction();
        try {
            ContentValues weeklyValues = new ContentValues();
            weeklyValues.put(WeatherDatabaseHelper.COLUMN_WEEKLY_CITY_ID, cityId);
            weeklyValues.put(WeatherDatabaseHelper.COLUMN_WEEKLY_DATETIME, weekly.getTime().get(0)); // Assume Weather class has a method getWeeklyDatetime()

            long weeklyId = database.insert(WeatherDatabaseHelper.TABLE_WEEKLY, null, weeklyValues);

            for (int i=0; i<weekly.getTime().size(); i++) { // Assume Weather class has a method getHourlyData()
                ContentValues weeklyDataValues = new ContentValues();
                weeklyDataValues.put(WeatherDatabaseHelper.COLUMN_HOURLY_VALUES_WEATHER_CODE, weekly.getWeatherCode().get(i));
                weeklyDataValues.put(WeatherDatabaseHelper.COLUMN_HOURLY_VALUES_TEMPERATURE, (weekly.getTemperature2mMax().get(i)+weekly.getTemperature2mMin().get(i)/2));
                weeklyDataValues.put(WeatherDatabaseHelper.COLUMN_HOURLY_VALUES_APPARENT_TEMP,( (weekly.getApparentTemperatureMax().get(i)+weekly.getApparentTemperatureMin().get(i))/2));
                weeklyDataValues.put(WeatherDatabaseHelper.COLUMN_HOURLY_VALUES_HOURLY_ID, weeklyId);

                database.insert(WeatherDatabaseHelper.TABLE_WEEKLY_VALUES, null, weeklyDataValues);
            }

            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }
}
