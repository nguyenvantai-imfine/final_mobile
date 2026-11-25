package com.example.ck_mobile.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ck_mobile.R;
import com.example.ck_mobile.model.UserProfile;
import com.example.ck_mobile.repository.CustomerRepository;
import com.google.android.material.appbar.MaterialToolbar;

public class ActivityProfile extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private EditText etFullName, etPhone, etEmail, etAddress;
    private Button btnSaveProfile;
    private UserProfile userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userProfile = CustomerRepository.getInstance().getUserProfile();

        toolbar = findViewById(R.id.toolbar);
        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etAddress = findViewById(R.id.etAddress);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);

        toolbar.setNavigationOnClickListener(v -> finish());

        loadProfileData();

        btnSaveProfile.setOnClickListener(v -> saveProfileData());
    }

    private void loadProfileData() {
        if (userProfile != null) {
            etFullName.setText(userProfile.getFullName());
            etPhone.setText(userProfile.getPhone());
            etEmail.setText(userProfile.getEmail());
            etAddress.setText(userProfile.getAddress());
        }
    }

    private void saveProfileData() {
        String name = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String address = etAddress.getText() != null ? etAddress.getText().toString().trim() : "";

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Họ tên và Số điện thoại không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        userProfile.setFullName(name);
        userProfile.setPhone(phone);
        userProfile.setEmail(email);
        userProfile.setAddress(address);

        Toast.makeText(this, "Đã cập nhật hồ sơ cá nhân thành công!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
