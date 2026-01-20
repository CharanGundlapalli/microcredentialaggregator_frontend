package com.simats.microcredential;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MicroCredentialPrefs";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            // Navigate to Subscription Screen
            startActivity(new Intent(SplashActivity.this, SubscriptionActivity.class));

            finish();
        }, 2000); // 2 seconds delay
    }
}
