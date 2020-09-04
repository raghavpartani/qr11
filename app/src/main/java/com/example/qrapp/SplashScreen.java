package com.example.qrapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Thread t1 = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(1000);
                    startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                    finish();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        t1.start();
    }
    public void onBackPressed() {


    }


}
