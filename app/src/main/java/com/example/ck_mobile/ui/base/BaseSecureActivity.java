package com.example.ck_mobile.ui.base;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ck_mobile.ui.auth.LogoutConfirmDialogFragment;
import com.example.ck_mobile.utils.AuthSessionHelper;
import com.example.ck_mobile.utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public abstract class BaseSecureActivity extends AppCompatActivity {

    protected FirebaseAuth auth;
    protected FirebaseFirestore db;
    private boolean isRedirecting = false;

    protected abstract String getRequiredRole();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        isRedirecting = false;

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            redirectToLogin("Vui lòng đăng nhập để tiếp tục.");
            return;
        }

        SessionManager sessionManager = SessionManager.getInstance(this);
        if (sessionManager.isSessionExpired()) {
            sessionManager.logoutAndRedirectToLogin(SessionManager.REASON_TIMEOUT);
            isRedirecting = true;
            finish();
            return;
        }

        sessionManager.setTimeoutCallback(() -> {
            if (!isFinishing() && !isDestroyed() && !isRedirecting) {
                isRedirecting = true;
                sessionManager.logoutAndRedirectToLogin(SessionManager.REASON_TIMEOUT);
                finish();
            }
        });
        sessionManager.touchActivity();

        String uid = currentUser.getUid();
        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(this, this::verifyUserAccess)
                .addOnFailureListener(this, e ->
                        redirectToLogin("Không thể xác thực quyền truy cập."));
    }

    private void redirectToLogin(String message) {
        if (isRedirecting || isFinishing() || isDestroyed()) {
            return;
        }
        isRedirecting = true;
        SessionManager.getInstance(this).cancelTimer();
        AuthSessionHelper.signOutAndGoLogin(this, message);
        finish();
    }

    private void verifyUserAccess(DocumentSnapshot snapshot) {
        if (isRedirecting || isFinishing() || isDestroyed()) {
            return;
        }
        if (snapshot == null || !snapshot.exists()) {
            redirectToLogin("Tài khoản chưa được cấp hồ sơ.");
            return;
        }

        String status = snapshot.getString("status");
        if (status != null && AuthSessionHelper.STATUS_LOCKED.equalsIgnoreCase(status.trim())) {
            redirectToLogin("Tài khoản đang bị khóa.");
            return;
        }

        String role = snapshot.getString("role");
        if (role == null || role.trim().isEmpty()) {
            redirectToLogin("Tài khoản thiếu thông tin phân quyền.");
            return;
        }

        String normalizedRole = role.trim().toUpperCase(Locale.ROOT);
        String requiredRole = getRequiredRole() == null ? "" : getRequiredRole().trim().toUpperCase(Locale.ROOT);

        if (!requiredRole.equals(normalizedRole)) {
            redirectToLogin("Bạn không có quyền truy cập màn hình này.");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SessionManager.getInstance(this).setTimeoutCallback(null);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        View contentView = findViewById(android.R.id.content);
        if (contentView != null) {
            AuthSessionHelper.applySystemBarInsets(contentView);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            SessionManager.getInstance(this).touchActivity();
        }
        return super.dispatchTouchEvent(ev);
    }

    protected void showLogoutConfirmDialog(@Nullable String email) {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        if (getSupportFragmentManager().findFragmentByTag(LogoutConfirmDialogFragment.TAG) != null) {
            return;
        }
        LogoutConfirmDialogFragment dialog = LogoutConfirmDialogFragment.newInstance(email);
        dialog.setOnLogoutConfirmedListener(() -> {
            if (!isFinishing() && !isDestroyed() && !isRedirecting) {
                isRedirecting = true;
                SessionManager.getInstance(this).logoutAndRedirectToLogin(null);
                finish();
            }
        });
        dialog.show(getSupportFragmentManager(), LogoutConfirmDialogFragment.TAG);
    }
}