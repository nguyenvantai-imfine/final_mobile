package com.example.ck_mobile.utils;

import androidx.annotation.NonNull;

import com.example.ck_mobile.model.AccountTransaction;
import com.example.ck_mobile.model.BankAccount;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;

public final class FirestoreTestDataSeeder {

    public interface SeedCallback {
        void onSuccess(String message);
        void onError(String errorMessage);
    }

    private FirestoreTestDataSeeder() {
    }

    /**
     * Tạo toàn bộ dữ liệu mẫu để test đăng nhập (Email / SĐT / STK / Tài khoản khóa / Officer)
     * và toàn bộ luồng tài khoản (Checking, Saving, Mortgage, Transactions).
     *
     * @param customerUid UID thật trên Firebase Auth của tài khoản CUSTOMER test
     * @param officerUid  UID thật trên Firebase Auth của tài khoản OFFICER test (có thể null nếu chưa tạo)
     * @param lockedUid   UID thật trên Firebase Auth của tài khoản bị khóa (có thể null nếu chưa tạo)
     */
    public static void seedAllTestData(
            @NonNull String customerUid,
            String officerUid,
            String lockedUid,
            @NonNull SeedCallback callback
    ) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        WriteBatch batch = db.batch();

        // 1. COLLECTION: banks
        Map<String, Object> tdtBank = new HashMap<>();
        tdtBank.put("code", "TDTB");
        tdtBank.put("shortName", "TDT Sovereign");
        tdtBank.put("fullName", "Ngân hàng TMCP TDT Sovereign Elite");
        tdtBank.put("swiftCode", "TDTBVNVX");
        tdtBank.put("active", true);
        batch.set(db.collection("banks").document("bank_tdtb"), tdtBank);

        Map<String, Object> vcbBank = new HashMap<>();
        vcbBank.put("code", "VCB");
        vcbBank.put("shortName", "Vietcombank");
        vcbBank.put("fullName", "Ngân hàng TMCP Ngoại thương Việt Nam");
        vcbBank.put("swiftCode", "BFTVVNVX");
        vcbBank.put("active", true);
        batch.set(db.collection("banks").document("bank_vcb"), vcbBank);

        // 2. COLLECTION: branches
        Map<String, Object> branchQ7 = new HashMap<>();
        branchQ7.put("code", "CN-Q7-01");
        branchQ7.put("name", "TDTU - CN Tân Phong, Q.7");
        branchQ7.put("address", "19 Nguyễn Hữu Thọ, Tân Phong, Quận 7, TP. HCM");
        branchQ7.put("phone", "02837755035");
        branchQ7.put("hotline", "19006868");
        branchQ7.put("active", true);
        batch.set(db.collection("branches").document("branch_q7_tanphong"), branchQ7);

        Map<String, Object> branchTanBinh = new HashMap<>();
        branchTanBinh.put("code", "CN-TB-02");
        branchTanBinh.put("name", "Phòng Tín dụng BĐS • TDT Tân Bình");
        branchTanBinh.put("address", "45 Cộng Hòa, Phường 4, Quận Tân Bình, TP. HCM");
        branchTanBinh.put("phone", "02838112233");
        branchTanBinh.put("hotline", "19006868");
        branchTanBinh.put("active", true);
        batch.set(db.collection("branches").document("branch_tb_conghoa"), branchTanBinh);

        // 3. COLLECTION: config
        Map<String, Object> appConfig = new HashMap<>();
        appConfig.put("coreBankingVersion", "Core Banking v4.2");
        appConfig.put("sessionTimeoutMinutes", 10);
        appConfig.put("maxLoginFailedAttempts", 5);
        appConfig.put("loginLockoutSeconds", 30);
        appConfig.put("defaultSavingRate", 5.8);
        appConfig.put("demandDepositRate", 0.2);
        appConfig.put("defaultMortgageRate", 9.8);
        appConfig.put("supportHotline", "19006868");
        batch.set(db.collection("config").document("app_config"), appConfig);

        // 4. COLLECTION: users (CUSTOMER)
        Map<String, Object> customerUser = new HashMap<>();
        customerUser.put("fullName", "Nguyễn Văn Tài");
        customerUser.put("email", "customer.test@tdtbank.vn");
        customerUser.put("phone", "0901234567");
        customerUser.put("address", "Quận 7, TP. Hồ Chí Minh");
        customerUser.put("accountNumber", "190388886666");
        customerUser.put("role", AuthSessionHelper.ROLE_CUSTOMER);
        customerUser.put("status", "ACTIVE");
        batch.set(db.collection("users").document(customerUid), customerUser);

        // Tra cứu đăng nhập bằng SĐT và Số tài khoản cho CUSTOMER
        Map<String, Object> lookupPhoneCustomer = new HashMap<>();
        lookupPhoneCustomer.put("email", "customer.test@tdtbank.vn");
        lookupPhoneCustomer.put("role", AuthSessionHelper.ROLE_CUSTOMER);
        lookupPhoneCustomer.put("status", "ACTIVE");
        lookupPhoneCustomer.put("uid", customerUid);
        batch.set(db.collection("login_lookup").document("phone_0901234567"), lookupPhoneCustomer);

        Map<String, Object> lookupAccCustomer = new HashMap<>();
        lookupAccCustomer.put("email", "customer.test@tdtbank.vn");
        lookupAccCustomer.put("role", AuthSessionHelper.ROLE_CUSTOMER);
        lookupAccCustomer.put("status", "ACTIVE");
        lookupAccCustomer.put("uid", customerUid);
        batch.set(db.collection("login_lookup").document("acc_190388886666"), lookupAccCustomer);

