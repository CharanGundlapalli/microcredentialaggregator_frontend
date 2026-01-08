package com.example.microcredential;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.microcredential.network.ApiConfig;
import com.example.microcredential.session.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.example.microcredential.LoginActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProfileActivity extends AppCompatActivity {

    private TextInputEditText etFullName, etEmail;
    private MaterialButton btnEditProfile, btnSaveProfile, btnLogout;
    private SessionManager sessionManager;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sessionManager = new SessionManager(this);
        userRole = sessionManager.getRole();

        etFullName = findViewById(R.id.et_full_name);
        etEmail = findViewById(R.id.et_email);
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnSaveProfile = findViewById(R.id.btn_save_profile);
        btnLogout = findViewById(R.id.btn_logout);

        // Hide Edit button for Issuer
        if ("issuer".equalsIgnoreCase(userRole)) {
            btnEditProfile.setVisibility(View.GONE);
        }

        btnEditProfile.setOnClickListener(v -> enableEditing());
        btnSaveProfile.setOnClickListener(v -> saveProfile());
        btnLogout.setOnClickListener(v -> logout());

        // Back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Profile");
        }

        // App Lock Switch

        // Settings Button
        MaterialButton btnSettings = findViewById(R.id.btn_settings);
        btnSettings.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, SettingsActivity.class));
        });

        // Fetch Certificates Button (Users only)
        MaterialButton btnFetch = findViewById(R.id.btn_fetch_certificate);
        if ("user".equalsIgnoreCase(userRole)) {
            btnFetch.setVisibility(View.VISIBLE);
            btnFetch.setOnClickListener(v -> fetchPendingCertificates());
        } else {
            btnFetch.setVisibility(View.GONE);
        }

        // Toolbar setup helper if needed, or rely on layout's toolbar
        View toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setOnClickListener(v -> finish());
        }

        setupBottomNavigation();
        fetchProfile();
    }

    private void setupBottomNavigation() {
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNavigationView = findViewById(
                R.id.bottom_navigation);

        // Inflate menu based on role
        if ("admin".equalsIgnoreCase(userRole)) {
            bottomNavigationView.inflateMenu(R.menu.admin_bottom_navigation_menu);
        } else if ("issuer".equalsIgnoreCase(userRole)) {
            bottomNavigationView.inflateMenu(R.menu.issuer_bottom_navigation_menu);
        } else if ("employer".equalsIgnoreCase(userRole)) {
            bottomNavigationView.inflateMenu(R.menu.employer_bottom_navigation_menu);
        } else {
            bottomNavigationView.inflateMenu(R.menu.bottom_navigation_menu); // Default user
        }

        // Set selected item
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);

        // Handle navigation
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_profile) {
                return true;
            }

            Intent intent = null;
            if ("admin".equalsIgnoreCase(userRole)) {
                if (itemId == R.id.navigation_home) {
                    intent = new Intent(this, AdmindashboardActivity.class);
                } else if (itemId == R.id.navigation_users) {
                    intent = new Intent(this, AdminUsersActivity.class);
                } else if (itemId == R.id.navigation_requests) {
                    intent = new Intent(this, AdminRequestsActivity.class);
                }
            } else if ("issuer".equalsIgnoreCase(userRole)) {
                if (itemId == R.id.navigation_home) {
                    intent = new Intent(this, IssuerdashboardActivity.class);
                } else if (itemId == R.id.navigation_issue) {
                    intent = new Intent(this, IssuerIssueCertificateActivity.class);
                }
                // Add History if available
                else if (itemId == R.id.navigation_history) {
                    startActivity(new Intent(this, IssuerHistoryActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                }
            } else if ("employer".equalsIgnoreCase(userRole)) {
                if (itemId == R.id.navigation_home) {
                    intent = new Intent(this, EmployerdashboardActivity.class);
                } else if (itemId == R.id.navigation_search) {
                    intent = new Intent(this, EmployerSearchActivity.class);
                }
            } else { // User
                if (itemId == R.id.navigation_home) {
                    intent = new Intent(this, DashboardActivity.class);
                } else if (itemId == R.id.navigation_certificates) {
                    intent = new Intent(this, CertificatesActivity.class);
                } else if (itemId == R.id.navigation_add) {
                    intent = new Intent(this, UploadCertificateActivity.class);
                }
            }

            if (intent != null) {
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void fetchProfile() {
        new Thread(() -> {
            try {
                URL url = new URL(ApiConfig.PROFILE_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                String sessionId = sessionManager.getSessionId();
                android.util.Log.d("ProfileActivity", "Session ID: " + sessionId);

                if (sessionId != null) {
                    conn.setRequestProperty("Cookie", "PHPSESSID=" + sessionId);
                }

                int responseCode = conn.getResponseCode();
                android.util.Log.d("ProfileActivity", "Response Code: " + responseCode);

                if (responseCode == 200) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null)
                        response.append(line);

                    String respStr = response.toString();
                    android.util.Log.d("ProfileActivity", "Response: " + respStr);

                    JSONObject jsonResponse = new JSONObject(respStr);
                    if ("success".equals(jsonResponse.optString("status"))) {
                        JSONObject data = jsonResponse.optJSONObject("data");
                        if (data != null) {
                            JSONObject finalData = data;
                            runOnUiThread(() -> {
                                etFullName.setText(finalData.optString("full_name"));
                                etEmail.setText(finalData.optString("email"));
                                String role = finalData.optString("role");
                                TextView tvRole = findViewById(R.id.tv_role);
                                tvRole.setText(role);

                                // Joined Date
                                String createdAt = finalData.optString("created_at");
                                TextView tvJoinedDate = findViewById(R.id.tv_joined_date);
                                if (!createdAt.isEmpty() && !createdAt.equals("null")) {
                                    tvJoinedDate.setText("Member since: " + createdAt);
                                } else {
                                    tvJoinedDate.setVisibility(View.GONE);
                                }

                                // Status (Issuer only)
                                TextView tvStatus = findViewById(R.id.tv_status);
                                if ("issuer".equalsIgnoreCase(role)) {
                                    tvStatus.setVisibility(View.VISIBLE);
                                    String verified = finalData.optString("verified");
                                    if ("1".equals(verified)) {
                                        tvStatus.setText("Status: Verified Account");
                                        tvStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                                    } else {
                                        tvStatus.setText("Status: Verification Pending");
                                        tvStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                                    }
                                } else {
                                    tvStatus.setVisibility(View.GONE);
                                }
                            });
                        } else {
                            runOnUiThread(() -> Toast
                                    .makeText(this, "Profile data missing from server response", Toast.LENGTH_SHORT)
                                    .show());
                        }
                    } else {
                        runOnUiThread(() -> Toast.makeText(this,
                                "Failed to load profile: " + jsonResponse.optString("message"), Toast.LENGTH_SHORT)
                                .show());
                    }
                } else {
                    runOnUiThread(
                            () -> Toast.makeText(this, "Server Error: " + responseCode, Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void enableEditing() {
        etFullName.setEnabled(true);
        btnEditProfile.setVisibility(View.GONE);
        btnSaveProfile.setVisibility(View.VISIBLE);
    }

    private void saveProfile() {
        String newName = etFullName.getText().toString().trim();
        if (newName.isEmpty()) {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                URL url = new URL(ApiConfig.UPDATE_PROFILE_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Cookie", "PHPSESSID=" + sessionManager.getSessionId());
                conn.setDoOutput(true);

                JSONObject params = new JSONObject();
                params.put("full_name", newName);

                OutputStream os = conn.getOutputStream();
                os.write(params.toString().getBytes());
                os.flush();
                os.close();

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null)
                    response.append(line);

                JSONObject jsonResponse = new JSONObject(response.toString());
                runOnUiThread(() -> {
                    Toast.makeText(this, jsonResponse.optString("message"), Toast.LENGTH_SHORT).show();
                    if ("success".equals(jsonResponse.optString("status"))) {
                        etFullName.setEnabled(false);
                        btnSaveProfile.setVisibility(View.GONE);
                        btnEditProfile.setVisibility(View.VISIBLE);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void logout() {
        sessionManager.logoutUser();
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void fetchPendingCertificates() {
        new Thread(() -> {
            try {
                // Change URL to point to fetch_pending_certificates.php
                // Assuming ApiConfig has a base URL, constructing full path since it might not
                // be in ApiConfig yet
                // Or better, add it to ApiConfig later. For now, construct manually or replace
                // "profile.php" with "fetch_pending_certificates.php"
                String fetchUrl = ApiConfig.PROFILE_URL.replace("profile.php", "fetch_pending_certificates.php");

                URL url = new URL(fetchUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST"); // POST or GET, PHP script handles both or doesn't specify. Session is
                                               // key.

                String sessionId = sessionManager.getSessionId();
                if (sessionId != null) {
                    conn.setRequestProperty("Cookie", "PHPSESSID=" + sessionId);
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null)
                        response.append(line);

                    JSONObject jsonResponse = new JSONObject(response.toString());

                    if ("session_expired".equals(jsonResponse.optString("status"))) {
                        runOnUiThread(() -> {
                            Toast.makeText(this, jsonResponse.optString("message"), Toast.LENGTH_LONG).show();
                            sessionManager.logoutUser();
                        });
                        return;
                    }

                    if ("success".equals(jsonResponse.optString("status"))) {
                        String message = jsonResponse.optString("message");
                        int count = jsonResponse.optInt("count");
                        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_LONG).show());
                    } else {
                        String message = jsonResponse.optString("message");
                        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(
                            () -> Toast.makeText(this, "Failed to fetch: " + responseCode, Toast.LENGTH_SHORT).show());
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(
                        () -> Toast.makeText(this, "Error fetching: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
