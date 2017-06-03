package com.example.tsunehitokita.cryapp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;


import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
//import com.mapbox.mapboxsdk.MapboxAccountManager;


public class MapActivity extends AppCompatActivity {

    private MapView mapView;

    //配列の宣言ーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーー
    private JSONArray responseArray;



    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //MapBoxから地図を取得
        mapView = (MapView) findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {

                //現在地をCryActivityから受け取る*マップを読み込むここじゃないとだめだった。上だとダメだった*
                Intent intent = getIntent();
                String stringMyLatitude = intent.getStringExtra("Latitude");
                String stringMyLongitude = intent.getStringExtra("Longitude");
                double myLatitude = Double.parseDouble(stringMyLatitude);
                double myLongitude = Double.parseDouble(stringMyLongitude);

                mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                        new CameraPosition.Builder()
                        .target(new LatLng(myLatitude,myLongitude)).zoom(12).build()
                ));
                //地図読込完了...
            }
        });


       //今まで押した人の位置を取得
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                Log.d("debug", "\n\n\n\n\n1");
                String result = null;

                // リクエストオブジェクトを作って
                Request request = new Request.Builder()
                        .url("https://private-6cfe3-bee2066.apiary-mock.com/map")
                        .get()
                        .build();

                // クライアントオブジェクトを作って
                OkHttpClient client = new OkHttpClient();

                // リクエストして結果を受け取って
                try {
                    Response response = client.newCall(request).execute();
                    String json = response.body().string();
                    responseArray = new JSONArray(json);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // 返す
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                addPoint(savedInstanceState);
            }


        }.execute();
    }


    //ここでポイントを出すのをfor文で繰り返すーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーー
    public void addPoint(Bundle savedInstanceState){
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                Log.d("debug", "\n\n\n\n\n3");
                for (int i = 0; i < responseArray.length(); i++) {
                    Log.d("debug", "\n\n\n\n\n3");
                    try {
                        JSONObject data = responseArray.getJSONObject(i);
                        double latitudes = Double.parseDouble(data.getString("latitude"));
                        double longitudes = Double.parseDouble(data.getString("longitude"));
                        Log.d("debug", "\n\n\n\n\n4");
//                        mapboxMap.addMarker(new MarkerOptions()
//                                .position(new LatLng(latitudes, longitudes)) );

                        IconFactory iconFactory = IconFactory.getInstance(MapActivity.this);
                        com.mapbox.mapboxsdk.annotations.Icon icon = iconFactory.fromResource(R.drawable.red2);

                        mapboxMap.addMarker(new MarkerOptions()
                                .position(new LatLng(latitudes, longitudes))
                                .icon(icon));
                        Log.d("debug", "=========");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

}