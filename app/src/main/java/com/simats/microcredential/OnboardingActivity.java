package com.simats.microcredential;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

public class OnboardingActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MicroCredentialPrefs";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        loadWelcomePage();
    }

    private void loadWelcomePage() {
        View view = LayoutInflater.from(this)
                .inflate(R.layout.onboarding_page_welcome, null);

        Button nextBtn = view.findViewById(R.id.btnNext);
        nextBtn.setOnClickListener(v -> loadFeaturesPage());

        setPage(view);
    }

    private void loadFeaturesPage() {
        View view = LayoutInflater.from(this)
                .inflate(R.layout.onboarding_page_features, null);

        Button nextBtn = view.findViewById(R.id.btnNext);
        nextBtn.setOnClickListener(v -> {
            setFirstTimeLaunchFlag();
            goToLogin();
        });

        setPage(view);
    }

    private void setPage(View view) {
        FrameLayout container = findViewById(R.id.onboardingContainer);
        container.removeAllViews();
        container.addView(view);
    }

    private void setFirstTimeLaunchFlag() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, false);
        editor.apply();
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish(); // remove onboarding from back stack
    }
}
