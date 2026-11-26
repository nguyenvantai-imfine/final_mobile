package com.example.ck_mobile.chat;

import com.example.ck_mobile.model.ChatMessage;
import com.example.ck_mobile.repository.RepoCallback;

import java.util.List;

/** Một "bộ não" của chatbot. Callback luôn được gọi trên main thread. */
public interface ChatEngine {
    void reply(String userInput, List<ChatMessage> history, RepoCallback<BotReply> callback);
}
