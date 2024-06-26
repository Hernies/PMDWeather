package com.pmdweather;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import com.pmdweather.api.Weather;
import com.pmdweather.db.Request;
import com.pmdweather.db.Response;
import com.pmdweather.services.DatabaseService;

import java.util.Date;

public class HistoryActivity extends AppCompatActivity {
    public static final String ACTION_REQUEST_HISTORY = "com.pmdweather.REQUEST_HISTORY";
    public static final String EXTRA_REQUEST_BODY = "com.pmdweather.REQUEST_BODY";
    private Response historyWeather;
    @Override
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);
        IntentFilter filter = new IntentFilter("com.pmdweather.RESPONSE_HISTORY");
        registerReceiver(getHistory,filter);

    }
    

//////// CONSULTAS DE DATOS (datos del historial)
    private void sendRequest(Request request){
        Intent intent = new Intent(ACTION_REQUEST_HISTORY);
        intent.putExtra(EXTRA_REQUEST_BODY,request);
        sendBroadcast(intent);
    }

    private final BroadcastReceiver getHistory = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(DatabaseService.ACTION_RESPONSE_HISTORY.equals(intent.getAction())){
            Response response = (Response) intent.getSerializableExtra(DatabaseService.EXTRA_RESPONSE_BODY);
            if (response != null && response.getHourly()!=null && response.getDaily()!=null){
                historyWeather = response;
                setActivityValues();
            } else {
                System.out.println("response is or holds null values");
            }
        }
    }
};
////////
    
    //todo sacar datos de request
    //un listener por dato ()
    
    private void setActivityValues(){
        //todo aqui se ponen los datos al front
    }
}
