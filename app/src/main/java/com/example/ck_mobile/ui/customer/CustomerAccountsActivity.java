package com.example.ck_mobile.ui.customer;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.ck_mobile.R;
import com.example.ck_mobile.model.BankAccount;
import com.example.ck_mobile.repository.AccountRepository;
import com.example.ck_mobile.ui.base.BaseSecureActivity;
import com.example.ck_mobile.utils.AuthSessionHelper;
import com.example.ck_mobile.utils.FinancialCalculator;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class CustomerAccountsActivity extends BaseSecureActivity {

    public static final String EXTRA_ACCOUNT_ID = "extra_account_id";
    public static final String EXTRA_ACCOUNT_DATA = "extra_account_data";
    public static final String EXTRA_CHECKING_ACCOUNT = "extra_checking_account";

    private TextView tvNetWorthBalance;
    private TextView tvTotalDeposit;
    private TextView tvActiveDepositCount;
    private TextView tvTotalDebt;
    private TextView tvMortgageCountLabel;
    private TextView tvAccountsCountBadge;
    private TextView tvToggleBalance;

    private MaterialCardView cardChecking;
    private MaterialCardView cardSaving;
    private MaterialCardView cardMortgage;

    private TextView tvCheckingNumber;
    private TextView tvCheckingBalance;
    private TextView tvSavingNumber;
    private TextView tvSavingBalance;
    private TextView tvSavingRateBadge;
    private TextView tvSavingTerm;
    private TextView tvSavingMaturity;
    private TextView tvMortgageContractId;
    private TextView tvMortgageNextDue;
    private TextView tvMortgageDebt;
    private TextView tvMortgageProgressPercent;
    private TextView tvMortgagePaidAmount;
    private TextView tvMortgagePrincipalAmount;
    private com.google.android.material.progressindicator.LinearProgressIndicator progressMortgagePaid;

    private AccountRepository accountRepository;
    private final List<BankAccount> allAccounts = new ArrayList<>();
    private BankAccount checkingAccount;
    private BankAccount savingAccount;
    private BankAccount mortgageAccount;

    private boolean isBalanceVisible = true;
    private String currentFilter = "ALL"; // ALL, DEPOSIT, MORTGAGE

    @Override
    protected String getRequiredRole() {
        return AuthSessionHelper.ROLE_CUSTOMER;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_customer_accounts);

        accountRepository = new AccountRepository();
        bindViews();
        bindActions();
        bindBottomNav();
        loadAccounts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (auth.getCurrentUser() != null) {
            loadAccounts();
        }
    }

    private void bindViews() {
        tvNetWorthBalance = findViewById(R.id.tv_net_worth_balance);
        tvTotalDeposit = findViewById(R.id.tv_total_deposit);
        tvActiveDepositCount = findViewById(R.id.tv_active_deposit_count);
        tvTotalDebt = findViewById(R.id.tv_total_debt);
        tvMortgageCountLabel = findViewById(R.id.tv_mortgage_count_label);
        tvAccountsCountBadge = findViewById(R.id.tv_accounts_count_badge);
        tvToggleBalance = findViewById(R.id.tv_toggle_balance);

        cardChecking = findViewById(R.id.card_account_checking);
        cardSaving = findViewById(R.id.card_account_saving);
        cardMortgage = findViewById(R.id.card_account_mortgage);

        tvCheckingNumber = findViewById(R.id.tv_checking_account_number);
        tvCheckingBalance = findViewById(R.id.tv_checking_balance);

        tvSavingNumber = findViewById(R.id.tv_saving_account_number);
        tvSavingBalance = findViewById(R.id.tv_saving_balance);
        tvSavingRateBadge = findViewById(R.id.tv_saving_rate_badge);
        tvSavingTerm = findViewById(R.id.tv_saving_term);
        tvSavingMaturity = findViewById(R.id.tv_saving_maturity_date);

        tvMortgageContractId = findViewById(R.id.tv_mortgage_contract_id);
        tvMortgageNextDue = findViewById(R.id.tv_mortgage_next_due);
        tvMortgageDebt = findViewById(R.id.tv_mortgage_debt);
        tvMortgageProgressPercent = findViewById(R.id.tv_mortgage_progress_percent);
        tvMortgagePaidAmount = findViewById(R.id.tv_mortgage_paid_amount);
        tvMortgagePrincipalAmount = findViewById(R.id.tv_mortgage_principal_amount);
        progressMortgagePaid = findViewById(R.id.progress_mortgage_paid);
    }

    private void bindActions() {
        View btnToggleBalance = findViewById(R.id.btn_toggle_balance);
        if (btnToggleBalance != null) {
            btnToggleBalance.setOnClickListener(v -> {
                isBalanceVisible = !isBalanceVisible;
                renderSummaryAndCards();
            });
        }

        ImageButton btnFilter = findViewById(R.id.btn_filter_accounts);
        if (btnFilter != null) {
            btnFilter.setOnClickListener(this::showFilterMenu);
        }

        ImageButton btnCopyChecking = findViewById(R.id.btn_copy_checking);
        if (btnCopyChecking != null) {
            btnCopyChecking.setOnClickListener(v -> {
                if (checkingAccount != null) {
                    copyToClipboard("Số tài khoản thanh toán", checkingAccount.getAccountNumber());
                }
            });
        }

        ImageButton btnCopySaving = findViewById(R.id.btn_copy_saving);
        if (btnCopySaving != null) {
            btnCopySaving.setOnClickListener(v -> {
                if (savingAccount != null) {
                    copyToClipboard("Số tài khoản tiết kiệm", savingAccount.getAccountNumber());
                }
            });
        }

        if (cardChecking != null) {
            cardChecking.setOnClickListener(v -> {
                Intent intent = new Intent(this, CheckingDetailActivity.class);
                if (checkingAccount != null) {
                    intent.putExtra(EXTRA_ACCOUNT_ID, checkingAccount.getAccountId());
                    intent.putExtra(EXTRA_ACCOUNT_DATA, checkingAccount);
                }
                startActivity(intent);
            });
        }

        if (cardSaving != null) {
            cardSaving.setOnClickListener(v -> {
                Intent intent = new Intent(this, SavingDetailActivity.class);
                if (savingAccount != null) {
                    intent.putExtra(EXTRA_ACCOUNT_ID, savingAccount.getAccountId());
                    intent.putExtra(EXTRA_ACCOUNT_DATA, savingAccount);
                }
                if (checkingAccount != null) {
                    intent.putExtra(EXTRA_CHECKING_ACCOUNT, checkingAccount);
                }
                startActivity(intent);
            });
        }

        if (cardMortgage != null) {
            cardMortgage.setOnClickListener(v -> {
                Intent intent = new Intent(this, MortgageDetailActivity.class);
                if (mortgageAccount != null) {
                    intent.putExtra(EXTRA_ACCOUNT_ID, mortgageAccount.getAccountId());
                    intent.putExtra(EXTRA_ACCOUNT_DATA, mortgageAccount);
                }
                if (checkingAccount != null) {
                    intent.putExtra(EXTRA_CHECKING_ACCOUNT, checkingAccount);
                }
                startActivity(intent);
            });
        }

        View btnTransfer = findViewById(R.id.btn_checking_transfer);
        if (btnTransfer != null) {
            btnTransfer.setOnClickListener(v -> AuthSessionHelper.showDevelopingToast(this));
        }

        MaterialButton btnOpenSaving = findViewById(R.id.btn_open_saving_account);
        if (btnOpenSaving != null) {
            btnOpenSaving.setOnClickListener(v -> AuthSessionHelper.showDevelopingToast(this));
        }

        View btnNotif = findViewById(R.id.btn_accounts_notifications);
        if (btnNotif != null) {
            btnNotif.setOnClickListener(v -> AuthSessionHelper.showDevelopingToast(this));
        }

        View avatar = findViewById(R.id.card_top_avatar);
        if (avatar != null) {
            avatar.setOnClickListener(v -> {
                FirebaseUser user = auth.getCurrentUser();
                showLogoutConfirmDialog(user != null ? user.getEmail() : null);
            });
        }
    }

    private void bindBottomNav() {
        View navHome = findViewById(R.id.nav_cust_home);
        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                if (isTaskRoot()) {
                    startActivity(new Intent(this, CustomerMainActivity.class));
                }
                finish();
            });
        }

        int[] devNavIds = {
                R.id.nav_cust_utilities,
                R.id.nav_cust_chatbot,
                R.id.nav_cust_profile
        };
        View.OnClickListener devNavListener = v -> AuthSessionHelper.showDevelopingToast(this);
        for (int id : devNavIds) {
            View target = findViewById(id);
            if (target != null) {
                target.setOnClickListener(devNavListener);
            }
        }
    }

    private void showFilterMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenu().add(0, 1, 0, "Tất cả tài khoản");
        popup.getMenu().add(0, 2, 1, "Tiền gửi (Checking & Saving)");
        popup.getMenu().add(0, 3, 2, "Khoản vay thế chấp");
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == 2) {
                currentFilter = "DEPOSIT";
            } else if (item.getItemId() == 3) {
                currentFilter = "MORTGAGE";
            } else {
                currentFilter = "ALL";
            }
            renderSummaryAndCards();
            return true;
        });
        popup.show();
    }

    private void loadAccounts() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        accountRepository.getCustomerAccounts(user.getUid(), new AccountRepository.AccountsCallback() {
            @Override
            public void onSuccess(List<BankAccount> accounts) {
                if (isFinishing() || isDestroyed()) return;
                allAccounts.clear();
                if (accounts != null) {
                    allAccounts.addAll(accounts);
                }

                checkingAccount = null;
                savingAccount = null;
                mortgageAccount = null;

                for (BankAccount acc : allAccounts) {
                    if (BankAccount.TYPE_CHECKING.equalsIgnoreCase(acc.getAccountType())) {
                        checkingAccount = acc;
                    } else if (BankAccount.TYPE_SAVING.equalsIgnoreCase(acc.getAccountType())) {
                        savingAccount = acc;
                    } else if (BankAccount.TYPE_MORTGAGE.equalsIgnoreCase(acc.getAccountType())) {
                        mortgageAccount = acc;
                    }
                }
                renderSummaryAndCards();
            }

            @Override
            public void onError(String message) {
                // Bỏ qua lỗi khi chưa có dữ liệu để không chặn điều hướng
            }
        });
    }

    private void renderSummaryAndCards() {
        if (allAccounts.isEmpty() && checkingAccount == null && savingAccount == null && mortgageAccount == null) {
            return;
        }

        double checkingBal = checkingAccount != null ? checkingAccount.getBalance() : 0;
        double savingBal = savingAccount != null ? savingAccount.getBalance() : 0;
        double totalDeposit = checkingBal + savingBal;
        double totalDebt = mortgageAccount != null ? mortgageAccount.getBalance() : 0;

        int depositCount = (checkingAccount != null ? 1 : 0) + (savingAccount != null ? 1 : 0);
        int mortgageCount = mortgageAccount != null ? 1 : 0;

        if (tvToggleBalance != null) {
            tvToggleBalance.setText(isBalanceVisible ? "Ẩn" : "Hiện");
        }

        if (isBalanceVisible) {
            if (tvNetWorthBalance != null) tvNetWorthBalance.setText(FinancialCalculator.formatCurrencyNumber(totalDeposit));
            if (tvTotalDeposit != null) tvTotalDeposit.setText(FinancialCalculator.formatVnd(totalDeposit));
            if (tvTotalDebt != null) tvTotalDebt.setText(FinancialCalculator.formatVnd(totalDebt));
        } else {
            if (tvNetWorthBalance != null) tvNetWorthBalance.setText("••••••••");
            if (tvTotalDeposit != null) tvTotalDeposit.setText("•••••••• đ");
            if (tvTotalDebt != null) tvTotalDebt.setText("•••••••• đ");
        }

        if (tvActiveDepositCount != null) tvActiveDepositCount.setText(depositCount + " tài khoản hoạt động");
        if (tvMortgageCountLabel != null) tvMortgageCountLabel.setText(mortgageCount + " khoản vay thế chấp");

        boolean showChecking = checkingAccount != null && !"MORTGAGE".equals(currentFilter);
        boolean showSaving = savingAccount != null && !"MORTGAGE".equals(currentFilter);
        boolean showMortgage = mortgageAccount != null && !"DEPOSIT".equals(currentFilter);

        int visibleCount = (showChecking ? 1 : 0) + (showSaving ? 1 : 0) + (showMortgage ? 1 : 0);
        if (tvAccountsCountBadge != null) tvAccountsCountBadge.setText(String.valueOf(visibleCount));

        if (cardChecking != null) cardChecking.setVisibility(showChecking ? View.VISIBLE : View.GONE);
        if (cardSaving != null) cardSaving.setVisibility(showSaving ? View.VISIBLE : View.GONE);
        if (cardMortgage != null) cardMortgage.setVisibility(showMortgage ? View.VISIBLE : View.GONE);

        if (checkingAccount != null) {
            if (tvCheckingNumber != null) tvCheckingNumber.setText(checkingAccount.getAccountNumber());
            if (tvCheckingBalance != null) {
                tvCheckingBalance.setText(isBalanceVisible
                        ? FinancialCalculator.formatCurrencyNumber(checkingAccount.getBalance())
                        : "••••••••");
            }
        }

        if (savingAccount != null) {
            if (tvSavingNumber != null) tvSavingNumber.setText(savingAccount.getAccountNumber());
            if (tvSavingBalance != null) {
                tvSavingBalance.setText(isBalanceVisible
                        ? FinancialCalculator.formatCurrencyNumber(savingAccount.getBalance())
                        : "••••••••");
            }
            if (tvSavingRateBadge != null) tvSavingRateBadge.setText("Lãi suất " + savingAccount.getInterestRate() + "%/năm");
            if (tvSavingTerm != null) tvSavingTerm.setText("Kỳ hạn: " + savingAccount.getTermMonths() + " tháng");
            if (tvSavingMaturity != null) tvSavingMaturity.setText("Đáo hạn: " + savingAccount.getMaturityDate());
        }

        if (mortgageAccount != null) {
            if (tvMortgageContractId != null) tvMortgageContractId.setText(mortgageAccount.getAccountNumber());
            if (tvMortgageNextDue != null) tvMortgageNextDue.setText("Kỳ tới: " + mortgageAccount.getNextDueDate());
            if (tvMortgageDebt != null) {
                tvMortgageDebt.setText(isBalanceVisible
                        ? FinancialCalculator.formatCurrencyNumber(mortgageAccount.getBalance())
                        : "••••••••");
            }

            double paid = Math.max(0, mortgageAccount.getInitialPrincipal() - mortgageAccount.getBalance());
            int pct = FinancialCalculator.calculateRepaymentProgressPercent(
                    mortgageAccount.getInitialPrincipal(), mortgageAccount.getBalance());

            if (progressMortgagePaid != null) progressMortgagePaid.setProgress(pct);
            if (tvMortgageProgressPercent != null) {
                tvMortgageProgressPercent.setText(pct + "% (" + FinancialCalculator.formatVnd(paid) + ")");
            }
            if (tvMortgagePaidAmount != null) tvMortgagePaidAmount.setText("Đã trả: " + FinancialCalculator.formatVnd(paid));
            if (tvMortgagePrincipalAmount != null) {
                tvMortgagePrincipalAmount.setText("Gốc vay: " + FinancialCalculator.formatVnd(mortgageAccount.getInitialPrincipal()));
            }
        }
    }

    private void copyToClipboard(String label, String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null && text != null) {
            clipboard.setPrimaryClip(ClipData.newPlainText(label, text.replace(" ", "")));
            Toast.makeText(this, "Đã sao chép " + label.toLowerCase(), Toast.LENGTH_SHORT).show();
        }
    }
}