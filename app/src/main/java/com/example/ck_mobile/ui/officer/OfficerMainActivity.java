package com.example.ck_mobile.ui.officer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.ck_mobile.R;
import com.example.ck_mobile.ui.base.BaseSecureActivity;
import com.example.ck_mobile.utils.AuthSessionHelper;

import java.util.Locale;

public class OfficerMainActivity extends BaseSecureActivity {

    private TextView tvSessionTimer;
    private TextView tvTicketTimer;
    private TextView tvTicketNumber;

    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private int sessionSeconds = 2 * 3600 + 34 * 60 + 18; // 02:34:18
    private int ticketSeconds = 3 * 60 + 45;             // 03:45s
    private int currentTicketNo = 12;

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            sessionSeconds++;
            ticketSeconds++;

            int sHours = sessionSeconds / 3600;
            int sMins = (sessionSeconds % 3600) / 60;
            int sSecs = sessionSeconds % 60;
            if (tvSessionTimer != null) {
                tvSessionTimer.setText(String.format(Locale.US, "%02d:%02d:%02d", sHours, sMins, sSecs));
            }

            int tMins = ticketSeconds / 60;
            int tSecs = ticketSeconds % 60;
            if (tvTicketTimer != null) {
                tvTicketTimer.setText(String.format(Locale.US, "%02d:%02ds", tMins, tSecs));
            }

            timerHandler.postDelayed(this, 1000);
        }
    };

    @Override
    protected String getRequiredRole() {
        return AuthSessionHelper.ROLE_OFFICER;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_officer);

        tvSessionTimer = findViewById(R.id.tv_session_timer);
        tvTicketTimer = findViewById(R.id.tv_ticket_timer);
        tvTicketNumber = findViewById(R.id.tv_ticket_number);

        bindOfficerActions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        timerHandler.removeCallbacks(timerRunnable);
        timerHandler.postDelayed(timerRunnable, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
    }

    private void bindOfficerActions() {
        // Nút bàn giao ca / đăng xuất -> signOut + FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK + finish()
        View btnLogout = findViewById(R.id.btn_officer_logout_switch);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v ->
                    AuthSessionHelper.signOutAndGoLogin(this, "Đã đăng xuất khỏi phiên làm việc."));
        }

        // Tương tác trực tiếp trên thẻ hàng đợi
        View btnCompleteTicket = findViewById(R.id.btn_complete_ticket);
        if (btnCompleteTicket != null) {
            btnCompleteTicket.setOnClickListener(v -> {
                ticketSeconds = 0;
                if (tvTicketTimer != null) {
                    tvTicketTimer.setText("00:00s");
                }
                AuthSessionHelper.showDevelopingToast(this);
            });
        }

        View btnCallNext = findViewById(R.id.btn_call_next_ticket);
        if (btnCallNext != null) {
            btnCallNext.setOnClickListener(v -> {
                currentTicketNo++;
                ticketSeconds = 0;
                if (tvTicketNumber != null) {
                    tvTicketNumber.setText(String.format(Locale.US, "A-%03d", currentTicketNo));
                }
                AuthSessionHelper.showDevelopingToast(this);
            });
        }

        // Các nút nghiệp vụ còn lại tạm hiển thị "Đang phát triển"
        int[] developingIds = {
                R.id.btn_officer_notifications,
                R.id.card_kpi_customers,
                R.id.card_kpi_ekyc,
                R.id.card_kpi_deposit,
                R.id.card_kpi_new_accounts,
                R.id.btn_officer_open_account,
                R.id.btn_officer_review_ekyc,
                R.id.btn_officer_biometric_reg,
                R.id.btn_officer_interest_rates,
                R.id.btn_view_all_ekyc,
                R.id.btn_approve_ekyc_1,
                R.id.btn_check_ekyc_2,
                R.id.btn_verify_ekyc_3,
                R.id.layout_shift_support,
                R.id.nav_off_overview,
                R.id.nav_off_customers,
                R.id.nav_off_ekyc,
                R.id.nav_off_profile_shift
        };

        View.OnClickListener devListener = v -> AuthSessionHelper.showDevelopingToast(this);
        for (int id : developingIds) {
            View target = findViewById(id);
            if (target != null) {
                target.setOnClickListener(devListener);
            }
        }

        // Các chức năng đã hoàn thiện (ghi đè lên trình xử lý "Đang phát triển")
        open(new int[]{R.id.btn_officer_open_account}, CustomerFormActivity.class);
        open(new int[]{R.id.card_kpi_customers, R.id.nav_off_customers}, CustomerListActivity.class);
        open(new int[]{R.id.btn_officer_review_ekyc, R.id.card_kpi_ekyc, R.id.btn_view_all_ekyc,
                R.id.nav_off_ekyc}, EkycListActivity.class);
        open(new int[]{R.id.btn_officer_biometric_reg}, EkycRegisterActivity.class);
        open(new int[]{R.id.btn_officer_interest_rates}, InterestRateActivity.class);
    }

    private void open(int[] viewIds, Class<?> target) {
        for (int id : viewIds) {
            View v = findViewById(id);
            if (v != null) {
                v.setOnClickListener(x -> startActivity(new Intent(this, target)));
            }
        }
    }
}
