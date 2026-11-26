package com.example.ck_mobile.ui.customer;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ck_mobile.model.AccountTransaction;
import com.example.ck_mobile.utils.FinancialCalculator;

import java.util.ArrayList;
import java.util.List;

public class CheckingTransactionAdapter extends RecyclerView.Adapter<CheckingTransactionAdapter.TxViewHolder> {

    private final List<AccountTransaction> items = new ArrayList<>();

    public void submitList(List<AccountTransaction> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new TxViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TxViewHolder holder, int position) {
        AccountTransaction tx = items.get(position);
        String amountStr = FinancialCalculator.formatSignedVnd(tx.getAmount());
        holder.title.setText(tx.getTitle() + "  •  " + amountStr);
        holder.subtitle.setText(tx.getDateTimeLabel() + "  •  Thành công");

        if (tx.getAmount() >= 0) {
            holder.title.setTextColor(Color.parseColor("#2E9E5B"));
        } else {
            holder.title.setTextColor(Color.parseColor("#BA1A1A"));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class TxViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView subtitle;

        TxViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(android.R.id.text1);
            subtitle = itemView.findViewById(android.R.id.text2);
        }
    }
}