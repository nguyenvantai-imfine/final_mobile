package com.example.ck_mobile.chat;

import android.content.Context;

import com.example.ck_mobile.model.ChatMessage;
import com.example.ck_mobile.repository.RepoCallback;

import java.util.List;

/**
 * Cờ chuyển A/B (T53): chế độ LLM dùng Gemini, tự động rơi về rule-based khi chưa có khóa API,
 * lỗi mạng, hết quota hoặc phản hồi không hợp lệ.
 */
public class HybridChatEngine implements ChatEngine {

    private final Context appContext;
    private final RuleBasedChatEngine rule = new RuleBasedChatEngine();
    private final GeminiChatEngine llm = new GeminiChatEngine();

    public HybridChatEngine(Context context) {
        this.appContext = context.getApplicationContext();
    }

    @Override
    public void reply(String userInput, List<ChatMessage> history, RepoCallback<BotReply> cb) {
        boolean useLlm = ChatPrefs.MODE_LLM.equals(ChatPrefs.getMode(appContext)) && GeminiChatEngine.isConfigured();
        if (!useLlm) {
            rule.reply(userInput, history, cb);
            return;
        }
        llm.reply(userInput, history, new RepoCallback<BotReply>() {
            @Override
            public void onSuccess(BotReply result) {
                cb.onSuccess(result);
            }

            @Override
            public void onError(String message) {
                // Tự về hướng B; báo rõ để người dùng biết đang ở chế độ dự phòng
                rule.reply(userInput, history, new RepoCallback<BotReply>() {
                    @Override
                    public void onSuccess(BotReply r) {
                        cb.onSuccess(new BotReply("(Trợ lý AI tạm thời không khả dụng, đang dùng chế độ trả lời tự động.)\n"
                                + r.text, r.actionTarget));
                    }

                    @Override
                    public void onError(String m) {
                        cb.onError(m);
                    }
                });
            }
        });
    }
}
