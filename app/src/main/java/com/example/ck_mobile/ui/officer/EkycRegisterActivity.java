package com.example.ck_mobile.ui.officer;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.example.ck_mobile.databinding.ActivityEkycRegisterBinding;
import com.example.ck_mobile.model.UserProfile;
import com.example.ck_mobile.repository.CustomerRepository;
import com.example.ck_mobile.repository.KycRepository;
import com.example.ck_mobile.repository.RepoCallback;
import com.example.ck_mobile.ui.base.BaseSecureActivity;
import com.example.ck_mobile.utils.AuthSessionHelper;
import com.example.ck_mobile.utils.Constants;
import com.example.ck_mobile.utils.ImageUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** T33: Officer hỗ trợ đăng ký sinh trắc: chọn khách hàng, chụp ảnh, upload Storage, tạo hồ sơ kyc. */
public class EkycRegisterActivity extends BaseSecureActivity {

    private ActivityEkycRegisterBinding b;
    private final CustomerRepository customerRepo = new CustomerRepository();
    private final KycRepository kycRepo = new KycRepository();
    private final List<UserProfile> customers = new ArrayList<>();
    private UserProfile selected;
    private Uri photoUri;
    private boolean hasPhoto;

    private final ActivityResultLauncher<Uri> takePicture =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), ok -> {
                if (Boolean.TRUE.equals(ok) && photoUri != null) {
                    hasPhoto = true;
                    b.ivPreview.setImageURI(null); // buộc làm mới khi chụp lại
                    b.ivPreview.setImageURI(photoUri);
                } else {
                    Toast.makeText(this, "Chưa có ảnh được chụp.", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected String getRequiredRole() {
        return AuthSessionHelper.ROLE_OFFICER;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityEkycRegisterBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        b.toolbar.setNavigationOnClickListener(v -> finish());

        b.btnCapture.setOnClickListener(v -> capture());
        b.btnSubmit.setOnClickListener(v -> submit());
        loadCustomers();
    }

    private void loadCustomers() {
        b.progress.setVisibility(View.VISIBLE);
        final String preselect = getIntent().getStringExtra(Constants.EXTRA_UID);
        customerRepo.listCustomers(new RepoCallback<List<UserProfile>>() {
            @Override
            public void onSuccess(List<UserProfile> result) {
                b.progress.setVisibility(View.GONE);
                customers.clear();
                customers.addAll(result);
                List<String> labels = new ArrayList<>();
                for (UserProfile u : customers) {
                    labels.add(u.getFullName() + " - " + u.getPhone());
                    if (preselect != null && preselect.equals(u.getUid())) {
                        selected = u;
                        b.ddCustomer.setText(labels.get(labels.size() - 1), false);
                    }
                }
                b.ddCustomer.setAdapter(new ArrayAdapter<>(EkycRegisterActivity.this,
                        android.R.layout.simple_list_item_1, labels));
                b.ddCustomer.setOnItemClickListener((p, v, pos, id) -> selected = customers.get(pos));
            }

            @Override
            public void onError(String message) {
                b.progress.setVisibility(View.GONE);
                Toast.makeText(EkycRegisterActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void capture() {
        try {
            File dir = new File(getCacheDir(), "kyc");
            if (!dir.exists() && !dir.mkdirs()) throw new IOException("Không tạo được thư mục tạm.");
            File file = File.createTempFile("face_", ".jpg", dir);
            photoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
            takePicture.launch(photoUri);
        } catch (Exception e) {
            Toast.makeText(this, "Không mở được camera: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void submit() {
        if (selected == null) {
            Toast.makeText(this, "Vui lòng chọn khách hàng.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!hasPhoto || photoUri == null) {
            Toast.makeText(this, "Vui lòng chụp ảnh khuôn mặt.", Toast.LENGTH_SHORT).show();
            return;
        }
        final byte[] bytes;
        try {
            bytes = ImageUtils.toJpegBytes(getContentResolver(), photoUri, 1024, 80);
        } catch (IOException e) {
            Toast.makeText(this, "Không đọc được ảnh: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        b.progress.setVisibility(View.VISIBLE);
        b.btnSubmit.setEnabled(false);
        kycRepo.register(selected.getUid(), selected.getFullName(), bytes, new RepoCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                b.progress.setVisibility(View.GONE);
                Toast.makeText(EkycRegisterActivity.this, "Đã gửi hồ sơ, chờ duyệt.", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onError(String message) {
                b.progress.setVisibility(View.GONE);
                b.btnSubmit.setEnabled(true);
                Toast.makeText(EkycRegisterActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
