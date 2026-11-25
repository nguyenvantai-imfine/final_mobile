package com.example.ck_mobile.repository;

import com.example.ck_mobile.model.Account;
import com.example.ck_mobile.model.Branch;
import com.example.ck_mobile.model.Transaction;
import com.example.ck_mobile.model.UserProfile;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CustomerRepository {
    private static CustomerRepository instance;

    private UserProfile userProfile;
    private final List<Account> accounts = new ArrayList<>();
    private final List<Transaction> transactions = new ArrayList<>();
    private final List<Branch> branches = new ArrayList<>();

    private CustomerRepository() {
        initSampleData();
    }

    public static synchronized CustomerRepository getInstance() {
        if (instance == null) {
            instance = new CustomerRepository();
        }
        return instance;
    }

    private void initSampleData() {
        // User Profile
        userProfile = new UserProfile(
                "USR998211",
                "NGUYỄN VĂN TÀI",
                "0909123456",
                "tai.nguyen@tdtu.edu.vn",
                "19 Nguyễn Hữu Thọ, P. Tân Phong, Q.7, TP.HCM",
                "VIP Sovereign Member",
                true
        );

        // Accounts
        accounts.add(new Account("1029384756", Account.TYPE_CHECKING, 128500000, 0.2, 0));
        accounts.add(new Account("8827364510", Account.TYPE_SAVING, 350000000, 6.8, 0));
        accounts.add(new Account("5510293847", Account.TYPE_MORTGAGE, 850000000, 8.5, 12500000));

        // Sample Transactions
        transactions.add(new Transaction("TXN90281", "Chuyển tiền điện thoại - Viettel", "Điện thoại", 100000, false, "23/11/2026 18:30", "SUCCESS", "Nạp SĐT 0909123456"));
        transactions.add(new Transaction("TXN90280", "Nhận tiền từ Cty TNHH TDT", "Chuyển khoản", 25000000, true, "23/11/2026 09:15", "SUCCESS", "Lương tháng 11/2026"));
        transactions.add(new Transaction("TXN90279", "Thanh toán hóa đơn Tiền điện EVN", "Điện", 1240000, false, "22/11/2026 14:20", "SUCCESS", "Mã HĐ: EVN-882910"));
        transactions.add(new Transaction("TXN90278", "Nạp tiền tài khoản Checking", "Nạp tiền", 5000000, true, "21/11/2026 10:00", "SUCCESS", "Nạp tiền qua Stripe Sandbox"));

        // Sample Branches around TDTU / District 7, HCMC
        branches.add(new Branch("TDT Bank - Chi nhánh Tân Phong", "19 Nguyễn Hữu Thọ, Tân Phong, Q.7, TP.HCM", 10.7325, 106.6992, 0.3, "08:00 - 17:00"));
        branches.add(new Branch("TDT Bank - Phòng giao dịch Lotte Mart", "469 Nguyễn Hữu Thọ, Tân Hưng, Q.7, TP.HCM", 10.7410, 106.6980, 1.2, "08:00 - 19:30"));
        branches.add(new Branch("TDT Bank - PGD Phú Mỹ Hưng", "101 Nguyễn Đức Cảnh, Tân Phong, Q.7, TP.HCM", 10.7280, 106.7080, 1.8, "08:00 - 17:00"));
        branches.add(new Branch("TDT Bank - Chi nhánh Quận 4", "182 Khánh Hội, Phường 6, Q.4, TP.HCM", 10.7550, 106.7010, 3.5, "08:00 - 17:00"));
        branches.add(new Branch("TDT Bank - ATM 24/7 Bùi Bằng Đoàn", "55 Bùi Bằng Đoàn, Tân Phong, Q.7, TP.HCM", 10.7295, 106.7050, 0.9, "Mở cửa 24/7"));
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public Account getCheckingAccount() {
        for (Account acc : accounts) {
            if (Account.TYPE_CHECKING.equals(acc.getAccountType())) return acc;
        }
        return accounts.isEmpty() ? null : accounts.get(0);
    }

    public Account getSavingAccount() {
        for (Account acc : accounts) {
            if (Account.TYPE_SAVING.equals(acc.getAccountType())) return acc;
        }
        return null;
    }

    public Account getMortgageAccount() {
        for (Account acc : accounts) {
            if (Account.TYPE_MORTGAGE.equals(acc.getAccountType())) return acc;
        }
        return null;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public List<Branch> getBranches() {
        return branches;
    }

    public void addTransaction(String title, String category, double amount, boolean isCredit, String details) {
        String txId = "TXN" + (System.currentTimeMillis() % 100000);
        String timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
        Transaction tx = new Transaction(txId, title, category, amount, isCredit, timestamp, "SUCCESS", details);
        transactions.add(0, tx);

        // Update Checking Account balance
        Account checking = getCheckingAccount();
        if (checking != null) {
            if (isCredit) {
                checking.setBalance(checking.getBalance() + amount);
            } else {
                checking.setBalance(Math.max(0, checking.getBalance() - amount));
            }
        }
    }

    public String formatCurrency(double amount) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(amount) + " VNĐ";
    }
}
