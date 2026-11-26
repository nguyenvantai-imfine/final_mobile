package com.example.ck_mobile.ui.customer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.ck_mobile.databinding.ActivityInterestRateViewBinding;
import com.example.ck_mobile.databinding.ItemRateViewBinding;
import com.example.ck_mobile.model.InterestRate;
import com.example.ck_mobile.repository.RateRepository;
import com.example.ck_mobile.repository.RepoCallback;
import com.example.ck_mobile.ui.base.BaseSecureActivity;
import com.example.ck_mobile.utils.AuthSessionHelper;

import java.util.List;
import java.util.Locale;

/**
 * T28 (phía Customer): xem lãi suất tiết kiệm mới nhất do Officer cấu hình.
 * Mở từ nút deep link của chatbot; màn hình quản lý tài khoản tiết kiệm có thể mở bằng
 * startActivity(new Intent(this, InterestRateViewActivity.class)).
 */
public class InterestRateViewActivity extends BaseSecureActivity {

    @Override
    protected String getRequiredRole() {
        return AuthSessionHelper.ROLE_CUSTOMER;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityInterestRateViewBinding b = ActivityInterestRateViewBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        b.toolbar.setNavigationOnClickListener(v -> finish());

        b.progress.setVisibility(View.VISIBLE);
        new RateRepository().loadRates(new RepoCallback<List<InterestRate>>() {
            @Override
            public void onSuccess(List<InterestRate> rates) {
                b.progress.setVisibility(View.GONE);
                b.containerRates.removeAllViews();
                for (InterestRate r : rates) {
                    ItemRateViewBinding row = ItemRateViewBinding.inflate(
                            LayoutInflater.from(InterestRateViewActivity.this), b.containerRates, false);
                    row.tvTerm.setText("Kỳ hạn " + r.getTermMonths() + " tháng");
                    row.tvRate.setText(String.format(Locale.US, "%.2f%%", r.getRate()));
                    b.containerRates.addView(row.getRoot());
                }
            }

            @Override
            public void onError(String message) {
                b.progress.setVisibility(View.GONE);
                b.tvError.setText(message);
                b.tvError.setVisibility(View.VISIBLE);
            }
        });
    }
}
