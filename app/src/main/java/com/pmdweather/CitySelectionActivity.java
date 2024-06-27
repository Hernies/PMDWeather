package com.pmdweather;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CitySelectionActivity extends AppCompatActivity {


    private EditText searchCityEditText;
    private ListView cityListView;
    private List<String> cityList;
    private ArrayAdapter<String> cityAdapter;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sity_selection);

        searchCityEditText = findViewById(R.id.searchCityEditText);
        cityListView = findViewById(R.id.selectedCitiesListView);

        // Initialize city list and adapter
        cityList = new ArrayList<>();
        cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cityList);
        cityListView.setAdapter(cityAdapter);

        // Get current location and add it to the top of the list
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location currentLocation = getCurrentLocation();
        if (currentLocation != null) {
            String currentLocationString = "Current Location: " + currentLocation.getLatitude() + ", " + currentLocation.getLongitude();
            cityList.add(currentLocationString);
        } else {
            cityList.add("Current Location: Unknown");
        }

        // Set a listener for the search input
        searchCityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();
                if (!query.isEmpty()) {
                    performGeocoding(query);
                } else {
                    cityList.clear();
                    cityAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Handle city selection
                cityListView.setOnItemClickListener((parent, view, position, id) -> {
                    String selectedCity = cityAdapter.getItem(position);
                    if (selectedCity != null) {
                        // Extract latitude and longitude from the selected item
                        String[] parts = selectedCity.split(":")[1].split(",");
                        double latitude = Double.parseDouble(parts[0].trim());
                        double longitude = Double.parseDouble(parts[1].trim());
                        System.out.println("CitySel Latitude "+latitude);
                        System.out.println("CitySel Longitude "+longitude);


                        String locationString = "Selected City: " + selectedCity + " (" + latitude + ", " + longitude + ")";
                        Toast.makeText(CitySelectionActivity.this, locationString, Toast.LENGTH_SHORT).show();

                        // Pass the selected location back to the main activity
                        Intent resultIntent = new Intent("com.pmdweather.EXPLORE");
                        resultIntent.putExtra("LATITUDE", latitude);
                        resultIntent.putExtra("LONGITUDE", longitude);
                        resultIntent.putExtra("EXPLORING", id != 0);
                        sendBroadcast(resultIntent);
                        finish();
                    }
                });
            }
        });


    }

    private Location getCurrentLocation() {
        try {
            return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } catch (SecurityException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void performGeocoding(String cityName) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(cityName, 5);
            cityList.clear();
            if (addresses != null && !addresses.isEmpty()) {
                for (Address address : addresses) {
                    String cityInfo = address.getAddressLine(0) + ": " + address.getLatitude() + ", " + address.getLongitude();
                    cityList.add(cityInfo);
                }
            } else {
                cityList.add("No results found");
            }
            cityAdapter.notifyDataSetChanged();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Geocoding failed", Toast.LENGTH_SHORT).show();
        }
    }

}
