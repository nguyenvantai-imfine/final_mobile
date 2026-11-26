package com.example.ck_mobile.repository;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.ck_mobile.model.KycRecord;
import com.example.ck_mobile.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** T33 + T34: Officer đăng ký sinh trắc (ảnh khuôn mặt) cho khách hàng và duyệt/từ chối hồ sơ eKYC. */
public class KycRepository {

    private static final long MAX_PHOTO_BYTES = 5L * 1024 * 1024;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    /** status = null để lấy tất cả. Sắp xếp mới nhất trước (client-side, tránh phải tạo index). */
    public void list(String status, RepoCallback<List<KycRecord>> cb) {
        Query q = db.collection(Constants.COL_KYC);
        if (status != null) q = q.whereEqualTo("status", status);
        q.get().addOnSuccessListener(qs -> {
            List<KycRecord> out = new ArrayList<>();
            for (DocumentSnapshot d : qs.getDocuments()) {
                KycRecord r = d.toObject(KycRecord.class);
                if (r != null) out.add(r);
            }
            Collections.sort(out, (a, b) -> {
                long ta = a.getCreatedAt() == null ? 0 : a.getCreatedAt().getTime();
                long tb = b.getCreatedAt() == null ? 0 : b.getCreatedAt().getTime();
                return Long.compare(tb, ta);
            });
            cb.onSuccess(out);
        }).addOnFailureListener(e -> cb.onError(RepoCallback.messageOf(e, "Không tải được hồ sơ eKYC.")));
    }

    public void get(String customerUid, RepoCallback<KycRecord> cb) {
        db.collection(Constants.COL_KYC).document(customerUid).get()
                .addOnSuccessListener(d -> {
                    KycRecord r = d.exists() ? d.toObject(KycRecord.class) : null;
                    if (r == null) cb.onError("Chưa có hồ sơ eKYC.");
                    else cb.onSuccess(r);
                })
                .addOnFailureListener(e -> cb.onError(RepoCallback.messageOf(e, "Không tải được hồ sơ eKYC.")));
    }

    /** Upload ảnh lên Storage rồi tạo/ghi đè hồ sơ kyc ở trạng thái PENDING. */
    public void register(String customerUid, String customerName, byte[] jpegBytes, RepoCallback<Void> cb) {
        String path = "kyc/" + customerUid + "/face_" + System.currentTimeMillis() + ".jpg";
        StorageReference ref = storage.getReference().child(path);
        ref.putBytes(jpegBytes).addOnSuccessListener(task -> {
            FirebaseUser officer = FirebaseAuth.getInstance().getCurrentUser();
            WriteBatch batch = db.batch();

            Map<String, Object> kyc = new HashMap<>();
            kyc.put("customerName", customerName);
            kyc.put("photoPath", path);
            kyc.put("status", Constants.KYC_PENDING);
            kyc.put("note", "");
            kyc.put("registeredBy", officer != null ? officer.getUid() : "unknown");
            kyc.put("createdAt", FieldValue.serverTimestamp());
            kyc.put("reviewedBy", null);
            kyc.put("reviewedAt", null);
            batch.set(db.collection(Constants.COL_KYC).document(customerUid), kyc);

            batch.update(db.collection(Constants.COL_USERS).document(customerUid),
                    "kycStatus", Constants.KYC_PENDING);
            AuditRepository.addToBatch(db, batch, "KYC_REGISTER", customerUid,
                    "Đăng ký sinh trắc cho " + customerName);

            batch.commit()
                    .addOnSuccessListener(v -> cb.onSuccess(null))
                    .addOnFailureListener(e -> cb.onError(RepoCallback.messageOf(e, "Không lưu được hồ sơ eKYC.")));
        }).addOnFailureListener(e -> cb.onError(RepoCallback.messageOf(e, "Tải ảnh lên thất bại.")));
    }

    /** Duyệt hoặc từ chối; trạng thái được đồng bộ sang users.kycStatus để Customer thấy. */
    public void review(KycRecord record, boolean approve, String note, RepoCallback<Void> cb) {
        FirebaseUser officer = FirebaseAuth.getInstance().getCurrentUser();
        String status = approve ? Constants.KYC_APPROVED : Constants.KYC_REJECTED;
        WriteBatch batch = db.batch();

        Map<String, Object> fields = new HashMap<>();
        fields.put("status", status);
        fields.put("note", note == null ? "" : note);
        fields.put("reviewedBy", officer != null ? officer.getUid() : "unknown");
        fields.put("reviewedAt", FieldValue.serverTimestamp());
        batch.update(db.collection(Constants.COL_KYC).document(record.getId()), fields);
        batch.update(db.collection(Constants.COL_USERS).document(record.getId()), "kycStatus", status);
        AuditRepository.addToBatch(db, batch, approve ? "KYC_APPROVE" : "KYC_REJECT", record.getId(),
                (approve ? "Duyệt" : "Từ chối") + " eKYC " + record.getCustomerName()
                        + (approve || note == null || note.isEmpty() ? "" : ": " + note));

        batch.commit()
                .addOnSuccessListener(v -> cb.onSuccess(null))
                .addOnFailureListener(e -> cb.onError(RepoCallback.messageOf(e, "Không cập nhật được hồ sơ.")));
    }

    public void loadPhoto(String photoPath, RepoCallback<Bitmap> cb) {
        storage.getReference().child(photoPath).getBytes(MAX_PHOTO_BYTES)
                .addOnSuccessListener(bytes -> {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    if (bmp == null) cb.onError("Ảnh không hợp lệ.");
                    else cb.onSuccess(bmp);
                })
                .addOnFailureListener(e -> cb.onError(RepoCallback.messageOf(e, "Không tải được ảnh.")));
    }
}
