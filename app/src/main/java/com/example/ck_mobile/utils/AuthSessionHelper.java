package com.example.ck_mobile.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.ck_mobile.ui.customer.CustomerMainActivity;
import com.example.ck_mobile.ui.auth.LoginActivity;
import com.example.ck_mobile.ui.officer.OfficerMainActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public final class AuthSessionHelper {

    public static final String ROLE_CUSTOMER = "CUSTOMER";
    public static final String ROLE_OFFICER = "OFFICER";
    public static final String STATUS_LOCKED = "LOCKED";

    private AuthSessionHelper() {
    }

    public static void hideKeyboard(Activity activity) {
        if (activity == null) return;
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public static void showDevelopingToast(Context context) {
        Toast.makeText(context, "Đang phát triển", Toast.LENGTH_SHORT).show();
    }

    public static boolean isEmail(String input) {
        return input != null && input.contains("@");
    }

    /**
     * Chuẩn hóa SĐT về dạng 0xxxxxxxxx (VD: +84901234567 -> 0901234567).
     * Trả về chuỗi rỗng nếu không hợp lệ.
     */
    public static String normalizePhone(String raw) {
        if (raw == null) return "";
        String cleaned = raw.trim().replaceAll("[\\s\\-().]", "");
        if (cleaned.startsWith("+84")) {
            cleaned = "0" + cleaned.substring(3);
        } else if (cleaned.startsWith("84") && cleaned.length() >= 10) {
            cleaned = "0" + cleaned.substring(2);
        }
        if (cleaned.matches("^0\\d{9,10}$")) {
            return cleaned;
        }
        return "";
    }

    /**
     * Chuẩn hóa Số tài khoản (6 - 20 chữ số).
     */
    public static String normalizeAccountNumber(String raw) {
        if (raw == null) return "";
        String cleaned = raw.trim().replaceAll("\\s+", "");
        if (cleaned.matches("^\\d{6,20}$")) {
            return cleaned;
        }
        return "";
    }

    public static void navigateByRole(Activity currentActivity, String role) {
        String normalizedRole = role == null ? "" : role.trim().toUpperCase(Locale.ROOT);
        Intent intent;
        if (ROLE_OFFICER.equals(normalizedRole)) {
            intent = new Intent(currentActivity, OfficerMainActivity.class);
        } else if (ROLE_CUSTOMER.equals(normalizedRole)) {
            intent = new Intent(currentActivity, CustomerMainActivity.class);
        } else {
            signOutAndGoLogin(currentActivity, "Vai trò tài khoản không hợp lệ.");
            return;
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        currentActivity.startActivity(intent);
        currentActivity.finish();
    }

    public static void signOutAndGoLogin(Activity currentActivity, String message) {
        FirebaseAuth.getInstance().signOut();
        if (message != null && !message.isEmpty()) {
            Toast.makeText(currentActivity, message, Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(currentActivity, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        currentActivity.startActivity(intent);
        currentActivity.finish();
    }

    public static void applySystemBarInsets(View rootView) {
        if (rootView == null) return;

        final int initialLeft = rootView.getPaddingLeft();
        final int initialTop = rootView.getPaddingTop();
        final int initialRight = rootView.getPaddingRight();
        final int initialBottom = rootView.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, windowInsets) -> {
            Insets bars = windowInsets.getInsets(
                    WindowInsetsCompat.Type.statusBars() | WindowInsetsCompat.Type.navigationBars()
            );
            v.setPadding(
                    initialLeft + bars.left,
                    initialTop + bars.top,
                    initialRight + bars.right,
                    initialBottom + bars.bottom
            );
            return windowInsets;
        });
        ViewCompat.requestApplyInsets(rootView);
    }
}