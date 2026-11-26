package com.example.ck_mobile.repository;

import com.example.ck_mobile.model.AccountTransaction;
import com.example.ck_mobile.model.BankAccount;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AccountRepository {

    public interface AccountsCallback {
        void onSuccess(List<BankAccount> accounts);
        void onError(String message);
    }

    public interface SingleAccountCallback {
        void onSuccess(BankAccount account);
        void onError(String message);
    }

    public interface TransactionsCallback {
        void onSuccess(List<AccountTransaction> transactions);
        void onError(String message);
    }

    public interface ActionCallback {
        void onSuccess(String message);
        void onError(String message);
    }

    private final FirebaseFirestore db;

    public AccountRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Lấy danh sách tài khoản của khách hàng. Nếu chưa có trên Firestore sẽ tự khởi tạo dữ liệu mẫu.
     */
    public void getCustomerAccounts(String uid, AccountsCallback callback) {
        if (uid == null || uid.trim().isEmpty()) {
            callback.onError("Không xác định được người dùng.");
            return;
        }

        db.collection("users")
                .document(uid)
                .collection("accounts")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot == null || querySnapshot.isEmpty()) {
                        seedInitialAccounts(uid, callback);
                        return;
                    }
                    List<BankAccount> accounts = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        BankAccount acc = doc.toObject(BankAccount.class);
                        if (acc != null) {
                            acc.setAccountId(doc.getId());
                            accounts.add(acc);
                        }
                    }
                    callback.onSuccess(accounts);
                })
                .addOnFailureListener(e -> callback.onError("Lỗi tải danh sách tài khoản: " + e.getMessage()));
    }

    /**
     * Lấy chi tiết 1 tài khoản theo accountId
     */
    public void getAccountById(String uid, String accountId, SingleAccountCallback callback) {
        db.collection("users")
                .document(uid)
                .collection("accounts")
                .document(accountId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc != null && doc.exists()) {
                        BankAccount acc = doc.toObject(BankAccount.class);
                        if (acc != null) {
                            acc.setAccountId(doc.getId());
                            callback.onSuccess(acc);
                            return;
                        }
                    }
                    callback.onError("Không tìm thấy thông tin tài khoản.");
                })
                .addOnFailureListener(e -> callback.onError("Lỗi tải chi tiết tài khoản: " + e.getMessage()));
    }

    /**
     * Lấy biến động số dư của tài khoản thanh toán
     */
    public void getCheckingTransactions(String uid, String accountId, TransactionsCallback callback) {
        db.collection("users")
                .document(uid)
                .collection("accounts")
                .document(accountId)
                .collection("transactions")
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<AccountTransaction> list = new ArrayList<>();
                    if (snapshot != null) {
                        for (DocumentSnapshot doc : snapshot.getDocuments()) {
                            AccountTransaction tx = doc.toObject(AccountTransaction.class);
                            if (tx != null) {
                                tx.setTransactionId(doc.getId());
                                list.add(tx);
                            }
                        }
                    }
                    list.sort((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
                    callback.onSuccess(list);
                })
                .addOnFailureListener(e -> callback.onError("Lỗi tải biến động số dư: " + e.getMessage()));
    }

    /**
     * Cập nhật trạng thái thanh toán trực tuyến (E-Commerce) cho thẻ ghi nợ liên kết
     */
    public void updateOnlinePaymentToggle(String uid, String accountId, boolean enabled, ActionCallback callback) {
        db.collection("users")
                .document(uid)
                .collection("accounts")
                .document(accountId)
                .update("onlinePaymentEnabled", enabled)
                .addOnSuccessListener(unused -> callback.onSuccess(
                        enabled ? "Đã bật thanh toán trực tuyến (E-Commerce)" : "Đã tắt thanh toán trực tuyến"))
                .addOnFailureListener(e -> callback.onError("Cập nhật thất bại: " + e.getMessage()));
    }

    /**
     * Cập nhật chỉ thị đáo hạn sổ tiết kiệm (B02)
     */
    public void updateSavingRenewalOption(String uid, String accountId, String newOption, ActionCallback callback) {
        db.collection("users")
                .document(uid)
                .collection("accounts")
                .document(accountId)
                .update("renewalOption", newOption)
                .addOnSuccessListener(unused -> callback.onSuccess("Đã cập nhật chỉ thị đáo hạn: " + newOption))
                .addOnFailureListener(e -> callback.onError("Không thể cập nhật chỉ thị đáo hạn: " + e.getMessage()));
    }

    /**
     * Gửi thêm tiền vào sổ tiết kiệm từ tài khoản thanh toán (B02)
     */
    public void topUpSavingFromChecking(String uid, BankAccount checking, BankAccount saving,
                                        double topUpAmount, ActionCallback callback) {
        if (topUpAmount <= 0) {
            callback.onError("Số tiền gửi thêm phải lớn hơn 0 đ.");
            return;
        }
        if (checking.getBalance() < topUpAmount) {
            callback.onError("Số dư khả dụng tài khoản thanh toán không đủ.");
            return;
        }

        double newCheckingBalance = checking.getBalance() - topUpAmount;
        double newCheckingActual = checking.getActualBalance() - topUpAmount;
        double newSavingBalance = saving.getBalance() + topUpAmount;

        WriteBatch batch = db.batch();
        DocumentReference checkingRef = db.collection("users").document(uid)
                .collection("accounts").document(checking.getAccountId());
        DocumentReference savingRef = db.collection("users").document(uid)
                .collection("accounts").document(saving.getAccountId());

        batch.update(checkingRef,
                "balance", newCheckingBalance,
                "actualBalance", newCheckingActual);
        batch.update(savingRef, "balance", newSavingBalance);

        String nowStr = new SimpleDateFormat("dd/MM/yyyy • HH:mm", Locale.US).format(new Date());
        DocumentReference txRef = checkingRef.collection("transactions").document();
        AccountTransaction tx = new AccountTransaction(
                txRef.getId(),
                checking.getAccountId(),
                "Gửi thêm tiết kiệm TDT Plus",
                nowStr,
                "Hôm nay",
                -topUpAmount,
                "SUCCESS",
                "TRANSFER",
                System.currentTimeMillis()
        );
        batch.set(txRef, tx);

        batch.commit()
                .addOnSuccessListener(unused -> callback.onSuccess("Gửi thêm tiền vào sổ tiết kiệm thành công!"))
                .addOnFailureListener(e -> callback.onError("Giao dịch thất bại: " + e.getMessage()));
    }

    /**
     * Bật/tắt tự động trích nợ khoản vay thế chấp (B03)
     */
    public void updateMortgageAutoDebit(String uid, String mortgageAccountId, boolean enabled, ActionCallback callback) {
        db.collection("users")
                .document(uid)
                .collection("accounts")
                .document(mortgageAccountId)
                .update("autoDebitEnabled", enabled)
                .addOnSuccessListener(unused -> callback.onSuccess(
                        enabled ? "Đã bật tự động trích nợ" : "Đã tắt tự động trích nợ"))
                .addOnFailureListener(e -> callback.onError("Cập nhật thất bại: " + e.getMessage()));
    }

    /**
     * Thanh toán 1 kỳ vay thế chấp từ tài khoản thanh toán (B03 / Schedule)
     */
    public void payMortgageInstallment(String uid, BankAccount checking, BankAccount mortgage,
                                       double principalPaid, double totalPaid, ActionCallback callback) {
        if (checking.getBalance() < totalPaid) {
            callback.onError("Số dư tài khoản thanh toán không đủ để thanh toán kỳ vay.");
            return;
        }
        if (mortgage.getPaidInstallments() >= mortgage.getTotalInstallments()) {
            callback.onError("Khoản vay đã hoàn tất tất cả các kỳ thanh toán.");
            return;
        }

        double newCheckingBalance = checking.getBalance() - totalPaid;
        double newCheckingActual = checking.getActualBalance() - totalPaid;
        double newRemainingDebt = Math.max(0, mortgage.getBalance() - principalPaid);
        int newPaidCount = mortgage.getPaidInstallments() + 1;

        WriteBatch batch = db.batch();
        DocumentReference checkingRef = db.collection("users").document(uid)
                .collection("accounts").document(checking.getAccountId());
        DocumentReference mortgageRef = db.collection("users").document(uid)
                .collection("accounts").document(mortgage.getAccountId());

        batch.update(checkingRef,
                "balance", newCheckingBalance,
                "actualBalance", newCheckingActual);
        batch.update(mortgageRef,
                "balance", newRemainingDebt,
                "paidInstallments", newPaidCount);

        String nowStr = new SimpleDateFormat("dd/MM/yyyy • HH:mm", Locale.US).format(new Date());
        DocumentReference txRef = checkingRef.collection("transactions").document();
        AccountTransaction tx = new AccountTransaction(
                txRef.getId(),
                checking.getAccountId(),
                "Thanh toán kỳ " + newPaidCount + " - " + mortgage.getAccountNumber(),
                nowStr,
                "Hôm nay",
                -totalPaid,
                "SUCCESS",
                "BILL",
                System.currentTimeMillis()
        );
        batch.set(txRef, tx);

        batch.commit()
                .addOnSuccessListener(unused -> callback.onSuccess("Thanh toán kỳ " + newPaidCount + " thành công!"))
                .addOnFailureListener(e -> callback.onError("Thanh toán kỳ vay thất bại: " + e.getMessage()));
    }

    /**
     * Khởi tạo bộ 3 tài khoản mặc định khớp thiết kế TDT Bank Sovereign khi user chưa có dữ liệu
     */
    private void seedInitialAccounts(String uid, AccountsCallback callback) {
        WriteBatch batch = db.batch();

        BankAccount checking = new BankAccount();
        checking.setAccountId("acc_checking_01");
        checking.setUserId(uid);
        checking.setAccountType(BankAccount.TYPE_CHECKING);
        checking.setAccountNumber("1903 8888 6666");
        checking.setTitle("Tài khoản thanh toán");
        checking.setStatus("ACTIVE");
        checking.setBalance(15_850_000);
        checking.setActualBalance(15_900_000);
        checking.setHoldBalance(50_000);
        checking.setDailyTransferLimit(500_000_000);
        checking.setBranchName("TDTU - CN Tân Phong, Q.7");
        checking.setOpenedDate("15/09/2021");
        checking.setLinkedCardMasked("Visa Platinum •••• 4289");
        checking.setOnlinePaymentEnabled(true);

        BankAccount saving = new BankAccount();
        saving.setAccountId("acc_saving_01");
        saving.setUserId(uid);
        saving.setAccountType(BankAccount.TYPE_SAVING);
        saving.setAccountNumber("1903 9999 1111");
        saving.setTitle("Tiết kiệm điện tử TDT Plus");
        saving.setStatus("ACTIVE");
        saving.setBalance(30_000_000);
        saving.setInterestRate(5.8);
        saving.setTermMonths(12);
        saving.setElapsedMonths(3);
        saving.setOpenedDate("15/08/2026");
        saving.setMaturityDate("15/08/2027");
        saving.setPayoutMethod("Cuối kỳ");
        saving.setRenewalOption("Tự động tái tục cả gốc & lãi");
        saving.setBeneficiaryAccount("1903 8888 6666");

        BankAccount mortgage = new BankAccount();
        mortgage.setAccountId("acc_mortgage_01");
        mortgage.setUserId(uid);
        mortgage.setAccountType(BankAccount.TYPE_MORTGAGE);
        mortgage.setAccountNumber("MTG-2026-089");
        mortgage.setTitle("Vay mua nhà TDT Home");
        mortgage.setStatus("ON_TIME");
        mortgage.setBalance(420_000_000);
        mortgage.setInitialPrincipal(600_000_000);
        mortgage.setInterestRate(9.8);
        mortgage.setPaidInstallments(36);
        mortgage.setTotalInstallments(120);
        mortgage.setNextDueDate("05/12/2026");
        mortgage.setDaysUntilDue(12);
        mortgage.setCollateralName("Căn hộ Sky Garden 3");
        mortgage.setCollateralLocation("Quận 7, TP. Hồ Chí Minh");
        mortgage.setCollateralValuation(1_850_000_000);
        mortgage.setAutoDebitEnabled(true);
        mortgage.setLoanOfficerName("Nguyễn Hoàng Nam");
        mortgage.setLoanOfficerBranch("Phòng Tín dụng BĐS • TDT Tân Bình");
        mortgage.setLoanOfficerPhone("0912345678");

        DocumentReference accCol = db.collection("users").document(uid).collection("accounts").document("acc_checking_01");
        batch.set(accCol, checking);
        batch.set(db.collection("users").document(uid).collection("accounts").document("acc_saving_01"), saving);
        batch.set(db.collection("users").document(uid).collection("accounts").document("acc_mortgage_01"), mortgage);

        long now = System.currentTimeMillis();
        batch.set(accCol.collection("transactions").document("tx_01"),
                new AccountTransaction("tx_01", "acc_checking_01", "Chuyển tiền - LÊ HOÀNG ĐẠT",
                        "23/11/2026 • 09:30", "Hôm nay, 23/11/2026", -2_500_000, "SUCCESS", "TRANSFER", now));
        batch.set(accCol.collection("transactions").document("tx_02"),
                new AccountTransaction("tx_02", "acc_checking_01", "Nạp tiền từ VNPay",
                        "22/11/2026 • 14:15", "Giao dịch trước đó", 5_000_000, "SUCCESS", "TOPUP", now - 86_400_000L));
        batch.set(accCol.collection("transactions").document("tx_03"),
                new AccountTransaction("tx_03", "acc_checking_01", "Tiền điện - Điện lực MN",
                        "20/11/2026 • 18:40", "Giao dịch trước đó", -845_000, "SUCCESS", "BILL", now - 259_200_000L));
        batch.set(accCol.collection("transactions").document("tx_04"),
                new AccountTransaction("tx_04", "acc_checking_01", "Nhận lương - TDT Corp",
                        "15/11/2026 • 08:00", "Giao dịch trước đó", 25_000_000, "SUCCESS", "SALARY", now - 691_200_000L));

        List<BankAccount> seeded = new ArrayList<>();
        seeded.add(checking);
        seeded.add(saving);
        seeded.add(mortgage);

        batch.commit()
                .addOnSuccessListener(unused -> callback.onSuccess(seeded))
                .addOnFailureListener(e -> callback.onError("Không thể khởi tạo dữ liệu tài khoản: " + e.getMessage()));
    }
}