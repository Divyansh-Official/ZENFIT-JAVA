package com.stream.zenfit;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent signupIntent = new Intent(SplashScreenActivity.this, SignUpNameActivity.class);
                SplashScreenActivity.this.startActivity(signupIntent);
                SplashScreenActivity.this.finish();
            }
        }, 2250);

    }
}