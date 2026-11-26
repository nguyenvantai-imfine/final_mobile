package com.example.ck_mobile.ui.customer;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ck_mobile.databinding.ActivityTransactionHistoryBinding;
import com.example.ck_mobile.model.TransactionRecord;
import com.example.ck_mobile.ui.base.BaseSecureActivity;
import com.example.ck_mobile.ui.customer.adapter.TransactionAdapter;
import com.example.ck_mobile.utils.AuthSessionHelper;
import com.example.ck_mobile.utils.Constants;
import com.example.ck_mobile.utils.MoneyUtils;
import com.example.ck_mobile.utils.UiText;
import com.example.ck_mobile.viewmodel.TransactionHistoryViewModel;

import java.util.ArrayList;
import java.util.List;

/** T27: lịch sử giao dịch (danh sách, lọc theo tháng / loại, xem chi tiết). */
public class TransactionHistoryActivity extends BaseSecureActivity {

    private static final String ALL = "Tất cả";
    private static final String[] TYPES = {
            null, Constants.TX_DEPOSIT, Constants.TX_WITHDRAW, Constants.TX_TRANSFER_OUT,
            Constants.TX_TRANSFER_IN, Constants.TX_BILL, Constants.TX_MOVIE, Constants.TX_HOTEL,
            Constants.TX_INTEREST
    };

    private ActivityTransactionHistoryBinding b;
    private TransactionHistoryViewModel vm;
    private TransactionAdapter adapter;
    private List<String> monthKeys = new ArrayList<>();

    @Override
    protected String getRequiredRole() {
        return AuthSessionHelper.ROLE_CUSTOMER;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityTransactionHistoryBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        b.toolbar.setNavigationOnClickListener(v -> finish());

        adapter = new TransactionAdapter(this::showDetail);
        b.rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        b.rvTransactions.setAdapter(adapter);

        setupTypeDropdown();
        b.ddMonth.setText(ALL, false);
        b.ddMonth.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new String[]{ALL}));

        vm = new ViewModelProvider(this).get(TransactionHistoryViewModel.class);
        vm.getFiltered().observe(this, list -> {
            adapter.submit(list);
            b.tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
            updateSummary(list);
        });
        vm.getMonths().observe(this, this::setupMonthDropdown);
        vm.getLoading().observe(this, l ->
                b.progress.setVisibility(Boolean.TRUE.equals(l) ? View.VISIBLE : View.GONE));
        vm.getError().observe(this, msg -> {
            if (msg != null) Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        });

        if (auth.getCurrentUser() != null) vm.load(auth.getCurrentUser().getUid());
    }

    private void setupTypeDropdown() {
        String[] labels = new String[TYPES.length];
        for (int i = 0; i < TYPES.length; i++) labels[i] = TYPES[i] == null ? ALL : UiText.txTypeLabel(TYPES[i]);
        b.ddType.setText(ALL, false);
        b.ddType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, labels));
        b.ddType.setOnItemClickListener((p, v, pos, id) -> vm.setTypeFilter(TYPES[pos]));
    }

    private void setupMonthDropdown(List<String> months) {
        monthKeys = months;
        List<String> labels = new ArrayList<>();
        labels.add(ALL);
        for (String m : months) labels.add(m.substring(5) + "/" + m.substring(0, 4)); // MM/yyyy
        b.ddMonth.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, labels));
        b.ddMonth.setOnItemClickListener((p, v, pos, id) -> vm.setMonthFilter(pos == 0 ? null : monthKeys.get(pos - 1)));
    }

    private void updateSummary(List<TransactionRecord> list) {
        long in = 0, out = 0;
        for (TransactionRecord t : list) {
            if (t.isCredit()) in += t.getAmount();
            else out += t.getAmount();
        }
        b.tvSummary.setText(list.size() + " giao dịch • Tiền vào " + MoneyUtils.formatVnd(in)
                + " • Tiền ra " + MoneyUtils.formatVnd(out));
    }

    private void showDetail(TransactionRecord t) {
        String msg = "Loại: " + UiText.txTypeLabel(t.getType())
                + "\nSố tiền: " + (t.isCredit() ? "+" : "-") + MoneyUtils.formatVnd(t.getAmount())
                + "\nThời gian: " + MoneyUtils.formatDateTime(t.getCreatedAt())
                + "\nTài khoản: " + (t.getAccountNumber() == null ? "—" : t.getAccountNumber())
                + "\nSố dư sau giao dịch: " + MoneyUtils.formatVnd(t.getBalanceAfter())
                + "\nNội dung: " + (t.getDescription() == null ? "—" : t.getDescription())
                + "\nMã giao dịch: " + t.getId();
        new AlertDialog.Builder(this)
                .setTitle("Chi tiết giao dịch")
                .setMessage(msg)
                .setPositiveButton("Đóng", null)
                .show();
    }
}
