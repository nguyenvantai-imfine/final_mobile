package com.example.ck_mobile.model;

import java.io.Serializable;

public class MortgageInstallment implements Serializable {
    public static final String STATUS_PAID = "PAID";
    public static final String STATUS_DUE = "DUE";
    public static final String STATUS_UPCOMING = "UPCOMING";
    public static final String STATUS_OVERDUE = "OVERDUE";

    private int periodNumber;          // 1..120
    private int totalPeriods;          // 120
    private int year;                  // VD: 2026
    private String dueDate;            // VD: "05/12/2026"
    private double principalAmount;    // Gốc phải trả kỳ này
    private double interestAmount;     // Lãi phải trả kỳ này
    private double totalAmount;        // Tổng gốc + lãi kỳ này
    private double remainingBalance;   // Dư nợ gốc sau khi trả kỳ này
    private String status;             // PAID, DUE, UPCOMING, OVERDUE
    private String receiptCode;        // VD: "MTG-36"

    public MortgageInstallment() {
    }

    public int getPeriodNumber() { return periodNumber; }
    public void setPeriodNumber(int periodNumber) { this.periodNumber = periodNumber; }

    public int getTotalPeriods() { return totalPeriods; }
    public void setTotalPeriods(int totalPeriods) { this.totalPeriods = totalPeriods; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public double getPrincipalAmount() { return principalAmount; }
    public void setPrincipalAmount(double principalAmount) { this.principalAmount = principalAmount; }

    public double getInterestAmount() { return interestAmount; }
    public void setInterestAmount(double interestAmount) { this.interestAmount = interestAmount; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public double getRemainingBalance() { return remainingBalance; }
    public void setRemainingBalance(double remainingBalance) { this.remainingBalance = remainingBalance; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReceiptCode() { return receiptCode; }
    public void setReceiptCode(String receiptCode) { this.receiptCode = receiptCode; }
}