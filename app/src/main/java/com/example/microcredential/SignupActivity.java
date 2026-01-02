package com.example.microcredential;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.microcredential.network.ApiConfig;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignupActivity extends AppCompatActivity {

    EditText etFullName, etEmail, etPassword;
    TextView tvLoginTab, btnSignup;

    LinearLayout roleLearner, roleAdmin, roleEmployer, roleIssuer;

    String selectedRole = "user"; // default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Bind inputs
        etFullName = findViewById(R.id.et_full_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);

        tvLoginTab = findViewById(R.id.tv_login_tab);
        btnSignup = findViewById(R.id.btn_signup);

        // ✅ Correctly bind role layouts by ID
        roleLearner = findViewById(R.id.role_learner);
        roleAdmin = findViewById(R.id.role_admin);
        roleEmployer = findViewById(R.id.role_employer);
        roleIssuer = findViewById(R.id.role_issuer);

        // Role selection
        roleLearner.setOnClickListener(v -> selectRole("user"));
        roleAdmin.setOnClickListener(v -> selectRole("admin"));
        roleEmployer.setOnClickListener(v -> selectRole("employer"));
        roleIssuer.setOnClickListener(v -> selectRole("issuer"));

        // Signup
        btnSignup.setOnClickListener(v -> {
            String name = etFullName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            } else {
                signupUser(name, email, password, selectedRole);
            }
        });

        // Back to login
        tvLoginTab.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void selectRole(String role) {
        selectedRole = role;
        Toast.makeText(this, "Selected role: " + role, Toast.LENGTH_SHORT).show();
    }

    private void signupUser(String name, String email, String password, String role) {

        new Thread(() -> {
            try {
                URL url = new URL(ApiConfig.SIGNUP_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);

                JSONObject json = new JSONObject();
                json.put("full_name", name);
                json.put("email", email);
                json.put("password", password);
                json.put("role", role);

                OutputStream os = conn.getOutputStream();
                os.write(json.toString().getBytes("UTF-8"));
                os.close();

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream())
                );

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();

                JSONObject jsonResponse = new JSONObject(response.toString());

                runOnUiThread(() -> {
                    try {
                        if (jsonResponse.getString("status").equals("success")) {
                            Toast.makeText(this,
                                    "Signup successful. Please login.",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this,
                                    jsonResponse.getString("message"),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Signup error", Toast.LENGTH_SHORT).show();
                    }
                });

                conn.disconnect();

            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
}
