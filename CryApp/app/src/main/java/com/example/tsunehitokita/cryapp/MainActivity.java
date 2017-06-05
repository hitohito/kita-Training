package com.example.tsunehitokita.cryapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import com.mapbox.mapboxsdk.Mapbox;

/**
 * Created by tsunehitokita on 2017/06/05.
 */

public class MainActivity extends Activity {
    public static final String PREFERENCES_FILE_NAME = "preference";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // MapBoxのアクセストークン
        Mapbox.getInstance(this, getString(R.string.access_token));


        if(loginCheck()){ // CryActivity に遷移
            Intent intent = new Intent(getApplicationContext(), CryActivity.class);
            startActivity(intent);
        }else{ // LoginActivity に遷移
            Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
            startActivity(intent);
        }
    }

    // ログイン判定
    public Boolean loginCheck(){
        SharedPreferences settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0); // 0 -> MODE_PRIVATE
        if(settings == null) return false;
        int login = (int) settings.getLong("logged-in", 0);
        if(login == 1) return true;
        else return false;
    }
}
