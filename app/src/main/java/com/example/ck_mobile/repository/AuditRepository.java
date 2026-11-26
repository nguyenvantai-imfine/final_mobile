package com.example.ck_mobile.repository;

import com.example.ck_mobile.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;

/** Ghi nhật ký thao tác của Officer vào collection "audit_logs". */
public final class AuditRepository {

    private AuditRepository() {
    }

    /** Thêm một bản ghi audit vào batch đang xây dựng. */
    public static void addToBatch(FirebaseFirestore db, WriteBatch batch,
                                  String action, String targetUid, String detail) {
        FirebaseUser actor = FirebaseAuth.getInstance().getCurrentUser();
        Map<String, Object> log = new HashMap<>();
        log.put("actorUid", actor != null ? actor.getUid() : "unknown");
        log.put("action", action);
        log.put("targetUid", targetUid);
        log.put("detail", detail);
        log.put("createdAt", FieldValue.serverTimestamp());
        batch.set(db.collection(Constants.COL_AUDIT).document(), log);
    }
}
