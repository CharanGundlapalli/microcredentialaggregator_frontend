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

public class IssuerdashboardActivity extends AppCompatActivity {

    TextView tvWelcome, tvUserInitial, tvTotalCertsIssued, tvTotalLearnersCovered;
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
        setContentView(R.layout.activity_issuer_dashboard);

        // Bind UI elements
        tvWelcome = findViewById(R.id.tv_welcome);
        tvUserInitial = findViewById(R.id.tv_user_initial);
        ivLogout = findViewById(R.id.iv_logout);
        bottomNavigation = findViewById(R.id.bottom_navigation_issuer);
        tvTotalCertsIssued = findViewById(R.id.tv_total_certs_issued);
        tvTotalLearnersCovered = findViewById(R.id.tv_total_learners_covered);

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
            } else if (itemId == R.id.navigation_issue) {
                startActivity(new Intent(IssuerdashboardActivity.this, IssuerIssueCertificateActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.navigation_history) {
                startActivity(new Intent(IssuerdashboardActivity.this, IssuerHistoryActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.navigation_profile) {
                startActivity(new Intent(IssuerdashboardActivity.this, ProfileActivity.class));
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
                        Toast.makeText(IssuerdashboardActivity.this, jsonResponse.optString("message"),
                                Toast.LENGTH_LONG).show();
                        sessionManager.logoutUser();
                    });
                    return;
                }

                if (jsonResponse.getString("status").equals("success")) {
                    runOnUiThread(() -> {
                        try {
                            tvTotalCertsIssued.setText(String.valueOf(jsonResponse.getInt("certificates_issued")));
                            tvTotalLearnersCovered
                                    .setText(String.valueOf(jsonResponse.getInt("pending_user_certificates")));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    runOnUiThread(() -> {
                        try {
                            Toast.makeText(IssuerdashboardActivity.this, jsonResponse.getString("message"),
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
                        Toast.makeText(IssuerdashboardActivity.this, "Logged out successfully", Toast.LENGTH_SHORT)
                                .show();
                        redirectToLogin();
                    });
                } else {
                    runOnUiThread(() -> {
                        try {
                            Toast.makeText(IssuerdashboardActivity.this, jsonResponse.getString("message"),
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
        Intent intent = new Intent(IssuerdashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
