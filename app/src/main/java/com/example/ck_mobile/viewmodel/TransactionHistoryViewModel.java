package com.example.ck_mobile.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ck_mobile.model.TransactionRecord;
import com.example.ck_mobile.repository.RepoCallback;
import com.example.ck_mobile.repository.TransactionRepository;
import com.example.ck_mobile.utils.MoneyUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/** T27: lịch sử giao dịch với bộ lọc theo tháng (yyyy-MM) và loại giao dịch. */
public class TransactionHistoryViewModel extends ViewModel {

    private final TransactionRepository repo = new TransactionRepository();
    private final List<TransactionRecord> all = new ArrayList<>();
    private final MutableLiveData<List<TransactionRecord>> filtered = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<String>> months = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    private String monthFilter; // null = tất cả
    private String typeFilter;  // null = tất cả

    public LiveData<List<TransactionRecord>> getFiltered() {
        return filtered;
    }

    public LiveData<List<String>> getMonths() {
        return months;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void load(String ownerUid) {
        loading.setValue(true);
        repo.loadByOwner(ownerUid, new RepoCallback<List<TransactionRecord>>() {
            @Override
            public void onSuccess(List<TransactionRecord> result) {
                loading.setValue(false);
                all.clear();
                all.addAll(result);
                TreeSet<String> set = new TreeSet<>((a, b) -> b.compareTo(a)); // tháng mới nhất trước
                for (TransactionRecord t : all) set.add(MoneyUtils.monthKey(t.getCreatedAt()));
                set.remove("");
                months.setValue(new ArrayList<>(set));
                applyFilters();
            }

            @Override
            public void onError(String message) {
                loading.setValue(false);
                error.setValue(message);
            }
        });
    }

    public void setMonthFilter(String monthKeyOrNull) {
        monthFilter = monthKeyOrNull;
        applyFilters();
    }

    public void setTypeFilter(String typeOrNull) {
        typeFilter = typeOrNull;
        applyFilters();
    }

    private void applyFilters() {
        List<TransactionRecord> out = new ArrayList<>();
        for (TransactionRecord t : all) {
            if (monthFilter != null && !monthFilter.equals(MoneyUtils.monthKey(t.getCreatedAt()))) continue;
            if (typeFilter != null && !typeFilter.equals(t.getType())) continue;
            out.add(t);
        }
        filtered.setValue(out);
    }
}
