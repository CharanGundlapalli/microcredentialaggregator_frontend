package com.example.microcredential;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.microcredential.network.ApiConfig;
import com.example.microcredential.session.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class IssuerIssueCertificateActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1;

    private MaterialButton btnSelectFile, btnUpload;
    private TextView tvSelectedFile, tvRemoveFile;
    private View uploadView, fileSelectedView;
    private ProgressBar progressBar;
    private SessionManager sessionManager;
    private Uri selectedFileUri;
    private String selectedFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issuer_issue_certificate);

        sessionManager = new SessionManager(this);

        btnSelectFile = findViewById(R.id.btn_select_file);
        btnUpload = findViewById(R.id.btn_upload);

        tvSelectedFile = findViewById(R.id.tv_selected_file);
        tvRemoveFile = findViewById(R.id.tv_remove_file);

        uploadView = findViewById(R.id.upload_view);
        fileSelectedView = findViewById(R.id.file_selected_view);

        progressBar = findViewById(R.id.progress_bar);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_issuer);

        // Bottom Navigation Setup
        bottomNav.setSelectedItemId(R.id.navigation_issue);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                startActivity(new Intent(getApplicationContext(), IssuerdashboardActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.navigation_history) {
                startActivity(new Intent(this, IssuerHistoryActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.navigation_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });

        btnSelectFile.setOnClickListener(v -> openFilePicker());
        tvRemoveFile.setOnClickListener(v -> removeFile());
        btnUpload.setOnClickListener(v -> uploadFile());
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/zip");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select ZIP File"), PICK_FILE_REQUEST);
    }

    private void removeFile() {
        selectedFileUri = null;
        selectedFileName = null;
        updateUI();
    }

    private void updateUI() {
        if (selectedFileUri != null) {
            uploadView.setVisibility(View.GONE);
            fileSelectedView.setVisibility(View.VISIBLE);
            tvSelectedFile.setText(selectedFileName);

            btnUpload.setEnabled(true);
            btnUpload.setBackgroundResource(R.drawable.bg_continue_button_active);
            btnUpload.setTextColor(getResources().getColor(android.R.color.white));
        } else {
            uploadView.setVisibility(View.VISIBLE);
            fileSelectedView.setVisibility(View.GONE);
            tvSelectedFile.setText("");

            btnUpload.setEnabled(false);
            btnUpload.setBackgroundResource(R.drawable.bg_continue_button_inactive);
            btnUpload.setTextColor(android.graphics.Color.parseColor("#A5A5A5"));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedFileUri = data.getData();
            if (selectedFileUri != null) {
                selectedFileName = getFileName(selectedFileUri);
                updateUI();
            }
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        if (index >= 0) {
                            result = cursor.getString(index);
                        }
                    }
                } finally {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void uploadFile() {
        if (selectedFileUri == null)
            return;

        progressBar.setVisibility(View.VISIBLE);
        btnUpload.setEnabled(false);

        new Thread(() -> {
            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****" + System.currentTimeMillis() + "*****";

            try {
                URL url = new URL(ApiConfig.ISSUER_UPLOAD_CERTIFICATES_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("Cookie", "PHPSESSID=" + sessionManager.getSessionId());

                dos = new DataOutputStream(conn.getOutputStream());

                // Write File
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"zip_file\"; filename=\"" + selectedFileName
                        + "\"" + lineEnd);
                dos.writeBytes("Content-Type: application/zip" + lineEnd);
                dos.writeBytes(lineEnd);

                InputStream fileInputStream = getContentResolver().openInputStream(selectedFileUri);
                int bytesAvailable = fileInputStream.available();
                int maxBufferSize = 1 * 1024 * 1024;
                int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                byte[] buffer = new byte[bufferSize];

                int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                fileInputStream.close();
                dos.flush();
                dos.close();

                // Read Response
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
                        if (jsonResponse.optString("status").equals("session_expired")) {
                            Toast.makeText(IssuerIssueCertificateActivity.this, jsonResponse.optString("message"),
                                    Toast.LENGTH_LONG).show();
                            sessionManager.logoutUser();
                            return;
                        }

                        if (jsonResponse.getString("status").equals("success")) {
                            Toast.makeText(IssuerIssueCertificateActivity.this,
                                    "Upload Successful! Issued: " + jsonResponse.optString("issued"),
                                    Toast.LENGTH_LONG).show();
                            selectedFileUri = null;
                            selectedFileName = null;
                            updateUI(); // Reset UI
                        } else {
                            Toast.makeText(IssuerIssueCertificateActivity.this,
                                    "Error: " + jsonResponse.optString("message"),
                                    Toast.LENGTH_LONG).show();
                            btnUpload.setEnabled(true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnUpload.setEnabled(true);
                    Toast.makeText(IssuerIssueCertificateActivity.this, "Network Error: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
}
