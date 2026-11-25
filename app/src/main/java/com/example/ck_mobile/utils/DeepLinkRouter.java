package com.example.ck_mobile.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.ck_mobile.chat.ChatActions;
import com.example.ck_mobile.ui.customer.KycStatusActivity;
import com.example.ck_mobile.ui.customer.ServiceOrderActivity;
import com.example.ck_mobile.ui.customer.TransactionHistoryActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * T54: mở đúng màn hình chức năng từ nút deep link trong chatbot.
 * Màn hình của thành viên khác đăng ký bằng {@link #register(String, Class)} (hoặc khai báo tên lớp);
 * chức năng chưa có màn hình sẽ hiện thông báo thay vì làm app lỗi.
 */
public final class DeepLinkRouter {

    private static final Map<String, Class<?>> TARGETS = new HashMap<>();

    static {
        TARGETS.put(ChatActions.HISTORY, TransactionHistoryActivity.class);
        TARGETS.put(ChatActions.MOVIE, ServiceOrderActivity.class);
        TARGETS.put(ChatActions.HOTEL, ServiceOrderActivity.class);
        TARGETS.put(ChatActions.KYC, KycStatusActivity.class);
    }

    private DeepLinkRouter() {
    }

    /** Ví dụ: DeepLinkRouter.register(ChatActions.TRANSFER, TransferActivity.class). */
    public static void register(String target, Class<?> activity) {
        TARGETS.put(target, activity);
    }

    public static boolean open(Context ctx, String target) {
        Class<?> cls = TARGETS.get(target);
        if (cls == null) {
            Toast.makeText(ctx, "Chức năng này sẽ sớm có trong ứng dụng.", Toast.LENGTH_SHORT).show();
            return false;
        }
        Intent i = new Intent(ctx, cls);
        if (ChatActions.MOVIE.equals(target)) i.putExtra(Constants.EXTRA_SERVICE_TYPE, Constants.TX_MOVIE);
        if (ChatActions.HOTEL.equals(target)) i.putExtra(Constants.EXTRA_SERVICE_TYPE, Constants.TX_HOTEL);
        ctx.startActivity(i);
        return true;
    }
}
