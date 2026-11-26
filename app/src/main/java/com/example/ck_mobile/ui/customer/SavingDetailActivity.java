package com.example.ck_mobile.ui.customer;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.ck_mobile.R;
import com.example.ck_mobile.model.BankAccount;
import com.example.ck_mobile.repository.AccountRepository;
import com.example.ck_mobile.ui.base.BaseSecureActivity;
import com.example.ck_mobile.utils.AuthSessionHelper;
import com.example.ck_mobile.utils.FinancialCalculator;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseUser;

public class SavingDetailActivity extends BaseSecureActivity {

    private TextView tvRatePill;
    private TextView tvAccountNumber;
    private TextView tvBalance;
    private TextView tvProjectedInterest;
    private TextView tvElapsedTerm;
    private TextView tvTermPercent;
    private TextView tvStartLabel;
    private TextView tvEndLabel;
    private LinearProgressIndicator progressTerm;

    private TextView tvOpenedDate;
    private TextView tvDueDate;
    private TextView tvTermMonthsSub;
    private TextView tvPayoutMethod;
    private TextView tvRenewalInstruction;
    private TextView tvBeneficiaryAccount;
    private TextView tvEffectiveRate;

    private TextView tvAccruedDays;
    private TextView tvAccruedInterest;
    private TextView tvAccruedNote;
    private TextView tvMonthlyProfit;

    private AccountRepository accountRepository;
    private BankAccount savingAccount;
    private BankAccount checkingAccount;

    @Override
    protected String getRequiredRole() {
        return AuthSessionHelper.ROLE_CUSTOMER;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saving_detail);

        accountRepository = new AccountRepository();
        bindViews();
        bindActions();

        savingAccount = (BankAccount) getIntent().getSerializableExtra(CustomerAccountsActivity.EXTRA_ACCOUNT_DATA);
        checkingAccount = (BankAccount) getIntent().getSerializableExtra(CustomerAccountsActivity.EXTRA_CHECKING_ACCOUNT);

