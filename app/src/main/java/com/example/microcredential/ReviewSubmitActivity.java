package com.example.microcredential;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.microcredential.network.ApiConfig;
import com.example.microcredential.session.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.database.Cursor;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.FrameLayout;

public class ReviewSubmitActivity extends AppCompatActivity {

    private static final String TAG = "ReviewSubmitActivity";

    SessionManager sessionManager;
    ImageView ivLogout;
    TextView tvUserInitial;
    EditText etCertificateTitle, etIssuingOrganization, etIssueDate, etExpiryDate, etCertificateType, etCredentialId;
    CheckBox cbNoExpiry;
    Button btnSubmitVerification, btnBack;
    BottomNavigationView bottomNavigation;

    private Uri fileUri;
    FrameLayout loadingOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_submit);
        loadingOverlay = findViewById(R.id.loading_overlay);
        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            redirectToLogin();
            return;
        }

        ivLogout = findViewById(R.id.iv_logout);
        tvUserInitial = findViewById(R.id.tv_user_initial);
        etCertificateTitle = findViewById(R.id.et_certificate_title);
        etIssuingOrganization = findViewById(R.id.et_issuing_organization);
        etIssueDate = findViewById(R.id.et_issue_date);
        etExpiryDate = findViewById(R.id.et_expiry_date);
        cbNoExpiry = findViewById(R.id.cb_no_expiry);
        btnSubmitVerification = findViewById(R.id.btn_submit_verification);
        btnBack = findViewById(R.id.btn_back);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        etCertificateType = findViewById(R.id.et_certificate_type);
        etCredentialId = findViewById(R.id.et_credential_id);

        String fullName = sessionManager.getFullName();
        if (fullName != null && !fullName.isEmpty()) {
            tvUserInitial.setText(String.valueOf(fullName.charAt(0)));
        } else {
            tvUserInitial.setText("D");
        }

        ivLogout.setOnClickListener(v -> logoutUser());

        btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        Intent intent = getIntent();
        etCertificateTitle.setText(intent.getStringExtra("certificate_title"));
        etCertificateType.setText(intent.getStringExtra("certificate_type"));
        etIssuingOrganization.setText(intent.getStringExtra("issuing_organization"));
        etCredentialId.setText(intent.getStringExtra("credential_id"));
        etIssueDate.setText(intent.getStringExtra("issue_date"));
        etExpiryDate.setText(intent.getStringExtra("expiry_date"));
        cbNoExpiry.setChecked(intent.getBooleanExtra("no_expiry", false));
        String fileUriString = intent.getStringExtra("fileUri");
        if (fileUriString != null) {
            fileUri = Uri.parse(fileUriString);
        }

        etCertificateTitle.setEnabled(false);
        etCertificateType.setEnabled(false);
        etIssuingOrganization.setEnabled(false);
        etCredentialId.setEnabled(false);
        etIssueDate.setEnabled(false);
        etExpiryDate.setEnabled(false);
        cbNoExpiry.setEnabled(false);

        btnSubmitVerification.setOnClickListener(v -> {
            loadingOverlay.setVisibility(View.VISIBLE);
            btnSubmitVerification.setEnabled(false);
            submitCertificate();
        });

        bottomNavigation.setSelectedItemId(R.id.navigation_add);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                startActivity(new Intent(this, DashboardActivity.class));
                return true;
            } else if (itemId == R.id.navigation_certificates) {
                startActivity(new Intent(this, CertificatesActivity.class));
                return true;
            } else if (itemId == R.id.navigation_add) {
                startActivity(new Intent(this, UploadCertificateActivity.class));
                return true;
            } else if (itemId == R.id.navigation_profile) {
                Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }

    private void submitCertificate() {
        new Thread(() -> {
            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            BufferedReader br = null;
            try {
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****" + System.currentTimeMillis() + "*****";

                URL url = new URL(ApiConfig.UPLOAD_CERTIFICATE_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("Cookie", "PHPSESSID=" + sessionManager.getSessionId());

                dos = new DataOutputStream(conn.getOutputStream());

                addFormField(dos, boundary, "certificate_title", etCertificateTitle.getText().toString());
                String type = etCertificateType.getText().toString().toLowerCase();

                if (!type.equals("academic") && !type.equals("professional") && !type.equals("skill")) {
                    type = "skill";
                }

                addFormField(dos, boundary, "certificate_type", type);
                addFormField(dos, boundary, "issuing_organization", etIssuingOrganization.getText().toString());

                String issuer = getIntent().getStringExtra("issuing_organization");
                if (issuer != null && issuer.equalsIgnoreCase("OTHER")) {
                    addFormField(dos, boundary, "new_issuer_name", getIntent().getStringExtra("new_issuer_name"));
                }

                addFormField(dos, boundary, "credential_id", etCredentialId.getText().toString());
                addFormField(dos, boundary, "issue_date", etIssueDate.getText().toString());
                addFormField(dos, boundary, "expiry_date", etExpiryDate.getText().toString());

                if (fileUri != null && fileUri.getPath() != null) {
                    String fileName = getFileName(fileUri);
                    String mimeType = getContentResolver().getType(fileUri);
                    if (mimeType == null) {
                        mimeType = "application/octet-stream";
                    }

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes(
                            "Content-Disposition: form-data; name=\"certificate\"; filename=\""
                                    + fileName + "\"" + lineEnd);
                    dos.writeBytes("Content-Type: " + mimeType + lineEnd);
                    dos.writeBytes(lineEnd);

                    try (InputStream inputStream = getContentResolver().openInputStream(fileUri);
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                        if (inputStream != null) {
                            byte[] buffer = new byte[1024];
                            int bytesRead;
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                byteArrayOutputStream.write(buffer, 0, bytesRead);
                            }
                            byte[] fileBytes = byteArrayOutputStream.toByteArray();
                            dos.write(fileBytes);
                        }
                    }

                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                }

                dos.flush();

                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }

                JSONObject jsonResponse = new JSONObject(response.toString());

                runOnUiThread(() -> {
                    loadingOverlay.setVisibility(View.GONE);
                    btnSubmitVerification.setEnabled(true);

                    try {
                        Toast.makeText(this, jsonResponse.getString("message"), Toast.LENGTH_LONG).show();
                        if ("success".equals(jsonResponse.getString("status"))) {
                            startActivity(new Intent(this, DashboardActivity.class));
                            finish();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON error", e);
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Error submitting certificate", e);
                runOnUiThread(() -> {
                    loadingOverlay.setVisibility(View.GONE);
                    btnSubmitVerification.setEnabled(true);
                    Toast.makeText(this, "Error submitting certificate", Toast.LENGTH_SHORT).show();
                });
            } finally {
                try {
                    if (dos != null)
                        dos.close();
                    if (br != null)
                        br.close();
                    if (conn != null)
                        conn.disconnect();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing streams", e);
                }
            }
        }).start();
    }

    private void addFormField(DataOutputStream dos, String boundary, String name, String value) throws IOException {
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        dos.writeBytes(twoHyphens + boundary + lineEnd);
        dos.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"" + lineEnd);
        dos.writeBytes(lineEnd);
        dos.writeBytes(value);
        dos.writeBytes(lineEnd);
    }

    private void logoutUser() {
        sessionManager.logout();
        Toast.makeText(ReviewSubmitActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        redirectToLogin();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private String getFileName(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) {
                        result = cursor.getString(index);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    @Override
    public void onBackPressed() {
        if (loadingOverlay.getVisibility() == View.VISIBLE)
            return;
        super.onBackPressed();
    }

}