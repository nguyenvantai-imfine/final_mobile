package com.example.ck_mobile.ui.officer;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.ck_mobile.databinding.ActivityCustomerFormBinding;
import com.example.ck_mobile.model.UserProfile;
import com.example.ck_mobile.repository.CustomerRepository;
import com.example.ck_mobile.repository.RepoCallback;
import com.example.ck_mobile.ui.base.BaseSecureActivity;
import com.example.ck_mobile.utils.AuthSessionHelper;
import com.example.ck_mobile.utils.Constants;
import com.example.ck_mobile.utils.UiText;

/**
 * T21 + T22: Officer tạo khách hàng mới (không có extra uid) hoặc sửa/khóa khách hàng (có extra uid).
 */
public class CustomerFormActivity extends BaseSecureActivity {

    private ActivityCustomerFormBinding b;
    private final CustomerRepository repo = new CustomerRepository();
    private UserProfile editing; // null = chế độ tạo mới

    @Override
    protected String getRequiredRole() {
        return AuthSessionHelper.ROLE_OFFICER;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityCustomerFormBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        b.toolbar.setNavigationOnClickListener(v -> finish());

        String uid = getIntent().getStringExtra(Constants.EXTRA_UID);
        if (uid == null) {
            setupCreateMode();
        } else {
            setupEditMode(uid);
        }
    }

    // ---------------- Tạo mới ----------------

    private void setupCreateMode() {
        b.btnSave.setOnClickListener(v -> submitCreate());
    }

