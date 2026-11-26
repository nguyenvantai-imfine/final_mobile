package com.example.ck_mobile.ui.auth;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ck_mobile.R;
import com.example.ck_mobile.utils.AuthSessionHelper;
import com.example.ck_mobile.utils.SessionManager;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION_MS = 30_000L;

    private EditText etEmail;
    private EditText etPassword;
    private ImageButton btnTogglePassword;
    private Button btnLoginSubmit;

    private View boxInputEmail;
    private View boxInputPassword;
    private TextView tvEmailErrorBadge;
    private TextView tvEmailLockedBadge;
    private TextView tvPasswordLockedBadge;

    private View cardLoginErrorBanner;
    private TextView tvLoginErrorMessage;

    private View cardLockoutAlert;
    private TextView tvLockoutAttemptsBadge;
    private TextView tvLockoutDescription;
    private LinearLayout layoutLockoutCountdown;
    private Button btnCountdownLock;
    private LinearProgressIndicator progressLockCountdown;
    private View layoutHeaderGreetingDefault;
    private View layoutHeaderGreetingLocked;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private boolean isPasswordVisible = false;
    private boolean isRequestInFlight = false;
    private boolean isTemporarilyLocked = false;
    private int failedAttempts = 0;
    private CountDownTimer lockCountDownTimer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AuthSessionHelper.applySystemBarInsets(findViewById(R.id.scroll_login_root));

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        bindViews();
        bindEvents();
        String logoutReason = getIntent().getStringExtra(SessionManager.EXTRA_LOGOUT_REASON);
        if (SessionManager.REASON_TIMEOUT.equals(logoutReason)) {
            showBannerError("Phiên làm việc đã hết hạn sau 10 phút. Vui lòng đăng nhập lại.");
        } else if (SessionManager.REASON_TASK_REMOVED.equals(logoutReason)) {
            showBannerError("Phiên đăng nhập đã kết thúc do ứng dụng bị đóng khỏi bộ nhớ tạm.");
        }
    }

    private void bindViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnTogglePassword = findViewById(R.id.btn_toggle_password);
        btnLoginSubmit = findViewById(R.id.btn_login_submit);

        boxInputEmail = findViewById(R.id.box_input_email);
        boxInputPassword = findViewById(R.id.box_input_password);
        tvEmailErrorBadge = findViewById(R.id.tv_email_error_badge);
        tvEmailLockedBadge = findViewById(R.id.tv_email_locked_badge);
        tvPasswordLockedBadge = findViewById(R.id.tv_password_locked_badge);

        cardLoginErrorBanner = findViewById(R.id.card_login_error_banner);
        tvLoginErrorMessage = findViewById(R.id.tv_login_error_message);

        cardLockoutAlert = findViewById(R.id.card_lockout_alert);
        tvLockoutAttemptsBadge = findViewById(R.id.tv_lockout_attempts_badge);
        tvLockoutDescription = findViewById(R.id.tv_lockout_description);
        layoutLockoutCountdown = findViewById(R.id.layout_lockout_countdown);
        btnCountdownLock = findViewById(R.id.btn_countdown_lock);
        progressLockCountdown = findViewById(R.id.progress_lock_countdown);
        layoutHeaderGreetingDefault = findViewById(R.id.layout_header_greeting_default);
        layoutHeaderGreetingLocked = findViewById(R.id.layout_header_greeting_locked);
    }

    private void bindEvents() {
        btnLoginSubmit.setOnClickListener(v -> attemptLogin());

        btnTogglePassword.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                btnTogglePassword.setAlpha(1f);
            } else {
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                btnTogglePassword.setAlpha(0.75f);
            }
            etPassword.setSelection(etPassword.getText().length());
        });

        View.OnClickListener devListener = v -> AuthSessionHelper.showDevelopingToast(this);
        int[] devIds = {
                R.id.btn_lang_switch,
                R.id.btn_header_hotline,
                R.id.tv_forgot_password,
                R.id.btn_biometric,
                R.id.tv_open_ekyc
        };
        for (int id : devIds) {
            View target = findViewById(id);
            if (target != null) {
                target.setOnClickListener(devListener);
            }
        }
    }

    private void attemptLogin() {
        if (isTemporarilyLocked || isRequestInFlight) {
            return;
        }

        AuthSessionHelper.hideKeyboard(this);
        clearErrorState();

        String rawIdentifier = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        // 1. Validate input
        if (TextUtils.isEmpty(rawIdentifier)) {
            showInputError(etEmail, "Vui lòng nhập Email, SĐT hoặc Số tài khoản.");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            showInputError(etPassword, "Vui lòng nhập mật khẩu.");
            return;
        }

        String normalizedPhone = AuthSessionHelper.normalizePhone(rawIdentifier);
        String normalizedAccount = AuthSessionHelper.normalizeAccountNumber(rawIdentifier);

        if (!AuthSessionHelper.isEmail(rawIdentifier)
                && normalizedPhone.isEmpty()
                && normalizedAccount.isEmpty()) {
            showInputError(etEmail, "Định dạng Email, SĐT hoặc Số tài khoản không hợp lệ.");
            return;
        }

        if (password.length() < 6) {
            showInputError(etPassword, "Mật khẩu phải có ít nhất 6 ký tự.");
            return;
        }

        setLoading(true);

        if (AuthSessionHelper.isEmail(rawIdentifier)) {
            signInWithEmail(rawIdentifier.toLowerCase(Locale.ROOT), password);
        } else if (!normalizedPhone.isEmpty()) {
            lookupEmailAndSignIn(
                    "phone_" + normalizedPhone,
                    password,
                    "Số điện thoại chưa được liên kết tài khoản."
            );
        } else {
            lookupEmailAndSignIn(
                    "acc_" + normalizedAccount,
                    password,
                    "Số tài khoản chưa tồn tại trên hệ thống."
            );
        }
    }

    private void lookupEmailAndSignIn(String lookupDocId, String password, String notFoundMsg) {
        db.collection("login_lookup")
                .document(lookupDocId)
                .get()
                .addOnSuccessListener(this, doc -> {
                    if (doc == null || !doc.exists()) {
                        setLoading(false);
                        recordFailedAttempt(notFoundMsg, false);
                        return;
                    }

                    String status = doc.getString("status");
                    if (status != null && AuthSessionHelper.STATUS_LOCKED.equalsIgnoreCase(status.trim())) {
                        setLoading(false);
                        showBannerError("Tài khoản đang bị khóa.");
                        return;
                    }

                    String email = doc.getString("email");
                    if (email == null || email.trim().isEmpty()) {
                        setLoading(false);
                        showBannerError("Không tìm thấy thông tin đăng nhập cho định danh này.");
                        return;
                    }

                    signInWithEmail(email.trim(), password);
                })
                .addOnFailureListener(this, e -> {
                    setLoading(false);
                    if (e instanceof FirebaseNetworkException) {
                        showBannerError("Không có kết nối mạng. Vui lòng kiểm tra lại Internet.");
                    } else {
                        showBannerError("Không thể tra cứu thông tin đăng nhập. Vui lòng thử lại.");
                    }
                });
    }

    private void signInWithEmail(String email, String password) {
        // 2. signInWithEmailAndPassword
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(this, result -> {
                    FirebaseUser user = result.getUser();
                    if (user == null) {
                        setLoading(false);
                        showBannerError("Không thể xác thực phiên đăng nhập.");
                        return;
                    }
                    // 3. Lấy UID, đọc users/{uid} trong Firestore
                    fetchUserProfileAndRoute(user.getUid());
                })
                .addOnFailureListener(this, e -> {
                    setLoading(false);
                    if (e instanceof FirebaseNetworkException) {
                        showBannerError("Lỗi kết nối mạng. Vui lòng thử lại.");
                    } else if (e instanceof FirebaseAuthInvalidUserException
                            || e instanceof FirebaseAuthInvalidCredentialsException) {
                        recordFailedAttempt("Email hoặc mật khẩu không đúng.", true);
                    } else {
                        recordFailedAttempt("Email hoặc mật khẩu không đúng.", true);
                    }
                });
    }

    private void fetchUserProfileAndRoute(String uid) {
        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(this, (DocumentSnapshot snapshot) -> {
                    // 4. Kiểm tra tồn tại, có role, status không phải LOCKED
                    if (snapshot == null || !snapshot.exists()) {
                        auth.signOut();
                        setLoading(false);
                        showBannerError("Tài khoản chưa được cấp hồ sơ.");
                        return;
                    }

                    String status = snapshot.getString("status");
                    if (status != null && AuthSessionHelper.STATUS_LOCKED.equalsIgnoreCase(status.trim())) {
                        auth.signOut();
                        setLoading(false);
                        showBannerError("Tài khoản của bạn đang bị khóa.");
                        return;
                    }

                    String role = snapshot.getString("role");
                    if (role == null || role.trim().isEmpty()) {
                        auth.signOut();
                        setLoading(false);
                        showBannerError("Hồ sơ chưa được gán vai trò (role).");
                        return;
                    }

                    String normalizedRole = role.trim().toUpperCase(Locale.ROOT);
                    if (!AuthSessionHelper.ROLE_CUSTOMER.equals(normalizedRole)
                            && !AuthSessionHelper.ROLE_OFFICER.equals(normalizedRole)) {
                        auth.signOut();
                        setLoading(false);
                        showBannerError("Vai trò không hợp lệ: " + normalizedRole);
                        return;
                    }

                    // 5. Điều hướng theo role
                    failedAttempts = 0;
                    setLoading(false);
                    SessionManager.getInstance(this).startSession();
                    AuthSessionHelper.navigateByRole(this, normalizedRole);
                })
                .addOnFailureListener(this, e -> {
                    auth.signOut();
                    setLoading(false);
                    if (e instanceof FirebaseNetworkException) {
                        showBannerError("Lỗi kết nối mạng khi đọc hồ sơ.");
                    } else {
                        showBannerError("Không đọc được hồ sơ người dùng.");
                    }
                });
    }

    private void recordFailedAttempt(String baseMessage, boolean countTowardsLockout) {
        if (!countTowardsLockout) {
            showBannerError(baseMessage);
            return;
        }

        failedAttempts++;
        if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
            startTemporaryLockout();
            return;
        }

        int remaining = MAX_FAILED_ATTEMPTS - failedAttempts;
        showBannerError(baseMessage + ". Bạn còn " + remaining + " lần thử trước khi tài khoản bị tạm khóa.");
    }

    private void startTemporaryLockout() {
        isTemporarilyLocked = true;
        btnLoginSubmit.setVisibility(View.GONE);
        layoutLockoutCountdown.setVisibility(View.VISIBLE);
        cardLockoutAlert.setVisibility(View.VISIBLE);
        layoutHeaderGreetingDefault.setVisibility(View.GONE);
        layoutHeaderGreetingLocked.setVisibility(View.VISIBLE);

        if (tvEmailLockedBadge != null) tvEmailLockedBadge.setVisibility(View.VISIBLE);
        if (tvPasswordLockedBadge != null) tvPasswordLockedBadge.setVisibility(View.VISIBLE);
        if (boxInputEmail != null) boxInputEmail.setBackgroundResource(R.drawable.bg_input_locker);
        if (boxInputPassword != null) boxInputPassword.setBackgroundResource(R.drawable.bg_input_locker);

        etEmail.setEnabled(false);
        etPassword.setEnabled(false);
        btnTogglePassword.setEnabled(false);

        if (tvLockoutAttemptsBadge != null) {
            tvLockoutAttemptsBadge.setText(failedAttempts + "/" + MAX_FAILED_ATTEMPTS + " lần lỗi");
        }
        if (tvLockoutDescription != null) {
            tvLockoutDescription.setText(
                    "Bạn đã nhập sai thông tin quá " + MAX_FAILED_ATTEMPTS
                            + " lần liên tiếp. Hệ thống tạm thời khóa tính năng đăng nhập trong 30 giây để bảo vệ an toàn tài khoản của bạn."
            );
        }

        if (progressLockCountdown != null) {
            progressLockCountdown.setMax(30);
            progressLockCountdown.setProgress(30);
        }

        if (lockCountDownTimer != null) {
            lockCountDownTimer.cancel();
        }

        lockCountDownTimer = new CountDownTimer(LOCKOUT_DURATION_MS, 1000L) {
            @Override
            public void onTick(long millisUntilFinished) {
                int secondsLeft = (int) Math.ceil(millisUntilFinished / 1000.0);
                if (btnCountdownLock != null) {
                    btnCountdownLock.setText(String.format(Locale.US, "Vui lòng thử lại sau 00:%02d", secondsLeft));
                }
                if (progressLockCountdown != null) {
                    progressLockCountdown.setProgress(secondsLeft);
                }
            }

            @Override
            public void onFinish() {
                endTemporaryLockout();
            }
        }.start();
    }

    private void endTemporaryLockout() {
        isTemporarilyLocked = false;
        failedAttempts = 0;

        layoutLockoutCountdown.setVisibility(View.GONE);
        cardLockoutAlert.setVisibility(View.GONE);
        layoutHeaderGreetingLocked.setVisibility(View.GONE);
        layoutHeaderGreetingDefault.setVisibility(View.VISIBLE);

        if (tvEmailLockedBadge != null) tvEmailLockedBadge.setVisibility(View.GONE);
        if (tvPasswordLockedBadge != null) tvPasswordLockedBadge.setVisibility(View.GONE);
        if (boxInputEmail != null) boxInputEmail.setBackgroundResource(R.drawable.bg_input_normal);
        if (boxInputPassword != null) boxInputPassword.setBackgroundResource(R.drawable.bg_input_normal);

        etEmail.setEnabled(true);
        etPassword.setEnabled(true);
        btnTogglePassword.setEnabled(true);
        btnLoginSubmit.setVisibility(View.VISIBLE);
        btnLoginSubmit.setEnabled(true);
        clearErrorState();
    }

    private void setLoading(boolean loading) {
        isRequestInFlight = loading;
        if (!isTemporarilyLocked) {
            btnLoginSubmit.setEnabled(!loading);
            btnLoginSubmit.setText(loading ? "Đang đăng nhập..." : "Đăng nhập");
            etEmail.setEnabled(!loading);
            etPassword.setEnabled(!loading);
            btnTogglePassword.setEnabled(!loading);
        }
    }

    private void showInputError(EditText target, String message) {
        target.requestFocus();
        if (target == etEmail) {
            if (boxInputEmail != null) boxInputEmail.setBackgroundResource(R.drawable.bg_input_error);
            if (tvEmailErrorBadge != null) tvEmailErrorBadge.setVisibility(View.VISIBLE);
        } else if (target == etPassword) {
            if (boxInputPassword != null) boxInputPassword.setBackgroundResource(R.drawable.bg_input_error);
        }
        showBannerError(message);
    }

    private void showBannerError(String message) {
        if (cardLoginErrorBanner != null && tvLoginErrorMessage != null) {
            tvLoginErrorMessage.setText(message);
            cardLoginErrorBanner.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    private void clearErrorState() {
        if (!isTemporarilyLocked) {
            if (boxInputEmail != null) boxInputEmail.setBackgroundResource(R.drawable.bg_input_normal);
            if (boxInputPassword != null) boxInputPassword.setBackgroundResource(R.drawable.bg_input_normal);
            if (tvEmailErrorBadge != null) tvEmailErrorBadge.setVisibility(View.GONE);
        }
        if (cardLoginErrorBanner != null) {
            cardLoginErrorBanner.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (lockCountDownTimer != null) {
            lockCountDownTimer.cancel();
        }
    }
}