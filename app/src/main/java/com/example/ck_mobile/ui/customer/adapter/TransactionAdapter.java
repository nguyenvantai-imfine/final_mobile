package com.example.ck_mobile.ui.customer.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ck_mobile.R;
import com.example.ck_mobile.databinding.ItemTransactionBinding;
import com.example.ck_mobile.model.TransactionRecord;
import com.example.ck_mobile.utils.MoneyUtils;
import com.example.ck_mobile.utils.UiText;

import java.util.ArrayList;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.VH> {

    public interface OnClick {
        void onClick(TransactionRecord t);
    }

    private final List<TransactionRecord> items = new ArrayList<>();
    private final OnClick listener;

    public TransactionAdapter(OnClick listener) {
        this.listener = listener;
    }

    public void submit(List<TransactionRecord> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(ItemTransactionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        TransactionRecord t = items.get(position);
        h.b.tvTitle.setText(UiText.txTypeLabel(t.getType()));
        h.b.tvDesc.setText(t.getDescription() == null ? "" : t.getDescription());
        h.b.tvDate.setText(MoneyUtils.formatDateTime(t.getCreatedAt()));
        h.b.tvAmount.setText((t.isCredit() ? "+" : "-") + MoneyUtils.formatVnd(t.getAmount()));
        h.b.tvAmount.setTextColor(ContextCompat.getColor(h.b.getRoot().getContext(),
                t.isCredit() ? R.color.tdt_success : R.color.tdt_error_alert));
        h.b.getRoot().setOnClickListener(v -> listener.onClick(t));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final ItemTransactionBinding b;

        VH(ItemTransactionBinding b) {
            super(b.getRoot());
            this.b = b;
        }
    }
}
