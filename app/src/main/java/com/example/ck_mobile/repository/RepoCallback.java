package com.example.ck_mobile.repository;

/** Callback kết quả bất đồng bộ cho các Repository. */
public interface RepoCallback<T> {
    void onSuccess(T result);

    void onError(String message);

    static String messageOf(Exception e, String fallback) {
        if (e == null || e.getMessage() == null || e.getMessage().trim().isEmpty()) return fallback;
        return e.getMessage();
    }
}
