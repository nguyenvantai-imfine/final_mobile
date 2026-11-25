package com.example.ck_mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ck_mobile.R;
import com.example.ck_mobile.model.Account;
import com.example.ck_mobile.repository.CustomerRepository;

import java.util.List;

public class AccountsAdapter extends RecyclerView.Adapter<AccountsAdapter.AccountViewHolder> {

    public interface OnAccountClickListener {
        void onAccountClick(Account account);
    }

    private final List<Account> accountList;
    private final OnAccountClickListener listener;

    public AccountsAdapter(List<Account> accountList, OnAccountClickListener listener) {
        this.accountList = accountList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account_card, parent, false);
        return new AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        Account account = accountList.get(position);
        holder.bind(account, listener);
    }

    @Override
    public int getItemCount() {
        return accountList.size();
    }

    static class AccountViewHolder extends RecyclerView.ViewHolder {
        TextView tvAccountType, tvAccountNumber, tvBalance, tvSubtitle;

        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAccountType = itemView.findViewById(R.id.tvAccountType);
            tvAccountNumber = itemView.findViewById(R.id.tvAccountNumber);
            tvBalance = itemView.findViewById(R.id.tvBalance);
            tvSubtitle = itemView.findViewById(R.id.tvSubtitle);
        }

        public void bind(final Account account, final OnAccountClickListener listener) {
            tvAccountType.setText(account.getAccountTypeName());
            tvAccountNumber.setText("STK: " + account.getAccountNumber());
            tvBalance.setText(CustomerRepository.getInstance().formatCurrency(account.getBalance()));

            if (Account.TYPE_SAVING.equals(account.getAccountType())) {
                tvSubtitle.setText("Lãi suất: " + account.getInterestRate() + "%/năm • Lời: " +
                        CustomerRepository.getInstance().formatCurrency(account.getMonthlyProfit()) + "/tháng");
                tvSubtitle.setVisibility(View.VISIBLE);
            } else if (Account.TYPE_MORTGAGE.equals(account.getAccountType())) {
                tvSubtitle.setText("Trả định kỳ: " +
                        CustomerRepository.getInstance().formatCurrency(account.getMonthlyPayment()) + "/tháng");
                tvSubtitle.setVisibility(View.VISIBLE);
            } else {
                tvSubtitle.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onAccountClick(account);
            });
        }
    }
}
