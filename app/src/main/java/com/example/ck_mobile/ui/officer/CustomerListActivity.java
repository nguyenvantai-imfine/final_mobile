package com.example.ck_mobile.ui.officer;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ck_mobile.databinding.ActivityCustomerListBinding;
import com.example.ck_mobile.R;
import com.example.ck_mobile.repository.RepoCallback;
import com.example.ck_mobile.repository.SeedRepository;
import com.example.ck_mobile.ui.base.BaseSecureActivity;
import com.example.ck_mobile.ui.officer.adapter.CustomerAdapter;
import com.example.ck_mobile.utils.AuthSessionHelper;
import com.example.ck_mobile.utils.Constants;
import com.example.ck_mobile.viewmodel.CustomerListViewModel;

/** T13: danh sách khách hàng (RecyclerView + Firestore) cho Officer. */
public class CustomerListActivity extends BaseSecureActivity {

    private ActivityCustomerListBinding binding;
    private CustomerListViewModel viewModel;
    private CustomerAdapter adapter;

    @Override
    protected String getRequiredRole() {
        return AuthSessionHelper.ROLE_OFFICER;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.toolbar.inflateMenu(R.menu.menu_customer_list);
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_seed) {
                confirmSeed();
                return true;
            }
            return false;
        });

        adapter = new CustomerAdapter(customer -> {
            Intent i = new Intent(this, CustomerFormActivity.class);
            i.putExtra(Constants.EXTRA_UID, customer.getUid());
            startActivity(i);
        });
        binding.rvCustomers.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCustomers.setAdapter(adapter);

        binding.fabAdd.setOnClickListener(v -> startActivity(new Intent(this, CustomerFormActivity.class)));

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.setQuery(s.toString());
                updateEmpty();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        viewModel = new ViewModelProvider(this).get(CustomerListViewModel.class);
        viewModel.getCustomers().observe(this, list -> {
            adapter.submit(list);
            adapter.setQuery(binding.etSearch.getText() == null ? "" : binding.etSearch.getText().toString());
            updateEmpty();
        });
        viewModel.getLoading().observe(this, loading ->
                binding.progress.setVisibility(Boolean.TRUE.equals(loading) ? View.VISIBLE : View.GONE));
        viewModel.getError().observe(this, msg -> {
            if (msg != null) Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.load(); // tải lại sau khi tạo/sửa/khóa khách hàng
    }

    private void updateEmpty() {
        binding.tvEmpty.setVisibility(adapter.shownCount() == 0 ? View.VISIBLE : View.GONE);
    }

    /** T14: nạp dữ liệu mẫu phục vụ demo (lãi suất, chi nhánh, khách hàng demo + giao dịch). */
    private void confirmSeed() {
        new AlertDialog.Builder(this)
                .setTitle("Nạp dữ liệu mẫu?")
                .setMessage("Tạo khách hàng demo, 3 loại tài khoản, giao dịch mẫu, lãi suất và 8 chi nhánh.")
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Nạp", (d, w) -> {
                    binding.progress.setVisibility(View.VISIBLE);
                    new SeedRepository().seed(this, new RepoCallback<String>() {
                        @Override
                        public void onSuccess(String message) {
                            binding.progress.setVisibility(View.GONE);
                            new AlertDialog.Builder(CustomerListActivity.this)
                                    .setTitle("Hoàn tất")
                                    .setMessage(message)
                                    .setPositiveButton("Đóng", null)
                                    .show();
                            viewModel.load();
                        }

                        @Override
                        public void onError(String message) {
                            binding.progress.setVisibility(View.GONE);
                            Toast.makeText(CustomerListActivity.this, message, Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .show();
    }
}
