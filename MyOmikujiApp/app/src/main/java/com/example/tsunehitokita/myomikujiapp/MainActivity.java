package com.example.tsunehitokita.myomikujiapp;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void getOmikuji(View view){
        TextView tv = (TextView) findViewById(R.id.myTextView);
        String[] results = {
                "大吉",
                "中吉",
                "小吉"
        };

        Random randomGenerator = new Random();
        int num = randomGenerator.nextInt(results.length);
//        String result = Integer.toString(num);
        if (num == 0){
            tv.setTextColor(Color.RED);
        }else{
            tv.setTextColor(Color.rgb(0,0,0));
        }

        tv.setText(results[num]);

    }

}
