package com.example.ck_mobile.ui.customer.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ck_mobile.databinding.ItemServiceBinding;
import com.example.ck_mobile.model.ServiceItem;
import com.example.ck_mobile.utils.MoneyUtils;

import java.util.ArrayList;
import java.util.List;

public class ServiceItemAdapter extends RecyclerView.Adapter<ServiceItemAdapter.VH> {

    public interface OnSelect {
        void onSelect(ServiceItem item);
    }

    private final List<ServiceItem> items = new ArrayList<>();
    private final OnSelect listener;
    private int selected = RecyclerView.NO_POSITION;

    public ServiceItemAdapter(List<ServiceItem> data, OnSelect listener) {
        items.addAll(data);
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(ItemServiceBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        ServiceItem s = items.get(position);
        h.b.tvTitle.setText(s.title);
        h.b.tvSubtitle.setText(s.subtitle);
        h.b.tvPrice.setText(MoneyUtils.formatVnd(s.unitPrice) + " / " + s.unitLabel);
        ((com.google.android.material.card.MaterialCardView) h.b.getRoot()).setChecked(position == selected);
        h.b.getRoot().setOnClickListener(v -> {
            int pos = h.getBindingAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;
            int old = selected;
            selected = pos;
            if (old != RecyclerView.NO_POSITION) notifyItemChanged(old);
            notifyItemChanged(selected);
            listener.onSelect(items.get(pos));
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final ItemServiceBinding b;

        VH(ItemServiceBinding b) {
            super(b.getRoot());
            this.b = b;
        }
    }
}
