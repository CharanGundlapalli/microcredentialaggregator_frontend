package com.example.microcredential;

import android.content.Intent; // Added import
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.microcredential.network.ApiConfig;
import com.example.microcredential.session.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class EmployerSearchActivity extends AppCompatActivity {

    private TextInputEditText etSearch;
    private MaterialButton btnSearch, btnViewCertificates; // Added btnViewCertificates
    private ProgressBar progressBar;
    private MaterialCardView cardUserDetails;
    private TextView tvUserName, tvUserEmail, tvErrorMessage;
    private SessionManager sessionManager;
    private String foundUserUid; // Store found user UID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_search);

        sessionManager = new SessionManager(this);

        etSearch = findViewById(R.id.et_search_query);
        btnSearch = findViewById(R.id.btn_search);
        btnViewCertificates = findViewById(R.id.btn_view_user_certificates); // Bind button
        progressBar = findViewById(R.id.progress_bar);
        cardUserDetails = findViewById(R.id.card_user_details);
        tvUserName = findViewById(R.id.tv_user_name);
        tvUserEmail = findViewById(R.id.tv_user_email);
        tvErrorMessage = findViewById(R.id.tv_error_message);

        // Bottom Navigation
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(
                R.id.bottom_navigation_employer);
        bottomNav.setSelectedItemId(R.id.navigation_search); // Set Search as selected

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                startActivity(new Intent(getApplicationContext(), EmployerdashboardActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.navigation_search) {
                return true;
            } else if (itemId == R.id.navigation_profile) {
                // TODO: Navigate to Profile
                Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });

        btnSearch.setOnClickListener(v -> performSearch());

        btnViewCertificates.setOnClickListener(v -> {
            if (foundUserUid != null) {
                Intent intent = new Intent(EmployerSearchActivity.this, EmployerUserCertificatesActivity.class);
                intent.putExtra("user_uid", foundUserUid);
                startActivity(intent);
            }
        });
    }

    // ... (performSearch method) ...
    // Inside displayUserDetails:

    private void displayUserDetails(JSONObject user) {
        try {
            String name = user.getString("full_name");
            String email = user.getString("email");
            foundUserUid = user.getString("user_uid"); // Store UID

            tvUserName.setText("Name: " + name);
            tvUserEmail.setText("Email: " + email);

            cardUserDetails.setVisibility(View.VISIBLE);
            btnViewCertificates.setVisibility(View.VISIBLE); // Show button
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error displaying data");
        }
    }

    private void performSearch() {
        String query = etSearch.getText().toString().trim();
        if (query.isEmpty()) {
            etSearch.setError("Please enter User ID or Email");
            return;
        }

        etSearch.setError(null);
        showLoading(true);
        hideError();
        cardUserDetails.setVisibility(View.GONE);

        new Thread(() -> {
            try {
                URL url = new URL(ApiConfig.EMPLOYER_SEARCH_USER_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Cookie", "PHPSESSID=" + sessionManager.getSessionId());
                conn.setDoOutput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("search", query);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                writer.write(jsonParam.toString());
                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    br.close();

                    JSONObject jsonResponse = new JSONObject(response.toString());

                    runOnUiThread(() -> {
                        showLoading(false);
                        try {
                            if (jsonResponse.getString("status").equals("success")) {
                                JSONObject user = jsonResponse.getJSONObject("user");
                                displayUserDetails(user);
                            } else {
                                showError(jsonResponse.getString("message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            showError("Parsing Error");
                        }
                    });
                } else {
                    runOnUiThread(() -> {
                        showLoading(false);
                        showError("Server Error: " + responseCode);
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    showLoading(false);
                    showError("Network Error");
                });
            }
        }).start();
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnSearch.setEnabled(!isLoading);
    }

    private void showError(String message) {
        tvErrorMessage.setText(message);
        tvErrorMessage.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        tvErrorMessage.setVisibility(View.GONE);
    }
}
