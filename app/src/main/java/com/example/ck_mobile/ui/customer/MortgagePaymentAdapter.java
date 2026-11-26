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

public class MortgagePaymentAdapter extends RecyclerView.Adapter<MortgagePaymentAdapter.PaymentViewHolder> {

    private final List<MortgageInstallment> items = new ArrayList<>();

    public void submitList(List<MortgageInstallment> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new PaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentViewHolder holder, int position) {
        MortgageInstallment item = items.get(position);
        holder.line1.setText("Kỳ " + item.getPeriodNumber() + " (" + item.getDueDate() + ")  •  "
                + FinancialCalculator.formatVnd(item.getTotalAmount()));
        holder.line1.setTextColor(Color.parseColor("#1A1C1D"));

        holder.line2.setText("Gốc " + FinancialCalculator.formatVnd(item.getPrincipalAmount())
                + " • Lãi " + FinancialCalculator.formatVnd(item.getInterestAmount())
                + " • Thành công");
        holder.line2.setTextColor(Color.parseColor("#2E9E5B"));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class PaymentViewHolder extends RecyclerView.ViewHolder {
        final TextView line1;
        final TextView line2;

        PaymentViewHolder(@NonNull View itemView) {
            super(itemView);
            line1 = itemView.findViewById(android.R.id.text1);
            line2 = itemView.findViewById(android.R.id.text2);
        }
    }
}