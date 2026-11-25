package com.example.ck_mobile.model;

import java.io.Serializable;

public class Account implements Serializable {
    public static final String TYPE_CHECKING = "CHECKING";
    public static final String TYPE_SAVING = "SAVING";
    public static final String TYPE_MORTGAGE = "MORTGAGE";

    private String accountNumber;
    private String accountType; // CHECKING, SAVING, MORTGAGE
    private double balance;
    private double interestRate; // Annual rate in % (e.g., 6.5%)
    private double monthlyPayment; // For Mortgage
    private String currency = "VND";

    public Account(String accountNumber, String accountType, double balance, double interestRate, double monthlyPayment) {
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
        this.interestRate = interestRate;
        this.monthlyPayment = monthlyPayment;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getAccountTypeName() {
        switch (accountType) {
            case TYPE_SAVING: return "Tài khoản Tiết kiệm";
            case TYPE_MORTGAGE: return "Tài khoản Vay thế chấp";
            default: return "Tài khoản Thanh toán";
        }
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public double getMonthlyPayment() {
        return monthlyPayment;
    }

    public double getMonthlyProfit() {
        if (TYPE_SAVING.equals(accountType)) {
            return (balance * (interestRate / 100.0)) / 12.0;
        }
        return 0.0;
    }

    public String getCurrency() {
        return currency;
    }
}
