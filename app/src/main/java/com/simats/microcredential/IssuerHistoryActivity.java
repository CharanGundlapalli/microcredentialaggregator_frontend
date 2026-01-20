package com.simats.microcredential;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.simats.microcredential.network.ApiConfig;
import com.simats.microcredential.session.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class IssuerHistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private ProgressBar progressBar;
    private TextView tvNoData;
    private SwipeRefreshLayout swipeRefresh;
    private HistoryAdapter adapter;
    private List<HistoryItem> historyList = new ArrayList<>();
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issuer_history);

        sessionManager = new SessionManager(this);

        rvHistory = findViewById(R.id.rv_history);
        progressBar = findViewById(R.id.progress_bar);
        tvNoData = findViewById(R.id.tv_no_data);
        swipeRefresh = findViewById(R.id.swipe_refresh);

        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistoryAdapter(historyList);
        rvHistory.setAdapter(adapter);

        setupBottomNavigation();

        fetchHistory();

        swipeRefresh.setOnRefreshListener(this::fetchHistory);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_history);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_history) {
                return true;
            } else if (itemId == R.id.navigation_home) {
                startActivity(new Intent(getApplicationContext(), IssuerdashboardActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.navigation_issue) {
                startActivity(new Intent(getApplicationContext(), IssuerIssueCertificateActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.navigation_requests) {
                // Assuming request fragment is hosted in dashboard or similar,
                // but checking menu ID 'navigation_requests' matches what's in
                // 'issuer_bottom_navigation_menu'
                // Re-using dashboard logic if requests is a tab there?
                // Or if there's a specific activity:
                // For now, let's assume Requests is reachable via dashboard or its own
                // activity.
                // Checking previous code: IssuerdashboardActivity seems to use fragments.
                // Let's redirect to Dashboard with extra to open requests tab if possible,
                // otherwise just Dashboard.
                // Actually, let's stick to known standard.
                startActivity(new Intent(getApplicationContext(), IssuerdashboardActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.navigation_profile) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }

    private void fetchHistory() {
        swipeRefresh.setRefreshing(true);
        new Thread(() -> {
            try {
                // Assuming base URL logic
                String urlStr = ApiConfig.PROFILE_URL.replace("profile.php", "issuer_view_history.php");
                URL url = java.net.URI.create(urlStr).toURL();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Cookie", "PHPSESSID=" + sessionManager.getSessionId());

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null)
                        response.append(line);

                    JSONObject jsonResponse = new JSONObject(response.toString());

                    if ("session_expired".equals(jsonResponse.optString("status"))) {
                        runOnUiThread(() -> {
                            Toast.makeText(this, jsonResponse.optString("message"), Toast.LENGTH_LONG).show();
                            sessionManager.logoutUser();
                        });
                        return;
                    }

                    if ("success".equals(jsonResponse.optString("status"))) {
                        JSONArray data = jsonResponse.optJSONArray("data");
                        historyList.clear();
                        if (data != null) {
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);
                                historyList.add(new HistoryItem(
                                        obj.optString("title"),
                                        obj.optString("recipient"),
                                        obj.optString("date"),
                                        obj.optString("status")));
                            }
                        }
                        runOnUiThread(() -> {
                            adapter.notifyDataSetChanged();
                            tvNoData.setVisibility(historyList.isEmpty() ? View.VISIBLE : View.GONE);
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(this, jsonResponse.optString("message"), Toast.LENGTH_SHORT)
                                .show());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } finally {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    swipeRefresh.setRefreshing(false);
                });
            }
        }).start();
    }

    // Modal Class
    static class HistoryItem {
        String title, recipient, date, status;

        public HistoryItem(String title, String recipient, String date, String status) {
            this.title = title;
            this.recipient = recipient;
            this.date = date;
            this.status = status;
        }
    }

    // Adapter Class
    class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
        private List<HistoryItem> list;

        public HistoryAdapter(List<HistoryItem> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_issuer_history, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            HistoryItem item = list.get(position);
            holder.tvTitle.setText(item.title);
            holder.tvRecipient.setText(item.recipient);
            holder.tvDate.setText(item.date);
            holder.tvStatus.setText(item.status);

            // Simple status coloring
            if ("Active".equalsIgnoreCase(item.status) || "Issued".equalsIgnoreCase(item.status)) {
                holder.tvStatus.setTextColor(getColor(android.R.color.holo_green_dark));
            } else {
                holder.tvStatus.setTextColor(getColor(android.R.color.holo_orange_dark));
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvRecipient, tvDate, tvStatus;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tv_title);
                tvRecipient = itemView.findViewById(R.id.tv_recipient);
                tvDate = itemView.findViewById(R.id.tv_date);
                tvStatus = itemView.findViewById(R.id.tv_status);
            }
        }
    }
}
