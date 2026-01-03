package com.example.microcredential;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.microcredential.network.ApiConfig;
import com.example.microcredential.session.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PendingCertificatesFragment extends Fragment {

    private RecyclerView recyclerView;
    private CertificateAdapter adapter;
    private List<Certificate> certificateList;
    private TextView tvEmptyState;
    private ProgressBar progressBar;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending_certificates, container, false);

        sessionManager = new SessionManager(requireContext());
        recyclerView = view.findViewById(R.id.recycler_view_pending_certificates);
        tvEmptyState = view.findViewById(R.id.tv_empty_state);
        progressBar = view.findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        certificateList = new ArrayList<>();
        adapter = new CertificateAdapter(certificateList, certificate -> {
            // Navigate to Certificate Details
            Intent intent = new Intent(getContext(), CertificateDetailsActivity.class);
            intent.putExtra("certificate_uid", certificate.getCertificateUid());
            intent.putExtra("certificate_title", certificate.getTitle());
            intent.putExtra("issue_date", certificate.getIssueDate());
            intent.putExtra("expiry_date", certificate.getExpiryDate());
            intent.putExtra("verification_status", certificate.getVerificationStatus());
            intent.putExtra("is_admin", true);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        fetchPendingCertificates();

        return view;
    }

    private void fetchPendingCertificates() {
        progressBar.setVisibility(View.VISIBLE);
        new Thread(() -> {
            try {
                URL url = new URL(ApiConfig.ADMIN_VIEW_PENDING_CERTIFICATES_URL);
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

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
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
                                            obj.getString("status")));
                                }
                                adapter.notifyDataSetChanged();

                                if (certificateList.isEmpty()) {
                                    tvEmptyState.setVisibility(View.VISIBLE);
                                } else {
                                    tvEmptyState.setVisibility(View.GONE);
                                }
                            } else {
                                Toast.makeText(getContext(), jsonResponse.getString("message"), Toast.LENGTH_SHORT)
                                        .show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Error fetching pending certificates", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh list when coming back from details
        fetchPendingCertificates();
    }
}
