package com.example.tsunehitokita.cryapp;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.location.Location;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.Mapbox;

import java.io.IOException;
import java.util.jar.*;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class LogInActivity extends AppCompatActivity {

    private final int REQUEST_PERMISSION = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        // MapBoxのアクセストークン
        Mapbox.getInstance(this, getString(R.string.access_token));



        if(Build.VERSION.SDK_INT >= 23){
            checkPermission();
        }
        else{
            //何もしない
        }

        initButtons();

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
            ActivityCompat.requestPermissions(LogInActivity.this,
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
                Toast toast = Toast.makeText(this, "次の画面でCryボタン押したら落ちます", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }



    private void initButtons(){
        Button btnLogIn = (Button) this.findViewById(R.id.logInButton);

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ログインのPOST
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {

                        //フォームの値を取得(ユーザーネーム)
                        EditText editText1 = (EditText) findViewById(R.id.EditUserNameLogIn);
                        String strUserName = editText1.getText().toString();
                        //フォームの値を取得(パスワード)
                        EditText editText2 = (EditText) findViewById(R.id.EditPasswordLogIn);
                        String strPassword = editText2.getText().toString();

                        RequestBody formBody = new FormBody.Builder()

                                //フォームの内容を入れる
                                .add("userName", strUserName)
                                .add("passWord", strPassword )
                                .build();

                        // リクエストオブジェクトを作って
                        Request request = new Request.Builder()
                                .url("https://private-6cfe3-bee2066.apiary-mock.com/login")
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
                        Intent intent = new Intent(LogInActivity.this, CryActivity.class);
                        startActivity(intent);
                    }
                }.execute();
            }
        });
        //画面遷移
        TextView toSignUp= (TextView) this.findViewById(R.id.toSignUp);
        toSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(LogInActivity.this, SignUpActivity.class);
                startActivity(intent2);
            }
        });
    }
}
