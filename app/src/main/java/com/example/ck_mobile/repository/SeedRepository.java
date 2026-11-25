package com.example.ck_mobile.repository;

import android.content.Context;

import com.example.ck_mobile.utils.AuthSessionHelper;
import com.example.ck_mobile.utils.Constants;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.WriteBatch;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * T14: nạp dữ liệu mẫu để demo: lãi suất, 8 chi nhánh quanh TP.HCM, và một khách hàng demo
 * (3 loại tài khoản + giao dịch mẫu). Dùng doc id cố định nên chạy lại nhiều lần vẫn an toàn.
 */
public class SeedRepository {

    public static final String DEMO_EMAIL = "khach.demo@tdtbank.vn";
    public static final String DEMO_PASSWORD = "Demo@123456";
    public static final String DEMO_PHONE = "0900000001";
    private static final String DEMO_CHECKING_NO = "1000000001";
    private static final String DEMO_SAVING_NO = "1000000002";
    private static final String DEMO_MORTGAGE_NO = "1000000003";
    private static final String SECONDARY_APP = "officer_seed_demo";

    private static final String[][] BRANCHES = {
            {"TDT Bank - Chi nhánh Quận 1", "12 Nguyễn Huệ, Quận 1", "10.7743", "106.7030"},
            {"TDT Bank - Chi nhánh Quận 3", "88 Võ Văn Tần, Quận 3", "10.7786", "106.6907"},
            {"TDT Bank - Chi nhánh Quận 7", "19 Nguyễn Hữu Thọ, Quận 7", "10.7295", "106.7218"},
            {"TDT Bank - Chi nhánh Bình Thạnh", "45 Điện Biên Phủ, Bình Thạnh", "10.8012", "106.7109"},
            {"TDT Bank - Chi nhánh Tân Bình", "210 Hoàng Văn Thụ, Tân Bình", "10.7993", "106.6615"},
            {"TDT Bank - Chi nhánh Thủ Đức", "1 Võ Văn Ngân, Thủ Đức", "10.8509", "106.7717"},
            {"TDT Bank - Chi nhánh Gò Vấp", "72 Quang Trung, Gò Vấp", "10.8346", "106.6633"},
            {"TDT Bank - Chi nhánh Quận 5", "301 An Dương Vương, Quận 5", "10.7599", "106.6676"}
    };

    // {loại, số tiền, nội dung, ngày trước đây, credit(1/0)} theo thứ tự cũ -> mới
    private static final Object[][] TX = {
            {Constants.TX_DEPOSIT, 20_000_000L, "Nạp tiền mặt tại quầy", 85, 1},
            {Constants.TX_BILL, 850_000L, "Thanh toán tiền điện T9", 80, 0},
            {Constants.TX_TRANSFER_OUT, 3_000_000L, "Chuyển tiền cho Nguyễn Văn B", 72, 0},
            {Constants.TX_TRANSFER_IN, 5_000_000L, "Nhận tiền từ Trần Thị C", 60, 1},
            {Constants.TX_BILL, 320_000L, "Thanh toán tiền nước T10", 48, 0},
            {Constants.TX_MOVIE, 180_000L, "Vé phim 2 vé", 40, 0},
            {Constants.TX_DEPOSIT, 10_000_000L, "Nạp tiền mặt tại quầy", 33, 1},
            {Constants.TX_WITHDRAW, 2_000_000L, "Rút tiền ATM", 25, 0},
            {Constants.TX_HOTEL, 1_500_000L, "Đặt phòng khách sạn 1 đêm", 17, 0},
            {Constants.TX_BILL, 880_000L, "Thanh toán tiền điện T10", 9, 0},
            {Constants.TX_TRANSFER_IN, 4_000_000L, "Nhận tiền từ Lê Văn D", 4, 1},
            {Constants.TX_INTEREST, 150_000L, "Lãi tiền gửi tiết kiệm", 1, 1}
    };

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void seed(Context ctx, RepoCallback<String> cb) {
        FirebaseAuth secondaryAuth = FirebaseAuth.getInstance(secondaryApp(ctx));
        secondaryAuth.createUserWithEmailAndPassword(DEMO_EMAIL, DEMO_PASSWORD)
                .addOnSuccessListener(r -> {
                    String uid = r.getUser() != null ? r.getUser().getUid() : null;
                    secondaryAuth.signOut();
                    if (uid == null) cb.onError("Không tạo được tài khoản demo.");
                    else writeAll(uid, cb);
                })
                .addOnFailureListener(e -> {
                    if (e instanceof FirebaseAuthUserCollisionException) {
                        // Tài khoản demo đã có từ lần seed trước: đăng nhập app phụ để lấy uid
                        secondaryAuth.signInWithEmailAndPassword(DEMO_EMAIL, DEMO_PASSWORD)
                                .addOnSuccessListener(r -> {
                                    String uid = r.getUser() != null ? r.getUser().getUid() : null;
                                    secondaryAuth.signOut();
                                    if (uid == null) cb.onError("Không lấy được uid tài khoản demo.");
                                    else writeAll(uid, cb);
                                })
                                .addOnFailureListener(e2 -> cb.onError(
                                        "Email demo đã tồn tại nhưng mật khẩu khác: " + RepoCallback.messageOf(e2, "")));
                    } else {
                        cb.onError(RepoCallback.messageOf(e, "Không tạo được tài khoản demo."));
                    }
                });
    }

