package com.pmdweather.db;import android.content.Context;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WeatherDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "weather.db";
    private static final int DATABASE_VERSION = 2; // Incremented the version

    public static final String TABLE_WEATHER = "weather";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_WEATHER_CODE = "weather_code";
    public static final String COLUMN_TEMPERATURE = "temperature";
    public static final String COLUMN_HUMIDITY = "humidity";
    public static final String COLUMN_DATETIME = "datetime";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_WEATHER + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_WEATHER_CODE + " INTEGER, " +
                    COLUMN_TEMPERATURE + " REAL, " +
                    COLUMN_HUMIDITY + " REAL, " +
                    COLUMN_DATETIME + " TEXT, " +
                    "UNIQUE (" + COLUMN_DATETIME + ") ON CONFLICT REPLACE);";

    public WeatherDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // For future database upgrades, handle migration here
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEATHER);
            onCreate(db);
        }
    }
}
