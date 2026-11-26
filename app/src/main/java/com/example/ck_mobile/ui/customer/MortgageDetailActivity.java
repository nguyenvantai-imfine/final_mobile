package com.example.ck_mobile.ui.customer;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MortgageDetailActivity extends BaseSecureActivity {

    public static final String EXTRA_MORTGAGE_ACCOUNT = "extra_mortgage_account";

    private TextView tvStatusBadge;
    private TextView tvContractCode;
    private TextView tvRemainingPrincipal;
    private TextView tvInitialLimit;
    private TextView tvPaidSummary;
    private TextView tvRepaymentProgress;
    private TextView tvRemainingPercent;
    private LinearProgressIndicator progressRepayment;

    private TextView tvDaysLeft;
    private TextView tvNextDueDate;
    private TextView tvNextTotalAmount;
    private TextView tvNextPrincipal;
    private TextView tvNextInterest;
    private TextView tvRateNote;
    private TextView tvAutoDebitSource;
    private MaterialSwitch switchAutoDebit;

    private TextView tvLoanTerm;
    private TextView tvPaidMonths;
    private TextView tvCurrentRate;
    private TextView tvFloatingMargin;
    private TextView tvCollateralName;
    private TextView tvCollateralLocation;
    private TextView tvCollateralValuation;
    private TextView tvCollateralLtv;
    private TextView tvInstallmentCount;

    private TextView tvOfficerName;
    private TextView tvOfficerBranch;
    private MaterialButton btnCallOfficer;

    private MortgagePaymentAdapter recentPaymentsAdapter;
    private AccountRepository accountRepository;
    private BankAccount mortgageAccount;
    private BankAccount checkingAccount;

    @Override
    protected String getRequiredRole() {
        return AuthSessionHelper.ROLE_CUSTOMER;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mortgage_detail);

        accountRepository = new AccountRepository();
        bindViews();
        bindActions();

        mortgageAccount = (BankAccount) getIntent().getSerializableExtra(CustomerAccountsActivity.EXTRA_ACCOUNT_DATA);
        checkingAccount = (BankAccount) getIntent().getSerializableExtra(CustomerAccountsActivity.EXTRA_CHECKING_ACCOUNT);

        if (mortgageAccount != null) {
            renderMortgageDetails(mortgageAccount);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (auth.getCurrentUser() != null && mortgageAccount != null) {
            accountRepository.getAccountById(auth.getCurrentUser().getUid(), mortgageAccount.getAccountId(),
                    new AccountRepository.SingleAccountCallback() {
                        @Override
                        public void onSuccess(BankAccount account) {
                            mortgageAccount = account;
                            renderMortgageDetails(account);
                        }

                        @Override
                        public void onError(String message) {}
                    });
        }
    }

    private void bindViews() {
        tvStatusBadge = findViewById(R.id.tv_mortgage_status_badge);
        tvContractCode = findViewById(R.id.tv_mortgage_contract_code);
        tvRemainingPrincipal = findViewById(R.id.tv_mortgage_remaining_principal);
        tvInitialLimit = findViewById(R.id.tv_mortgage_initial_limit);
        tvPaidSummary = findViewById(R.id.tv_mortgage_paid_summary);
        tvRepaymentProgress = findViewById(R.id.tv_mortgage_repayment_progress);
        tvRemainingPercent = findViewById(R.id.tv_mortgage_remaining_percent);
        progressRepayment = findViewById(R.id.progress_mortgage_repayment);

        tvDaysLeft = findViewById(R.id.tv_mortgage_days_left);
        tvNextDueDate = findViewById(R.id.tv_mortgage_next_due_date);
        tvNextTotalAmount = findViewById(R.id.tv_mortgage_next_total_amount);
        tvNextPrincipal = findViewById(R.id.tv_mortgage_next_principal);
        tvNextInterest = findViewById(R.id.tv_mortgage_next_interest);
        tvRateNote = findViewById(R.id.tv_mortgage_rate_note);
        tvAutoDebitSource = findViewById(R.id.tv_mortgage_auto_debit_source);
        switchAutoDebit = findViewById(R.id.switch_mortgage_auto_debit);

        tvLoanTerm = findViewById(R.id.tv_mortgage_loan_term);
        tvPaidMonths = findViewById(R.id.tv_mortgage_paid_months);
        tvCurrentRate = findViewById(R.id.tv_mortgage_current_rate);
        tvFloatingMargin = findViewById(R.id.tv_mortgage_floating_margin);
        tvCollateralName = findViewById(R.id.tv_collateral_name);
        tvCollateralLocation = findViewById(R.id.tv_collateral_location);
        tvCollateralValuation = findViewById(R.id.tv_collateral_valuation);
        tvCollateralLtv = findViewById(R.id.tv_collateral_ltv);
        tvInstallmentCount = findViewById(R.id.tv_mortgage_installment_count);

        tvOfficerName = findViewById(R.id.tv_loan_officer_name);
        tvOfficerBranch = findViewById(R.id.tv_loan_officer_branch);
        btnCallOfficer = findViewById(R.id.btn_call_loan_officer);

        RecyclerView rvRecent = findViewById(R.id.rv_mortgage_recent_payments);
        rvRecent.setLayoutManager(new LinearLayoutManager(this));
        recentPaymentsAdapter = new MortgagePaymentAdapter();
        rvRecent.setAdapter(recentPaymentsAdapter);
    }

    private void bindActions() {
        ImageButton btnBack = findViewById(R.id.btn_back_mortgage);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        ImageButton btnCopy = findViewById(R.id.btn_copy_mortgage_code);
        if (btnCopy != null) {
            btnCopy.setOnClickListener(v -> {
                if (mortgageAccount != null) {
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    if (cm != null) {
                        cm.setPrimaryClip(ClipData.newPlainText("Mã hợp đồng", mortgageAccount.getAccountNumber()));
                        Toast.makeText(this, "Đã sao chép mã hợp đồng", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        findViewById(R.id.btn_view_amortization_schedule).setOnClickListener(v -> {
            if (mortgageAccount == null) return;
            Intent intent = new Intent(this, MortgageScheduleActivity.class);
            intent.putExtra(EXTRA_MORTGAGE_ACCOUNT, mortgageAccount);
            if (checkingAccount != null) {
                intent.putExtra(CustomerAccountsActivity.EXTRA_CHECKING_ACCOUNT, checkingAccount);
            }
            startActivity(intent);
        });

        MaterialButton btnPayNow = findViewById(R.id.btn_pay_mortgage_now);
        if (btnPayNow != null) {
            btnPayNow.setOnClickListener(v -> confirmPayNextInstallment());
        }

        switchAutoDebit.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!buttonView.isPressed() || mortgageAccount == null || auth.getCurrentUser() == null) return;
            accountRepository.updateMortgageAutoDebit(
                    auth.getCurrentUser().getUid(),
                    mortgageAccount.getAccountId(),
                    isChecked,
                    new AccountRepository.ActionCallback() {
                        @Override
                        public void onSuccess(String message) {
                            mortgageAccount.setAutoDebitEnabled(isChecked);
                            Toast.makeText(MortgageDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String message) {
                            switchAutoDebit.setChecked(!isChecked);
                            Toast.makeText(MortgageDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        });

        btnCallOfficer.setOnClickListener(v -> {
            String phone = (mortgageAccount != null && mortgageAccount.getLoanOfficerPhone() != null)
                    ? mortgageAccount.getLoanOfficerPhone()
                    : "0912345678";
            Intent dial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
            startActivity(dial);
        });

        findViewById(R.id.btn_chat_loan_officer).setOnClickListener(v -> AuthSessionHelper.showDevelopingToast(this));
        findViewById(R.id.btn_more_mortgage).setOnClickListener(v -> AuthSessionHelper.showDevelopingToast(this));
    }

    private void renderMortgageDetails(BankAccount acc) {
        double initial = acc.getInitialPrincipal() > 0 ? acc.getInitialPrincipal() : 600_000_000;
        double remaining = acc.getBalance();
        double paid = Math.max(0, initial - remaining);
        int progress = FinancialCalculator.calculateRepaymentProgressPercent(initial, remaining);
        int totalPeriods = acc.getTotalInstallments() > 0 ? acc.getTotalInstallments() : 120;
        int paidPeriods = Math.max(0, Math.min(totalPeriods, acc.getPaidInstallments()));
        double rate = acc.getInterestRate() > 0 ? acc.getInterestRate() : 9.8;

        tvStatusBadge.setText("OVERDUE".equalsIgnoreCase(acc.getStatus()) ? "• Quá hạn" : "• Đúng hạn");
        tvContractCode.setText(acc.getAccountNumber());
        tvRemainingPrincipal.setText(FinancialCalculator.formatCurrencyNumber(remaining));
        tvInitialLimit.setText(FinancialCalculator.formatVnd(initial));
        tvPaidSummary.setText(FinancialCalculator.formatVnd(paid) + " (" + progress + "%)");
        tvRepaymentProgress.setText("✓ Tiến độ trả nợ: " + progress + "%");
        tvRemainingPercent.setText("Còn lại " + Math.max(0, 100 - progress) + "%");
        progressRepayment.setProgress(progress);

        tvDaysLeft.setText("Còn " + Math.max(0, acc.getDaysUntilDue()) + " ngày");
        tvNextDueDate.setText(acc.getNextDueDate() != null ? acc.getNextDueDate() : "05/12/2026");

        List<MortgageInstallment> schedule = FinancialCalculator.generateAmortizationSchedule(
                initial, rate, totalPeriods, paidPeriods, 2023, 12
        );

        MortgageInstallment nextDue = null;
        List<MortgageInstallment> recentPaid = new ArrayList<>();
        for (MortgageInstallment item : schedule) {
            if (MortgageInstallment.STATUS_DUE.equals(item.getStatus()) && nextDue == null) {
                nextDue = item;
            }
            if (MortgageInstallment.STATUS_PAID.equals(item.getStatus())) {
                recentPaid.add(item);
            }
        }

        if (nextDue != null) {
            tvNextTotalAmount.setText(FinancialCalculator.formatVnd(nextDue.getTotalAmount()));
            tvNextPrincipal.setText(FinancialCalculator.formatVnd(nextDue.getPrincipalAmount()));
            tvNextInterest.setText(FinancialCalculator.formatVnd(nextDue.getInterestAmount()));
        }

        // Lấy 2 kỳ gần nhất đã trả (đảo ngược để kỳ mới nhất lên đầu)
        List<MortgageInstallment> lastTwo = new ArrayList<>();
        for (int i = recentPaid.size() - 1; i >= 0 && lastTwo.size() < 2; i--) {
            lastTwo.add(recentPaid.get(i));
        }
        recentPaymentsAdapter.submitList(lastTwo);

        tvRateNote.setText("ⓘ Áp dụng lãi suất ưu đãi " + rate + "%/năm");
        tvAutoDebitSource.setText("TK TDT " + (checkingAccount != null ? checkingAccount.getAccountNumber() : "1903 8888 6666"));
        switchAutoDebit.setChecked(acc.isAutoDebitEnabled());

        tvLoanTerm.setText((totalPeriods / 12) + " năm");
        tvPaidMonths.setText("Đã trả " + paidPeriods + "/" + totalPeriods + " tháng");
        tvCurrentRate.setText(rate + "%/năm");
        tvFloatingMargin.setText("Biên độ +3.2% thả nổi");

        tvCollateralName.setText(acc.getCollateralName() != null ? acc.getCollateralName() : "Căn hộ Sky Garden 3");
        tvCollateralLocation.setText(acc.getCollateralLocation() != null ? acc.getCollateralLocation() : "Quận 7, TP. Hồ Chí Minh");
        double valuation = acc.getCollateralValuation() > 0 ? acc.getCollateralValuation() : 1_850_000_000;
        tvCollateralValuation.setText(FinancialCalculator.formatVnd(valuation));

        double ltv = FinancialCalculator.calculateLtvPercent(initial, valuation);
        tvCollateralLtv.setText("🛡 " + ltv + "% (Rất an toàn)");
        tvInstallmentCount.setText(paidPeriods + "/" + totalPeriods + " kỳ");

        tvOfficerName.setText(acc.getLoanOfficerName() != null ? acc.getLoanOfficerName() : "Nguyễn Hoàng Nam");
        tvOfficerBranch.setText(acc.getLoanOfficerBranch() != null ? acc.getLoanOfficerBranch() : "Phòng Tín dụng BĐS • TDT Tân Bình");
        if (acc.getLoanOfficerPhone() != null && !acc.getLoanOfficerPhone().isEmpty()) {
            btnCallOfficer.setText("📞 " + acc.getLoanOfficerPhone());
        }
    }

    private void confirmPayNextInstallment() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null || mortgageAccount == null) return;
        if (checkingAccount == null) {
            Toast.makeText(this, "Không tìm thấy tài khoản thanh toán.", Toast.LENGTH_SHORT).show();
            return;
        }

        double initial = mortgageAccount.getInitialPrincipal() > 0 ? mortgageAccount.getInitialPrincipal() : 600_000_000;
        int totalPeriods = mortgageAccount.getTotalInstallments() > 0 ? mortgageAccount.getTotalInstallments() : 120;
        int paidPeriods = mortgageAccount.getPaidInstallments();
        double rate = mortgageAccount.getInterestRate() > 0 ? mortgageAccount.getInterestRate() : 9.8;

        List<MortgageInstallment> schedule = FinancialCalculator.generateAmortizationSchedule(
                initial, rate, totalPeriods, paidPeriods, 2023, 12
        );
        MortgageInstallment nextDue = null;
        for (MortgageInstallment item : schedule) {
            if (MortgageInstallment.STATUS_DUE.equals(item.getStatus())) {
                nextDue = item;
                break;
            }
        }
        if (nextDue == null) return;

        final MortgageInstallment target = nextDue;
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận thanh toán kỳ " + target.getPeriodNumber())
                .setMessage("Tổng thanh toán: " + FinancialCalculator.formatVnd(target.getTotalAmount())
                        + "\n• Gốc: " + FinancialCalculator.formatVnd(target.getPrincipalAmount())
                        + "\n• Lãi: " + FinancialCalculator.formatVnd(target.getInterestAmount())
                        + "\nTrích từ TK: " + checkingAccount.getAccountNumber())
                .setPositiveButton("Thanh toán", (dialog, which) -> accountRepository.payMortgageInstallment(
                        user.getUid(),
                        checkingAccount,
                        mortgageAccount,
                        target.getPrincipalAmount(),
                        target.getTotalAmount(),
                        new AccountRepository.ActionCallback() {
                            @Override
                            public void onSuccess(String message) {
                                mortgageAccount.setBalance(Math.max(0, mortgageAccount.getBalance() - target.getPrincipalAmount()));
                                mortgageAccount.setPaidInstallments(mortgageAccount.getPaidInstallments() + 1);
                                checkingAccount.setBalance(checkingAccount.getBalance() - target.getTotalAmount());
                                renderMortgageDetails(mortgageAccount);
                                Toast.makeText(MortgageDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(String message) {
                                Toast.makeText(MortgageDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        }
                ))
                .setNegativeButton("Hủy", null)
                .show();
    }
}