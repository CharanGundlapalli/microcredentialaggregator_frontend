package com.simats.microcredential;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CertificateAdapter extends RecyclerView.Adapter<CertificateAdapter.ViewHolder> {

    private final List<Certificate> certificateList;
    private final OnCertificateClickListener listener;

    public interface OnCertificateClickListener {
        void onCertificateClick(Certificate certificate);
    }

    public CertificateAdapter(List<Certificate> certificateList, OnCertificateClickListener listener) {
        this.certificateList = certificateList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_certificate, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Certificate certificate = certificateList.get(position);
        holder.bind(certificate, listener);
    }

    @Override
    public int getItemCount() {
        return certificateList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvIssueDate, tvExpiryDate, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_certificate_title);
            tvIssueDate = itemView.findViewById(R.id.tv_issue_date);
            tvExpiryDate = itemView.findViewById(R.id.tv_expiry_date);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }

        public void bind(final Certificate certificate, final OnCertificateClickListener listener) {
            tvTitle.setText(certificate.getTitle());
            tvIssueDate.setText("Issued: " + certificate.getIssueDate());

            String status = certificate.getVerificationStatus();
            if (status == null)
                status = "pending";

            tvStatus.setText(status.substring(0, 1).toUpperCase() + status.substring(1)); // Capitalize first letter

            if ("verified".equalsIgnoreCase(status)) {
                tvStatus.setBackgroundResource(R.drawable.bg_status_verified);
            } else if ("rejected".equalsIgnoreCase(status)) {
                tvStatus.setBackgroundResource(R.drawable.bg_status_rejected);
            } else {
                tvStatus.setBackgroundResource(R.drawable.bg_status_pending);
            }

            if (certificate.getExpiryDate() != null && !certificate.getExpiryDate().isEmpty()) {
                tvExpiryDate.setText("Expires: " + certificate.getExpiryDate());
                tvExpiryDate.setVisibility(View.VISIBLE);
            } else {
                tvExpiryDate.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> listener.onCertificateClick(certificate));
        }
    }
}