    private void submitCreate() {
        clearErrors();
        String name = text(b.etName);
        String email = text(b.etEmail);
        String password = text(b.etPassword);
        String phoneRaw = text(b.etPhone);
        String address = text(b.etAddress);
        String balanceRaw = text(b.etBalance);

        boolean ok = true;
        if (name.isEmpty()) {
            b.tilName.setError("Vui lòng nhập họ tên.");
            ok = false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            b.tilEmail.setError("Email không hợp lệ.");
            ok = false;
        }
        if (password.length() < 6) {
            b.tilPassword.setError("Mật khẩu phải có ít nhất 6 ký tự.");
            ok = false;
        }
        String phone = AuthSessionHelper.normalizePhone(phoneRaw);
        if (phone.isEmpty()) {
            b.tilPhone.setError("Số điện thoại không hợp lệ.");
            ok = false;
        }
        long balance = 0;
        if (!balanceRaw.isEmpty()) {
            try {
                balance = Long.parseLong(balanceRaw);
                if (balance < 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                b.tilBalance.setError("Số dư không hợp lệ.");
                ok = false;
            }
        }
        if (!ok) return;

        setLoading(true);
        repo.createCustomer(this, name, email, password, phone, address, balance,
                new RepoCallback<UserProfile>() {
                    @Override
                    public void onSuccess(UserProfile result) {
                        setLoading(false);
                        new AlertDialog.Builder(CustomerFormActivity.this)
                                .setTitle("Tạo khách hàng thành công")
                                .setMessage("Số tài khoản: " + result.getAccountNumber()
                                        + "\nEmail: " + result.getEmail()
                                        + "\nKhách hàng có thể đăng nhập ngay.")
                                .setCancelable(false)
                                .setPositiveButton("Đóng", (d, w) -> finish())
                                .show();
                    }

                    @Override
                    public void onError(String message) {
                        setLoading(false);
                        Toast.makeText(CustomerFormActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                });
    }

    // ---------------- Sửa / khóa ----------------

    private void setupEditMode(String uid) {
        b.toolbar.setTitle("Thông tin khách hàng");
        b.tilPassword.setVisibility(View.GONE);
        b.tilEmail.setEnabled(false); // email gắn với tài khoản Auth, không sửa ở đây
        b.tilBalance.setVisibility(View.GONE);
        b.btnSave.setText("Lưu thay đổi");
        b.btnSave.setEnabled(false);
        b.btnLock.setVisibility(View.VISIBLE);
        b.btnLock.setEnabled(false);

        setLoading(true);
        repo.getCustomer(uid, new RepoCallback<UserProfile>() {
            @Override
            public void onSuccess(UserProfile u) {
                setLoading(false);
                editing = u;
                bindEditing();
            }

            @Override
            public void onError(String message) {
                setLoading(false);
                Toast.makeText(CustomerFormActivity.this, message, Toast.LENGTH_LONG).show();
                finish();
            }
        });

        b.btnSave.setOnClickListener(v -> submitUpdate());
        b.btnLock.setOnClickListener(v -> confirmToggleLock());
    }

    private void bindEditing() {
        b.etName.setText(editing.getFullName());
        b.etEmail.setText(editing.getEmail());
        b.etPhone.setText(editing.getPhone());
        b.etAddress.setText(editing.getAddress());
        b.tvAccountInfo.setVisibility(View.VISIBLE);
        b.tvAccountInfo.setText("STK: " + nz(editing.getAccountNumber()) + "\n" + UiText.kycLabel(editing.getKycStatus()));
        b.btnSave.setEnabled(true);
        b.btnLock.setEnabled(true);
        refreshLockButton();
    }

    private boolean isLocked() {
        return editing != null && Constants.STATUS_LOCKED.equalsIgnoreCase(editing.getStatus());
    }

    private void refreshLockButton() {
        b.btnLock.setText(isLocked() ? "Mở khóa tài khoản" : "Khóa tài khoản");
    }

    private void submitUpdate() {
        if (editing == null) return;
        clearErrors();
        String name = text(b.etName);
        String address = text(b.etAddress);
        String phone = AuthSessionHelper.normalizePhone(text(b.etPhone));
        boolean ok = true;
        if (name.isEmpty()) {
            b.tilName.setError("Vui lòng nhập họ tên.");
            ok = false;
        }
        if (phone.isEmpty()) {
            b.tilPhone.setError("Số điện thoại không hợp lệ.");
            ok = false;
        }
        if (!ok) return;

        setLoading(true);
        repo.updateCustomer(editing, name, phone, address, new RepoCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                setLoading(false);
                Toast.makeText(CustomerFormActivity.this, "Đã lưu thay đổi.", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String message) {
                setLoading(false);
                Toast.makeText(CustomerFormActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void confirmToggleLock() {
        if (editing == null) return;
        final boolean lock = !isLocked();
        new AlertDialog.Builder(this)
                .setTitle(lock ? "Khóa tài khoản?" : "Mở khóa tài khoản?")
                .setMessage(lock
                        ? "Khách hàng sẽ không thể đăng nhập cho đến khi được mở khóa."
                        : "Khách hàng sẽ đăng nhập được trở lại.")
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Xác nhận", (d, w) -> {
                    setLoading(true);
                    repo.setLocked(editing, lock, new RepoCallback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            setLoading(false);
                            editing.setStatus(lock ? Constants.STATUS_LOCKED : Constants.STATUS_ACTIVE);
                            refreshLockButton();
                            Toast.makeText(CustomerFormActivity.this,
                                    lock ? "Đã khóa tài khoản." : "Đã mở khóa tài khoản.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String message) {
                            setLoading(false);
                            Toast.makeText(CustomerFormActivity.this, message, Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .show();
    }

    // ---------------- helpers ----------------

    private void setLoading(boolean loading) {
        b.progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        b.btnSave.setEnabled(!loading && (editing != null || getIntent().getStringExtra(Constants.EXTRA_UID) == null));
        if (loading) AuthSessionHelper.hideKeyboard(this);
    }

    private void clearErrors() {
        b.tilName.setError(null);
        b.tilEmail.setError(null);
        b.tilPassword.setError(null);
        b.tilPhone.setError(null);
        b.tilBalance.setError(null);
    }

    private static String text(com.google.android.material.textfield.TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    private static String nz(String s) {
        return TextUtils.isEmpty(s) ? "—" : s;
    }
}
