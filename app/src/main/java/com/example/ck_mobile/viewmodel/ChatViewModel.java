package com.example.ck_mobile.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ck_mobile.chat.BotReply;
import com.example.ck_mobile.chat.ChatEngine;
import com.example.ck_mobile.chat.ChatPrefs;
import com.example.ck_mobile.chat.GeminiChatEngine;
import com.example.ck_mobile.chat.HybridChatEngine;
import com.example.ck_mobile.model.ChatMessage;
import com.example.ck_mobile.repository.ChatRepository;
import com.example.ck_mobile.repository.RepoCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

/** T46/T47/T53: trạng thái màn hình chat (tin nhắn, đang gõ, chế độ A/B). */
public class ChatViewModel extends AndroidViewModel {

    private final ChatRepository repo = new ChatRepository();
    private final ChatEngine engine;
    private final List<ChatMessage> messages = new ArrayList<>();
    private final MutableLiveData<List<ChatMessage>> live = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> typing = new MutableLiveData<>(false);
    private final MutableLiveData<String> mode = new MutableLiveData<>();
    private boolean historyLoaded;

    public ChatViewModel(@NonNull Application app) {
        super(app);
        engine = new HybridChatEngine(app);
        mode.setValue(ChatPrefs.getMode(app));
    }

    public LiveData<List<ChatMessage>> getMessages() {
        return live;
    }

    public LiveData<Boolean> getTyping() {
        return typing;
    }

    public LiveData<String> getMode() {
        return mode;
    }

    public boolean isLlmAvailable() {
        return GeminiChatEngine.isConfigured();
    }

    public void setLlmMode(boolean enabled) {
        String m = enabled ? ChatPrefs.MODE_LLM : ChatPrefs.MODE_RULE;
        ChatPrefs.setMode(getApplication(), m);
        mode.setValue(m);
    }

    public void loadHistory() {
        if (historyLoaded) return;
        historyLoaded = true;
        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        if (u == null) return;
        repo.loadRecent(u.getUid(), new RepoCallback<List<ChatMessage>>() {
            @Override
            public void onSuccess(List<ChatMessage> result) {
                // Tin nhắn người dùng vừa gửi trong lúc tải vẫn được giữ lại phía sau lịch sử
                List<ChatMessage> pending = new ArrayList<>(messages);
                messages.clear();
                messages.addAll(result);
                messages.addAll(pending);
                if (messages.isEmpty()) {
                    messages.add(new ChatMessage("Xin chào! Tôi là trợ lý TDT Bank. Bạn cần giúp gì hôm nay?", false, null, null));
                }
                publish();
            }

            @Override
            public void onError(String message) {
                if (messages.isEmpty()) {
                    messages.add(new ChatMessage("Xin chào! Tôi là trợ lý TDT Bank. Bạn cần giúp gì hôm nay?", false, null, null));
                    publish();
                }
            }
        });
    }

    public void send(String text) {
        String input = text == null ? "" : text.trim();
        if (input.isEmpty() || Boolean.TRUE.equals(typing.getValue())) return;

        List<ChatMessage> historyForEngine = new ArrayList<>(messages);
        ChatMessage userMsg = new ChatMessage(input, true, null, null);
        messages.add(userMsg);
        persist(userMsg);
        typing.setValue(true);
        publish();

        engine.reply(input, historyForEngine, new RepoCallback<BotReply>() {
            @Override
            public void onSuccess(BotReply r) {
                finish(new ChatMessage(r.text, false, r.actionLabel, r.actionTarget));
            }

            @Override
            public void onError(String message) {
                finish(new ChatMessage("Xin lỗi, tôi gặp sự cố khi trả lời. Vui lòng thử lại.", false, null, null));
            }
        });
    }

    private void finish(ChatMessage botMsg) {
        messages.add(botMsg);
        persist(botMsg);
        typing.setValue(false);
        publish();
    }

    private void persist(ChatMessage m) {
        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        if (u != null) repo.save(u.getUid(), m);
    }

    private void publish() {
        live.setValue(new ArrayList<>(messages));
    }
}
