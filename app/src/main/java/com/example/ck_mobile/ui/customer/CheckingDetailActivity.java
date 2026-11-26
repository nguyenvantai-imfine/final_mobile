package com.example.ck_mobile.ui.customer;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ck_mobile.R;
import com.example.ck_mobile.model.AccountTransaction;
import com.example.ck_mobile.model.BankAccount;
import com.example.ck_mobile.repository.AccountRepository;
import com.example.ck_mobile.ui.base.BaseSecureActivity;
import com.example.ck_mobile.utils.AuthSessionHelper;
import com.example.ck_mobile.utils.FinancialCalculator;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CheckingDetailActivity extends BaseSecureActivity {

    private TextView tvAccountNumber;
    private TextView tvBalance;
    private TextView tvActualBalance;
    private TextView tvHoldBalance;
    private TextView tvOwnerName;
    private TextView tvBranch;
    private TextView tvOpenedDate;
    private TextView tvDailyLimit;
    private TextView tvLinkedCard;
    private TextView tvEmptyTx;
    private MaterialSwitch switchOnlinePayment;
    private EditText etSearchTx;
    private ChipGroup chipGroupFilter;

    private AccountRepository accountRepository;
    private CheckingTransactionAdapter txAdapter;
    private BankAccount currentAccount;
    private final List<AccountTransaction> allTransactions = new ArrayList<>();

    private boolean isBalanceVisible = true;
    private String txFilterMode = "ALL"; // ALL, IN, OUT, 7D, MONTH

    @Override
    protected String getRequiredRole() {
        return AuthSessionHelper.ROLE_CUSTOMER;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checking_detail);

        accountRepository = new AccountRepository();
        bindViews();
        bindActions();

        BankAccount passed = (BankAccount) getIntent().getSerializableExtra(CustomerAccountsActivity.EXTRA_ACCOUNT_DATA);
        String accountId = getIntent().getStringExtra(CustomerAccountsActivity.EXTRA_ACCOUNT_ID);

        if (passed != null) {
            currentAccount = passed;
            bindAccountData(currentAccount);
            loadTransactions(currentAccount.getAccountId());
        } else if (accountId != null) {
            loadAccountDetail(accountId);
        }
    }

    private void bindViews() {
        tvAccountNumber = findViewById(R.id.tv_checking_detail_number);
        tvBalance = findViewById(R.id.tv_checking_detail_balance);
        tvActualBalance = findViewById(R.id.tv_checking_actual_balance);
        tvHoldBalance = findViewById(R.id.tv_checking_hold_balance);
        tvOwnerName = findViewById(R.id.tv_checking_owner_name);
        tvBranch = findViewById(R.id.tv_checking_branch);
        tvOpenedDate = findViewById(R.id.tv_checking_opened_date);
        tvDailyLimit = findViewById(R.id.tv_checking_daily_limit);
        tvLinkedCard = findViewById(R.id.tv_linked_debit_card);
        tvEmptyTx = findViewById(R.id.tv_empty_checking_tx);
        switchOnlinePayment = findViewById(R.id.switch_online_payment);
        etSearchTx = findViewById(R.id.et_search_checking_tx);
        chipGroupFilter = findViewById(R.id.chip_group_tx_filter);

        RecyclerView rvTx = findViewById(R.id.rv_checking_transactions);
        rvTx.setLayoutManager(new LinearLayoutManager(this));
        txAdapter = new CheckingTransactionAdapter();
        rvTx.setAdapter(txAdapter);
    }

    private void bindActions() {
        ImageButton btnBack = findViewById(R.id.btn_back_checking);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        ImageButton btnCopy = findViewById(R.id.btn_copy_checking_detail);
        if (btnCopy != null) {
            btnCopy.setOnClickListener(v -> {
                if (currentAccount != null) {
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    if (cm != null) {
                        cm.setPrimaryClip(ClipData.newPlainText("Số tài khoản",
                                currentAccount.getAccountNumber().replace(" ", "")));
                        Toast.makeText(this, "Đã sao chép số tài khoản", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        ImageButton btnToggle = findViewById(R.id.btn_toggle_checking_balance);
        if (btnToggle != null) {
            btnToggle.setOnClickListener(v -> {
                isBalanceVisible = !isBalanceVisible;
                if (currentAccount != null) {
                    bindAccountData(currentAccount);
                }
            });
        }

        switchOnlinePayment.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!buttonView.isPressed() || currentAccount == null || auth.getCurrentUser() == null) return;
            accountRepository.updateOnlinePaymentToggle(
                    auth.getCurrentUser().getUid(),
                    currentAccount.getAccountId(),
                    isChecked,
                    new AccountRepository.ActionCallback() {
                        @Override
                        public void onSuccess(String message) {
                            currentAccount.setOnlinePaymentEnabled(isChecked);
                            Toast.makeText(CheckingDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String message) {
                            switchOnlinePayment.setChecked(!isChecked);
                            Toast.makeText(CheckingDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        });

        etSearchTx.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyTransactionFilter();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        chipGroupFilter.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.contains(R.id.chip_tx_in)) {
                txFilterMode = "IN";
            } else if (checkedIds.contains(R.id.chip_tx_out)) {
                txFilterMode = "OUT";
            } else if (checkedIds.contains(R.id.chip_tx_7d)) {
                txFilterMode = "7D";
            } else if (checkedIds.contains(R.id.chip_tx_month)) {
                txFilterMode = "MONTH";
            } else {
                txFilterMode = "ALL";
            }
            applyTransactionFilter();
        });

        int[] devIds = {
                R.id.btn_more_checking,
                R.id.btn_action_transfer,
                R.id.btn_action_scan_qr,
                R.id.btn_action_deposit,
                R.id.btn_action_manage_card,
                R.id.btn_advanced_filter,
                R.id.btn_export_statement
        };
        View.OnClickListener devListener = v -> AuthSessionHelper.showDevelopingToast(this);
        for (int id : devIds) {
            View v = findViewById(id);
            if (v != null) v.setOnClickListener(devListener);
        }
    }

    private void loadAccountDetail(String accountId) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        accountRepository.getAccountById(user.getUid(), accountId, new AccountRepository.SingleAccountCallback() {
            @Override
            public void onSuccess(BankAccount account) {
                currentAccount = account;
                bindAccountData(account);
                loadTransactions(account.getAccountId());
            }

            @Override
            public void onError(String message) {
                Toast.makeText(CheckingDetailActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTransactions(String accountId) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        accountRepository.getCheckingTransactions(user.getUid(), accountId,
                new AccountRepository.TransactionsCallback() {
                    @Override
                    public void onSuccess(List<AccountTransaction> transactions) {
                        allTransactions.clear();
                        allTransactions.addAll(transactions);
                        applyTransactionFilter();
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(CheckingDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void applyTransactionFilter() {
        String keyword = etSearchTx.getText() != null
                ? etSearchTx.getText().toString().trim().toLowerCase(Locale.ROOT)
                : "";
        long sevenDaysAgo = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000);
        long thirtyDaysAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000);

        List<AccountTransaction> filtered = new ArrayList<>();
        for (AccountTransaction tx : allTransactions) {
            boolean matchesKeyword = keyword.isEmpty()
                    || (tx.getTitle() != null && tx.getTitle().toLowerCase(Locale.ROOT).contains(keyword))
                    || (tx.getDateTimeLabel() != null && tx.getDateTimeLabel().toLowerCase(Locale.ROOT).contains(keyword));

            boolean matchesMode;
            switch (txFilterMode) {
                case "IN":
                    matchesMode = tx.getAmount() > 0;
                    break;
                case "OUT":
                    matchesMode = tx.getAmount() < 0;
                    break;
                case "7D":
                    matchesMode = tx.getTimestamp() >= sevenDaysAgo;
                    break;
                case "MONTH":
                    matchesMode = tx.getTimestamp() >= thirtyDaysAgo;
                    break;
                default:
                    matchesMode = true;
                    break;
            }

            if (matchesKeyword && matchesMode) {
                filtered.add(tx);
            }
        }

        txAdapter.submitList(filtered);
        tvEmptyTx.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void bindAccountData(BankAccount acc) {
        tvAccountNumber.setText(acc.getAccountNumber());
        tvBalance.setText(isBalanceVisible
                ? FinancialCalculator.formatCurrencyNumber(acc.getBalance())
                : "••••••••");
        tvActualBalance.setText(isBalanceVisible
                ? FinancialCalculator.formatVnd(acc.getActualBalance())
                : "•••••••• đ");
        tvHoldBalance.setText(FinancialCalculator.formatVnd(acc.getHoldBalance()));

        if (auth.getCurrentUser() != null && auth.getCurrentUser().getDisplayName() != null
                && !auth.getCurrentUser().getDisplayName().isEmpty()) {
            tvOwnerName.setText(auth.getCurrentUser().getDisplayName().toUpperCase(Locale.ROOT));
        }
        tvBranch.setText(acc.getBranchName() != null ? acc.getBranchName() : "TDTU - CN Tân Phong, Q.7");
        tvOpenedDate.setText(acc.getOpenedDate() != null ? acc.getOpenedDate() : "15/09/2021");
        tvDailyLimit.setText(FinancialCalculator.formatVnd(acc.getDailyTransferLimit()));
        tvLinkedCard.setText(acc.getLinkedCardMasked() != null ? acc.getLinkedCardMasked() : "Visa Platinum •••• 4289");
        switchOnlinePayment.setChecked(acc.isOnlinePaymentEnabled());
    }
}