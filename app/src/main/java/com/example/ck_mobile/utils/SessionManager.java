package com.example.ck_mobile.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ck_mobile.ui.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public final class SessionManager {

    public static final long SESSION_TIMEOUT_MS = 10L * 60L * 1000L; // 10 phút
    public static final String EXTRA_LOGOUT_REASON = "extra_logout_reason";
    public static final String REASON_TIMEOUT = "TIMEOUT";
    public static final String REASON_TASK_REMOVED = "TASK_REMOVED";

    private static final String PREF_NAME = "tdt_bank_session_prefs";
    private static final String KEY_LAST_ACTIVE_AT = "last_active_at";
    private static final String KEY_SESSION_ACTIVE = "session_active";

    private static volatile SessionManager instance;
    private final Context appContext;
    private final SharedPreferences prefs;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private Runnable timeoutRunnable;
    private TimeoutCallback timeoutCallback;

    public interface TimeoutCallback {
        void onSessionTimedOut();
    }

    private SessionManager(@NonNull Context context) {
        this.appContext = context.getApplicationContext();
        this.prefs = this.appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static SessionManager getInstance(@NonNull Context context) {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) {
                    instance = new SessionManager(context);
                }
            }
        }
        return instance;
    }

    public void setTimeoutCallback(@Nullable TimeoutCallback callback) {
        this.timeoutCallback = callback;
    }

    /** Gọi khi đăng nhập thành công */
    public void startSession() {
        long now = System.currentTimeMillis();
        prefs.edit()
                .putBoolean(KEY_SESSION_ACTIVE, true)
                .putLong(KEY_LAST_ACTIVE_AT, now)
                .apply();

        SessionWatchService.start(appContext);
        rescheduleTimer();
    }

    /** Cập nhật thời điểm hoạt động gần nhất (chạm màn hình / tương tác / vào foreground) */
    public void touchActivity() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            return;
        }
        prefs.edit()
                .putLong(KEY_LAST_ACTIVE_AT, System.currentTimeMillis())
                .apply();
        rescheduleTimer();
    }

    /** Kiểm tra đã quá 10 phút chưa */
    public boolean isSessionExpired() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            return false;
        }
        long lastActive = prefs.getLong(KEY_LAST_ACTIVE_AT, -1L);
        if (lastActive <= 0L) {
            return false;
        }
        return (System.currentTimeMillis() - lastActive) >= SESSION_TIMEOUT_MS;
    }

    /** Kiểm tra có bị xóa tab khỏi Recent Apps ở lần chạy trước hay không */
    public boolean wasRemovedFromRecents() {
        return FirebaseAuth.getInstance().getCurrentUser() != null
                && !prefs.getBoolean(KEY_SESSION_ACTIVE, false);
    }

    /** Đánh dấu khi người dùng vuốt xóa tab chương trình ở Recent Apps */
    public void onTaskRemovedFromRecents() {
        prefs.edit()
                .putBoolean(KEY_SESSION_ACTIVE, false)
                .commit();
        cancelTimer();
        FirebaseAuth.getInstance().signOut();
    }

    /** Đăng xuất chủ động hoặc do hết hạn / xóa tab và chuyển về LoginActivity */
    public void logoutAndRedirectToLogin(@Nullable String reason) {
        cancelTimer();
        prefs.edit()
                .putBoolean(KEY_SESSION_ACTIVE, false)
                .remove(KEY_LAST_ACTIVE_AT)
                .apply();

        SessionWatchService.stop(appContext);
        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(appContext, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if (reason != null) {
            intent.putExtra(EXTRA_LOGOUT_REASON, reason);
        }
        appContext.startActivity(intent);
    }

    public void rescheduleTimer() {
        cancelTimer();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            return;
        }
        long lastActive = prefs.getLong(KEY_LAST_ACTIVE_AT, System.currentTimeMillis());
        long elapsed = System.currentTimeMillis() - lastActive;
        long remaining = SESSION_TIMEOUT_MS - elapsed;

        if (remaining <= 0L) {
            triggerTimeoutNow();
            return;
        }

        timeoutRunnable = this::triggerTimeoutNow;
        mainHandler.postDelayed(timeoutRunnable, remaining);
    }

    private void triggerTimeoutNow() {
        if (timeoutCallback != null) {
            timeoutCallback.onSessionTimedOut();
        } else {
            logoutAndRedirectToLogin(REASON_TIMEOUT);
        }
    }

    public void cancelTimer() {
        if (timeoutRunnable != null) {
            mainHandler.removeCallbacks(timeoutRunnable);
            timeoutRunnable = null;
        }
    }
}