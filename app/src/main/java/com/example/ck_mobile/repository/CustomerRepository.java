package com.example.ck_mobile.repository;

import android.content.Context;

import com.example.ck_mobile.model.UserProfile;
import com.example.ck_mobile.utils.AuthSessionHelper;
import com.example.ck_mobile.utils.Constants;
import com.example.ck_mobile.utils.MoneyUtils;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Nghiệp vụ Officer: danh sách / tạo / sửa / khóa khách hàng (T13, T21, T22). */
public class CustomerRepository {

    private static final String SECONDARY_APP = "officer_create_customer";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void listCustomers(RepoCallback<List<UserProfile>> cb) {
        db.collection(Constants.COL_USERS)
                .whereEqualTo("role", AuthSessionHelper.ROLE_CUSTOMER)
                .get()
                .addOnSuccessListener(qs -> {
                    List<UserProfile> out = new ArrayList<>();
                    for (DocumentSnapshot d : qs.getDocuments()) {
                        UserProfile u = d.toObject(UserProfile.class);
                        if (u != null) out.add(u);
                    }
                    Collections.sort(out, (a, b) -> safe(a.getFullName()).compareToIgnoreCase(safe(b.getFullName())));
                    cb.onSuccess(out);
                })
                .addOnFailureListener(e -> cb.onError(RepoCallback.messageOf(e, "Không tải được danh sách khách hàng.")));
    }

    public void getCustomer(String uid, RepoCallback<UserProfile> cb) {
        db.collection(Constants.COL_USERS).document(uid).get()
                .addOnSuccessListener(d -> {
                    UserProfile u = d.exists() ? d.toObject(UserProfile.class) : null;
                    if (u == null) cb.onError("Không tìm thấy khách hàng.");
                    else cb.onSuccess(u);
                })
                .addOnFailureListener(e -> cb.onError(RepoCallback.messageOf(e, "Không tải được khách hàng.")));
    }

    /**
     * Tạo khách hàng mới. Auth user được tạo bằng FirebaseApp phụ để Officer KHÔNG bị đăng xuất
     * (createUserWithEmailAndPassword trên app chính sẽ đăng nhập thay Officer).
     */
    public void createCustomer(Context ctx, String fullName, String email, String password,
                               String phone, String address, long initialBalance,
                               RepoCallback<UserProfile> cb) {
        final String normEmail = email.trim().toLowerCase(Locale.ROOT);
        FirebaseAuth secondaryAuth = FirebaseAuth.getInstance(secondaryApp(ctx));

        secondaryAuth.createUserWithEmailAndPassword(normEmail, password)
                .addOnSuccessListener(result -> {
                    if (result.getUser() == null) {
                        cb.onError("Không tạo được tài khoản đăng nhập.");
                        return;
                    }
                    String uid = result.getUser().getUid();
                    secondaryAuth.signOut();
                    writeNewCustomerDocs(uid, fullName, normEmail, phone, address, initialBalance, cb);
                })
                .addOnFailureListener(e -> {
                    if (e instanceof FirebaseAuthUserCollisionException) {
                        cb.onError("Email này đã được sử dụng.");
                    } else {
                        cb.onError(RepoCallback.messageOf(e, "Không tạo được tài khoản đăng nhập."));
                    }
                });
    }

    private void writeNewCustomerDocs(String uid, String fullName, String email, String phone,
                                      String address, long initialBalance, RepoCallback<UserProfile> cb) {
        String accountNumber = MoneyUtils.randomAccountNumber();
        WriteBatch batch = db.batch();

        Map<String, Object> user = new HashMap<>();
        user.put("fullName", fullName);
        user.put("email", email);
        user.put("phone", phone);
        user.put("address", address);
        user.put("accountNumber", accountNumber);
        user.put("role", AuthSessionHelper.ROLE_CUSTOMER);
        user.put("status", Constants.STATUS_ACTIVE);
        user.put("kycStatus", Constants.KYC_NONE);
        user.put("createdAt", FieldValue.serverTimestamp());
        batch.set(db.collection(Constants.COL_USERS).document(uid), user);

        DocumentReference accRef = db.collection(Constants.COL_ACCOUNTS).document();
        Map<String, Object> acc = new HashMap<>();
        acc.put("ownerUid", uid);
        acc.put("type", Constants.ACC_CHECKING);
        acc.put("number", accountNumber);
        acc.put("balance", initialBalance);
        acc.put("createdAt", FieldValue.serverTimestamp());
        batch.set(accRef, acc);

        if (!phone.isEmpty()) {
            batch.set(db.collection(Constants.COL_LOGIN_LOOKUP).document("phone_" + phone),
                    lookup(uid, email, Constants.STATUS_ACTIVE));
        }
        batch.set(db.collection(Constants.COL_LOGIN_LOOKUP).document("acc_" + accountNumber),
                lookup(uid, email, Constants.STATUS_ACTIVE));

        AuditRepository.addToBatch(db, batch, "CREATE_CUSTOMER", uid, "Tạo khách hàng " + fullName);

        batch.commit()
                .addOnSuccessListener(v -> {
                    UserProfile p = new UserProfile();
                    p.setUid(uid);
                    p.setFullName(fullName);
                    p.setEmail(email);
                    p.setPhone(phone);
                    p.setAddress(address);
                    p.setAccountNumber(accountNumber);
                    p.setRole(AuthSessionHelper.ROLE_CUSTOMER);
                    p.setStatus(Constants.STATUS_ACTIVE);
                    cb.onSuccess(p);
                })
                .addOnFailureListener(e -> cb.onError("Đã tạo tài khoản đăng nhập nhưng ghi hồ sơ thất bại: "
                        + RepoCallback.messageOf(e, "lỗi không xác định")
                        + " (kiểm tra Firestore Security Rules cho role OFFICER)."));
    }

