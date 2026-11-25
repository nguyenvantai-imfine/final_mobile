package com.example.ck_mobile.ui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.ck_mobile.R;
import com.example.ck_mobile.ui.base.BaseSecureActivity;
import com.example.ck_mobile.utils.AuthSessionHelper;
import com.example.ck_mobile.utils.Constants;
import com.google.firebase.auth.FirebaseUser;

public class CustomerMainActivity extends BaseSecureActivity {

    private TextView tvCustomerName;
    private TextView tvTotalBalance;
    private boolean isBalanceVisible = true;
    private static final String BALANCE_FORMATTED = "45.850.000";

    @Override
    protected String getRequiredRole() {
        return AuthSessionHelper.ROLE_CUSTOMER;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_customer);

        tvCustomerName = findViewById(R.id.tv_customer_name);
        tvTotalBalance = findViewById(R.id.tv_total_balance);

        loadCustomerInfoFromFirestore();
        bindCustomerActions();
    }

    private void loadCustomerInfoFromFirestore() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) return;

        String fallbackEmail = currentUser.getEmail() != null ? currentUser.getEmail() : "";
        db.collection("users")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(this, snapshot -> {
                    if (snapshot != null && snapshot.exists()) {
                        String fullName = snapshot.getString("fullName");
                        String email = snapshot.getString("email");
                        String displayName = (fullName != null && !fullName.trim().isEmpty())
                                ? fullName.trim()
                                : fallbackEmail;
                        String displayEmail = (email != null && !email.trim().isEmpty())
                                ? email.trim()
                                : fallbackEmail;

                        if (!displayName.isEmpty() && !displayEmail.isEmpty()) {
                            tvCustomerName.setText(displayName + " (" + displayEmail + ")");
                        } else if (!displayName.isEmpty()) {
                            tvCustomerName.setText(displayName);
                        }
                    }
                });
    }

    private void bindCustomerActions() {
        View btnToggleBalance = findViewById(R.id.btn_toggle_balance);
        if (btnToggleBalance != null) {
            btnToggleBalance.setOnClickListener(v -> {
                isBalanceVisible = !isBalanceVisible;
                tvTotalBalance.setText(isBalanceVisible ? BALANCE_FORMATTED : "••••••••");
            });
        }

        int[] developingIds = {
                R.id.btn_top_notifications,
                R.id.card_top_avatar,
                R.id.btn_hero_search,
                R.id.btn_hero_notification,
                R.id.btn_quick_deposit_withdraw,
                R.id.btn_quick_transfer,
                R.id.btn_quick_pay_bill,
                R.id.btn_quick_scan_qr,
                R.id.btn_manage_accounts,
                R.id.card_acc_checking,
                R.id.card_acc_saving,
                R.id.card_acc_mortgage,
                R.id.btn_all_utilities,
                R.id.util_electricity,
                R.id.util_water,
                R.id.util_topup_phone,
                R.id.util_movie_ticket,
                R.id.util_flight,
                R.id.util_hotel,
                R.id.util_shopping,
                R.id.util_branches,
                R.id.btn_all_transactions,
                R.id.tx_item_1,
                R.id.tx_item_2,
                R.id.tx_item_3,
                R.id.nav_cust_home,
                R.id.nav_cust_accounts,
                R.id.nav_cust_utilities,
                R.id.nav_cust_chatbot,
                R.id.nav_cust_profile
        };

        View.OnClickListener devListener = v -> AuthSessionHelper.showDevelopingToast(this);
        for (int id : developingIds) {
            View target = findViewById(id);
            if (target != null) {
                target.setOnClickListener(devListener);
            }
        }

        // Các chức năng đã hoàn thiện (ghi đè lên trình xử lý "Đang phát triển")
        open(new int[]{R.id.btn_all_transactions, R.id.tx_item_1, R.id.tx_item_2, R.id.tx_item_3},
                TransactionHistoryActivity.class, null);
        open(new int[]{R.id.nav_cust_chatbot}, ChatActivity.class, null);
        open(new int[]{R.id.util_movie_ticket}, ServiceOrderActivity.class, Constants.TX_MOVIE);
        open(new int[]{R.id.util_hotel}, ServiceOrderActivity.class, Constants.TX_HOTEL);
        open(new int[]{R.id.card_top_avatar}, KycStatusActivity.class, null);
    }

    private void open(int[] viewIds, Class<?> target, @Nullable String serviceType) {
        for (int id : viewIds) {
            View v = findViewById(id);
            if (v != null) {
                v.setOnClickListener(x -> {
                    Intent i = new Intent(this, target);
                    if (serviceType != null) i.putExtra(Constants.EXTRA_SERVICE_TYPE, serviceType);
                    startActivity(i);
                });
            }
        }
    }
}
