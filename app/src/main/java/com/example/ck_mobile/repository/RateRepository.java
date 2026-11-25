package com.example.ck_mobile.repository;

import com.example.ck_mobile.model.InterestRate;
import com.example.ck_mobile.utils.Constants;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** T28: lãi suất tiết kiệm theo kỳ hạn (collection "rates"), Officer chỉnh, Customer đọc. */
public class RateRepository {

    public static final int[] TERMS = {1, 3, 6, 12, 24};
    private static final double[] DEFAULT_RATES = {3.0, 4.5, 5.5, 6.0, 6.2};

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    /** Trả về đủ các kỳ hạn chuẩn; kỳ hạn chưa có trong Firestore dùng lãi suất mặc định. */
    public void loadRates(RepoCallback<List<InterestRate>> cb) {
        db.collection(Constants.COL_RATES).get()
                .addOnSuccessListener(qs -> {
                    Map<Integer, Double> stored = new HashMap<>();
                    for (DocumentSnapshot d : qs.getDocuments()) {
                        InterestRate r = d.toObject(InterestRate.class);
                        if (r != null) stored.put(r.getTermMonths(), r.getRate());
                    }
                    List<InterestRate> out = new ArrayList<>();
                    for (int i = 0; i < TERMS.length; i++) {
                        Double v = stored.get(TERMS[i]);
                        out.add(new InterestRate(TERMS[i], v != null ? v : DEFAULT_RATES[i]));
                    }
                    Collections.sort(out, (a, b) -> Integer.compare(a.getTermMonths(), b.getTermMonths()));
                    cb.onSuccess(out);
                })
                .addOnFailureListener(e -> cb.onError(RepoCallback.messageOf(e, "Không tải được lãi suất.")));
    }

    public void saveRates(List<InterestRate> rates, RepoCallback<Void> cb) {
        WriteBatch batch = db.batch();
        StringBuilder detail = new StringBuilder("Lãi suất mới:");
        for (InterestRate r : rates) {
            Map<String, Object> m = new HashMap<>();
            m.put("termMonths", r.getTermMonths());
            m.put("rate", r.getRate());
            batch.set(db.collection(Constants.COL_RATES).document("m" + r.getTermMonths()), m);
            detail.append(String.format(Locale.US, " %dT=%.2f%%", r.getTermMonths(), r.getRate()));
        }
        AuditRepository.addToBatch(db, batch, "UPDATE_RATES", "-", detail.toString());
        batch.commit()
                .addOnSuccessListener(v -> cb.onSuccess(null))
                .addOnFailureListener(e -> cb.onError(RepoCallback.messageOf(e, "Không lưu được lãi suất.")));
    }
}