    /** Sửa họ tên / SĐT / địa chỉ và đồng bộ login_lookup theo SĐT (có audit log). */
    public void updateCustomer(UserProfile old, String fullName, String phone, String address,
                               RepoCallback<Void> cb) {
        WriteBatch batch = db.batch();

        Map<String, Object> fields = new HashMap<>();
        fields.put("fullName", fullName);
        fields.put("phone", phone);
        fields.put("address", address);
        batch.update(db.collection(Constants.COL_USERS).document(old.getUid()), fields);

        String oldPhone = safe(old.getPhone());
        if (!oldPhone.equals(phone)) {
            if (!oldPhone.isEmpty()) {
                batch.delete(db.collection(Constants.COL_LOGIN_LOOKUP).document("phone_" + oldPhone));
            }
            if (!phone.isEmpty()) {
                batch.set(db.collection(Constants.COL_LOGIN_LOOKUP).document("phone_" + phone),
                        lookup(old.getUid(), old.getEmail(), statusOrActive(old)));
            }
        }

        AuditRepository.addToBatch(db, batch, "UPDATE_CUSTOMER", old.getUid(),
                "Sửa: tên=" + fullName + ", sđt=" + phone + ", địa chỉ=" + address);

        batch.commit()
                .addOnSuccessListener(v -> cb.onSuccess(null))
                .addOnFailureListener(e -> cb.onError(RepoCallback.messageOf(e, "Không lưu được thay đổi.")));
    }

    /** Khóa / mở khóa khách hàng; đồng bộ login_lookup để LoginActivity chặn đúng. */
    public void setLocked(UserProfile u, boolean locked, RepoCallback<Void> cb) {
        String status = locked ? Constants.STATUS_LOCKED : Constants.STATUS_ACTIVE;
        WriteBatch batch = db.batch();

        batch.update(db.collection(Constants.COL_USERS).document(u.getUid()), "status", status);

        Map<String, Object> lk = lookup(u.getUid(), u.getEmail(), status);
        if (!safe(u.getPhone()).isEmpty()) {
            batch.set(db.collection(Constants.COL_LOGIN_LOOKUP).document("phone_" + u.getPhone()),
                    lk, SetOptions.merge());
        }
        if (!safe(u.getAccountNumber()).isEmpty()) {
            batch.set(db.collection(Constants.COL_LOGIN_LOOKUP).document("acc_" + u.getAccountNumber()),
                    lk, SetOptions.merge());
        }

        AuditRepository.addToBatch(db, batch, locked ? "LOCK_CUSTOMER" : "UNLOCK_CUSTOMER", u.getUid(),
                (locked ? "Khóa " : "Mở khóa ") + safe(u.getFullName()));

        batch.commit()
                .addOnSuccessListener(v -> cb.onSuccess(null))
                .addOnFailureListener(e -> cb.onError(RepoCallback.messageOf(e, "Không đổi được trạng thái.")));
    }

    // ---- helpers ----

    private static Map<String, Object> lookup(String uid, String email, String status) {
        Map<String, Object> m = new HashMap<>();
        m.put("uid", uid);
        m.put("email", email);
        m.put("status", status);
        return m;
    }

    private static String statusOrActive(UserProfile u) {
        return u.getStatus() == null ? Constants.STATUS_ACTIVE : u.getStatus();
    }

    private static FirebaseApp secondaryApp(Context ctx) {
        try {
            return FirebaseApp.getInstance(SECONDARY_APP);
        } catch (IllegalStateException notInitialized) {
            return FirebaseApp.initializeApp(ctx.getApplicationContext(),
                    FirebaseApp.getInstance().getOptions(), SECONDARY_APP);
        }
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }
}
