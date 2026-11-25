package com.example.ck_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ck_mobile.adapter.AccountsAdapter;
import com.example.ck_mobile.adapter.TransactionsAdapter;
import com.example.ck_mobile.model.Account;
import com.example.ck_mobile.model.UserProfile;
import com.example.ck_mobile.repository.CustomerRepository;
import com.example.ck_mobile.ui.ActivityDepositWithdraw;
import com.example.ck_mobile.ui.ActivityNearbyBranches;
import com.example.ck_mobile.ui.ActivityProfile;
import com.example.ck_mobile.ui.ActivityTransfer;
import com.example.ck_mobile.ui.ActivityUtilities;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private CustomerRepository repository;

    private TextView tvWelcomeName, tvMemberBadge, tvMainBalance, tvMainAccountNumber;
    private ImageButton btnProfile;
    private View btnDepositWithdraw, btnTransferInternal, btnUtilities, btnBranches;
    private RecyclerView rvAccounts, rvTransactions;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repository = CustomerRepository.getInstance();

        initViews();
        setupHeader();
        setupQuickActions();
        setupRecyclerViews();
        setupBottomNav();
    }

    private void initViews() {
        tvWelcomeName = findViewById(R.id.tvWelcomeName);
        tvMemberBadge = findViewById(R.id.tvMemberBadge);
        tvMainBalance = findViewById(R.id.tvMainBalance);
        tvMainAccountNumber = findViewById(R.id.tvMainAccountNumber);
        btnProfile = findViewById(R.id.btnProfile);

        btnDepositWithdraw = findViewById(R.id.btnDepositWithdraw);
        btnTransferInternal = findViewById(R.id.btnTransferInternal);
        btnUtilities = findViewById(R.id.btnUtilities);
        btnBranches = findViewById(R.id.btnBranches);

        rvAccounts = findViewById(R.id.rvAccounts);
        rvTransactions = findViewById(R.id.rvTransactions);
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    private void setupHeader() {
        UserProfile profile = repository.getUserProfile();
        tvWelcomeName.setText("Xin chào, " + profile.getFullName());
        tvMemberBadge.setText("★ " + profile.getMemberStatus());

        btnProfile.setOnClickListener(v -> startActivity(new Intent(this, ActivityProfile.class)));
    }

    private void refreshData() {
        Account checking = repository.getCheckingAccount();
        if (checking != null) {
            tvMainBalance.setText(repository.formatCurrency(checking.getBalance()));
            tvMainAccountNumber.setText("STK: " + checking.getAccountNumber() + " • TDT Bank Sovereign");
        }
        setupRecyclerViews();
    }

    private void setupQuickActions() {
        btnDepositWithdraw.setOnClickListener(v -> startActivity(new Intent(this, ActivityDepositWithdraw.class)));
        btnTransferInternal.setOnClickListener(v -> startActivity(new Intent(this, ActivityTransfer.class)));
        btnUtilities.setOnClickListener(v -> startActivity(new Intent(this, ActivityUtilities.class)));
        btnBranches.setOnClickListener(v -> startActivity(new Intent(this, ActivityNearbyBranches.class)));
    }

    private void setupRecyclerViews() {
        rvAccounts.setLayoutManager(new LinearLayoutManager(this));
        AccountsAdapter accountsAdapter = new AccountsAdapter(repository.getAccounts(), account -> {
            Toast.makeText(this, account.getAccountTypeName() + ": " + repository.formatCurrency(account.getBalance()), Toast.LENGTH_SHORT).show();
        });
        rvAccounts.setAdapter(accountsAdapter);

        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        TransactionsAdapter txAdapter = new TransactionsAdapter(repository.getTransactions());
        rvTransactions.setAdapter(txAdapter);
    }

    private void setupBottomNav() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_accounts) {
                Toast.makeText(this, "Tài khoản (TDT Sovereign)", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_utilities) {
                startActivity(new Intent(this, ActivityUtilities.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ActivityProfile.class));
                return true;
            }
            return false;
        });
    }
}