        // 5. COLLECTION: users (OFFICER - tùy chọn)
        if (officerUid != null && !officerUid.trim().isEmpty()) {
            Map<String, Object> officerUser = new HashMap<>();
            officerUser.put("fullName", "Trần Minh Quân");
            officerUser.put("email", "officer.test@tdtbank.vn");
            officerUser.put("phone", "0909888777");
            officerUser.put("address", "Quận 7, TP. Hồ Chí Minh");
            officerUser.put("accountNumber", "200199998888");
            officerUser.put("role", AuthSessionHelper.ROLE_OFFICER);
            officerUser.put("status", "ACTIVE");
            batch.set(db.collection("users").document(officerUid), officerUser);

            Map<String, Object> lookupPhoneOfficer = new HashMap<>();
            lookupPhoneOfficer.put("email", "officer.test@tdtbank.vn");
            lookupPhoneOfficer.put("role", AuthSessionHelper.ROLE_OFFICER);
            lookupPhoneOfficer.put("status", "ACTIVE");
            lookupPhoneOfficer.put("uid", officerUid);
            batch.set(db.collection("login_lookup").document("phone_0909888777"), lookupPhoneOfficer);
        }

        // 6. COLLECTION: users (LOCKED USER - tùy chọn để test chặn tài khoản khóa)
        if (lockedUid != null && !lockedUid.trim().isEmpty()) {
            Map<String, Object> lockedUser = new HashMap<>();
            lockedUser.put("fullName", "Tài Khoản Bị Khóa");
            lockedUser.put("email", "locked.test@tdtbank.vn");
            lockedUser.put("phone", "0905555444");
            lockedUser.put("address", "TP. Hồ Chí Minh");
            lockedUser.put("accountNumber", "190300001111");
            lockedUser.put("role", AuthSessionHelper.ROLE_CUSTOMER);
            lockedUser.put("status", AuthSessionHelper.STATUS_LOCKED);
            batch.set(db.collection("users").document(lockedUid), lockedUser);

            Map<String, Object> lookupLocked = new HashMap<>();
            lookupLocked.put("email", "locked.test@tdtbank.vn");
            lookupLocked.put("role", AuthSessionHelper.ROLE_CUSTOMER);
            lookupLocked.put("status", AuthSessionHelper.STATUS_LOCKED);
            lookupLocked.put("uid", lockedUid);
            batch.set(db.collection("login_lookup").document("phone_0905555444"), lookupLocked);
        }

        // 7. SUBCOLLECTION: users/{customerUid}/accounts (CHECKING, SAVING, MORTGAGE)
        CollectionReference accountsCol = db.collection("users")
                .document(customerUid)
                .collection("accounts");

        BankAccount checking = new BankAccount();
        checking.setAccountId("acc_checking_01");
        checking.setUserId(customerUid);
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

        DocumentReference checkingDoc = accountsCol.document("acc_checking_01");
        batch.set(checkingDoc, checking);

        // Giao dịch mẫu cho CHECKING
        long now = System.currentTimeMillis();
        batch.set(checkingDoc.collection("transactions").document("tx_01"),
                new AccountTransaction("tx_01", "acc_checking_01", "Chuyển tiền - LÊ HOÀNG ĐẠT",
                        "27/11/2026 • 09:30", "Hôm nay, 27/11/2026", -2_500_000, "SUCCESS", "TRANSFER", now));
        batch.set(checkingDoc.collection("transactions").document("tx_02"),
                new AccountTransaction("tx_02", "acc_checking_01", "Nạp tiền từ VNPay",
                        "25/11/2026 • 14:15", "Giao dịch gần đây", 5_000_000, "SUCCESS", "TOPUP", now - 2L * 86_400_000L));
        batch.set(checkingDoc.collection("transactions").document("tx_03"),
                new AccountTransaction("tx_03", "acc_checking_01", "Tiền điện - Điện lực MN",
                        "20/11/2026 • 18:40", "Giao dịch trước đó", -845_000, "SUCCESS", "BILL", now - 7L * 86_400_000L));
        batch.set(checkingDoc.collection("transactions").document("tx_04"),
                new AccountTransaction("tx_04", "acc_checking_01", "Nhận lương - TDT Corp",
                        "15/11/2026 • 08:00", "Giao dịch trước đó", 25_000_000, "SUCCESS", "SALARY", now - 12L * 86_400_000L));

        BankAccount saving = new BankAccount();
        saving.setAccountId("acc_saving_01");
        saving.setUserId(customerUid);
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
        batch.set(accountsCol.document("acc_saving_01"), saving);

        BankAccount mortgage = new BankAccount();
        mortgage.setAccountId("acc_mortgage_01");
        mortgage.setUserId(customerUid);
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
        mortgage.setDaysUntilDue(8);
        mortgage.setCollateralName("Căn hộ Sky Garden 3");
        mortgage.setCollateralLocation("Quận 7, TP. Hồ Chí Minh");
        mortgage.setCollateralValuation(1_850_000_000);
        mortgage.setAutoDebitEnabled(true);
        mortgage.setLoanOfficerName("Nguyễn Hoàng Nam");
        mortgage.setLoanOfficerBranch("Phòng Tín dụng BĐS • TDT Tân Bình");
        mortgage.setLoanOfficerPhone("0912345678");
        batch.set(accountsCol.document("acc_mortgage_01"), mortgage);

        batch.commit()
                .addOnSuccessListener(unused -> callback.onSuccess(
                        "Đã tạo full dữ liệu test trên Firestore cho UID: " + customerUid))
                .addOnFailureListener(e -> callback.onError(
                        "Lỗi ghi dữ liệu test lên Firestore: " + e.getMessage()));
    }
}