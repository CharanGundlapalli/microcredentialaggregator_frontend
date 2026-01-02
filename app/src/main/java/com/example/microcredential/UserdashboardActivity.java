package com.example.microcredential;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.microcredential.session.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UserdashboardActivity extends AppCompatActivity {

    TextView tvWelcome, tvUserInitial;
    ImageView ivLogout;
    SessionManager sessionManager;
    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize session manager
        sessionManager = new SessionManager(this);

        // 🔐 Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            redirectToLogin();
            return;
        }

        // Set layout
        setContentView(R.layout.activity_dashboard);

        // Bind UI elements
        tvWelcome = findViewById(R.id.tv_welcome);
        tvUserInitial = findViewById(R.id.tv_user_initial);
        ivLogout = findViewById(R.id.iv_logout);
        bottomNavigation = findViewById(R.id.bottom_navigation);

        // Fetch session data
        String fullName = sessionManager.getFullName();

        // Display user details
        if (fullName != null && !fullName.isEmpty()) {
            tvWelcome.setText("Welcome back,\n" + fullName);
            tvUserInitial.setText(String.valueOf(fullName.charAt(0)));
        } else {
            tvWelcome.setText("Welcome back,\nDemo User");
            tvUserInitial.setText("D");
        }

        // Logout functionality
        ivLogout.setOnClickListener(v -> {
            sessionManager.logout();
            Toast.makeText(UserdashboardActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            redirectToLogin();
        });

        // Bottom navigation
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                // You are already here
                return true;
            } else if (itemId == R.id.navigation_certificates) {
                // TODO: Navigate to Certificates screen
                Toast.makeText(this, "Certificates", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.navigation_add) {
                // TODO: Navigate to Add screen
                Toast.makeText(this, "Add", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.navigation_profile) {
                // TODO: Navigate to Profile screen
                Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }

    // Redirect to LoginActivity
    private void redirectToLogin() {
        Intent intent = new Intent(UserdashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
