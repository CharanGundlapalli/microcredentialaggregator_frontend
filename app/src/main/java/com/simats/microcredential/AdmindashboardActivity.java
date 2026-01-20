package com.simats.microcredential;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.simats.microcredential.network.ApiConfig;
import com.simats.microcredential.session.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AdmindashboardActivity extends AppCompatActivity {

    TextView tvWelcome, tvUserInitial, tvTotalUsers, tvTotalCertificates;
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
        setContentView(R.layout.activity_admin_dashboard);

        // Bind UI elements
        tvWelcome = findViewById(R.id.tv_welcome);
        tvUserInitial = findViewById(R.id.tv_user_initial);
        ivLogout = findViewById(R.id.iv_logout);
        bottomNavigation = findViewById(R.id.bottom_navigation_admin);
        tvTotalUsers = findViewById(R.id.tv_users_count);
        tvTotalCertificates = findViewById(R.id.tv_total_certs);

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
        ivLogout.setOnClickListener(v -> logoutUser());

        // Bottom navigation
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                // You are already here
                return true;
            } else if (itemId == R.id.navigation_users) {
                // Navigate to Users screen
                Intent intent = new Intent(AdmindashboardActivity.this, AdminUsersActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0); // Smooth transition
                finish(); // Optional: finish current activity if you want to keep stack clean or manage
                          // differently
                return true;
            } else if (itemId == R.id.navigation_requests) {
                Intent intent = new Intent(AdmindashboardActivity.this, AdminRequestsActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.navigation_profile) {
                startActivity(new Intent(AdmindashboardActivity.this, ProfileActivity.class));
                return true;
            }
            return false;
        });

        // Fetch dashboard stats
        fetchDashboardStats();
    }

    private void fetchDashboardStats() {
        new Thread(() -> {
            try {
                URL url = new URL(ApiConfig.DASHBOARD_STATS_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Cookie", "PHPSESSID=" + sessionManager.getSessionId());

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();

                JSONObject jsonResponse = new JSONObject(response.toString());

                if (jsonResponse.optString("status").equals("session_expired")) {
                    runOnUiThread(() -> {
                        Toast.makeText(AdmindashboardActivity.this, jsonResponse.optString("message"),
                                Toast.LENGTH_LONG).show();
                        sessionManager.logoutUser();
                    });
                    return;
                }

                if (jsonResponse.getString("status").equals("success")) {
                    runOnUiThread(() -> {
                        try {
                            tvTotalUsers.setText(String.valueOf(jsonResponse.getInt("total_users")));
                            tvTotalCertificates.setText(String.valueOf(jsonResponse.getInt("total_certificates")));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    runOnUiThread(() -> {
                        try {
                            Toast.makeText(AdmindashboardActivity.this, jsonResponse.getString("message"),
                                    Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void logoutUser() {
        new Thread(() -> {
            try {
                URL url = new URL(ApiConfig.LOGOUT_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Cookie", "PHPSESSID=" + sessionManager.getSessionId());

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();

                JSONObject jsonResponse = new JSONObject(response.toString());

                if (jsonResponse.getString("status").equals("success")) {
                    runOnUiThread(() -> {
                        sessionManager.logoutUser();
                        Toast.makeText(AdmindashboardActivity.this, "Logged out successfully", Toast.LENGTH_SHORT)
                                .show();
                        redirectToLogin();
                    });
                } else {
                    runOnUiThread(() -> {
                        try {
                            Toast.makeText(AdmindashboardActivity.this, jsonResponse.getString("message"),
                                    Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Redirect to LoginActivity
    private void redirectToLogin() {
        Intent intent = new Intent(AdmindashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