        if (savingAccount != null) {
            renderSavingDetails(savingAccount);
        }
        String accountId = getIntent().getStringExtra(CustomerAccountsActivity.EXTRA_ACCOUNT_ID);
        if (accountId != null && auth.getCurrentUser() != null) {
            accountRepository.getAccountById(auth.getCurrentUser().getUid(), accountId,
                    new AccountRepository.SingleAccountCallback() {
                        @Override
                        public void onSuccess(BankAccount account) {
                            savingAccount = account;
                            renderSavingDetails(account);
                        }

                        @Override
                        public void onError(String message) {
                            // Giữ dữ liệu hiện tại nếu đã có
                        }
                    });
        }
    }

    private void bindViews() {
        tvRatePill = findViewById(R.id.tv_saving_detail_rate_pill);
        tvAccountNumber = findViewById(R.id.tv_saving_detail_number);
        tvBalance = findViewById(R.id.tv_saving_detail_balance);
        tvProjectedInterest = findViewById(R.id.tv_saving_projected_interest);
        tvElapsedTerm = findViewById(R.id.tv_saving_elapsed_term);
        tvTermPercent = findViewById(R.id.tv_saving_term_percent);
        tvStartLabel = findViewById(R.id.tv_saving_start_label);
        tvEndLabel = findViewById(R.id.tv_saving_end_label);
        progressTerm = findViewById(R.id.progress_saving_term);

        tvOpenedDate = findViewById(R.id.tv_saving_opened_date);
        tvDueDate = findViewById(R.id.tv_saving_due_date);
        tvTermMonthsSub = findViewById(R.id.tv_saving_term_months_sub);
        tvPayoutMethod = findViewById(R.id.tv_saving_payout_method);
        tvRenewalInstruction = findViewById(R.id.tv_saving_renewal_instruction);
        tvBeneficiaryAccount = findViewById(R.id.tv_saving_beneficiary_account);
        tvEffectiveRate = findViewById(R.id.tv_saving_effective_rate);

        tvAccruedDays = findViewById(R.id.tv_saving_accrued_days);
        tvAccruedInterest = findViewById(R.id.tv_saving_accrued_interest);
        tvAccruedNote = findViewById(R.id.tv_saving_accrued_note);
        tvMonthlyProfit = findViewById(R.id.tv_saving_monthly_profit);
    }

    private void bindActions() {
        ImageButton btnBack = findViewById(R.id.btn_back_saving);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        ImageButton btnCopy = findViewById(R.id.btn_copy_saving_detail);
        if (btnCopy != null) {
            btnCopy.setOnClickListener(v -> {
                if (savingAccount != null) {
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    if (cm != null) {
                        cm.setPrimaryClip(ClipData.newPlainText("Số sổ tiết kiệm",
                                savingAccount.getAccountNumber().replace(" ", "")));
                        Toast.makeText(this, "Đã sao chép số tài khoản", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        MaterialButton btnTopUp = findViewById(R.id.btn_saving_topup);
        if (btnTopUp != null) {
            btnTopUp.setOnClickListener(v -> showTopUpDialog());
        }

        MaterialButton btnRenewal = findViewById(R.id.btn_saving_change_renewal);
        if (btnRenewal != null) {
            btnRenewal.setOnClickListener(v -> showRenewalDialog());
        }

        MaterialButton btnEarlyWithdraw = findViewById(R.id.btn_saving_early_withdraw);
        if (btnEarlyWithdraw != null) {
            btnEarlyWithdraw.setOnClickListener(v -> showPrematureWithdrawalDialog());
        }

        findViewById(R.id.btn_more_saving).setOnClickListener(v -> AuthSessionHelper.showDevelopingToast(this));
    }

    private void renderSavingDetails(BankAccount acc) {
        double principal = acc.getBalance();
        double rate = acc.getInterestRate() > 0 ? acc.getInterestRate() : 5.8;
        int termMonths = acc.getTermMonths() > 0 ? acc.getTermMonths() : 12;
        int elapsedMonths = Math.max(0, Math.min(termMonths, acc.getElapsedMonths()));
        int percent = termMonths > 0 ? Math.round((elapsedMonths * 100f) / termMonths) : 0;

        double projectedInterest = FinancialCalculator.calculateSavingProjectedInterest(principal, rate, termMonths);
        double accruedInterest = FinancialCalculator.calculateSavingAccruedInterest(principal, rate, elapsedMonths);
        double monthlyProfit = FinancialCalculator.calculateSavingMonthlyProfit(principal, rate);

        tvRatePill.setText(String.format("★ Lãi suất %.2f%%/năm", rate));
        tvAccountNumber.setText(acc.getAccountNumber());
        tvBalance.setText(FinancialCalculator.formatCurrencyNumber(principal));
        tvProjectedInterest.setText("+" + FinancialCalculator.formatVnd(projectedInterest));

        tvElapsedTerm.setText("Thời gian: " + elapsedMonths + " tháng / " + termMonths + " tháng");
        tvTermPercent.setText(percent + "% kỳ hạn");
        progressTerm.setProgress(percent);

        String opened = acc.getOpenedDate() != null ? acc.getOpenedDate() : "15/08/2026";
        String due = acc.getMaturityDate() != null ? acc.getMaturityDate() : "15/08/2027";
        tvStartLabel.setText("Bắt đầu (" + opened + ")");
        tvEndLabel.setText("Đáo hạn (" + due + ")");

        tvOpenedDate.setText(opened);
        tvDueDate.setText(due);
        tvTermMonthsSub.setText("(" + termMonths + " tháng)");
        tvPayoutMethod.setText(acc.getPayoutMethod() != null ? acc.getPayoutMethod() : "Cuối kỳ");
        tvRenewalInstruction.setText(acc.getRenewalOption() != null
                ? acc.getRenewalOption()
                : "Tự động tái tục cả gốc & lãi");
        tvBeneficiaryAccount.setText(acc.getBeneficiaryAccount() != null
                ? acc.getBeneficiaryAccount()
                : "1903 8888 6666");
        tvEffectiveRate.setText(String.format("%.2f%% / năm (Cố định)", rate));

        int approxDays = elapsedMonths * 30;
        tvAccruedDays.setText(approxDays + " ngày gửi");
        tvAccruedInterest.setText(FinancialCalculator.formatVnd(accruedInterest));
        tvAccruedNote.setText("Sinh lời sau " + elapsedMonths + " tháng");
        tvMonthlyProfit.setText("~" + FinancialCalculator.formatVnd(monthlyProfit));
    }

    private void showTopUpDialog() {
        if (savingAccount == null || auth.getCurrentUser() == null) return;
        if (checkingAccount == null) {
            Toast.makeText(this, "Không tìm thấy tài khoản thanh toán nguồn.", Toast.LENGTH_SHORT).show();
            return;
        }

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("Nhập số tiền gửi thêm (VNĐ), VD: 5000000");

        new AlertDialog.Builder(this)
                .setTitle("Gửi thêm tiền vào sổ tiết kiệm")
                .setMessage("Nguồn trích tiền: " + checkingAccount.getAccountNumber()
                        + "\nSố dư khả dụng: " + FinancialCalculator.formatVnd(checkingAccount.getBalance()))
                .setView(input)
                .setPositiveButton("Xác nhận gửi", (dialog, which) -> {
                    String raw = input.getText().toString().trim();
                    if (raw.isEmpty()) return;
                    double amount = Double.parseDouble(raw);
                    accountRepository.topUpSavingFromChecking(
                            auth.getCurrentUser().getUid(),
                            checkingAccount,
                            savingAccount,
                            amount,
                            new AccountRepository.ActionCallback() {
                                @Override
                                public void onSuccess(String message) {
                                    savingAccount.setBalance(savingAccount.getBalance() + amount);
                                    checkingAccount.setBalance(checkingAccount.getBalance() - amount);
                                    renderSavingDetails(savingAccount);
                                    Toast.makeText(SavingDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onError(String message) {
                                    Toast.makeText(SavingDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showRenewalDialog() {
        if (savingAccount == null || auth.getCurrentUser() == null) return;
        final String[] options = {
                "Tự động tái tục cả gốc & lãi",
                "Tái tục gốc, chuyển lãi về TK thanh toán",
                "Tất toán gốc & lãi về TK thanh toán khi đến hạn"
        };

        new AlertDialog.Builder(this)
                .setTitle("Chọn chỉ thị đáo hạn")
                .setItems(options, (dialog, which) -> {
                    String chosen = options[which];
                    accountRepository.updateSavingRenewalOption(
                            auth.getCurrentUser().getUid(),
                            savingAccount.getAccountId(),
                            chosen,
                            new AccountRepository.ActionCallback() {
                                @Override
                                public void onSuccess(String message) {
                                    savingAccount.setRenewalOption(chosen);
                                    tvRenewalInstruction.setText(chosen);
                                    Toast.makeText(SavingDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onError(String message) {
                                    Toast.makeText(SavingDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
                })
                .show();
    }

    private void showPrematureWithdrawalDialog() {
        if (savingAccount == null) return;
        int elapsedDays = Math.max(1, savingAccount.getElapsedMonths() * 30);
        double payout = FinancialCalculator.calculatePrematureWithdrawalPayout(savingAccount.getBalance(), elapsedDays);

        new AlertDialog.Builder(this)
                .setTitle("Tất toán trước hạn")
                .setMessage("Áp dụng lãi suất không kỳ hạn 0.2%/năm cho ~" + elapsedDays + " ngày gửi."
                        + "\nTổng tiền ước tính nhận về: " + FinancialCalculator.formatVnd(payout))
                .setPositiveButton("Đã hiểu", null)
                .show();
    }
}