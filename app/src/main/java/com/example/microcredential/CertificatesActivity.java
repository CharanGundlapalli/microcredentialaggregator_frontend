package com.example.microcredential;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.microcredential.network.ApiConfig;
import com.example.microcredential.session.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CertificatesActivity extends AppCompatActivity implements CertificateAdapter.OnCertificateClickListener {

    private static final String TAG = "CertificatesActivity";

    SessionManager sessionManager;
    ImageView ivLogout;
    TextView tvUserInitial;
    RecyclerView rvCertificates;
    CertificateAdapter certificateAdapter;
    List<Certificate> certificateList = new ArrayList<>();
    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificates);

        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            redirectToLogin();
            return;
        }

        ivLogout = findViewById(R.id.iv_logout);
        tvUserInitial = findViewById(R.id.tv_user_initial);
        rvCertificates = findViewById(R.id.rv_certificates);
        bottomNavigation = findViewById(R.id.bottom_navigation);

        String fullName = sessionManager.getFullName();
        tvUserInitial.setText(
                (fullName != null && !fullName.isEmpty())
                        ? String.valueOf(fullName.charAt(0))
                        : "D");

        ivLogout.setOnClickListener(v -> logoutUser());

        rvCertificates.setLayoutManager(new LinearLayoutManager(this));
        certificateAdapter = new CertificateAdapter(certificateList, this);
        rvCertificates.setAdapter(certificateAdapter);

        fetchCertificates();

        bottomNavigation.setSelectedItemId(R.id.navigation_certificates);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                startActivity(new Intent(this, DashboardActivity.class));
                return true;
            } else if (itemId == R.id.navigation_certificates) {
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

    private void fetchCertificates() {
        new Thread(() -> {
            try {
                URL url = new URL(ApiConfig.VIEW_MY_CERTIFICATES_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty(
                        "Cookie",
                        "PHPSESSID=" + sessionManager.getSessionId());

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();

                Log.d(TAG, "fetchCertificates Response: " + response.toString());

                JSONObject jsonResponse = new JSONObject(response.toString());

                if ("success".equals(jsonResponse.getString("status"))) {

                    JSONArray certificates = jsonResponse.getJSONArray("certificates");

                    certificateList.clear();

                    for (int i = 0; i < certificates.length(); i++) {
                        JSONObject certificate = certificates.getJSONObject(i);

                        certificateList.add(new Certificate(
                                certificate.getString("certificate_uid"),
                                certificate.getString("certificate_title"),
                                certificate.getString("issue_date"),
                                certificate.optString("expiry_date", null),
                                certificate.getString("verification_status")));
                    }

                    runOnUiThread(() -> {
                        certificateAdapter.notifyDataSetChanged();

                        if (certificateList.isEmpty()) {
                            Toast.makeText(
                                    this,
                                    "No certificates uploaded yet",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    String message = jsonResponse.optString("message", "Failed to fetch certificates");
                    runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
                }

            } catch (Exception e) {
                Log.e(TAG, "Error fetching certificates", e);
                runOnUiThread(() -> Toast.makeText(
                        this,
                        "Failed to load certificates",
                        Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void logoutUser() {
        new Thread(() -> {
            sessionManager.logout();
            runOnUiThread(() -> {
                Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                redirectToLogin();
            });
        }).start();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onCertificateClick(Certificate certificate) {
        Intent intent = new Intent(this, CertificateDetailsActivity.class);
        intent.putExtra("certificate_uid", certificate.getCertificateUid());
        intent.putExtra("certificate_title", certificate.getTitle());
        intent.putExtra("issue_date", certificate.getIssueDate());
        intent.putExtra("expiry_date", certificate.getExpiryDate());
        intent.putExtra("verification_status", certificate.getVerificationStatus());
        startActivity(intent);
    }
}
