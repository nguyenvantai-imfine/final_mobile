package com.example.ck_mobile.repository;

import com.example.ck_mobile.model.ServiceItem;
import com.example.ck_mobile.utils.Constants;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * T40: thanh toán đơn vé phim / đặt phòng khách sạn.
 * Trừ tiền tài khoản thanh toán, ghi giao dịch và đơn hàng trong CÙNG một Firestore transaction
 * phía client (hạn chế đã nêu trong báo cáo: không dùng Cloud Functions vì cần gói Blaze).
 */
public class ServiceOrderRepository {

    public static class OrderResult {
        public final String orderId;
        public final long total;
        public final long balanceAfter;

        OrderResult(String orderId, long total, long balanceAfter) {
            this.orderId = orderId;
            this.total = total;
            this.balanceAfter = balanceAfter;
        }
    }

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void pay(String ownerUid, ServiceItem item, int quantity, RepoCallback<OrderResult> cb) {
        if (quantity < 1) {
            cb.onError("Số lượng không hợp lệ.");
            return;
        }
        final long total = item.unitPrice * quantity;

        db.collection(Constants.COL_ACCOUNTS)
                .whereEqualTo("ownerUid", ownerUid)
                .whereEqualTo("type", Constants.ACC_CHECKING)
                .limit(1)
                .get()
                .addOnSuccessListener((QuerySnapshot qs) -> {
                    if (qs.isEmpty()) {
                        cb.onError("Bạn chưa có tài khoản thanh toán.");
                        return;
                    }
                    runPayment(qs.getDocuments().get(0).getReference(), ownerUid, item, quantity, total, cb);
                })
                .addOnFailureListener(e -> cb.onError(RepoCallback.messageOf(e, "Không tìm thấy tài khoản thanh toán.")));
    }

    private void runPayment(DocumentReference accRef, String ownerUid, ServiceItem item, int quantity,
                            long total, RepoCallback<OrderResult> cb) {
        final DocumentReference txRef = db.collection(Constants.COL_TRANSACTIONS).document();
        final DocumentReference orderRef = db.collection(Constants.COL_ORDERS).document();

        db.runTransaction(tr -> {
            DocumentSnapshot acc = tr.get(accRef);
            Long balanceObj = acc.getLong("balance");
            long balance = balanceObj == null ? 0 : balanceObj;
            if (balance < total) {
                throw new FirebaseFirestoreException("Số dư không đủ để thanh toán.",
                        FirebaseFirestoreException.Code.FAILED_PRECONDITION);
            }
            long after = balance - total;
            String number = acc.getString("number");

            tr.update(accRef, "balance", after);

            Map<String, Object> tx = new HashMap<>();
            tx.put("ownerUid", ownerUid);
            tx.put("accountId", accRef.getId());
            tx.put("accountNumber", number);
            tx.put("type", item.type);
            tx.put("amount", total);
            tx.put("credit", false);
            tx.put("description", item.title + " x" + quantity + " " + item.unitLabel);
            tx.put("balanceAfter", after);
            tx.put("createdAt", FieldValue.serverTimestamp());
            tr.set(txRef, tx);

            Map<String, Object> order = new HashMap<>();
            order.put("ownerUid", ownerUid);
            order.put("type", item.type);
            order.put("itemId", item.id);
            order.put("itemTitle", item.title);
            order.put("quantity", quantity);
            order.put("unitPrice", item.unitPrice);
            order.put("total", total);
            order.put("status", "PAID");
            order.put("transactionId", txRef.getId());
            order.put("createdAt", FieldValue.serverTimestamp());
            tr.set(orderRef, order);

            return after;
        }).addOnSuccessListener(after -> cb.onSuccess(new OrderResult(orderRef.getId(), total, after)))
          .addOnFailureListener(e -> cb.onError(RepoCallback.messageOf(e, "Thanh toán thất bại.")));
    }
}
