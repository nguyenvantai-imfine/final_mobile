package com.example.ck_mobile.ui.officer;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.ck_mobile.databinding.ActivityEkycReviewBinding;
import com.example.ck_mobile.model.KycRecord;
import com.example.ck_mobile.repository.KycRepository;
import com.example.ck_mobile.repository.RepoCallback;
import com.example.ck_mobile.ui.base.BaseSecureActivity;
import com.example.ck_mobile.utils.AuthSessionHelper;
import com.example.ck_mobile.utils.Constants;
import com.example.ck_mobile.utils.UiText;

/** T34: Officer xem ảnh và duyệt / từ chối hồ sơ eKYC. */
public class EkycReviewActivity extends BaseSecureActivity {

    private ActivityEkycReviewBinding b;
    private final KycRepository repo = new KycRepository();
    private KycRecord record;

    @Override
    protected String getRequiredRole() {
        return AuthSessionHelper.ROLE_OFFICER;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityEkycReviewBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        b.toolbar.setNavigationOnClickListener(v -> finish());
        b.layoutActions.setVisibility(View.GONE);

        String uid = getIntent().getStringExtra(Constants.EXTRA_UID);
        if (uid == null) {
            finish();
            return;
        }
        b.progress.setVisibility(View.VISIBLE);
        repo.get(uid, new RepoCallback<KycRecord>() {
            @Override
            public void onSuccess(KycRecord r) {
                record = r;
                bind();
                loadPhoto();
            }

            @Override
            public void onError(String message) {
                b.progress.setVisibility(View.GONE);
                Toast.makeText(EkycReviewActivity.this, message, Toast.LENGTH_LONG).show();
                finish();
            }
        });

        b.btnApprove.setOnClickListener(v -> submit(true));
        b.btnReject.setOnClickListener(v -> submit(false));
    }

    private void bind() {
        b.tvName.setText(record.getCustomerName());
        b.tvStatus.setText(UiText.kycLabel(record.getStatus()));
        b.etNote.setText(record.getNote());
        boolean pending = Constants.KYC_PENDING.equals(record.getStatus());
        b.layoutActions.setVisibility(pending ? View.VISIBLE : View.GONE);
        b.etNote.setEnabled(pending);
    }

    private void loadPhoto() {
        if (record.getPhotoPath() == null) {
            b.progress.setVisibility(View.GONE);
            return;
        }
        repo.loadPhoto(record.getPhotoPath(), new RepoCallback<android.graphics.Bitmap>() {
            @Override
            public void onSuccess(android.graphics.Bitmap bmp) {
                b.progress.setVisibility(View.GONE);
                b.ivPhoto.setImageBitmap(bmp);
            }

            @Override
            public void onError(String message) {
                b.progress.setVisibility(View.GONE);
                Toast.makeText(EkycReviewActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void submit(boolean approve) {
        String note = b.etNote.getText() == null ? "" : b.etNote.getText().toString().trim();
        if (!approve && note.isEmpty()) {
            b.tilNote.setError("Vui lòng nhập lý do từ chối.");
            return;
        }
        b.tilNote.setError(null);
        b.btnApprove.setEnabled(false);
        b.btnReject.setEnabled(false);
        repo.review(record, approve, note, new RepoCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(EkycReviewActivity.this,
                        approve ? "Đã duyệt hồ sơ." : "Đã từ chối hồ sơ.", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String message) {
                b.btnApprove.setEnabled(true);
                b.btnReject.setEnabled(true);
                Toast.makeText(EkycReviewActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
