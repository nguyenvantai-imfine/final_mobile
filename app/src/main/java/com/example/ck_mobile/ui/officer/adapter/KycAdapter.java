package com.example.ck_mobile.ui.officer.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ck_mobile.databinding.ItemKycBinding;
import com.example.ck_mobile.model.KycRecord;
import com.example.ck_mobile.utils.MoneyUtils;
import com.example.ck_mobile.utils.UiText;

import java.util.ArrayList;
import java.util.List;

public class KycAdapter extends RecyclerView.Adapter<KycAdapter.VH> {

    public interface OnKycClick {
        void onClick(KycRecord record);
    }

    private final List<KycRecord> items = new ArrayList<>();
    private final OnKycClick listener;

    public KycAdapter(OnKycClick listener) {
        this.listener = listener;
    }

    public void submit(List<KycRecord> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(ItemKycBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        KycRecord r = items.get(position);
        h.b.tvName.setText(r.getCustomerName());
        h.b.tvStatus.setText(UiText.kycLabel(r.getStatus()));
        h.b.tvDate.setText("Gửi lúc " + MoneyUtils.formatDateTime(r.getCreatedAt()));
        h.b.getRoot().setOnClickListener(v -> listener.onClick(r));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final ItemKycBinding b;

        VH(ItemKycBinding b) {
            super(b.getRoot());
            this.b = b;
        }
    }
}
