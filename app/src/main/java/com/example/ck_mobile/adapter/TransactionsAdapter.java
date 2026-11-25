package com.example.ck_mobile.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ck_mobile.R;
import com.example.ck_mobile.model.Transaction;
import com.example.ck_mobile.repository.CustomerRepository;

import java.util.List;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.TransactionViewHolder> {

    private final List<Transaction> transactionList;

    public TransactionsAdapter(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction_row, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction tx = transactionList.get(position);
        holder.bind(tx);
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvTxTitle, tvTxCategoryDate, tvTxAmount;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTxTitle = itemView.findViewById(R.id.tvTxTitle);
            tvTxCategoryDate = itemView.findViewById(R.id.tvTxCategoryDate);
            tvTxAmount = itemView.findViewById(R.id.tvTxAmount);
        }

        public void bind(Transaction tx) {
            tvTxTitle.setText(tx.getTitle());
            tvTxCategoryDate.setText(tx.getCategory() + " • " + tx.getTimestamp());

            String formattedAmount = CustomerRepository.getInstance().formatCurrency(tx.getAmount());
            if (tx.isCredit()) {
                tvTxAmount.setText("+" + formattedAmount);
                tvTxAmount.setTextColor(Color.parseColor("#2E9E5B"));
            } else {
                tvTxAmount.setText("-" + formattedAmount);
                tvTxAmount.setTextColor(Color.parseColor("#1A1C1D"));
            }
        }
    }
}
