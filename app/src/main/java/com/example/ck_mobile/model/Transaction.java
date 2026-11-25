package com.example.ck_mobile.model;

import java.io.Serializable;

public class Transaction implements Serializable {
    private String transactionId;
    private String title;
    private String category; // Nạp tiền, Rút tiền, Chuyển khoản, Điện, Nước, Điện thoại, Vé máy bay...
    private double amount;
    private boolean isCredit; // true if +, false if -
    private String timestamp;
    private String status; // SUCCESS, FAILED, PENDING
    private String details;

    public Transaction(String transactionId, String title, String category, double amount, boolean isCredit, String timestamp, String status, String details) {
        this.transactionId = transactionId;
        this.title = title;
        this.category = category;
        this.amount = amount;
        this.isCredit = isCredit;
        this.timestamp = timestamp;
        this.status = status;
        this.details = details;
    }

    public String getTransactionId() { return transactionId; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public double getAmount() { return amount; }
    public boolean isCredit() { return isCredit; }
    public String getTimestamp() { return timestamp; }
    public String getStatus() { return status; }
    public String getDetails() { return details; }
}
