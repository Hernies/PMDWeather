package com.pmdweather.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.pmdweather.services.NotificationService;

public class WeatherNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String weatherUpdate = "Weather at " + intent.getStringExtra("time") + ": Sunny"; // Simulación de datos del clima
        Intent serviceIntent = new Intent(context, NotificationService.class);
        serviceIntent.putExtra("title", "Weather Update");
        serviceIntent.putExtra("message", weatherUpdate);
        context.startService(serviceIntent);
    }
}
