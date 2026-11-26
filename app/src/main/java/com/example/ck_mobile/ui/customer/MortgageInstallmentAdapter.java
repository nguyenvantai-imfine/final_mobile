package com.example.ck_mobile.ui.customer;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ck_mobile.model.MortgageInstallment;
import com.example.ck_mobile.utils.FinancialCalculator;

import java.util.ArrayList;
import java.util.List;

public class MortgageInstallmentAdapter extends RecyclerView.Adapter<MortgageInstallmentAdapter.InstallmentViewHolder> {

    public interface OnPayClickListener {
        void onPayInstallment(MortgageInstallment installment);
    }

    private final List<MortgageInstallment> items = new ArrayList<>();
    private final OnPayClickListener payClickListener;

    public MortgageInstallmentAdapter(OnPayClickListener payClickListener) {
        this.payClickListener = payClickListener;
    }

    public void submitList(List<MortgageInstallment> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InstallmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new InstallmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InstallmentViewHolder holder, int position) {
        MortgageInstallment item = items.get(position);
        String statusLabel;
        if (MortgageInstallment.STATUS_PAID.equals(item.getStatus())) {
            statusLabel = "Đã trả";
            holder.title.setTextColor(Color.parseColor("#2E9E5B"));
        } else if (MortgageInstallment.STATUS_DUE.equals(item.getStatus())) {
            statusLabel = "Đến hạn (Nhấn để trả)";
            holder.title.setTextColor(Color.parseColor("#A50064"));
        } else {
            statusLabel = "Chưa tới hạn";
            holder.title.setTextColor(Color.parseColor("#1A1C1D"));
        }

        holder.title.setText("Kỳ " + item.getPeriodNumber() + "/" + item.getTotalPeriods()
                + " (" + item.getDueDate() + ") • " + FinancialCalculator.formatVnd(item.getTotalAmount())
                + " • " + statusLabel);

        holder.subtitle.setText("Gốc: " + FinancialCalculator.formatVnd(item.getPrincipalAmount())
                + " | Lãi: " + FinancialCalculator.formatVnd(item.getInterestAmount())
                + " | Dư nợ sau kỳ: " + FinancialCalculator.formatVnd(item.getRemainingBalance()));

        holder.itemView.setOnClickListener(v -> {
            if (payClickListener != null && MortgageInstallment.STATUS_DUE.equals(item.getStatus())) {
                payClickListener.onPayInstallment(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class InstallmentViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView subtitle;

        InstallmentViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(android.R.id.text1);
            subtitle = itemView.findViewById(android.R.id.text2);
        }
    }
}