package com.example.tsunehitokita.twotouchmail;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnPickUp = (Button)this.findViewById(R.id.button1);
        btnPickUp.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

            Intent intent = new Intent(MainActivity.this, PickUpActivity.class);
            startActivity(intent);
            }
        });
        Button btnNoDinner = (Button)this.findViewById(R.id.button2);
        btnNoDinner.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, NoDinnerActivity.class);
                startActivity(intent);
            }
        });
    }
}
