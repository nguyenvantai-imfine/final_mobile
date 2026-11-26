package com.example.ck_mobile.ui.customer;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ck_mobile.R;
import com.example.ck_mobile.model.BankAccount;
import com.example.ck_mobile.model.MortgageInstallment;
import com.example.ck_mobile.repository.AccountRepository;
import com.example.ck_mobile.ui.base.BaseSecureActivity;
import com.example.ck_mobile.utils.AuthSessionHelper;
import com.example.ck_mobile.utils.FinancialCalculator;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MortgageScheduleActivity extends BaseSecureActivity {

    private TextView tvContractCode;
    private TextView tvTermBadge;
    private TextView tvCompletionPercent;
    private TextView tvInstallmentRatio;
    private LinearProgressIndicator progressCompletion;
    private TextView tvPaidCount;
    private TextView tvRemainingCount;
    private TextView tvInterestRate;
    private TextView tvNextDueMeta;
    private TextView tvNextDueAmount;

    private Chip chipAll;
    private Chip chipDue;
    private Chip chipPaid;
    private Chip chipOverdue;
    private ChipGroup chipGroupFilter;

    private MaterialButton btnSelectYear;
    private TextView tvYearTotal;
    private MaterialCardView cardEmptyState;

    private MortgageInstallmentAdapter adapter;
    private AccountRepository accountRepository;
    private BankAccount mortgageAccount;
    private BankAccount checkingAccount;

    private final List<MortgageInstallment> fullSchedule = new ArrayList<>();
    private String statusFilter = "ALL"; // ALL, DUE_UPCOMING, PAID, OVERDUE
    private Integer selectedYear = null; // null = tất cả các năm

    @Override
    protected String getRequiredRole() {
        return AuthSessionHelper.ROLE_CUSTOMER;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mortgage_schedule);

        accountRepository = new AccountRepository();
        mortgageAccount = (BankAccount) getIntent().getSerializableExtra(MortgageDetailActivity.EXTRA_MORTGAGE_ACCOUNT);
        checkingAccount = (BankAccount) getIntent().getSerializableExtra(CustomerAccountsActivity.EXTRA_CHECKING_ACCOUNT);

        bindViews();
        bindActions();
        buildAndRenderSchedule();
    }

    private void bindViews() {
        tvContractCode = findViewById(R.id.tv_schedule_contract_code);
        tvTermBadge = findViewById(R.id.tv_schedule_term_badge);
        tvCompletionPercent = findViewById(R.id.tv_schedule_completion_percent);
        tvInstallmentRatio = findViewById(R.id.tv_schedule_installment_ratio);
        progressCompletion = findViewById(R.id.progress_schedule_completion);
        tvPaidCount = findViewById(R.id.tv_schedule_paid_count);
        tvRemainingCount = findViewById(R.id.tv_schedule_remaining_count);
        tvInterestRate = findViewById(R.id.tv_schedule_interest_rate);
        tvNextDueMeta = findViewById(R.id.tv_schedule_next_due_meta);
        tvNextDueAmount = findViewById(R.id.tv_schedule_next_due_amount);

        chipGroupFilter = findViewById(R.id.chip_group_schedule_filter);
        chipAll = findViewById(R.id.chip_filter_all);
        chipDue = findViewById(R.id.chip_filter_due);
        chipPaid = findViewById(R.id.chip_filter_paid);
        chipOverdue = findViewById(R.id.chip_filter_overdue);

        btnSelectYear = findViewById(R.id.btn_select_schedule_year);
        tvYearTotal = findViewById(R.id.tv_schedule_year_total);
        cardEmptyState = findViewById(R.id.card_schedule_empty_state);

        RecyclerView rv = findViewById(R.id.rv_installment_schedule);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MortgageInstallmentAdapter(this::handlePayInstallment);
        rv.setAdapter(adapter);
    }

    private void bindActions() {
        ImageButton btnBack = findViewById(R.id.btn_back_schedule);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        chipGroupFilter.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.contains(R.id.chip_filter_due)) {
                statusFilter = "DUE_UPCOMING";
            } else if (checkedIds.contains(R.id.chip_filter_paid)) {
                statusFilter = "PAID";
            } else if (checkedIds.contains(R.id.chip_filter_overdue)) {
                statusFilter = "OVERDUE";
            } else {
                statusFilter = "ALL";
            }
            applyFilters();
        });

        btnSelectYear.setOnClickListener(v -> showYearPickerDialog());

        findViewById(R.id.btn_download_schedule).setOnClickListener(v -> AuthSessionHelper.showDevelopingToast(this));
        findViewById(R.id.btn_export_schedule_120).setOnClickListener(v -> AuthSessionHelper.showDevelopingToast(this));
    }

    private void buildAndRenderSchedule() {
        double initial = (mortgageAccount != null && mortgageAccount.getInitialPrincipal() > 0)
                ? mortgageAccount.getInitialPrincipal() : 600_000_000;
        double rate = (mortgageAccount != null && mortgageAccount.getInterestRate() > 0)
                ? mortgageAccount.getInterestRate() : 9.8;
        int totalPeriods = (mortgageAccount != null && mortgageAccount.getTotalInstallments() > 0)
                ? mortgageAccount.getTotalInstallments() : 120;
        int paidPeriods = mortgageAccount != null
                ? Math.max(0, Math.min(totalPeriods, mortgageAccount.getPaidInstallments())) : 36;
        String code = mortgageAccount != null ? mortgageAccount.getAccountNumber() : "MTG-2026-089";

        int remainingPeriods = Math.max(0, totalPeriods - paidPeriods);
        int progress = FinancialCalculator.calculateRepaymentProgressPercent(
                initial, mortgageAccount != null ? mortgageAccount.getBalance() : initial * 0.7);

        tvContractCode.setText("🛡 Mã HĐ: " + code);
        tvTermBadge.setText(totalPeriods + " kỳ (" + (totalPeriods / 12) + " năm)");
        tvCompletionPercent.setText("Tiến độ hoàn thành: " + progress + "%");
        tvInstallmentRatio.setText(paidPeriods + "/" + totalPeriods + " kỳ");
        progressCompletion.setProgress(progress);

        tvPaidCount.setText(paidPeriods + " kỳ");
        tvRemainingCount.setText(remainingPeriods + " kỳ");
        tvInterestRate.setText(rate + "%/năm");

        fullSchedule.clear();
        fullSchedule.addAll(FinancialCalculator.generateAmortizationSchedule(
                initial, rate, totalPeriods, paidPeriods, 2023, 12
        ));

        int dueUpcomingCount = 0;
        int overdueCount = 0;
        MortgageInstallment nextDue = null;

        for (MortgageInstallment item : fullSchedule) {
            if (MortgageInstallment.STATUS_DUE.equals(item.getStatus())
                    || MortgageInstallment.STATUS_UPCOMING.equals(item.getStatus())) {
                dueUpcomingCount++;
                if (nextDue == null) {
                    nextDue = item;
                }
            } else if (MortgageInstallment.STATUS_OVERDUE.equals(item.getStatus())) {
                overdueCount++;
            }
        }

        if (nextDue != null) {
            tvNextDueMeta.setText("Kỳ kế tiếp: Kỳ " + nextDue.getPeriodNumber() + "/" + totalPeriods
                    + " • Hạn " + nextDue.getDueDate());
            tvNextDueAmount.setText("Số tiền: " + FinancialCalculator.formatVnd(nextDue.getTotalAmount()));
        }

        chipAll.setText("Tất cả (" + totalPeriods + ")");
        chipDue.setText("Đến hạn / Sắp tới (" + dueUpcomingCount + ")");
        chipPaid.setText("Đã thanh toán (" + paidPeriods + ")");
        chipOverdue.setText("• Quá hạn (" + overdueCount + ")");

        applyFilters();
    }

    private void applyFilters() {
        List<MortgageInstallment> filtered = new ArrayList<>();
        double yearSum = 0;

        for (MortgageInstallment item : fullSchedule) {
            boolean matchYear = (selectedYear == null || item.getYear() == selectedYear);
            if (matchYear) {
                yearSum += item.getTotalAmount();
            }

            boolean matchStatus;
            switch (statusFilter) {
                case "PAID":
                    matchStatus = MortgageInstallment.STATUS_PAID.equals(item.getStatus());
                    break;
                case "DUE_UPCOMING":
                    matchStatus = MortgageInstallment.STATUS_DUE.equals(item.getStatus())
                            || MortgageInstallment.STATUS_UPCOMING.equals(item.getStatus());
                    break;
                case "OVERDUE":
                    matchStatus = MortgageInstallment.STATUS_OVERDUE.equals(item.getStatus());
                    break;
                default:
                    matchStatus = true;
                    break;
            }

            if (matchYear && matchStatus) {
                filtered.add(item);
            }
        }

        adapter.submitList(filtered);
        cardEmptyState.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);

        String yearLabel = selectedYear != null ? "năm " + selectedYear : "toàn khóa";
        tvYearTotal.setText("Tổng " + yearLabel + ": " + FinancialCalculator.formatVnd(yearSum));
    }

    private void showYearPickerDialog() {
        final String[] labels = {
                "Tất cả các năm (120 kỳ)",
                "Năm 2026",
                "Năm 2027",
                "Năm 2028"
        };
        final Integer[] years = {null, 2026, 2027, 2028};

        new AlertDialog.Builder(this)
                .setTitle("Chọn năm thanh toán")
                .setItems(labels, (dialog, which) -> {
                    selectedYear = years[which];
                    btnSelectYear.setText(labels[which] + " ⌄");
                    applyFilters();
                })
                .show();
    }

    private void handlePayInstallment(MortgageInstallment installment) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null || mortgageAccount == null || checkingAccount == null) {
            Toast.makeText(this, "Thiếu thông tin tài khoản thanh toán hoặc khoản vay.", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Thanh toán kỳ " + installment.getPeriodNumber())
                .setMessage("Số tiền: " + FinancialCalculator.formatVnd(installment.getTotalAmount())
                        + "\nTrích từ TK: " + checkingAccount.getAccountNumber())
                .setPositiveButton("Xác nhận", (dialog, which) -> accountRepository.payMortgageInstallment(
                        user.getUid(),
                        checkingAccount,
                        mortgageAccount,
                        installment.getPrincipalAmount(),
                        installment.getTotalAmount(),
                        new AccountRepository.ActionCallback() {
                            @Override
                            public void onSuccess(String message) {
                                mortgageAccount.setBalance(Math.max(0, mortgageAccount.getBalance() - installment.getPrincipalAmount()));
                                mortgageAccount.setPaidInstallments(mortgageAccount.getPaidInstallments() + 1);
                                checkingAccount.setBalance(checkingAccount.getBalance() - installment.getTotalAmount());
                                buildAndRenderSchedule();
                                Toast.makeText(MortgageScheduleActivity.this, message, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(String message) {
                                Toast.makeText(MortgageScheduleActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        }
                ))
                .setNegativeButton("Hủy", null)
                .show();
    }
}