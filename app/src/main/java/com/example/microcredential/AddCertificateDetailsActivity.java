package com.example.microcredential;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.net.URL;
import java.util.Calendar;

public class AddCertificateDetailsActivity extends AppCompatActivity {

    private EditText etCertificateTitle, etCredentialId, etIssueDate, etExpiryDate, etOtherOrganization;
    private Spinner spinnerCertificateType, spinnerIssuingOrganization;
    private CheckBox cbNoExpiry;
    private Button btnBack, btnReviewSubmit;
    private BottomNavigationView bottomNavigation;
    private Uri constFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate_details);

        if (getIntent().hasExtra("file_uri")) {
            String uriString = getIntent().getStringExtra("file_uri");
            if (uriString != null) {
                constFileUri = Uri.parse(uriString);
            }
        }

        initializeViews();
        setupSpinners();
        setupDatePickers();
        setupListeners();
    }

    private void initializeViews() {
        etCertificateTitle = findViewById(R.id.et_certificate_title);
        etCredentialId = findViewById(R.id.et_credential_id);
        etIssueDate = findViewById(R.id.et_issue_date);
        etExpiryDate = findViewById(R.id.et_expiry_date);
        etOtherOrganization = findViewById(R.id.et_other_organization);

        spinnerCertificateType = findViewById(R.id.spinner_certificate_type);
        spinnerIssuingOrganization = findViewById(R.id.spinner_issuing_organization);

        cbNoExpiry = findViewById(R.id.cb_no_expiry);

        btnBack = findViewById(R.id.btn_back);
        btnReviewSubmit = findViewById(R.id.btn_review_submit);

        bottomNavigation = findViewById(R.id.bottom_navigation);
        // Set "Add" as selected
        bottomNavigation.setSelectedItemId(R.id.navigation_add);
    }

    private void setupSpinners() {
        // Dummy data for type spinner
        ArrayAdapter<CharSequence> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[] { "Select Type", "Professional", "Academic", "Skill Badge", "Other" });
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCertificateType.setAdapter(typeAdapter);

        // Fetch organizations from backend
        fetchIssuers();
    }

    private void fetchIssuers() {
        new Thread(() -> {
            try {
                URL url = new URL(com.example.microcredential.network.ApiConfig.GET_ISSUERS_URL);
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                // Optional: Pass session cookie if needed, though likely public or handled by
                // backend
                // com.example.microcredential.session.SessionManager sessionManager = new
                // com.example.microcredential.session.SessionManager(this);
                // conn.setRequestProperty("Cookie", "PHPSESSID=" +
                // sessionManager.getSessionId());

                java.io.BufferedReader br = new java.io.BufferedReader(
                        new java.io.InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();

                org.json.JSONObject jsonResponse = new org.json.JSONObject(response.toString());
                if ("success".equals(jsonResponse.getString("status"))) {
                    org.json.JSONArray issuersArray = jsonResponse.getJSONArray("issuers");
                    java.util.List<String> issuersList = new java.util.ArrayList<>();
                    issuersList.add("Select Organization");
                    for (int i = 0; i < issuersArray.length(); i++) {
                        issuersList.add(issuersArray.getString(i));
                    }
                    issuersList.add("Other");

                    runOnUiThread(() -> {
                        ArrayAdapter<String> orgAdapter = new ArrayAdapter<>(AddCertificateDetailsActivity.this,
                                android.R.layout.simple_spinner_item, issuersList);
                        orgAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerIssuingOrganization.setAdapter(orgAdapter);
                    });
                } else {
                    runOnUiThread(() -> Toast
                            .makeText(AddCertificateDetailsActivity.this, "Failed to load issuers", Toast.LENGTH_SHORT)
                            .show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    // Fallback in case of error
                    ArrayAdapter<CharSequence> orgAdapter = new ArrayAdapter<>(AddCertificateDetailsActivity.this,
                            android.R.layout.simple_spinner_item,
                            new String[] { "Select Organization", "Google", "Microsoft", "Oracle", "Coursera", "Udemy",
                                    "Other" });
                    orgAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerIssuingOrganization.setAdapter(orgAdapter);
                });
            }
        }).start();
    }

    private void setupDatePickers() {
        etIssueDate.setOnClickListener(v -> showDatePicker(etIssueDate));
        etExpiryDate.setOnClickListener(v -> showDatePicker(etExpiryDate));

        // Disable keyboard input for dates
        etIssueDate.setFocusable(false);
        etExpiryDate.setFocusable(false);
    }

    private void showDatePicker(EditText target) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    // Format as YYYY-MM-DD for backend compatibility
                    String date = String.format(java.util.Locale.US, "%d-%02d-%02d", year1, month1 + 1, dayOfMonth);
                    target.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void setupListeners() {
        cbNoExpiry.setOnCheckedChangeListener((buttonView, isChecked) -> {
            etExpiryDate.setEnabled(!isChecked);
            if (isChecked) {
                etExpiryDate.setText("");
            }
        });

        spinnerIssuingOrganization.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                if ("Other".equals(selected)) {
                    etOtherOrganization.setVisibility(View.VISIBLE);
                } else {
                    etOtherOrganization.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        btnBack.setOnClickListener(v -> onBackPressed());

        btnReviewSubmit.setOnClickListener(v -> validateAndSubmit());

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
                // Already here
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

    private void validateAndSubmit() {
        String title = etCertificateTitle.getText().toString().trim();
        String type = spinnerCertificateType.getSelectedItem().toString();
        String org = spinnerIssuingOrganization.getSelectedItem().toString();
        String issueDate = etIssueDate.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            etCertificateTitle.setError("Title is required");
            return;
        }

        if (type.equals("Select Type")) {
            Toast.makeText(this, "Please select a certificate type", Toast.LENGTH_SHORT).show();
            return;
        }

        if (org.equals("Select Organization")) {
            Toast.makeText(this, "Please select an issuing organization", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(issueDate)) {
            etIssueDate.setError("Issue date is required");
            return;
        }

        // Proceed to Review
        Intent intent = new Intent(this, ReviewSubmitActivity.class);
        intent.putExtra("certificate_title", title);
        intent.putExtra("certificate_type", type);

        if ("Other".equals(org)) {
            String otherOrg = etOtherOrganization.getText().toString().trim();
            if (TextUtils.isEmpty(otherOrg)) {
                etOtherOrganization.setError("Please enter organization name");
                return;
            }
            intent.putExtra("issuing_organization", "Other");
            intent.putExtra("new_issuer_name", otherOrg);
        } else {
            intent.putExtra("issuing_organization", org);
        }

        intent.putExtra("credential_id", etCredentialId.getText().toString().trim());
        intent.putExtra("issue_date", issueDate);

        if (cbNoExpiry.isChecked()) {
            intent.putExtra("no_expiry", true);
            intent.putExtra("expiry_date", "");
        } else {
            String expiryDate = etExpiryDate.getText().toString().trim();
            if (TextUtils.isEmpty(expiryDate)) {
                etExpiryDate.setError("Expiry date is required");
                return;
            }
            intent.putExtra("no_expiry", false);
            intent.putExtra("expiry_date", expiryDate);
        }

        if (constFileUri != null) {
            intent.putExtra("fileUri", constFileUri.toString());
        }

        startActivity(intent);
    }
}
