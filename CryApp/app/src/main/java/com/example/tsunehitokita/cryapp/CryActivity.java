package com.example.tsunehitokita.cryapp;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
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
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


import android.*;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;



import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



public class CryActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks {

    private final int REQUEST_PERMISSION = 10;


    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;
    private FusedLocationProviderApi fusedLocationProviderApi;
    private LocationRequest locationRequest;
    private Location location;
    private long lastLocationTime = 0;
    private String myLatitude;
    private String myLongitude;
    public static final String PREFERENCES_FILE_NAME = "preference";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cry);

        //ここなんだっけ
        fusedLocationProviderApi = LocationServices.FusedLocationApi;


//      GoogleApiClient のインスタンス生成
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build();


        if(Build.VERSION.SDK_INT >= 23){
            checkPermission();
        }
        else{
            //何もしない
        }



        // 測位開始
        Button buttonStart = (Button) findViewById(R.id.cryButton);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFusedLocation();
            }
        });

        //ログアウト画面遷移
        TextView logOut= (TextView) this.findViewById(R.id.logOut);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
                Intent intent2 = new Intent(CryActivity.this, LogInActivity.class);
                startActivity(intent2);
            }
            // ログアウト処理をpreferenceに
            public void logout(){
                SharedPreferences settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0); // 0 -> MODE_PRIVATE
                SharedPreferences.Editor editor = settings.edit();
                editor.putLong("logged-in", 0);
                editor.commit();
            }
        });

    }


    public void checkPermission(){
        //これなんだ
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            //なにもしない
        }
        // 拒否していた場合
        else{
            requestLocationPermission();
        }
    }
    //ダイアログ出す
    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(CryActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION);

        } else {
            //拒否されたらToast出してもっかいダイアログ出す
            Toast toast = Toast.makeText(this, "許可されないとアプリが実行できません", Toast.LENGTH_SHORT);
            toast.show();

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, REQUEST_PERMISSION);

        }
    }

    // 結果の受け取り
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //何もしない
                return;

            } else {
                // それでも拒否された時の対応
                Toast toast = Toast.makeText(this, "そのCryボタン押したら落ちます", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
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