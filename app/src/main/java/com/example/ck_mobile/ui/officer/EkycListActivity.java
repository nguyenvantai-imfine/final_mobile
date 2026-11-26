package com.example.ck_mobile.ui.officer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ck_mobile.R;
import com.example.ck_mobile.databinding.ActivityEkycListBinding;
import com.example.ck_mobile.model.KycRecord;
import com.example.ck_mobile.repository.KycRepository;
import com.example.ck_mobile.repository.RepoCallback;
import com.example.ck_mobile.ui.base.BaseSecureActivity;
import com.example.ck_mobile.ui.officer.adapter.KycAdapter;
import com.example.ck_mobile.utils.AuthSessionHelper;
import com.example.ck_mobile.utils.Constants;

import java.util.List;

/** T34: danh sách hồ sơ eKYC theo trạng thái, mở chi tiết để duyệt/từ chối. */
public class EkycListActivity extends BaseSecureActivity {

    private ActivityEkycListBinding b;
    private final KycRepository repo = new KycRepository();
    private KycAdapter adapter;

    @Override
    protected String getRequiredRole() {
        return AuthSessionHelper.ROLE_OFFICER;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityEkycListBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        b.toolbar.setNavigationOnClickListener(v -> finish());

        adapter = new KycAdapter(record -> {
            Intent i = new Intent(this, EkycReviewActivity.class);
            i.putExtra(Constants.EXTRA_UID, record.getId());
            startActivity(i);
        });
        b.rvKyc.setLayoutManager(new LinearLayoutManager(this));
        b.rvKyc.setAdapter(adapter);
        b.chipGroup.setOnCheckedStateChangeListener((group, ids) -> load());
    }

    @Override
    protected void onResume() {
        super.onResume();
        load();
    }

    private String currentFilter() {
        int id = b.chipGroup.getCheckedChipId();
        if (id == R.id.chip_pending) return Constants.KYC_PENDING;
        if (id == R.id.chip_approved) return Constants.KYC_APPROVED;
        if (id == R.id.chip_rejected) return Constants.KYC_REJECTED;
        return null;
    }

    private void load() {
        b.progress.setVisibility(View.VISIBLE);
        repo.list(currentFilter(), new RepoCallback<List<KycRecord>>() {
            @Override
            public void onSuccess(List<KycRecord> result) {
                b.progress.setVisibility(View.GONE);
                adapter.submit(result);
                b.tvEmpty.setVisibility(result.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(String message) {
                b.progress.setVisibility(View.GONE);
                Toast.makeText(EkycListActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
