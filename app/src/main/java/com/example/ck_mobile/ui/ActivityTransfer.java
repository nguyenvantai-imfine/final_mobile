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

public class ActivityTransfer extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private RadioButton rbInternal;
    private EditText etRecipientAccount, etAmount, etMessage, etOtp;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        toolbar = findViewById(R.id.toolbar);
        rbInternal = findViewById(R.id.rbInternal);
        etRecipientAccount = findViewById(R.id.etRecipientAccount);
        etAmount = findViewById(R.id.etAmount);
        etMessage = findViewById(R.id.etMessage);
        etOtp = findViewById(R.id.etOtp);
        btnSubmit = findViewById(R.id.btnSubmit);

        toolbar.setNavigationOnClickListener(v -> finish());
        btnSubmit.setOnClickListener(v -> processTransfer());
    }

    private void processTransfer() {
        String recipient = etRecipientAccount.getText() != null ? etRecipientAccount.getText().toString().trim() : "";
        String strAmount = etAmount.getText() != null ? etAmount.getText().toString().trim() : "";
        String msg = etMessage.getText() != null ? etMessage.getText().toString().trim() : "";
        String otp = etOtp.getText() != null ? etOtp.getText().toString().trim() : "";

        if (TextUtils.isEmpty(recipient) || TextUtils.isEmpty(strAmount)) {
            Toast.makeText(this, "Vui lòng nhập tài khoản nhận và số tiền", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(otp)) {
            Toast.makeText(this, "Vui lòng nhập mã OTP 2FA", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(strAmount);
        boolean isInternal = rbInternal.isChecked();
        String type = isInternal ? "Chuyển tiền nội bộ" : "Chuyển tiền liên ngân hàng Napas 24/7";
        String details = "Đến STK " + recipient + " • Lời nhắn: " + msg;

        CustomerRepository.getInstance().addTransaction(type, "Chuyển tiền", amount, false, details);

        Intent intent = new Intent(this, ActivityReceipt.class);
        intent.putExtra("status", "THÀNH CÔNG");
        intent.putExtra("amount", amount);
        intent.putExtra("details", type + "\n" + details);
        startActivity(intent);
        finish();
    }
}
