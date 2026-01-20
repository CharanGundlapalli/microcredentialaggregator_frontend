package com.simats.microcredential;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.simats.microcredential.session.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UploadCertificateActivity extends AppCompatActivity {

    SessionManager sessionManager;
    ImageView ivLogout;
    TextView tvUserInitial;
    Button btnBrowse, btnContinue;
    BottomNavigationView bottomNavigation;

    LinearLayout uploadView, fileSelectedView;
    TextView tvFileName, tvFileSize, tvRemoveFile;

    private Uri selectedFileUri;

    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedFileUri = result.getData().getData();
                    displayFileInfo(selectedFileUri);
                    updateContinueButtonState(true);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_certificate);

        sessionManager = new SessionManager(this);

        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            redirectToLogin();
            return;
        }

        // Bind UI elements
        ivLogout = findViewById(R.id.iv_logout);
        tvUserInitial = findViewById(R.id.tv_user_initial);
        btnBrowse = findViewById(R.id.btn_browse);
        btnContinue = findViewById(R.id.btn_continue);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        uploadView = findViewById(R.id.upload_view);
        fileSelectedView = findViewById(R.id.file_selected_view);
        tvFileName = findViewById(R.id.tv_file_name);
        tvFileSize = findViewById(R.id.tv_file_size);
        tvRemoveFile = findViewById(R.id.tv_remove_file);

        // Set user initial
        String fullName = sessionManager.getFullName();
        if (fullName != null && !fullName.isEmpty()) {
            tvUserInitial.setText(String.valueOf(fullName.charAt(0)));
        } else {
            tvUserInitial.setText("D");
        }

        // Initial state of the continue button
        updateContinueButtonState(false);

        // Logout functionality
        ivLogout.setOnClickListener(v -> logoutUser());

        // Browse button click listener
        btnBrowse.setOnClickListener(v -> openFilePicker());

        // Continue button click listener
        btnContinue.setOnClickListener(v -> {
            if (selectedFileUri != null) {
                // TODO: Implement continue logic with the selected file
                Intent intent = new Intent(UploadCertificateActivity.this, AddCertificateDetailsActivity.class);
                intent.putExtra("file_uri", selectedFileUri.toString());
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please select a file first", Toast.LENGTH_SHORT).show();
            }
        });

        // Remove file listener
        tvRemoveFile.setOnClickListener(v -> {
            selectedFileUri = null;
            updateContinueButtonState(false);
            uploadView.setVisibility(View.VISIBLE);
            fileSelectedView.setVisibility(View.GONE);
        });

        // Bottom navigation
        bottomNavigation.setSelectedItemId(R.id.navigation_add);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                startActivity(new Intent(this, DashboardActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.navigation_certificates) {
                startActivity(new Intent(this, CertificatesActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.navigation_add) {
                // You are already here
                return true;
            } else if (itemId == R.id.navigation_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] { "application/pdf", "image/jpeg", "image/png" });
        filePickerLauncher.launch(intent);
    }

    private void displayFileInfo(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);

            String fileName = cursor.getString(nameIndex);
            long fileSize = cursor.getLong(sizeIndex);

            tvFileName.setText(fileName);
            tvFileSize.setText(String.format("%.2f KB", fileSize / 1024.0));

            uploadView.setVisibility(View.GONE);
            fileSelectedView.setVisibility(View.VISIBLE);

            cursor.close();
        }
    }

    private void updateContinueButtonState(boolean isEnabled) {
        btnContinue.setEnabled(isEnabled);
        if (isEnabled) {
            btnContinue.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_continue_button_active));
            btnContinue.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        } else {
            btnContinue.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_continue_button_inactive));
            btnContinue.setTextColor(ContextCompat.getColor(this, R.color.material_grey_600));
        }
    }

    private void logoutUser() {
        new Thread(() -> {
            // Logout logic here...
        }).start();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
