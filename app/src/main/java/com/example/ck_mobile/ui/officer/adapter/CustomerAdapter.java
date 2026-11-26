package com.example.ck_mobile.ui.officer.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ck_mobile.R;
import com.example.ck_mobile.databinding.ItemCustomerBinding;
import com.example.ck_mobile.model.UserProfile;
import com.example.ck_mobile.utils.Constants;
import com.example.ck_mobile.utils.UiText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.VH> {

    public interface OnCustomerClick {
        void onClick(UserProfile customer);
    }

    private final List<UserProfile> all = new ArrayList<>();
    private final List<UserProfile> shown = new ArrayList<>();
    private final OnCustomerClick listener;
    private String query = "";

    public CustomerAdapter(OnCustomerClick listener) {
        this.listener = listener;
    }

    public void submit(List<UserProfile> customers) {
        all.clear();
        if (customers != null) all.addAll(customers);
        applyFilter();
    }

    public void setQuery(String q) {
        query = q == null ? "" : q.trim().toLowerCase(Locale.ROOT);
        applyFilter();
    }

    public int shownCount() {
        return shown.size();
    }

    private void applyFilter() {
        shown.clear();
        for (UserProfile u : all) {
            if (query.isEmpty() || contains(u.getFullName()) || contains(u.getEmail())
                    || contains(u.getPhone()) || contains(u.getAccountNumber())) {
                shown.add(u);
            }
        }
        notifyDataSetChanged();
    }

    private boolean contains(String field) {
        return field != null && field.toLowerCase(Locale.ROOT).contains(query);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(ItemCustomerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        UserProfile u = shown.get(position);
        h.b.tvName.setText(u.getFullName() == null ? "(chưa có tên)" : u.getFullName());
        h.b.tvContact.setText(join(u.getPhone(), u.getEmail()));
        h.b.tvAccount.setText(u.getAccountNumber() == null ? "" : "STK: " + u.getAccountNumber());
        h.b.tvKyc.setText(UiText.kycLabel(u.getKycStatus()));

        boolean locked = Constants.STATUS_LOCKED.equalsIgnoreCase(u.getStatus());
        h.b.tvStatus.setText(locked ? "Đã khóa" : "Hoạt động");
        h.b.tvStatus.setBackgroundResource(locked ? R.drawable.bg_status_error_pill : R.drawable.bg_soft_badge_green);
        h.b.tvStatus.setTextColor(ContextCompat.getColor(h.b.getRoot().getContext(),
                locked ? R.color.tdt_error_alert : R.color.tdt_success));

        h.b.getRoot().setOnClickListener(v -> listener.onClick(u));
    }

    @Override
    public int getItemCount() {
        return shown.size();
    }

    private static String join(String a, String b) {
        String x = a == null ? "" : a;
        String y = b == null ? "" : b;
        if (x.isEmpty()) return y;
        if (y.isEmpty()) return x;
        return x + " • " + y;
    }

    static class VH extends RecyclerView.ViewHolder {
        final ItemCustomerBinding b;

        VH(ItemCustomerBinding b) {
            super(b.getRoot());
            this.b = b;
        }
    }
}
