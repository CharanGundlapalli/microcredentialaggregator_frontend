package com.simats.microcredential;

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

public class IssuerRequestsFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private List<User> issuerList;
    private TextView tvEmptyState;
    private ProgressBar progressBar;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_issuer_requests, container, false);

        sessionManager = new SessionManager(requireContext());
        recyclerView = view.findViewById(R.id.recycler_view_issuer_requests);
        tvEmptyState = view.findViewById(R.id.tv_empty_state);
        progressBar = view.findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        issuerList = new ArrayList<>();

        // Reuse UserAdapter which already has click listener to
        // AdminUserDetailsActivity
        adapter = new UserAdapter(issuerList);
        recyclerView.setAdapter(adapter);

        fetchIssuers();

        return view;
    }

    private void fetchIssuers() {
        progressBar.setVisibility(View.VISIBLE);
        new Thread(() -> {
            try {
                URL url = new URL(ApiConfig.ADMIN_VIEW_UNVERIFIED_ISSUERS_URL);
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
                                JSONArray data = jsonResponse.getJSONArray("issuers");
                                issuerList.clear();

                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject obj = data.getJSONObject(i);
                                    issuerList.add(new User(
                                            obj.getString("user_uid"),
                                            obj.getString("issuer_name"),
                                            "Issuer",
                                            "Unverified"));
                                }
                                adapter.notifyDataSetChanged();

                                if (issuerList.isEmpty()) {
                                    tvEmptyState.setVisibility(View.VISIBLE);
                                } else {
                                    tvEmptyState.setVisibility(View.GONE);
                                }
                            } else {
                                // If no issuers found or error, show empty or toast
                                tvEmptyState.setVisibility(View.VISIBLE);
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
                        Toast.makeText(getContext(), "Error fetching issuer requests", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchIssuers();
    }
}
