package com.example.microcredential;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.microcredential.network.ApiConfig;
import com.example.microcredential.session.SessionManager;
import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class AdminUserDetailsActivity extends AppCompatActivity {

    private String userUid;
    private SessionManager sessionManager;

    private TextView tvInitial, tvName, tvRole, tvStatus, tvEmail, tvJoined;
    private MaterialButton btnViewCertificates, btnUpdateStatus;
    private ProgressBar progressBar;
    private ImageView btnBack;

    private String currentStatus = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_details);

        sessionManager = new SessionManager(this);

        // Get Intent Data
        userUid = getIntent().getStringExtra("user_uid");
        if (userUid == null) {
            Toast.makeText(this, "Error: Unknown User", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        fetchUserDetails();
    }

    private void initializeViews() {
        tvInitial = findViewById(R.id.tv_detail_initial);
        tvName = findViewById(R.id.tv_detail_name);
        tvRole = findViewById(R.id.tv_detail_role);
        tvStatus = findViewById(R.id.tv_detail_status);
        tvEmail = findViewById(R.id.tv_detail_email);
        tvJoined = findViewById(R.id.tv_detail_joined);

        btnViewCertificates = findViewById(R.id.btn_view_certificates);
        btnUpdateStatus = findViewById(R.id.btn_update_status);
        progressBar = findViewById(R.id.progressBar);
        btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> onBackPressed());

        btnUpdateStatus.setOnClickListener(v -> toggleUserStatus());

        btnViewCertificates.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminUserCertificatesActivity.class);
            intent.putExtra("user_uid", userUid);
            startActivity(intent);
        });
    }

    private void fetchUserDetails() {
        progressBar.setVisibility(View.VISIBLE);
        new Thread(() -> {
            try {
                URL url = new URL(ApiConfig.ADMIN_VIEW_USER_DETAILS_URL + "?user_uid=" + userUid);
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

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        if (jsonResponse.getString("status").equals("success")) {
                            JSONObject user = jsonResponse.getJSONObject("user");
                            populateData(user);
                        } else {
                            Toast.makeText(AdminUserDetailsActivity.this, jsonResponse.getString("message"),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AdminUserDetailsActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void populateData(JSONObject user) {
        try {
            String name = user.getString("full_name");
            String role = user.getString("role");
            String email = user.getString("email");
            String joined = user.getString("created_at");
            currentStatus = user.getString("status");

            tvName.setText(name);
            tvRole.setText("Role: " + role);
            tvEmail.setText(email);
            tvJoined.setText(joined);

            if (name != null && !name.isEmpty()) {
                tvInitial.setText(String.valueOf(name.trim().charAt(0)).toUpperCase());
            }

            updateStatusUI(currentStatus);

            // Hide "View Certificates" button if the user is an Issuer
            if ("issuer".equalsIgnoreCase(role)) {
                btnViewCertificates.setVisibility(View.GONE);
            } else {
                btnViewCertificates.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateStatusUI(String status) {
        tvStatus.setText(status.toUpperCase()); // "ACTIVE" or "INACTIVE" / "BLOCKED"?
        // PHP returns 'active' or 'blocked' based on get_users query,
        // but update_user_status.php accepts 'active' or 'inactive'.
        // Need to be consistent. Assuming 'inactive' == 'blocked' effectively or vice
        // versa.
        // Let's assume the DB uses 'active' / 'inactive' as per update script logic.

        if ("active".equalsIgnoreCase(status)) {
            tvStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
            tvStatus.setBackgroundResource(R.drawable.bg_status_active);

            btnUpdateStatus.setText("Deactivate User");
            btnUpdateStatus
                    .setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.holo_red_light));
        } else {
            tvStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
            // Reuse active bg or create new one? reuse for now or just text color
            tvStatus.setBackgroundResource(R.drawable.bg_status_active); // Just shape

            btnUpdateStatus.setText("Activate User");
            btnUpdateStatus
                    .setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.holo_green_light));
        }
    }

    private void toggleUserStatus() {
        String newStatus = "active".equalsIgnoreCase(currentStatus) ? "inactive" : "active";

        progressBar.setVisibility(View.VISIBLE);
        new Thread(() -> {
            try {
                URL url = new URL(ApiConfig.UPDATE_USER_STATUS_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Cookie", "PHPSESSID=" + sessionManager.getSessionId());
                conn.setDoOutput(true);

                JSONObject params = new JSONObject();
                params.put("user_uid", userUid);
                params.put("status", newStatus);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                writer.write(params.toString());
                writer.flush();
                writer.close();
                os.close();

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();

                JSONObject jsonResponse = new JSONObject(response.toString());

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        if (jsonResponse.getString("status").equals("success")) {
                            Toast.makeText(AdminUserDetailsActivity.this, "Status Updated", Toast.LENGTH_SHORT).show();
                            currentStatus = newStatus;
                            updateStatusUI(currentStatus);
                        } else {
                            Toast.makeText(AdminUserDetailsActivity.this, jsonResponse.getString("message"),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AdminUserDetailsActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
}
