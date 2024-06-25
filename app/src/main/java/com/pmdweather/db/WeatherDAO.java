package com.pmdweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pmdweather.db.WeatherDatabaseHelper;

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

    public void addWeatherData(int weatherCode, double temperature, double humidity, String datetime) {
        ContentValues values = new ContentValues();
        values.put(WeatherDatabaseHelper.COLUMN_WEATHER_CODE, weatherCode);
        values.put(WeatherDatabaseHelper.COLUMN_TEMPERATURE, temperature);
        values.put(WeatherDatabaseHelper.COLUMN_HUMIDITY, humidity);
        values.put(WeatherDatabaseHelper.COLUMN_DATETIME, datetime);

        long result = database.insertWithOnConflict(WeatherDatabaseHelper.TABLE_WEATHER, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        if (result == -1) {
            Log.e("WeatherDAO", "Failed to insert data");
        } else {
            Log.d("WeatherDAO", "Data inserted successfully");
        }
    }

    public Cursor getAllWeatherData() {
        Cursor cursor = database.query(WeatherDatabaseHelper.TABLE_WEATHER,
                null, null, null, null, null, null);
        Log.d("WeatherDAO", "Fetched data count: " + cursor.getCount());
        return cursor;
    }
}
