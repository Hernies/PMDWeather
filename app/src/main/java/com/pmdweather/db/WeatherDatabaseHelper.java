package com.pmdweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WeatherDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "weather.db";
    private static final int DATABASE_VERSION = 3;

    public static final String TABLE_CITIES = "cities";
    public static final String COLUMN_CITY_ID = "_id";
    public static final String COLUMN_CITY_NAME = "name";

    public static final String TABLE_HOURLY = "hourly";
    public static final String COLUMN_HOURLY_ID = "_id";
    public static final String COLUMN_HOURLY_CITY_ID = "city_id";
    public static final String COLUMN_HOURLY_DATETIME = "datetime";

    public static final String TABLE_WEEKLY = "weekly";
    public static final String COLUMN_WEEKLY_ID = "_id";
    public static final String COLUMN_WEEKLY_CITY_ID = "city_id";
    public static final String COLUMN_WEEKLY_DATETIME = "datetime";

    public static final String TABLE_HOURLY_VALUES = "hourly_values";
    public static final String COLUMN_HOURLY_VALUES_ID = "_id";
    public static final String COLUMN_HOURLY_VALUES_WEATHER_CODE = "weather_code";
    public static final String COLUMN_HOURLY_VALUES_TEMPERATURE = "temperature";
    public static final String COLUMN_HOURLY_VALUES_APPARENT_TEMP = "apparent_temp";
    public static final String COLUMN_HOURLY_VALUES_HUMIDITY = "humidity";
    public static final String COLUMN_HOURLY_VALUES_HOURLY_ID = "hourly_id";

    public static final String TABLE_WEEKLY_VALUES = "weekly_values";
    public static final String COLUMN_WEEKLY_VALUES_ID = "_id";
    public static final String COLUMN_WEEKLY_VALUES_WEATHER_CODE = "weather_code";
    public static final String COLUMN_WEEKLY_VALUES_TEMPERATURE = "temperature";
    public static final String COLUMN_WEEKLY_VALUES_APPARENT_TEMP = "apparent_temp";
    public static final String COLUMN_WEEKLY_VALUES_HUMIDITY = "humidity";
    public static final String COLUMN_WEEKLY_VALUES_WEEKLY_ID = "weekly_id";

    private static final String CREATE_TABLE_CITIES =
            "CREATE TABLE " + TABLE_CITIES + " (" +
                    COLUMN_CITY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CITY_NAME + " TEXT UNIQUE);";

    private static final String CREATE_TABLE_HOURLY =
            "CREATE TABLE " + TABLE_HOURLY + " (" +
                    COLUMN_HOURLY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_HOURLY_CITY_ID + " INTEGER, " +
                    COLUMN_HOURLY_DATETIME + " TEXT, " +
                    "FOREIGN KEY(" + COLUMN_HOURLY_CITY_ID + ") REFERENCES " + TABLE_CITIES + "(" + COLUMN_CITY_ID + "));";

    private static final String CREATE_TABLE_WEEKLY =
            "CREATE TABLE " + TABLE_WEEKLY + " (" +
                    COLUMN_WEEKLY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_WEEKLY_CITY_ID + " INTEGER, " +
                    COLUMN_WEEKLY_DATETIME + " TEXT, " +
                    "FOREIGN KEY(" + COLUMN_WEEKLY_CITY_ID + ") REFERENCES " + TABLE_CITIES + "(" + COLUMN_CITY_ID + "));";

    private static final String CREATE_TABLE_HOURLY_VALUES =
            "CREATE TABLE " + TABLE_HOURLY_VALUES + " (" +
                    COLUMN_HOURLY_VALUES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_HOURLY_VALUES_WEATHER_CODE + " INTEGER, " +
                    COLUMN_HOURLY_VALUES_TEMPERATURE + " REAL, " +
                    COLUMN_HOURLY_VALUES_APPARENT_TEMP + " REAL, " +
                    COLUMN_HOURLY_VALUES_HUMIDITY + " REAL, " +
                    COLUMN_HOURLY_VALUES_HOURLY_ID + " INTEGER, " +
                    "FOREIGN KEY(" + COLUMN_HOURLY_VALUES_HOURLY_ID + ") REFERENCES " + TABLE_HOURLY + "(" + COLUMN_HOURLY_ID + "));";

    private static final String CREATE_TABLE_WEEKLY_VALUES =
            "CREATE TABLE " + TABLE_WEEKLY_VALUES + " (" +
                    COLUMN_WEEKLY_VALUES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_WEEKLY_VALUES_WEATHER_CODE + " INTEGER, " +
                    COLUMN_WEEKLY_VALUES_TEMPERATURE + " REAL, " +
                    COLUMN_WEEKLY_VALUES_APPARENT_TEMP + " REAL, " +
                    COLUMN_WEEKLY_VALUES_WEEKLY_ID + " INTEGER, " +
                    "FOREIGN KEY(" + COLUMN_WEEKLY_VALUES_WEEKLY_ID + ") REFERENCES " + TABLE_WEEKLY + "(" + COLUMN_WEEKLY_ID + "));";

    public WeatherDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CITIES);
        db.execSQL(CREATE_TABLE_HOURLY);
        db.execSQL(CREATE_TABLE_WEEKLY);
        db.execSQL(CREATE_TABLE_HOURLY_VALUES);
        db.execSQL(CREATE_TABLE_WEEKLY_VALUES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CITIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HOURLY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEEKLY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HOURLY_VALUES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEEKLY_VALUES);
        onCreate(db);
    }
}