    private void writeAll(String uid, RepoCallback<String> cb) {
        WriteBatch batch = db.batch();

        // Lãi suất
        double[] rates = {3.0, 4.5, 5.5, 6.0, 6.2};
        for (int i = 0; i < RateRepository.TERMS.length; i++) {
            Map<String, Object> m = new HashMap<>();
            m.put("termMonths", RateRepository.TERMS[i]);
            m.put("rate", rates[i]);
            batch.set(db.collection(Constants.COL_RATES).document("m" + RateRepository.TERMS[i]), m);
        }

        // Chi nhánh
        for (int i = 0; i < BRANCHES.length; i++) {
            Map<String, Object> m = new HashMap<>();
            m.put("name", BRANCHES[i][0]);
            m.put("address", BRANCHES[i][1]);
            m.put("lat", Double.parseDouble(BRANCHES[i][2]));
            m.put("lng", Double.parseDouble(BRANCHES[i][3]));
            batch.set(db.collection(Constants.COL_BRANCHES).document(String.format("branch_%02d", i + 1)), m);
        }

        // Khách hàng demo
        Map<String, Object> user = new HashMap<>();
        user.put("fullName", "Khách Hàng Demo");
        user.put("email", DEMO_EMAIL);
        user.put("phone", DEMO_PHONE);
        user.put("address", "19 Nguyễn Hữu Thọ, Quận 7, TP.HCM");
        user.put("accountNumber", DEMO_CHECKING_NO);
        user.put("role", AuthSessionHelper.ROLE_CUSTOMER);
        user.put("status", Constants.STATUS_ACTIVE);
        user.put("kycStatus", Constants.KYC_NONE);
        user.put("createdAt", FieldValue.serverTimestamp());
        batch.set(db.collection(Constants.COL_USERS).document(uid), user);

        Map<String, Object> lk = new HashMap<>();
        lk.put("uid", uid);
        lk.put("email", DEMO_EMAIL);
        lk.put("status", Constants.STATUS_ACTIVE);
        batch.set(db.collection(Constants.COL_LOGIN_LOOKUP).document("phone_" + DEMO_PHONE), lk);
        batch.set(db.collection(Constants.COL_LOGIN_LOOKUP).document("acc_" + DEMO_CHECKING_NO), lk);

        // 3 loại tài khoản
        long checkingFinal = 25_000_000L;
        putAccount(batch, "demo_checking", uid, Constants.ACC_CHECKING, DEMO_CHECKING_NO, checkingFinal);
        putAccount(batch, "demo_saving", uid, Constants.ACC_SAVING, DEMO_SAVING_NO, 100_000_000L);
        putAccount(batch, "demo_mortgage", uid, Constants.ACC_MORTGAGE, DEMO_MORTGAGE_NO, 300_000_000L);

        // Giao dịch mẫu: tính số dư đầu kỳ sao cho số dư cuối = checkingFinal
        long delta = 0;
        for (Object[] t : TX) {
            boolean isCredit = (Integer) t[4] == 1;
            delta += isCredit ? (Long) t[1] : -(Long) t[1];
        }
        long running = checkingFinal - delta;
        long now = System.currentTimeMillis();
        for (int i = 0; i < TX.length; i++) {
            Object[] t = TX[i];
            boolean credit = (Integer) t[4] == 1;
            long amount = (Long) t[1];
            running += credit ? amount : -amount;
            Map<String, Object> m = new HashMap<>();
            m.put("ownerUid", uid);
            m.put("accountId", "demo_checking");
            m.put("accountNumber", DEMO_CHECKING_NO);
            m.put("type", t[0]);
            m.put("amount", amount);
            m.put("credit", credit);
            m.put("description", t[2]);
            m.put("balanceAfter", running);
            m.put("createdAt", new Date(now - (Integer) t[3] * 86_400_000L));
            batch.set(db.collection(Constants.COL_TRANSACTIONS).document(String.format("demo_tx_%02d", i + 1)), m);
        }

        AuditRepository.addToBatch(db, batch, "SEED_DATA", uid, "Nạp dữ liệu mẫu (lãi suất, chi nhánh, khách demo)");

        batch.commit()
                .addOnSuccessListener(v -> cb.onSuccess("Đã nạp dữ liệu mẫu.\nĐăng nhập demo: " + DEMO_EMAIL
                        + "\nMật khẩu: " + DEMO_PASSWORD + "\nSĐT: " + DEMO_PHONE))
                .addOnFailureListener(e -> cb.onError(RepoCallback.messageOf(e, "Nạp dữ liệu mẫu thất bại.")));
    }

    private void putAccount(WriteBatch batch, String docId, String uid, String type, String number, long balance) {
        Map<String, Object> m = new HashMap<>();
        m.put("ownerUid", uid);
        m.put("type", type);
        m.put("number", number);
        m.put("balance", balance);
        m.put("createdAt", FieldValue.serverTimestamp());
        batch.set(db.collection(Constants.COL_ACCOUNTS).document(docId), m);
    }

    private static FirebaseApp secondaryApp(Context ctx) {
        try {
            return FirebaseApp.getInstance(SECONDARY_APP);
        } catch (IllegalStateException e) {
            return FirebaseApp.initializeApp(ctx.getApplicationContext(),
                    FirebaseApp.getInstance().getOptions(), SECONDARY_APP);
        }
    }
}
