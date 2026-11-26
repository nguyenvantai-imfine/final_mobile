package com.example.ck_mobile.ui.officer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.ck_mobile.databinding.ActivityInterestRateBinding;
import com.example.ck_mobile.databinding.ItemRateRowBinding;
import com.example.ck_mobile.model.InterestRate;
import com.example.ck_mobile.repository.RateRepository;
import com.example.ck_mobile.repository.RepoCallback;
import com.example.ck_mobile.ui.base.BaseSecureActivity;
import com.example.ck_mobile.utils.AuthSessionHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** T28: Officer chỉnh lãi suất tiết kiệm (ghi Firestore + audit log). */
public class InterestRateActivity extends BaseSecureActivity {

    private ActivityInterestRateBinding b;
    private final RateRepository repo = new RateRepository();
    private final List<ItemRateRowBinding> rows = new ArrayList<>();
    private final List<InterestRate> loaded = new ArrayList<>();

    @Override
    protected String getRequiredRole() {
        return AuthSessionHelper.ROLE_OFFICER;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityInterestRateBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        b.toolbar.setNavigationOnClickListener(v -> finish());
        b.btnSave.setEnabled(false);
        b.btnSave.setOnClickListener(v -> save());
        load();
    }

    private void load() {
        b.progress.setVisibility(View.VISIBLE);
        repo.loadRates(new RepoCallback<List<InterestRate>>() {
            @Override
            public void onSuccess(List<InterestRate> result) {
                b.progress.setVisibility(View.GONE);
                loaded.clear();
                loaded.addAll(result);
                b.containerRates.removeAllViews();
                rows.clear();
                for (InterestRate r : result) {
                    ItemRateRowBinding row = ItemRateRowBinding.inflate(LayoutInflater.from(InterestRateActivity.this),
                            b.containerRates, false);
                    ((com.google.android.material.textfield.TextInputLayout) row.getRoot())
                            .setHint("Kỳ hạn " + r.getTermMonths() + " tháng");
                    row.etRate.setText(String.format(Locale.US, "%.2f", r.getRate()));
                    b.containerRates.addView(row.getRoot());
                    rows.add(row);
                }
                b.btnSave.setEnabled(true);
            }

            @Override
            public void onError(String message) {
                b.progress.setVisibility(View.GONE);
                Toast.makeText(InterestRateActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void save() {
        List<InterestRate> out = new ArrayList<>();
        boolean ok = true;
        for (int i = 0; i < rows.size(); i++) {
            ItemRateRowBinding row = rows.get(i);
            com.google.android.material.textfield.TextInputLayout til =
                    (com.google.android.material.textfield.TextInputLayout) row.getRoot();
            til.setError(null);
            String raw = row.etRate.getText() == null ? "" : row.etRate.getText().toString().trim().replace(',', '.');
            try {
                double v = Double.parseDouble(raw);
                if (v <= 0 || v > 20) throw new NumberFormatException();
                out.add(new InterestRate(loaded.get(i).getTermMonths(), v));
            } catch (NumberFormatException e) {
                til.setError("Nhập số trong khoảng (0; 20].");
                ok = false;
            }
        }
        if (!ok) return;

        AuthSessionHelper.hideKeyboard(this);
        b.btnSave.setEnabled(false);
        b.progress.setVisibility(View.VISIBLE);
        repo.saveRates(out, new RepoCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                b.progress.setVisibility(View.GONE);
                Toast.makeText(InterestRateActivity.this, "Đã cập nhật lãi suất.", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String message) {
                b.progress.setVisibility(View.GONE);
                b.btnSave.setEnabled(true);
                Toast.makeText(InterestRateActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
