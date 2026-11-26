package com.example.ck_mobile.repository;

import com.example.ck_mobile.model.ChatMessage;
import com.example.ck_mobile.utils.Constants;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** T46: lưu và tải lịch sử chat tại users/{uid}/chat_messages (ghi theo kiểu best-effort). */
public class ChatRepository {

    private static final int LOAD_LIMIT = 50;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference col(String uid) {
        return db.collection(Constants.COL_USERS).document(uid).collection(Constants.COL_CHAT);
    }

    public void loadRecent(String uid, RepoCallback<List<ChatMessage>> cb) {
        col(uid).orderBy("createdAt", Query.Direction.DESCENDING).limit(LOAD_LIMIT).get()
                .addOnSuccessListener(qs -> {
                    List<ChatMessage> out = new ArrayList<>();
                    for (DocumentSnapshot d : qs.getDocuments()) {
                        ChatMessage m = d.toObject(ChatMessage.class);
                        if (m != null) out.add(m);
                    }
                    Collections.reverse(out); // cũ -> mới
                    cb.onSuccess(out);
                })
                .addOnFailureListener(e -> cb.onError(RepoCallback.messageOf(e, "Không tải được lịch sử chat.")));
    }

    public void save(String uid, ChatMessage m) {
        col(uid).add(m); // lỗi ghi không chặn luồng chat
    }
}
