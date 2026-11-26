package com.example.ck_mobile.model;

import com.google.firebase.firestore.DocumentId;

public class ChatMessage {
    @DocumentId
    private String id;
    private String text;
    private boolean fromUser;
    private String actionLabel;   // nhãn nút deep link (có thể null)
    private String actionTarget;  // mã đích, xem DeepLinkRouter
    private long createdAt;

    public ChatMessage() {
    }

    public ChatMessage(String text, boolean fromUser, String actionLabel, String actionTarget) {
        this.text = text;
        this.fromUser = fromUser;
        this.actionLabel = actionLabel;
        this.actionTarget = actionTarget;
        this.createdAt = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public boolean isFromUser() { return fromUser; }
    public void setFromUser(boolean fromUser) { this.fromUser = fromUser; }
    public String getActionLabel() { return actionLabel; }
    public void setActionLabel(String actionLabel) { this.actionLabel = actionLabel; }
    public String getActionTarget() { return actionTarget; }
    public void setActionTarget(String actionTarget) { this.actionTarget = actionTarget; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
