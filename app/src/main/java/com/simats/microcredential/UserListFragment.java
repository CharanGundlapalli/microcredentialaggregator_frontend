package com.simats.microcredential;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UserListFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private UserAdapter adapter;
    private List<User> userList = new ArrayList<>();

    public UserListFragment() {
        // Required empty public constructor
    }

    // Static factory method to pass data
    /*
     * Note: Passing large lists via bundles is not ideal for large datasets,
     * but strictly following the prompt's simplicity and "don't change valid files"
     * constraint,
     * we will set the data directly via a method after creation or use a shared
     * ViewModel.
     * Given the constraints, a simple setter/update method is easiest or a static
     * instance (discouraged but simple).
     * Better: UserListFragment instance is held by Activity, Activity updates it.
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycler_view_users);
        tvEmpty = view.findViewById(R.id.tv_empty_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UserAdapter(userList);
        recyclerView.setAdapter(adapter);

        updateEmptyView();
    }

    public void setUsers(List<User> users) {
        this.userList = users;
        if (adapter != null) {
            adapter.updateList(users);
        }
        updateEmptyView();
    }

    private void updateEmptyView() {
        if (userList == null || userList.isEmpty()) {
            if (tvEmpty != null)
                tvEmpty.setVisibility(View.VISIBLE);
            if (recyclerView != null)
                recyclerView.setVisibility(View.GONE);
        } else {
            if (tvEmpty != null)
                tvEmpty.setVisibility(View.GONE);
            if (recyclerView != null)
                recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
