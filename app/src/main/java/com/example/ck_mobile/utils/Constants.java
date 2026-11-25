package com.example.ck_mobile.utils;

/** Tên collection Firestore và các hằng số dùng chung (khớp ERD v1). */
public final class Constants {

    private Constants() {
    }

    // Collections
    public static final String COL_USERS = "users";
    public static final String COL_ACCOUNTS = "accounts";
    public static final String COL_TRANSACTIONS = "transactions";
    public static final String COL_ORDERS = "orders";
    public static final String COL_BRANCHES = "branches";
    public static final String COL_RATES = "rates";
    public static final String COL_KYC = "kyc";
    public static final String COL_AUDIT = "audit_logs";
    public static final String COL_LOGIN_LOOKUP = "login_lookup";
    public static final String COL_CHAT = "chat_messages"; // users/{uid}/chat_messages

    // Loại tài khoản
    public static final String ACC_CHECKING = "CHECKING";
    public static final String ACC_SAVING = "SAVING";
    public static final String ACC_MORTGAGE = "MORTGAGE";

    // Loại giao dịch
    public static final String TX_DEPOSIT = "DEPOSIT";
    public static final String TX_WITHDRAW = "WITHDRAW";
    public static final String TX_TRANSFER_OUT = "TRANSFER_OUT";
    public static final String TX_TRANSFER_IN = "TRANSFER_IN";
    public static final String TX_BILL = "BILL";
    public static final String TX_MOVIE = "MOVIE";
    public static final String TX_HOTEL = "HOTEL";
    public static final String TX_INTEREST = "INTEREST";

    // Trạng thái
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_LOCKED = "LOCKED";

    // eKYC
    public static final String KYC_NONE = "NONE";
    public static final String KYC_PENDING = "PENDING";
    public static final String KYC_APPROVED = "APPROVED";
    public static final String KYC_REJECTED = "REJECTED";

    // Intent extras
    public static final String EXTRA_UID = "extra_uid";
    public static final String EXTRA_SERVICE_TYPE = "extra_service_type";
}
