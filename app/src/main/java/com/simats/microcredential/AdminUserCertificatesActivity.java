package com.simats.microcredential;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.microcredential.network.ApiConfig;
import com.simats.microcredential.session.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AdminUserCertificatesActivity extends AppCompatActivity
        implements CertificateAdapter.OnCertificateClickListener {

    private String userUid;
    private SessionManager sessionManager;
    private RecyclerView rvCertificates;
    private CertificateAdapter certificateAdapter;
    private List<Certificate> certificateList = new ArrayList<>();
    private TextView tvEmpty;
    private ProgressBar progressBar;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_certificates);

        sessionManager = new SessionManager(this);
        userUid = getIntent().getStringExtra("user_uid");

        if (userUid == null) {
            Toast.makeText(this, "Error: Unknown User", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        fetchUserCertificates();
    }

    private void initViews() {
        rvCertificates = findViewById(R.id.rv_certificates);
        tvEmpty = findViewById(R.id.tv_empty);
        progressBar = findViewById(R.id.progressBar);
        btnBack = findViewById(R.id.btn_back);
        TextView tvTitle = findViewById(R.id.tv_toolbar_title);

        tvTitle.setText("User Certificates");

        btnBack.setOnClickListener(v -> onBackPressed());

        rvCertificates.setLayoutManager(new LinearLayoutManager(this));
        certificateAdapter = new CertificateAdapter(certificateList, this);
        rvCertificates.setAdapter(certificateAdapter);
    }

    private void fetchUserCertificates() {
        progressBar.setVisibility(View.VISIBLE);
        new Thread(() -> {
            try {
                URL url = new URL(ApiConfig.ADMIN_VIEW_USER_CERTIFICATES_URL + "?user_uid=" + userUid);
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
                            JSONArray data = jsonResponse.getJSONArray("certificates");
                            certificateList.clear();

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject cert = data.getJSONObject(i);
                                certificateList.add(new Certificate(
                                        cert.getString("certificate_uid"),
                                        cert.getString("certificate_title"),
                                        cert.getString("issue_date"),
                                        cert.optString("expiry_date", null),
                                        cert.getString("verification_status")));
                            }

                            certificateAdapter.notifyDataSetChanged();

                            if (certificateList.isEmpty()) {
                                tvEmpty.setVisibility(View.VISIBLE);
                                rvCertificates.setVisibility(View.GONE);
                            } else {
                                tvEmpty.setVisibility(View.GONE);
                                rvCertificates.setVisibility(View.VISIBLE);
                            }

                        } else {
                            Toast.makeText(AdminUserCertificatesActivity.this, jsonResponse.getString("message"),
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
                    Toast.makeText(AdminUserCertificatesActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    @Override
    public void onCertificateClick(Certificate certificate) {
        Intent intent = new Intent(this, CertificateDetailsActivity.class);
        intent.putExtra("certificate_uid", certificate.getCertificateUid());
        intent.putExtra("certificate_title", certificate.getTitle());
        intent.putExtra("issue_date", certificate.getIssueDate());
        intent.putExtra("expiry_date", certificate.getExpiryDate());
        intent.putExtra("verification_status", certificate.getVerificationStatus());
        intent.putExtra("is_admin", true); // Flag to enable admin features
        startActivity(intent);
    }
}
