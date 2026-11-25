package com.example.ck_mobile.model;

import java.io.Serializable;

public class UserProfile implements Serializable {
    private String userId;
    private String fullName;
    private String phone;
    private String email;
    private String address;
    private String memberStatus; // VIP Sovereign, Standard
    private boolean isKycVerified;

    public UserProfile(String userId, String fullName, String phone, String email, String address, String memberStatus, boolean isKycVerified) {
        this.userId = userId;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.memberStatus = memberStatus;
        this.isKycVerified = isKycVerified;
    }

    public String getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getMemberStatus() { return memberStatus; }
    public boolean isKycVerified() { return isKycVerified; }
}
