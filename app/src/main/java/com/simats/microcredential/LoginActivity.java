package com.simats.microcredential;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.simats.microcredential.network.ApiConfig;
import com.simats.microcredential.session.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;
    TextView tvSignup;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Bind views
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvSignup = findViewById(R.id.tv_signup_tab);

        // Session manager
        sessionManager = new SessionManager(this);

        // If already logged in → go directly to the correct Dashboard
        if (sessionManager.isLoggedIn()) {
            redirectBasedOnRole(sessionManager.getRole());
            return; // Important to prevent further execution
        }

        // Login button click
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(email, password);
            }
        });

        // Navigate to Signup
        tvSignup.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignupActivity.class)));
    }

    // 🔐 LOGIN METHOD
    private void loginUser(String email, String password) {

        new Thread(() -> {
            try {
                // API URL
                URL url = new URL(ApiConfig.LOGIN_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                // Request setup
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);

                // JSON request body
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("email", email);
                jsonBody.put("password", password);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(jsonBody.toString().getBytes(StandardCharsets.UTF_8));
                }

                // Read response
                JSONObject jsonResponse = getJsonObject(conn);
                String status = jsonResponse.getString("status");

                runOnUiThread(() -> {
                    try {
                        if (status.equals("success")) {

                            String userUid = jsonResponse.getString("user_uid");
                            String role = jsonResponse.optString("role", "user");
                            String name = jsonResponse.getString("name");

                            // Extract session ID from headers
                            Map<String, List<String>> headerFields = conn.getHeaderFields();
                            List<String> cookiesHeader = headerFields.get("Set-Cookie");
                            if (cookiesHeader == null) {
                                // Try lowercase check if needed, or other casing
                                cookiesHeader = headerFields.get("set-cookie");
                            }

                            String sessionId = null;
                            if (cookiesHeader != null) {
                                for (String cookie : cookiesHeader) {
                                    if (cookie.contains("PHPSESSID")) {
                                        // content usually: PHPSESSID=xyz; path=/
                                        int start = cookie.indexOf("PHPSESSID=");
                                        if (start != -1) {
                                            start += "PHPSESSID=".length();
                                            int end = cookie.indexOf(";", start);
                                            if (end == -1) {
                                                end = cookie.length();
                                            }
                                            sessionId = cookie.substring(start, end);
                                            break;
                                        }
                                    }
                                }
                            }

                            // Save session
                            sessionManager.createLoginSession(userUid, role, name, sessionId);

                            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();

                            // Go to Dashboard based on role
                            redirectBasedOnRole(role);

                        } else {
                            String message = jsonResponse.getString("message");
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Response error", Toast.LENGTH_SHORT).show();
                    }
                });

                conn.disconnect();

            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Network error. Check server.", Toast.LENGTH_SHORT).show());
                e.printStackTrace();
            }
        }).start();
    }

    private static JSONObject getJsonObject(HttpURLConnection conn) throws IOException, JSONException {
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        // Parse JSON
        JSONObject jsonResponse = new JSONObject(response.toString());
        return jsonResponse;
    }

    private void redirectBasedOnRole(String role) {
        Intent intent;
        if (role == null) {
            role = "user"; // Default fallback
        }

        switch (role) {
            case "admin":
                intent = new Intent(LoginActivity.this, AdmindashboardActivity.class);
                break;
            case "employer":
                intent = new Intent(LoginActivity.this, EmployerdashboardActivity.class);
                break;
            case "issuer":
                intent = new Intent(LoginActivity.this, IssuerdashboardActivity.class);
                break;
            case "user":
            default:
                intent = new Intent(LoginActivity.this, DashboardActivity.class);
                break;
        }
        startActivity(intent);
        finish();
    }
}
