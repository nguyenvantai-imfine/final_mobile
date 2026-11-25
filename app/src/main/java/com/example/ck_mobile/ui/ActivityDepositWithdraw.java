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

public class ActivityDepositWithdraw extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private RadioButton rbDeposit, rbWithdraw;
    private EditText etAmount, etOtp;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit_withdraw);

        toolbar = findViewById(R.id.toolbar);
        rbDeposit = findViewById(R.id.rbDeposit);
        rbWithdraw = findViewById(R.id.rbWithdraw);
        etAmount = findViewById(R.id.etAmount);
        etOtp = findViewById(R.id.etOtp);
        btnSubmit = findViewById(R.id.btnSubmit);

        toolbar.setNavigationOnClickListener(v -> finish());
        btnSubmit.setOnClickListener(v -> processTransaction());
    }

    private void processTransaction() {
        String strAmount = etAmount.getText() != null ? etAmount.getText().toString().trim() : "";
        String otp = etOtp.getText() != null ? etOtp.getText().toString().trim() : "";

        if (TextUtils.isEmpty(strAmount)) {
            Toast.makeText(this, "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(otp)) {
            Toast.makeText(this, "Vui lòng nhập mã OTP (Ví dụ: 123456)", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(strAmount);
        boolean isDeposit = rbDeposit.isChecked();

        String typeName = isDeposit ? "Nạp tiền" : "Rút tiền";
        String details = isDeposit ? "Nạp tiền qua Stripe/VNPay Sandbox" : "Rút tiền về tài khoản ngân hàng liên kết";

        CustomerRepository.getInstance().addTransaction(typeName + " tài khoản Checking", typeName, amount, isDeposit, details);

        Intent intent = new Intent(this, ActivityReceipt.class);
        intent.putExtra("status", "THÀNH CÔNG");
        intent.putExtra("amount", amount);
        intent.putExtra("details", typeName + " • " + details);
        startActivity(intent);
        finish();
    }
}
