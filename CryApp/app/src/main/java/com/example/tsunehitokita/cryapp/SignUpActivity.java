package com.example.tsunehitokita.cryapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button btnSignIn = (Button) this.findViewById(R.id.SignUpButton);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //新規登録ののPOST
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {

                        //フォームの値を取得(ユーザーネーム)
                        EditText editText1 = (EditText) findViewById(R.id.EditUserNameSignUp);
                        String strUserName = editText1.getText().toString();
                        //フォームの値を取得(Eメール)
                        EditText editText2 = (EditText) findViewById(R.id.EditEmailSignUp);
                        String strEmail = editText2.getText().toString();
                        //フォームの値を取得(パスワード)
                        EditText editText3 = (EditText) findViewById(R.id.EditPasswordSignUp);
                        String strPassword = editText3.getText().toString();

                        // リクエストオブジェクトを作って
                        RequestBody formBody = new FormBody.Builder()
                                //フォームの内容を入れる、を実装
                                //フォームの内容を入れる
                                .add("userName", strUserName)
                                .add("email", strEmail)
                                .add("passWord", strPassword )
                                .build();


                        // リクエストオブジェクトを作って
                        Request request = new Request.Builder()
                                .url("https://private-6cfe3-bee2066.apiary-mock.com/signup")
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
                        Intent intent = new Intent(SignUpActivity.this, CryActivity.class);
                        startActivity(intent);
                    }
                }.execute();


            }
        });
        //画面遷移
        TextView toLogIn= (TextView) this.findViewById(R.id.toLogIn);
        toLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(SignUpActivity.this, LogInActivity.class);
                startActivity(intent2);
            }
        });
    }
}
