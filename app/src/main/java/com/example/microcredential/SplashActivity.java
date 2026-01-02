package com.example.microcredential;

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
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            boolean isFirstTime = prefs.getBoolean(IS_FIRST_TIME_LAUNCH, true);

            if (isFirstTime) {
                // Show Onboarding screen
                startActivity(new Intent(SplashActivity.this, OnboardingActivity.class));
            } else {
                // Show Login screen
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }

            finish();
        }, 2000); // 2 seconds delay
    }
}
