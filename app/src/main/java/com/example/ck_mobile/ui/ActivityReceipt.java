package com.example.ck_mobile.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ck_mobile.R;
import com.example.ck_mobile.repository.CustomerRepository;

public class ActivityReceipt extends AppCompatActivity {

    private TextView tvReceiptStatus, tvReceiptAmount, tvReceiptDetails;
    private Button btnDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        tvReceiptStatus = findViewById(R.id.tvReceiptStatus);
        tvReceiptAmount = findViewById(R.id.tvReceiptAmount);
        tvReceiptDetails = findViewById(R.id.tvReceiptDetails);
        btnDone = findViewById(R.id.btnDone);

        String status = getIntent().getStringExtra("status");
        double amount = getIntent().getDoubleExtra("amount", 0.0);
        String details = getIntent().getStringExtra("details");

        if (status != null) {
            tvReceiptStatus.setText("GIAO DỊCH " + status);
        }
        tvReceiptAmount.setText(CustomerRepository.getInstance().formatCurrency(amount));
        if (details != null) {
            tvReceiptDetails.setText(details);
        }

        btnDone.setOnClickListener(v -> finish());
    }
}
