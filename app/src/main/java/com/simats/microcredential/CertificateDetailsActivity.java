package com.simats.microcredential;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;

import com.simats.microcredential.network.ApiConfig;
import com.simats.microcredential.session.SessionManager;
import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CertificateDetailsActivity extends AppCompatActivity {

    private String certificateUid;
    private SessionManager sessionManager;
    private ProgressBar progressBar;

    // UI Elements
    private TextView tvTitle, tvStatus;
    private LinearLayout btnBack;
    private MaterialButton btnDownload, btnRemove;

    // Rows
    private View rowOrg, rowIssued, rowExpiry, rowCredentialId, rowType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_certificate);

        sessionManager = new SessionManager(this);
        certificateUid = getIntent().getStringExtra("certificate_uid");

        if (certificateUid == null) {
            Toast.makeText(this, "Invalid Certificate ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();

        // 🚀 Optimistic UI: Pre-fill data from previous screen
        if (getIntent().hasExtra("certificate_title")) {
            tvTitle.setText(getIntent().getStringExtra("certificate_title"));
        }
        if (getIntent().hasExtra("verification_status")) {
            setStatus(getIntent().getStringExtra("verification_status"));
        }
        if (getIntent().hasExtra("issue_date")) {
            setRow(rowIssued, "Issue Date", getIntent().getStringExtra("issue_date"));
        }
        if (getIntent().hasExtra("expiry_date")) {
            String expiry = getIntent().getStringExtra("expiry_date");
            if (expiry == null || expiry.isEmpty() || expiry.equals("null"))
                expiry = "No Expiry";
            setRow(rowExpiry, "Expiry Date", expiry);
        }

        fetchDetails();
    }

    private void showRemoveConfirmationDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Remove Certificate")
                .setMessage("Are you sure you want to remove this certificate? This action cannot be undone.")
                .setPositiveButton("Remove", (dialog, which) -> removeCertificate())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void removeCertificate() {
        // Optimistic UI: Feedback immediately and close
        Toast.makeText(getApplicationContext(), "Removing certificate...", Toast.LENGTH_SHORT).show();
        finish();

        new Thread(() -> {
            try {
                URL url = new URL(ApiConfig.REMOVE_CERTIFICATE_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                // Use Application Context for session manager to avoid leaking Activity context
                SessionManager appSessionManager = new SessionManager(getApplicationContext());
                conn.setRequestProperty("Cookie", "PHPSESSID=" + appSessionManager.getSessionId());
                conn.setDoOutput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("certificate_uid", certificateUid);

                java.io.OutputStream os = conn.getOutputStream();
                os.write(jsonParam.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Success (we assume success ideally)
                    // We could log it
                } else {
                    // Log error
                }
                conn.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
                // Since activity is finished, we can't easily show error to user without
                // disrupting them
                // Just log it
            }
        }).start();
    }

    private void fetchDetails() {
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            try {
                String apiUrl = ApiConfig.VIEW_CERTIFICATE_DETAILS_URL + "?certificate_uid=" + certificateUid;
                URL url = new URL(apiUrl);
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
                            populateData(jsonResponse.getJSONObject("certificate"));
                        } else {
                            // Toast.makeText(this, jsonResponse.getString("message"),
                            // Toast.LENGTH_SHORT).show();
                            // Maybe session expired or invalid ID
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Parsing error", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void populateData(JSONObject data) {
        try {
            tvTitle.setText(data.getString("certificate_title"));

            // Status Logic
            String status = data.getString("verification_status");
            setStatus(status);

            // Rows
            setRow(rowOrg, "Issuing Organization", data.getString("issuing_organization"));
            setRow(rowIssued, "Issue Date", data.getString("issue_date"));

            String expiry = data.optString("expiry_date", "No Expiry");
            if (expiry.equals("null") || expiry.isEmpty())
                expiry = "No Expiry";
            setRow(rowExpiry, "Expiry Date", expiry);

            setRow(rowCredentialId, "Credential ID", data.getString("credential_id"));
            setRow(rowType, "Type", data.getString("certificate_type"));

            // Download
            // Use certificate_file and construct URL dynamically to avoid localhost issues
            // from backend
            String fileName = data.getString("certificate_file");
            String downloadUrl = ApiConfig.BASE_URL + "certificates/" + fileName;

            btnDownload.setOnClickListener(v -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl));
                startActivity(browserIntent);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setRow(View row, String label, String value) {
        TextView tvLabel = row.findViewById(R.id.tv_label);
        TextView tvValue = row.findViewById(R.id.tv_value);

        tvLabel.setText(label);
        tvValue.setText(value);
    }

    private void setStatus(String status) {
        tvStatus.setText(status.substring(0, 1).toUpperCase() + status.substring(1));

        int color;
        if (status.equalsIgnoreCase("verified") || status.equalsIgnoreCase("accepted")) {
            color = Color.parseColor("#4CAF50"); // Green
        } else if (status.equalsIgnoreCase("rejected")) {
            color = Color.parseColor("#F44336"); // Red
        } else {
            color = Color.parseColor("#FFC107"); // Amber
        }

        Drawable background = tvStatus.getBackground();
        if (background != null) {
            background = background.mutate();
            if (background instanceof GradientDrawable) {
                ((GradientDrawable) background).setColor(color);
            } else {
                DrawableCompat.setTint(background, color);
            }
        }
    }

    private void initViews() {
        progressBar = findViewById(R.id.progressBar);
        tvTitle = findViewById(R.id.tv_title);
        tvStatus = findViewById(R.id.tv_status);
        btnBack = findViewById(R.id.btn_back);
        btnDownload = findViewById(R.id.btn_download);
        btnRemove = findViewById(R.id.btn_remove);

        rowOrg = findViewById(R.id.row_organization);
        rowIssued = findViewById(R.id.row_issued);
        rowExpiry = findViewById(R.id.row_expiry);
        rowCredentialId = findViewById(R.id.row_credential_id);
        rowType = findViewById(R.id.row_type);

        // Admin buttons
        LinearLayout layoutAdminActions = findViewById(R.id.layout_admin_actions);
        android.widget.Button btnVerify = findViewById(R.id.btn_verify);
        android.widget.Button btnReject = findViewById(R.id.btn_reject);

        boolean isAdmin = getIntent().getBooleanExtra("is_admin", false);
        boolean isEmployerView = getIntent().getBooleanExtra("is_employer_view", false);
        String role = sessionManager.getRole();

        if (isAdmin || "admin".equalsIgnoreCase(role)) {
            layoutAdminActions.setVisibility(View.VISIBLE);
            btnRemove.setVisibility(View.GONE); // Hide remove for admin

            btnVerify.setOnClickListener(v -> verifyCertificate("verified"));
            btnReject.setOnClickListener(v -> verifyCertificate("rejected"));
        } else if (isEmployerView) {
            // Employer view: Read-only, no actions
            layoutAdminActions.setVisibility(View.GONE);
            btnRemove.setVisibility(View.GONE);
        } else {
            // Regular user view
            layoutAdminActions.setVisibility(View.GONE);
            btnRemove.setVisibility(View.VISIBLE);
        }

        btnBack.setOnClickListener(v -> finish());
        btnRemove.setOnClickListener(v -> showRemoveConfirmationDialog());
    }

    private void verifyCertificate(String status) {
        progressBar.setVisibility(View.VISIBLE);
        new Thread(() -> {
            try {
                URL url = new URL(ApiConfig.VERIFY_CERTIFICATE_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Cookie", "PHPSESSID=" + sessionManager.getSessionId());
                conn.setDoOutput(true);

                JSONObject params = new JSONObject();
                params.put("certificate_uid", certificateUid);
                params.put("status", status);

                java.io.OutputStream os = conn.getOutputStream();
                java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.OutputStreamWriter(os, "UTF-8"));
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
                            Toast.makeText(CertificateDetailsActivity.this, "Certificate " + status, Toast.LENGTH_SHORT)
                                    .show();
                            setStatus(status);
                        } else {
                            Toast.makeText(CertificateDetailsActivity.this, jsonResponse.getString("message"),
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
                    Toast.makeText(CertificateDetailsActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
}
