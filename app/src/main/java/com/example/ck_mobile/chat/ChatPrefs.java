package com.example.ck_mobile.chat;

import android.content.Context;
import android.content.SharedPreferences;

/** Cờ chuyển giữa hướng A (LLM - Gemini) và hướng B (rule-based). Mặc định: rule-based. */
public final class ChatPrefs {

    public static final String MODE_RULE = "RULE";
    public static final String MODE_LLM = "LLM";
    private static final String FILE = "chat_prefs";
    private static final String KEY_MODE = "mode";

    private ChatPrefs() {
    }

    public static String getMode(Context ctx) {
        return prefs(ctx).getString(KEY_MODE, MODE_RULE);
    }

    public static void setMode(Context ctx, String mode) {
        prefs(ctx).edit().putString(KEY_MODE, mode).apply();
    }

    private static SharedPreferences prefs(Context ctx) {
        return ctx.getApplicationContext().getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }
}
