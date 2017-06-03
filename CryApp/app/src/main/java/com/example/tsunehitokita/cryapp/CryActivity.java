package com.example.tsunehitokita.cryapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


import android.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



public class CryActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks {
//        GoogleApiClient.OnConnectionFailedListener,
//        LocationListener {


    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;
    private FusedLocationProviderApi fusedLocationProviderApi;
    private LocationRequest locationRequest;
    private Location location;
    private long lastLocationTime = 0;
    private String myLatitude;
    private String myLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cry);

//        locationRequest = LocationRequest.create();
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setInterval(1000);
//        locationRequest.setFastestInterval(16);

        //ここなんだっけ
        fusedLocationProviderApi = LocationServices.FusedLocationApi;


//      GoogleApiClient のインスタンス生成
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
                .build();

        // 測位開始
        Button buttonStart = (Button) findViewById(R.id.cryButton);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFusedLocation();
                //ここにもう一つ処理を。クリックしたらポスト
                //------
//                new AsyncTask();
            }
        });

        //ログアウト画面遷移
        TextView logOut= (TextView) this.findViewById(R.id.logOut);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(CryActivity.this, LogInActivity.class);
                startActivity(intent2);
            }
        });

    }

    private void startFusedLocation() {
        // Connect the client.
        if (!mResolvingError) {
            // Connect the client.
            //GoogleApiClientの接続
            mGoogleApiClient.connect();
        }
    }
    //connectが終わったらonconnectedが呼ばれる。
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Location currentLocation = fusedLocationProviderApi.getLastLocation(mGoogleApiClient);

        //ここなんだ
        location = currentLocation;
        myLatitude = toString().valueOf(location.getLatitude());
        myLongitude = toString().valueOf(location.getLongitude());
        Log.d("LocationActivity", "onConnected");

        //ポスト処理
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                RequestBody formBody = new FormBody.Builder()
                        //フォームの内容を入れる、を実装
                        .add("Latitude", myLatitude)
                        .add("Longitude", myLongitude)
                        .build();

                Request request = new Request.Builder()
                        .url("https://private-6cfe3-bee2066.apiary-mock.com/cry")
                        .post(formBody)
                        .build();
                // クライアントオブジェクトを作って
                OkHttpClient client = new OkHttpClient();
                String result = null;

                // リクエストして結果を受け取って
                try {
                    Response response = client.newCall(request).execute();
                    result = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // 返す
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                Log.d("TAG", result);

                //画面遷移（成功したらを書き足す）
                Intent intent = new Intent(CryActivity.this, MapActivity.class);
                intent.putExtra("Latitude", myLatitude);
                intent.putExtra("Longitude", myLongitude);
                startActivity(intent);
            }
        }.execute();

    }


    @Override
    public void onConnectionSuspended(int i) {

    }
}

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//    }
    //GoogleApiClient の接続が完了すると、ConnectionCallbacksによってonConnectedが呼び出される。


//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//    }

//    @Override
//    public void onLocationChanged(Location location) {
//
//    }







//public class CryActivity extends AppCompatActivity{

//    private LocationManager locationManager;
    // private Location location;
//
//    private double lastLatitude;
//    private double lastLongitude;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        setContentView(R.layout.activity_cry);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        // set onclick listener to cry button
//        Button btnCry = (Button) this.findViewById(R.id.cryButton);
//        btnCry.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//
//            }
//        });
//
//        super.onCreate(savedInstanceState);
//    }

//    postMyLocation();{


//        new AsyncTask<Void, Void, String>(){
//@Override
//protected String doInBackground(Void...params){
//        RequestBody formBody=new FormBody.Builder()
//        //フォームの内容を入れる、を実装
//        .add("tokyo","location")
//        .build();
//
//        Request request=new Request.Builder()
//        .url("https://private-6cfe3-bee2066.apiary-mock.com/cry")
//        .post(formBody)
//        .build();
//        // クライアントオブジェクトを作って
//        OkHttpClient client=new OkHttpClient();
//        String result=null;
//
//        // リクエストして結果を受け取って
//        try{
//        Response response=client.newCall(request).execute();
//        result=response.body().string();
//        }catch(IOException e){
//        e.printStackTrace();
//        }
//        // 返す
//        return result;
//        }
//
//@Override
//protected void onPostExecute(String result){
//        Log.d("TAG",result);
//
//        //画面遷移（成功したらを書き足す）
//        Intent intent=new Intent(CryActivity.this,MapActivity.class);
//        startActivity(intent);
//        }
//        }.execute();
//        }

//    private void initButtons(){
//        Button btnCry = (Button) this.findViewById(R.id.cryButton);
//    }
//}





//import android.*;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//
//
//import java.io.IOException;
//
//import okhttp3.FormBody;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//
//public class CryActivity extends AppCompatActivity implements LocationListener{
//
//    private LocationManager locationManager;
//    // private Location location;
//
//    private double lastLatitude;
//    private double lastLongitude;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        Log.d("debug", "\n\n\n\n\n\n\n\n");
//
//        setContentView(R.layout.activity_cry);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, this);
//
//        Log.d("debug", "================================");
//        Log.d("debug", String.valueOf(isLocationEnabled()));
//
//        // set onclick listener to cry button
//        Button btnCry = (Button) this.findViewById(R.id.cryButton);
//        btnCry.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                logging();
//            }
//        });
//
//        super.onCreate(savedInstanceState);
//    }
//
//    private boolean isLocationEnabled() {
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
//                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//    }
//
//    private void logging() {
//        if(!isLocationEnabled()){
//            Log.d("debug", "================== 位置情報がOFFです ======================");
//            return;
//        }
//
//        Location location= locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        if(location == null) {
//            Log.d("debug", "=================== 位置情報が取得できません ==================");
//            return;
//        }
//        Log.d("debug", String.valueOf(location.getLatitude()));
//        Log.d("debug", String.valueOf(location.getLongitude()));
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//        // 緯度
//        Log.d("debug","Latitude:"+location.getLatitude());
//        lastLatitude = location.getLatitude();
//        // 経度
//        Log.d("debug","Latitude:"+location.getLongitude());
//        lastLongitude = location.getLongitude();
//
//    }
//
//    @Override
//    public void onProviderDisabled(String provider){
//
//    }
//
//    @Override
//    public void onProviderEnabled(String provider){
//
//    }
//
//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras){
//
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, this);
//    }
//
//
////     new AsyncTask<Void, Void, String>() {
////        @Override
////        protected String doInBackground(Void... params) {
////            RequestBody formBody = new FormBody.Builder()
////                    //フォームの内容を入れる、を実装
////                    .add("tokyo", "location")
////                    .build();
////
////            Request request = new Request.Builder()
////                    .url("https://private-6cfe3-bee2066.apiary-mock.com/cry")
////                    .post(formBody)
////                    .build();
////            // クライアントオブジェクトを作って
////            OkHttpClient client = new OkHttpClient();
////            String result = null;
////
////            // リクエストして結果を受け取って
////            try {
////                Response response = client.newCall(request).execute();
////                result = response.body().string();
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
////            // 返す
////            return result;
////        }
////
////        @Override
////        protected void onPostExecute(String result) {
////            Log.d("TAG", result);
////
////            //画面遷移（成功したらを書き足す）
////            Intent intent = new Intent(CryActivity.this, MapActivity.class);
////            startActivity(intent);
////        }
////    }.execute();
//
////    private void initButtons(){
////        Button btnCry = (Button) this.findViewById(R.id.cryButton);
////    }
//}
