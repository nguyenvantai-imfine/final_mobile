package com.example.ck_mobile.model;

import com.google.firebase.firestore.DocumentId;

import java.util.Date;

/** Hồ sơ eKYC; document id = uid của khách hàng. */
public class KycRecord {
    @DocumentId
    private String id;
    private String customerName;
    private String photoPath;   // đường dẫn trong Firebase Storage
    private String status;      // PENDING / APPROVED / REJECTED
    private String note;        // lý do từ chối
    private String registeredBy;
    private String reviewedBy;
    private Date createdAt;
    private Date reviewedAt;

    public KycRecord() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getRegisteredBy() { return registeredBy; }
    public void setRegisteredBy(String registeredBy) { this.registeredBy = registeredBy; }
    public String getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(String reviewedBy) { this.reviewedBy = reviewedBy; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(Date reviewedAt) { this.reviewedAt = reviewedAt; }
}
