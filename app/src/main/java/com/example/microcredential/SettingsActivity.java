package com.example.microcredential;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.microcredential.utils.AppLockManager;
import com.example.microcredential.utils.BiometricHelper;
import com.example.microcredential.utils.ThemeHelper;
import com.google.android.material.materialswitch.MaterialSwitch;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        setupAppLockSwitch();
        setupDarkThemeSwitch();
    }

    private void setupAppLockSwitch() {
        MaterialSwitch switchAppLock = findViewById(R.id.switch_app_lock);
        switchAppLock.setChecked(AppLockManager.getInstance(this).isAppLockEnabled());

        switchAppLock.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Verify before enabling
                BiometricHelper.authenticate(this, new BiometricHelper.BiometricCallback() {
                    @Override
                    public void onSuccess() {
                        AppLockManager.getInstance(SettingsActivity.this).setAppLockEnabled(true);
                        Toast.makeText(SettingsActivity.this, "App Lock Enabled", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure() {
                        buttonView.setChecked(false); // Revert
                        Toast.makeText(SettingsActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // Verify before disabling
                BiometricHelper.authenticate(this, new BiometricHelper.BiometricCallback() {
                    @Override
                    public void onSuccess() {
                        AppLockManager.getInstance(SettingsActivity.this).setAppLockEnabled(false);
                        Toast.makeText(SettingsActivity.this, "App Lock Disabled", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure() {
                        buttonView.setChecked(true); // Revert
                        Toast.makeText(SettingsActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void setupDarkThemeSwitch() {
        MaterialSwitch switchDarkTheme = findViewById(R.id.switch_dark_theme);
        switchDarkTheme.setChecked(ThemeHelper.isDarkModeEnabled(this));

        switchDarkTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Check if state actually changed to prevent loops if setChecked is called
            // programmatically
            if (isChecked != ThemeHelper.isDarkModeEnabled(this)) {
                ThemeHelper.setDarkModeEnabled(this, isChecked);
                // Recreation happens automatically by AppCompatDelegate, but sometimes needs a
                // nudge or purely semantic handling
            }
        });
    }
}
