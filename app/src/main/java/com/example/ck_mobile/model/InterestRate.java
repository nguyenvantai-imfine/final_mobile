package com.example.ck_mobile.model;

import com.google.firebase.firestore.DocumentId;

/** Lãi suất tiết kiệm theo kỳ hạn; document id = "m{termMonths}". */
public class InterestRate {
    @DocumentId
    private String id;
    private int termMonths;
    private double rate; // % / năm

    public InterestRate() {
    }

    public InterestRate(int termMonths, double rate) {
        this.termMonths = termMonths;
        this.rate = rate;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public int getTermMonths() { return termMonths; }
    public void setTermMonths(int termMonths) { this.termMonths = termMonths; }
    public double getRate() { return rate; }
    public void setRate(double rate) { this.rate = rate; }
}
