package com.example.microcredential;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.microcredential.network.ApiConfig;
import com.example.microcredential.session.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AdminUsersActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigation;

    // Data lists
    private List<User> allUsers = new ArrayList<>();
    private List<User> usersList = new ArrayList<>();
    private List<User> issuersList = new ArrayList<>();

    // Fragment instances
    private UserListFragment usersFragment;
    private UserListFragment issuersFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_users);

        sessionManager = new SessionManager(this);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        bottomNavigation = findViewById(R.id.bottom_navigation_admin);

        setupBottomNavigation();
        setupViewPager();
        fetchUsers();
    }

    private void setupViewPager() {
        usersFragment = new UserListFragment();
        issuersFragment = new UserListFragment();

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0) {
                        tab.setText("Users");
                    } else {
                        tab.setText("Issuers");
                    }
                }).attach();
    }

    private void setupBottomNavigation() {
        // Set correct selected item
        bottomNavigation.setSelectedItemId(R.id.navigation_users);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                startActivity(new Intent(this, AdmindashboardActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.navigation_users) {
                return true;
            } else if (itemId == R.id.navigation_requests) {
                startActivity(new Intent(this, AdminRequestsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.navigation_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    private void fetchUsers() {
        new Thread(() -> {
            try {
                URL url = new URL(ApiConfig.ADMIN_GET_USERS_URL);
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

                if (jsonResponse.optString("status").equals("session_expired")) {
                    runOnUiThread(() -> {
                        Toast.makeText(AdminUsersActivity.this, jsonResponse.optString("message"), Toast.LENGTH_LONG)
                                .show();
                        sessionManager.logoutUser();
                    });
                    return;
                }

                if (jsonResponse.getString("status").equals("success")) {
                    JSONArray data = jsonResponse.getJSONArray("data");

                    usersList.clear();
                    issuersList.clear();

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject obj = data.getJSONObject(i);
                        User user = new User(
                                obj.getString("user_id"),
                                obj.getString("name"),
                                obj.getString("role"),
                                obj.getString("status"));

                        if ("user".equalsIgnoreCase(user.getRole())) {
                            usersList.add(user);
                        } else if ("issuer".equalsIgnoreCase(user.getRole())) {
                            issuersList.add(user);
                        }
                    }

                    runOnUiThread(() -> {
                        usersFragment.setUsers(usersList);
                        issuersFragment.setUsers(issuersList);
                    });

                } else {
                    String message = jsonResponse.getString("message");
                    runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error fetching users", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    class ViewPagerAdapter extends FragmentStateAdapter {
        public ViewPagerAdapter(@NonNull AppCompatActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return usersFragment;
            } else {
                return issuersFragment;
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}
