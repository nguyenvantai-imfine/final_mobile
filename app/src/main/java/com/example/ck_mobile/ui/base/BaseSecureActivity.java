package com.example.ck_mobile.ui.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ck_mobile.ui.auth.LogoutConfirmDialogFragment;
import com.example.ck_mobile.utils.AuthSessionHelper;
import com.example.ck_mobile.utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.view.MotionEvent;
import android.view.View;
import com.example.ck_mobile.utils.AuthSessionHelper;

import java.util.Locale;

public abstract class BaseSecureActivity extends AppCompatActivity {

    protected FirebaseAuth auth;
    protected FirebaseFirestore db;

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
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            AuthSessionHelper.signOutAndGoLogin(this, "Vui lòng đăng nhập để tiếp tục.");
            return;
        }

        String uid = currentUser.getUid();
        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(this, snapshot -> verifyUserAccess(snapshot))
                .addOnFailureListener(this, e ->
                        AuthSessionHelper.signOutAndGoLogin(this, "Không thể xác thực quyền truy cập."));

        SessionManager sessionManager = SessionManager.getInstance(this);
        if (sessionManager.wasRemovedFromRecents() || sessionManager.isSessionExpired()) {
            String reason = sessionManager.wasRemovedFromRecents()
                    ? SessionManager.REASON_TASK_REMOVED
                    : SessionManager.REASON_TIMEOUT;
            sessionManager.logoutAndRedirectToLogin(reason);
            return;
        }
        sessionManager.setTimeoutCallback(() ->
                sessionManager.logoutAndRedirectToLogin(SessionManager.REASON_TIMEOUT)
        );
        sessionManager.touchActivity();
    }

    private void verifyUserAccess(DocumentSnapshot snapshot) {
        if (snapshot == null || !snapshot.exists()) {
            AuthSessionHelper.signOutAndGoLogin(this, "Tài khoản chưa được cấp hồ sơ.");
            return;
        }

        String status = snapshot.getString("status");
        if (status != null && AuthSessionHelper.STATUS_LOCKED.equalsIgnoreCase(status.trim())) {
            AuthSessionHelper.signOutAndGoLogin(this, "Tài khoản đang bị khóa.");
            return;
        }

        String role = snapshot.getString("role");
        if (role == null || role.trim().isEmpty()) {
            AuthSessionHelper.signOutAndGoLogin(this, "Tài khoản thiếu thông tin phân quyền.");
            return;
        }

        String normalizedRole = role.trim().toUpperCase(Locale.ROOT);
        String requiredRole = getRequiredRole() == null ? "" : getRequiredRole().trim().toUpperCase(Locale.ROOT);

        if (!requiredRole.equals(normalizedRole)) {
            AuthSessionHelper.signOutAndGoLogin(this, "Bạn không có quyền truy cập màn hình này.");
        }
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
        LogoutConfirmDialogFragment dialog = LogoutConfirmDialogFragment.newInstance(email);
        dialog.setOnLogoutConfirmedListener(() ->
                SessionManager.getInstance(this).logoutAndRedirectToLogin(null)
        );
        dialog.show(getSupportFragmentManager(), LogoutConfirmDialogFragment.TAG);
    }
}