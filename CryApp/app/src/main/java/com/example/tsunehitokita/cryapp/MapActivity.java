package com.example.tsunehitokita.cryapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;


import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

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
    private ArrayList<String> locationArray ;



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
                        .target(new LatLng(myLatitude,myLongitude))
                        .build()
                ));
                //地図読込完了...
            }
        });


       //今まで押した人の位置を取得
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
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
                    result = response.body().string();
                    //配列のインスタンスを作る
                    locationArray = new ArrayList<String>();
                    //locationArrayにresponseを格納
                    //ここで返ってきた緯度経度を上の配列に入れる。入れ子ね。ここも繰り返し文
                    for (int i = 0; i < locationArray.size(); i++)
                    locationArray.add(response.body()[i]);



//                    LinkedList<ResponseBody> longi = new LinkedList<>();
//                    LinkedList<ResponseBody> lati  = new LinkedList<>();
//                    for (int i = 0; i < result.length(); i++) //ここ、配列を文字列型にして間違ってるはず。
//                    longi.add(response.body()[i].longitude);
//                    for (int h = 0; h < result.length(); h++) //ここ、配列を文字列型にして間違ってるはず。
//                    lati.add(response.body()[h].latitude);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                // 返す
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                Log.d("TAG", result);

                Log.d("TAG", String.valueOf(locationArray));



                addPoint(savedInstanceState);
            }


        }.execute();
    }


    //ここでポイントを出すのをfor文で繰り返すーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーー
    public void addPoint(Bundle savedInstanceState){
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {

                for (int i = 0; i < locationArray.size(); i++)
                mapboxMap.addMarker(new MarkerOptions()
                        .position(new LatLng(locationArray[i].latitude,locationArray[i].longitude))
                );
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