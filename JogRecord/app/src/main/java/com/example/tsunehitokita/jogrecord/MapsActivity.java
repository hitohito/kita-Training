package com.example.tsunehitokita.jogrecord;

import android.Manifest;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.os.SystemClock;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.net.wifi.WifiManager;
////import android.support.annotation.Nullable;
//import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.util.Log;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity
        implements
        OnMapReadyCallback,
        ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener,
        LoaderManager.LoaderCallbacks<Address> {


        private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
        private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 2;
        private static final int ADDRESSLOADER_ID = 0;

        private static final int INTERVAL = 500;
        private static final int FASTESTINTERVAL = 16;

        private GoogleMap mMap;
        private GoogleApiClient mGoogleApiClient;

        private static final LocationRequest REQUEST = LocationRequest.create()
                .setInterval(INTERVAL)
                .setFastestInterval(FASTESTINTERVAL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        private FusedLocationProviderApi mFusedLocationProviderApi = LocationServices.FusedLocationApi;
        private List<LatLng> mRunList = new ArrayList<LatLng>();
        private WifiManager mWifi;
        private boolean mWifiOff = false;
        private long mStartTimeMillis;
        private double mMeter = 0.0;
        private double mElapsedTime = 0.0;
        private double mSpeed = 0.0;
        private DatabaseHelper mDbHelper;
        private boolean mStart = false;
        private boolean mFirst = false;
        private boolean mStop = false;
        private boolean mAsked = false;
        private Chronometer mChronometer;


        @Override
        protected void onSaveInstanceState (Bundle outState){
        super.onSaveInstanceState(outState);
            //メンバー変数が初期化されることへの対処
        outState.putBoolean("ASKED", mAsked);
    }

        @Override
        protected void onRestoreInstanceState (Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        mAsked = savedInstanceState.getBoolean("ASKED");
    }

        @Override
        protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mDbHelper = new DatabaseHelper(this);

        ToggleButton tb = (ToggleButton) findViewById(R.id.toggleButton);
        tb.setChecked(false);

        tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    startChronometer();
                    mStart = true;
                    mFirst = true;
                    mStop = false;
                    mRunList.clear();
                } else {
                    stopChronometer();
                    mStop = true;
                    calcSpeed();
                    saveConfirm();
                    mStart = false;
                }
            }
        });


    }

    private void startChronometer() {
        mChronometer = (Chronometer) findViewById(R.id.chronometer);
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();
        mStartTimeMillis = System.currentTimeMillis();
    }

    private void stopChronometer() {
        mChronometer.stop();
        //ミリ秒
        mElapsedTime = SystemClock.elapsedRealtime() - mChronometer.getBase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mAsked) {
            wifiConfirm();
            mAsked = !mAsked;
        }
        mGoogleApiClient.connect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latlng) {
                Intent intent = new Intent(MapsActivity.this, JogView.class);
                startActivity(intent);
            }
        });
        Log.v("OnCreate", "=============Map init===============");
        Log.d("CheckPermission", String.valueOf(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("許可が必要です")
                        .setMessage("移動に合わせて地図を動かすには、ACCESS_FINE_LOCATIONを許可してください")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestAccessFineLocation();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showToast ("GPS機能が使えないので、地図は動きません");
                            }
                        })
                        .show();
            } else {
                requestAccessFineLocation();
            }
        }
    }

    private void requestAccessFineLocation() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                //ユーザーが許可したとき
                //許可が必要な機能を改めて実行する
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //

                } else {
                    //ユーザーが許可しなかったとき
                    //許可しなかったため機能が実行できないことを表示する
                    showToast("GPS機能が使えないので、地図は動きません");
                    //以下は、java.lang.RuntimeExeptionになる
                    //mMap.setMyLocationEnabled(true);
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveConfirmDialog();
                } else {
                    showToast("外部へのファイルの保存が許可されなかったので、記録できません");
                }
                return;
            }
        }
    }


    private void wifiConfirm() {
        mWifi = (WifiManager) getSystemService(WIFI_SERVICE);
        if (mWifi.isWifiEnabled()) {
            wifiConfirmDialog();
        }
    }

    private void wifiConfirmDialog() {
        WifiConfirmDialogFragment newFragment = WifiConfirmDialogFragment.newInstance(
                R.string.wifi_confirm_dialog_title, R.string.wifi_confirm_dialog_message);

        newFragment.show(getFragmentManager(), "dialog");
    }

    public void wifiOff() {
        mWifi.setWifiEnabled(false);
        mWifiOff = true;
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationProviderApi.requestLocationUpdates(mGoogleApiClient, REQUEST, (com.google.android.gms.location.LocationListener) this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mStop) {
            return;
        }
        CameraPosition cameraPos = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(19)
                .bearing(0).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos));

        mMap.clear();
        LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
        Log.v("Latitude", String.valueOf(location.getLatitude()));
        Log.v("Longitude", String.valueOf(location.getLongitude()));
        MarkerOptions options = new MarkerOptions();
        options.position(latlng);

        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher);
        options.icon(icon);
        mMap.addMarker(options);

        if (mStart) {
            if (mFirst) {
                Bundle args = new Bundle();
                args.putDouble("lat", location.getLatitude());
                args.putDouble("lon", location.getLongitude());

                getLoaderManager().restartLoader(ADDRESSLOADER_ID, args, this);
                mFirst = !mFirst;
            } else {
                drawTrace(latlng);
                sumDistance();
            }
        }
    }


    public void onStatusChanged(String provider, int status, Bundle extras) {

    }


    public void onProviderEnabled(String provider) {

    }


    public void onProviderDisabled(String provider) {

    }

    private void drawTrace(LatLng latlng) {
        mRunList.add(latlng);
        if (mRunList.size() > 2) {
            PolylineOptions polyOptions = new PolylineOptions();
            for (LatLng polyLatLng : mRunList) {
                polyOptions.add(polyLatLng);
            }
            polyOptions.color(Color.BLUE);
            polyOptions.width(3);
            polyOptions.geodesic(false);
            mMap.addPolyline(polyOptions);
        }
    }

    private void sumDistance() {
        if (mRunList.size() < 2) {
            return;
        }

        mMeter = 0;
        float[] results = new float[3];
        int i = 1;
        while (i < mRunList.size()) {
            results[0] = 0;
            Location.distanceBetween(mRunList.get(i - 1).latitude, mRunList.get(i - 1).longitude,
                    mRunList.get(i).latitude, mRunList.get(i).longitude, results);
            mMeter += results[0];
            i++;
        }

        //distanceBetweenの距離はメートル単位
        double disMeter = mMeter / 1000;
        TextView disText = (TextView) findViewById(R.id.disText);
        disText.setText(String.format("%.2f" + " km", disMeter));
    }

    private void calcSpeed() {
        sumDistance();
        mSpeed = (mMeter / 1000) / (mElapsedTime / 1000) * 60 * 60;
    }
    private void saveConfirm() {
        //DangerousなPermissionはリクエストして許可をもらわないと使えない
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //一度拒否された時、Rationale（理論的根拠）を説明して、再度許可ダイアログを出すようにする
                new AlertDialog.Builder(this)
                        .setTitle("許可が必要です")
                        .setMessage("ジョギングの記録を保存するためには、WRITE_EXTERNAL_STARGEを許可してください")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //OK button pressed
                                requestWriteExternalStrage();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showToast("外部へのファイルの保存許可されなかったので、記録できません");
                            }
                        })
                        .show();
            } else {
                //まだ許可を求める前の時、許可を求めるタイプのダイアログを表示します。
                requestWriteExternalStrage();
            }
        } else {
            saveConfirmDialog();
        }
    }

    private void requestWriteExternalStrage(){
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
    }
    private void saveConfirmDialog() {
        String message = "時間:";
        TextView disText = (TextView) findViewById(R.id.disText);

        message = message + mChronometer.getText().toString() + " " +
                "距離" + disText.getText() + "¥n" +
                "時速" + String.format("%.2f" + " km", mSpeed);

        SaveConfirmDialogFragment newFragment = SaveConfirmDialogFragment.newInstance(
                R.string.save_confirm_dialog_title, message);

        newFragment.show(getFragmentManager(), "dialog");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected() ) {
            stopLocationUpdates();
        }
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onStop(){
        super.onStop();
        //自プログラムがオフにした場合はワイファイをオンにする処理
        if(mWifiOff){
            mWifi.setWifiEnabled(true);
        }
    }
    protected void stopLocationUpdates() {
        mFusedLocationProviderApi.removeLocationUpdates(mGoogleApiClient, (com.google.android.gms.location.LocationListener) this);
    }
    @Override
    public void onConnectionSuspended(int cause){
        //Do nothing
    }
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        //Do nothing
    }

    @Override
    public Loader<Address> onCreateLoader(int id, Bundle args){
        double lat = args.getDouble("lat");
        double lon = args.getDouble("lon");
        return new AddressTaskLoader(this,lat,lon);

    }

    @Override
    public void onLoadFinished(Loader<Address> loader, Address result) {
        if (result != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < result.getMaxAddressLineIndex() + 1; i++) {
                String item = result.getAddressLine(i);
                if (item == null) {
                    break;
                }
                sb.append(item);
            }
            TextView address = (TextView) findViewById(R.id.address);

            address.setText(sb.toString());
        }
    }

    @Override
    public void onLoaderReset(Loader<Address> loader){
    }

    public void saveJogViaCTP(){

        String strDate = new SimpleDateFormat("yyyy/MM/dd").format(mStartTimeMillis);

        TextView txtAddress = (TextView)findViewById(R.id.address);

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_DATE, strDate);
        values.put(DatabaseHelper.COLUMN_ELAPSEDTIME, mChronometer.getText().toString());
        values.put(DatabaseHelper.COLUMN_DISTANCE, mMeter);
        values.put(DatabaseHelper.COLUMN_SPEED, mSpeed);
        values.put(DatabaseHelper.COLUMN_ADDRESS, txtAddress.getText().toString());
        Uri uri = getContentResolver().insert(JogRecordContentProvider.CONTENT_URI,values);
        showToast("データを保存しました");
    }

    public void saveJog() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String strDate = new SimpleDateFormat("yyyy/MM/dd").format(mStartTimeMillis);
        TextView txtAddress = (TextView) findViewById(R.id.address);

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_DATE, strDate);
        values.put(DatabaseHelper.COLUMN_ELAPSEDTIME, mChronometer.getText().toString());
        values.put(DatabaseHelper.COLUMN_DISTANCE, mMeter);
        values.put(DatabaseHelper.COLUMN_SPEED, mSpeed);
        values.put(DatabaseHelper.COLUMN_ADDRESS, txtAddress.getText().toString());
        try {
            db.insert(DatabaseHelper.TABLE_JOGRECORD, null, values);
        } catch (Exception e) {
            showToast("データの保存に失敗しました");
        } finally {
            db.close();
        }
    }
    private void showToast(String msg){
        Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        error.show();
    }
}
