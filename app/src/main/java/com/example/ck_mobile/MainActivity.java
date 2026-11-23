package com.example.ck_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ck_mobile.ui.auth.LoginActivity;
import com.example.ck_mobile.utils.AuthSessionHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY_MS = 1000L;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AuthSessionHelper.applySystemBarInsets(findViewById(android.R.id.content));

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        handler.postDelayed(this::routeStartupSession, SPLASH_DELAY_MS);
    }

    private void routeStartupSession() {
        if (isFinishing() || isDestroyed()) return;

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            goToLogin();
            return;
        }

        // Giữ phiên: đọc lại role từ Firestore và vào thẳng màn hình tương ứng
        db.collection("users")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(this, snapshot -> {
                    if (snapshot == null || !snapshot.exists()) {
                        AuthSessionHelper.signOutAndGoLogin(this, "Tài khoản chưa được cấp hồ sơ.");
                        return;
                    }

                    String status = snapshot.getString("status");
                    if (status != null && AuthSessionHelper.STATUS_LOCKED.equalsIgnoreCase(status.trim())) {
                        AuthSessionHelper.signOutAndGoLogin(this, "Tài khoản của bạn đang bị khóa.");
                        return;
                    }

                    String role = snapshot.getString("role");
                    if (role == null || role.trim().isEmpty()) {
                        AuthSessionHelper.signOutAndGoLogin(this, "Hồ sơ chưa được gán vai trò.");
                        return;
                    }

                    String normalizedRole = role.trim().toUpperCase(Locale.ROOT);
                    if (AuthSessionHelper.ROLE_CUSTOMER.equals(normalizedRole)
                            || AuthSessionHelper.ROLE_OFFICER.equals(normalizedRole)) {
                        AuthSessionHelper.navigateByRole(this, normalizedRole);
                    } else {
                        AuthSessionHelper.signOutAndGoLogin(this, "Vai trò tài khoản không hợp lệ.");
                    }
                })
                .addOnFailureListener(this, e ->
                        AuthSessionHelper.signOutAndGoLogin(this, "Không thể kiểm tra phiên đăng nhập."));
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}