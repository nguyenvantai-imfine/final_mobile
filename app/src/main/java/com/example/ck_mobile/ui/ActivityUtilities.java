package com.example.ck_mobile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ck_mobile.R;
import com.example.ck_mobile.repository.CustomerRepository;
import com.google.android.material.appbar.MaterialToolbar;

public class ActivityUtilities extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private RadioButton rbElectricity, rbWater, rbPhone, rbAirTicket;
    private EditText etCode, etAmount, etOtp;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utilities);

        toolbar = findViewById(R.id.toolbar);
        rbElectricity = findViewById(R.id.rbElectricity);
        rbWater = findViewById(R.id.rbWater);
        rbPhone = findViewById(R.id.rbPhone);
        rbAirTicket = findViewById(R.id.rbAirTicket);

        etCode = findViewById(R.id.etCode);
        etAmount = findViewById(R.id.etAmount);
        etOtp = findViewById(R.id.etOtp);
        btnSubmit = findViewById(R.id.btnSubmit);

        toolbar.setNavigationOnClickListener(v -> finish());
        btnSubmit.setOnClickListener(v -> processUtility());
    }

    private void processUtility() {
        String code = etCode.getText() != null ? etCode.getText().toString().trim() : "";
        String strAmount = etAmount.getText() != null ? etAmount.getText().toString().trim() : "";
        String otp = etOtp.getText() != null ? etOtp.getText().toString().trim() : "";

        if (TextUtils.isEmpty(code) || TextUtils.isEmpty(strAmount)) {
            Toast.makeText(this, "Vui lòng nhập thông tin dịch vụ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(otp)) {
            Toast.makeText(this, "Vui lòng nhập mã OTP 2FA", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(strAmount);
        String category = "Tiện ích";
        String title = "Thanh toán dịch vụ";

        if (rbElectricity.isChecked()) {
            title = "Thanh toán Tiền điện EVN";
            category = "Điện";
        } else if (rbWater.isChecked()) {
            title = "Thanh toán Tiền nước SAWACO";
            category = "Nước";
        } else if (rbPhone.isChecked()) {
            title = "Nạp tiền điện thoại";
            category = "Điện thoại";
        } else if (rbAirTicket.isChecked()) {
            title = "Đặt vé máy bay / E-commerce";
            category = "Vé máy bay";
        }

        String details = "Mã Dịch vụ/SĐT: " + code;
        CustomerRepository.getInstance().addTransaction(title, category, amount, false, details);

        Intent intent = new Intent(this, ActivityReceipt.class);
        intent.putExtra("status", "THÀNH CÔNG");
        intent.putExtra("amount", amount);
        intent.putExtra("details", title + "\n" + details);
        startActivity(intent);
        finish();
    }
}
