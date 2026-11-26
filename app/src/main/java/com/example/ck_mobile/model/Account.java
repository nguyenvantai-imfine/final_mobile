package com.example.ck_mobile.model;

import com.google.firebase.firestore.DocumentId;

import java.util.Date;

public class Account {
    @DocumentId
    private String id;
    private String ownerUid;
    private String type;   // CHECKING / SAVING / MORTGAGE
    private String number;
    private long balance;  // VND
    private Date createdAt;

    public Account() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getOwnerUid() { return ownerUid; }
    public void setOwnerUid(String ownerUid) { this.ownerUid = ownerUid; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
    public long getBalance() { return balance; }
    public void setBalance(long balance) { this.balance = balance; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
