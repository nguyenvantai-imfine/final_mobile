package com.example.ck_mobile.model;

import com.google.firebase.firestore.DocumentId;

import java.util.Date;

/** Một dòng lịch sử giao dịch (collection "transactions"). */
public class TransactionRecord {
    @DocumentId
    private String id;
    private String ownerUid;
    private String accountId;
    private String accountNumber;
    private String type;        // xem Constants.TX_*
    private long amount;        // luôn dương
    private boolean credit;     // true = tiền vào, false = tiền ra
    private String description;
    private long balanceAfter;
    private Date createdAt;

    public TransactionRecord() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getOwnerUid() { return ownerUid; }
    public void setOwnerUid(String ownerUid) { this.ownerUid = ownerUid; }
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public long getAmount() { return amount; }
    public void setAmount(long amount) { this.amount = amount; }
    public boolean isCredit() { return credit; }
    public void setCredit(boolean credit) { this.credit = credit; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public long getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(long balanceAfter) { this.balanceAfter = balanceAfter; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
