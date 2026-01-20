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

public class EmployerUserCertificatesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CertificateAdapter adapter;
    private List<Certificate> certificateList;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private ImageView btnBack;
    private SessionManager sessionManager;
    private String userUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_certificates); // Reusing layout as it's just a list

        sessionManager = new SessionManager(this);
        userUid = getIntent().getStringExtra("user_uid");

        if (userUid == null) {
            Toast.makeText(this, "Error: Unknown User", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        fetchUserCertificates();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.rv_certificates);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyState = findViewById(R.id.tv_empty);
        btnBack = findViewById(R.id.btn_back);

        TextView tvTitle = findViewById(R.id.tv_toolbar_title);
        if (tvTitle != null)
            tvTitle.setText("Verified Certificates");

        btnBack.setOnClickListener(v -> onBackPressed());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        certificateList = new ArrayList<>();
        adapter = new CertificateAdapter(certificateList, certificate -> {
            Intent intent = new Intent(EmployerUserCertificatesActivity.this, CertificateDetailsActivity.class);
            intent.putExtra("certificate_uid", certificate.getCertificateUid());
            intent.putExtra("is_employer_view", true); // Flag to hide sensitive actions if needed
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }

    private void fetchUserCertificates() {
        progressBar.setVisibility(View.VISIBLE);
        new Thread(() -> {
            try {
                URL url = new URL(ApiConfig.EMPLOYER_VIEW_CERTIFICATES_URL + "?user_uid=" + userUid);
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
                            JSONArray certificates = jsonResponse.getJSONArray("certificates");
                            certificateList.clear();

                            for (int i = 0; i < certificates.length(); i++) {
                                JSONObject obj = certificates.getJSONObject(i);
                                certificateList.add(new Certificate(
                                        obj.getString("certificate_uid"),
                                        obj.getString("certificate_title"),
                                        obj.getString("issue_date"),
                                        obj.optString("expiry_date", ""),
                                        obj.getString("verification_status")));
                            }
                            adapter.notifyDataSetChanged();

                            if (certificateList.isEmpty()) {
                                tvEmptyState.setVisibility(View.VISIBLE);
                                tvEmptyState.setText("No verified certificates found.");
                            } else {
                                tvEmptyState.setVisibility(View.GONE);
                            }
                        } else {
                            tvEmptyState.setText(jsonResponse.getString("message"));
                            tvEmptyState.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(EmployerUserCertificatesActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
}
