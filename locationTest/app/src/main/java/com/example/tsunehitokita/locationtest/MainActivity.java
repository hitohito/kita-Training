package com.example.tsunehitokita.locationtest;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import android.widget.Button;


import java.io.IOException;


public class MainActivity extends AppCompatActivity implements LocationListener {

    private LocationManager locationManager;
    // private Location location;

    private double lastLatitude;
    private double lastLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("debug", "\n\n\n\n\n\n\n\n");

        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, this);

        Log.d("debug", "================================");
        Log.d("debug", String.valueOf(isLocationEnabled()));

        // set onclick listener to cry button
        Button btnCry = (Button) this.findViewById(R.id.button);
        btnCry.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                logging();
            }
        });

        super.onCreate(savedInstanceState);
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void logging() {
        if(!isLocationEnabled()){
            Log.d("debug", "================== 位置情報がOFFです ======================");
            return;
        }

        Location location= locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location == null) {
            Log.d("debug", "=================== 位置情報が取得できません ==================");
            return;
        }

        Log.d("debug", String.valueOf(location.getLongitude()));
    }

    @Override
    public void onLocationChanged(Location location) {
        // 緯度
        Log.d("debug","Latitude:"+location.getLatitude());
        lastLatitude = location.getLatitude();
        // 経度
        Log.d("debug","Latitude:"+location.getLongitude());
        lastLongitude = location.getLongitude();

    }

    @Override
    public void onProviderDisabled(String provider){

    }

    @Override
    public void onProviderEnabled(String provider){

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras){

    }

    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, this);
    }

}
