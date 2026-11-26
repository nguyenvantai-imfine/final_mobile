package com.example.ck_mobile.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ck_mobile.model.UserProfile;
import com.example.ck_mobile.repository.CustomerRepository;
import com.example.ck_mobile.repository.RepoCallback;

import java.util.List;

public class CustomerListViewModel extends ViewModel {

    private final CustomerRepository repo = new CustomerRepository();
    private final MutableLiveData<List<UserProfile>> customers = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public LiveData<List<UserProfile>> getCustomers() {
        return customers;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void load() {
        loading.setValue(true);
        repo.listCustomers(new RepoCallback<List<UserProfile>>() {
            @Override
            public void onSuccess(List<UserProfile> result) {
                loading.setValue(false);
                customers.setValue(result);
            }

            @Override
            public void onError(String message) {
                loading.setValue(false);
                error.setValue(message);
            }
        });
    }
}
