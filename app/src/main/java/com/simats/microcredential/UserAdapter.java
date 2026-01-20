package com.simats.microcredential;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;

    public UserAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.tvName.setText(user.getName());
        holder.tvRole.setText("Role: " + user.getRole());
        holder.tvStatus.setText(user.getStatus());

        // Set User Initial
        String name = user.getName();
        if (name != null) {
            name = name.trim();
        }

        if (name != null && !name.isEmpty()) {
            holder.tvInitial.setText(String.valueOf(name.charAt(0)).toUpperCase());
        } else {
            holder.tvInitial.setText("?");
        }

        // Simple status color logic
        // Simple status color logic
        if ("blocked".equalsIgnoreCase(user.getStatus()) || "inactive".equalsIgnoreCase(user.getStatus())) {
            holder.tvStatus.setTextColor(0xFFFF0000); // Red
            holder.tvStatus.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);
        } else {
            holder.tvStatus.setTextColor(0xFF4CAF50); // Green
        }

        holder.itemView.setOnClickListener(v -> {
            android.content.Context context = v.getContext();
            android.content.Intent intent = new android.content.Intent(context, AdminUserDetailsActivity.class);
            intent.putExtra("user_uid", user.getUserId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void updateList(List<User> newList) {
        this.userList = newList;
        notifyDataSetChanged();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvRole, tvStatus, tvInitial;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_user_name);
            tvRole = itemView.findViewById(R.id.tv_user_role);
            tvStatus = itemView.findViewById(R.id.tv_user_status);
            tvInitial = itemView.findViewById(R.id.tv_user_initial);
        }
    }
}
