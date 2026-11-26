package com.example.ck_mobile.repository;

import com.example.ck_mobile.model.TransactionRecord;
import com.example.ck_mobile.utils.Constants;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** T27: đọc lịch sử giao dịch của một khách hàng. */
public class TransactionRepository {

    private static final int MAX_ITEMS = 500;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    /** Lọc theo ownerUid rồi sắp xếp mới nhất trước ở client để không cần tạo composite index. */
    public void loadByOwner(String ownerUid, RepoCallback<List<TransactionRecord>> cb) {
        db.collection(Constants.COL_TRANSACTIONS)
                .whereEqualTo("ownerUid", ownerUid)
                .limit(MAX_ITEMS)
                .get()
                .addOnSuccessListener(qs -> {
                    List<TransactionRecord> out = new ArrayList<>();
                    for (DocumentSnapshot d : qs.getDocuments()) {
                        TransactionRecord t = d.toObject(TransactionRecord.class);
                        if (t != null) out.add(t);
                    }
                    Collections.sort(out, (a, b) -> {
                        long ta = a.getCreatedAt() == null ? 0 : a.getCreatedAt().getTime();
                        long tb = b.getCreatedAt() == null ? 0 : b.getCreatedAt().getTime();
                        return Long.compare(tb, ta);
                    });
                    cb.onSuccess(out);
                })
                .addOnFailureListener(e -> cb.onError(RepoCallback.messageOf(e, "Không tải được lịch sử giao dịch.")));
    }
}
