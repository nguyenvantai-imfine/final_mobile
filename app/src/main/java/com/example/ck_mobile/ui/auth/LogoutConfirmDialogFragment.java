package com.example.ck_mobile.ui.auth;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.ck_mobile.R;
import com.example.ck_mobile.databinding.DialogLogoutConfirmBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogoutConfirmDialogFragment extends DialogFragment {

    public static final String TAG = "LogoutConfirmDialog";
    private static final String ARG_EMAIL = "arg_email";

    private DialogLogoutConfirmBinding binding;
    private OnLogoutConfirmedListener listener;
    private final Handler handler = new Handler(Looper.getMainLooper());

    public interface OnLogoutConfirmedListener {
        void onLogoutConfirmed();
    }

    public static LogoutConfirmDialogFragment newInstance(@Nullable String email) {
        LogoutConfirmDialogFragment fragment = new LogoutConfirmDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL, email);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnLogoutConfirmedListener(@Nullable OnLogoutConfirmedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Window window = dialog.getWindow();
        if (window != null) {
            window.requestFeature(Window.FEATURE_NO_TITLE);
            window.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = DialogLogoutConfirmBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String email = getArguments() != null ? getArguments().getString(ARG_EMAIL) : null;
        if (email == null || email.trim().isEmpty()) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null && currentUser.getEmail() != null) {
                email = currentUser.getEmail();
            } else {
                email = "phiên hiện tại";
            }
        }

        String htmlText = "Tài khoản <font color=\"#7B0049\"><b>"
                + android.text.TextUtils.htmlEncode(email)
                + "</b></font> sẽ được ngắt kết nối an toàn.";
        Spanned spanned = Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY);
        binding.tvSessionAccountInfo.setText(spanned);

        View.OnClickListener dismissClick = v -> dismissAllowingStateLoss();
        binding.logoutBackdrop.setOnClickListener(dismissClick);
        binding.btnCancelLogout.setOnClickListener(dismissClick);
        binding.btnCloseX.setOnClickListener(dismissClick);
        binding.cardLogoutDialog.setOnClickListener(v -> { /* chặn click xuyên qua card */ });

        binding.btnConfirmLogout.setOnClickListener(v -> {
            binding.btnConfirmLogout.setEnabled(false);
            binding.btnCancelLogout.setEnabled(false);
            binding.btnCloseX.setEnabled(false);
            binding.btnConfirmLogout.setIcon(null);
            binding.btnConfirmLogout.setText("Đang thoát...");

            handler.postDelayed(() -> {
                if (!isAdded()) return;
                dismissAllowingStateLoss();
                if (listener != null) {
                    listener.onLogoutConfirmed();
                }
            }, 350);
        });
    }

    @Override
    public void onDestroyView() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroyView();
        binding = null;
    }
}