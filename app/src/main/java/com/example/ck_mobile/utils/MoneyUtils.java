package com.example.ck_mobile.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

public final class MoneyUtils {

    private static final Locale VI = new Locale("vi", "VN");

    private MoneyUtils() {
    }

    /** 45850000 -> "45.850.000 ₫". */
    public static String formatVnd(long amount) {
        return NumberFormat.getInstance(VI).format(amount) + " ₫";
    }

    public static String formatDateTime(Date date) {
        if (date == null) return "";
        DateFormat f = new SimpleDateFormat("dd/MM/yyyy HH:mm", VI);
        return f.format(date);
    }

    public static String monthKey(Date date) {
        if (date == null) return "";
        return new SimpleDateFormat("yyyy-MM", Locale.US).format(date);
    }

    /** Sinh số tài khoản 10 chữ số (hợp lệ với AuthSessionHelper.normalizeAccountNumber). */
    public static String randomAccountNumber() {
        long n = (long) (Math.random() * 9_000_000_000L) + 1_000_000_000L;
        return String.valueOf(n);
    }
}
