package com.example.ck_mobile.model;

import java.io.Serializable;

public class AccountTransaction implements Serializable {
    private String transactionId;
    private String accountId;
    private String title;          // VD: "Chuyển tiền - LÊ HOÀNG ĐẠT"
    private String dateTimeLabel;  // VD: "23/11/2026 • 09:30"
    private String groupHeader;    // VD: "Hôm nay, 23/11/2026"
    private double amount;         // Dương (+): tiền vào, Âm (-): tiền ra
    private String status;         // "SUCCESS"
    private String category;       // "TRANSFER", "TOPUP", "BILL", "SALARY"
    private long timestamp;

    public AccountTransaction() {
    }

    public AccountTransaction(String transactionId, String accountId, String title,
                              String dateTimeLabel, String groupHeader, double amount,
                              String status, String category, long timestamp) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.title = title;
        this.dateTimeLabel = dateTimeLabel;
        this.groupHeader = groupHeader;
        this.amount = amount;
        this.status = status;
        this.category = category;
        this.timestamp = timestamp;
    }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDateTimeLabel() { return dateTimeLabel; }
    public void setDateTimeLabel(String dateTimeLabel) { this.dateTimeLabel = dateTimeLabel; }

    public String getGroupHeader() { return groupHeader; }
    public void setGroupHeader(String groupHeader) { this.groupHeader = groupHeader; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}