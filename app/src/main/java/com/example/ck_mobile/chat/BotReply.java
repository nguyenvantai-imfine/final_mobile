package com.example.ck_mobile.chat;

/** Câu trả lời của chatbot, kèm tùy chọn một nút deep link. */
public class BotReply {
    public final String text;
    public final String actionTarget; // null nếu không có
    public final String actionLabel;

    public BotReply(String text, String actionTarget) {
        this.text = text;
        this.actionTarget = ChatActions.isKnown(actionTarget) ? actionTarget : null;
        this.actionLabel = ChatActions.label(this.actionTarget);
    }
}
