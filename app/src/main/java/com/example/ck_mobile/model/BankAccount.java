package com.example.ck_mobile.model;

import java.io.Serializable;

public class BankAccount implements Serializable {
    public static final String TYPE_CHECKING = "CHECKING";
    public static final String TYPE_SAVING = "SAVING";
    public static final String TYPE_MORTGAGE = "MORTGAGE";

    private String accountId;
    private String userId;
    private String accountType;      // CHECKING, SAVING, MORTGAGE
    private String accountNumber;    // VD: "1903 8888 6666" hoặc "MTG-2026-089"
    private String title;            // Tên sản phẩm: "Tài khoản thanh toán", "Tiết kiệm TDT Plus", "Vay mua nhà TDT Home"
    private String status;           // "ACTIVE", "ON_TIME", "OVERDUE"
    private double balance;          // Số dư khả dụng (Checking), Số dư gốc (Saving), Dư nợ còn lại (Mortgage)

    // Thuộc tính riêng cho CHECKING
    private double actualBalance;
    private double holdBalance;
    private double dailyTransferLimit;
    private String branchName;
    private String openedDate;
    private String linkedCardMasked;
    private boolean onlinePaymentEnabled;

    // Thuộc tính riêng cho SAVING
    private double interestRate;     // %/năm (VD: 5.8)
    private int termMonths;          // VD: 12
    private int elapsedMonths;       // VD: 3
    private String maturityDate;     // VD: "15/08/2027"
    private String payoutMethod;     // VD: "Cuối kỳ"
    private String renewalOption;    // VD: "Tự động tái tục cả gốc & lãi"
    private String beneficiaryAccount;

    // Thuộc tính riêng cho MORTGAGE
    private double initialPrincipal; // VD: 600_000_000
    private int paidInstallments;    // VD: 36
    private int totalInstallments;   // VD: 120
    private String nextDueDate;      // VD: "05/12/2026"
    private int daysUntilDue;        // VD: 12
    private String collateralName;   // VD: "Căn hộ Sky Garden 3"
    private String collateralLocation;
    private double collateralValuation;
    private boolean autoDebitEnabled;
    private String loanOfficerName;
    private String loanOfficerBranch;
    private String loanOfficerPhone;

    public BankAccount() {
        // Constructor rỗng bắt buộc cho Firebase Firestore
    }

    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public double getActualBalance() { return actualBalance; }
    public void setActualBalance(double actualBalance) { this.actualBalance = actualBalance; }

    public double getHoldBalance() { return holdBalance; }
    public void setHoldBalance(double holdBalance) { this.holdBalance = holdBalance; }

    public double getDailyTransferLimit() { return dailyTransferLimit; }
    public void setDailyTransferLimit(double dailyTransferLimit) { this.dailyTransferLimit = dailyTransferLimit; }

    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }

    public String getOpenedDate() { return openedDate; }
    public void setOpenedDate(String openedDate) { this.openedDate = openedDate; }

    public String getLinkedCardMasked() { return linkedCardMasked; }
    public void setLinkedCardMasked(String linkedCardMasked) { this.linkedCardMasked = linkedCardMasked; }

    public boolean isOnlinePaymentEnabled() { return onlinePaymentEnabled; }
    public void setOnlinePaymentEnabled(boolean onlinePaymentEnabled) { this.onlinePaymentEnabled = onlinePaymentEnabled; }

    public double getInterestRate() { return interestRate; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }

    public int getTermMonths() { return termMonths; }
    public void setTermMonths(int termMonths) { this.termMonths = termMonths; }

    public int getElapsedMonths() { return elapsedMonths; }
    public void setElapsedMonths(int elapsedMonths) { this.elapsedMonths = elapsedMonths; }

    public String getMaturityDate() { return maturityDate; }
    public void setMaturityDate(String maturityDate) { this.maturityDate = maturityDate; }

    public String getPayoutMethod() { return payoutMethod; }
    public void setPayoutMethod(String payoutMethod) { this.payoutMethod = payoutMethod; }

    public String getRenewalOption() { return renewalOption; }
    public void setRenewalOption(String renewalOption) { this.renewalOption = renewalOption; }

    public String getBeneficiaryAccount() { return beneficiaryAccount; }
    public void setBeneficiaryAccount(String beneficiaryAccount) { this.beneficiaryAccount = beneficiaryAccount; }

    public double getInitialPrincipal() { return initialPrincipal; }
    public void setInitialPrincipal(double initialPrincipal) { this.initialPrincipal = initialPrincipal; }

    public int getPaidInstallments() { return paidInstallments; }
    public void setPaidInstallments(int paidInstallments) { this.paidInstallments = paidInstallments; }

    public int getTotalInstallments() { return totalInstallments; }
    public void setTotalInstallments(int totalInstallments) { this.totalInstallments = totalInstallments; }

    public String getNextDueDate() { return nextDueDate; }
    public void setNextDueDate(String nextDueDate) { this.nextDueDate = nextDueDate; }

    public int getDaysUntilDue() { return daysUntilDue; }
    public void setDaysUntilDue(int daysUntilDue) { this.daysUntilDue = daysUntilDue; }

    public String getCollateralName() { return collateralName; }
    public void setCollateralName(String collateralName) { this.collateralName = collateralName; }

    public String getCollateralLocation() { return collateralLocation; }
    public void setCollateralLocation(String collateralLocation) { this.collateralLocation = collateralLocation; }

    public double getCollateralValuation() { return collateralValuation; }
    public void setCollateralValuation(double collateralValuation) { this.collateralValuation = collateralValuation; }

    public boolean isAutoDebitEnabled() { return autoDebitEnabled; }
    public void setAutoDebitEnabled(boolean autoDebitEnabled) { this.autoDebitEnabled = autoDebitEnabled; }

    public String getLoanOfficerName() { return loanOfficerName; }
    public void setLoanOfficerName(String loanOfficerName) { this.loanOfficerName = loanOfficerName; }

    public String getLoanOfficerBranch() { return loanOfficerBranch; }
    public void setLoanOfficerBranch(String loanOfficerBranch) { this.loanOfficerBranch = loanOfficerBranch; }

    public String getLoanOfficerPhone() { return loanOfficerPhone; }
    public void setLoanOfficerPhone(String loanOfficerPhone) { this.loanOfficerPhone = loanOfficerPhone; }
}