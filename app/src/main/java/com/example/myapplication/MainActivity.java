package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // getSupportActionBar().hide();

        //splash screen
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){

                Intent splashEndIntent = new Intent(MainActivity.this, Login.class);
                startActivity(splashEndIntent);
                finish();
            }
        },2000);
    }
}