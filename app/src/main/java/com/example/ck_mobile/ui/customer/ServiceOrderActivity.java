package com.example.ck_mobile.ui.customer;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ck_mobile.databinding.ActivityServiceOrderBinding;
import com.example.ck_mobile.model.ServiceItem;
import com.example.ck_mobile.repository.RepoCallback;
import com.example.ck_mobile.repository.ServiceCatalog;
import com.example.ck_mobile.repository.ServiceOrderRepository;
import com.example.ck_mobile.ui.base.BaseSecureActivity;
import com.example.ck_mobile.ui.customer.adapter.ServiceItemAdapter;
import com.example.ck_mobile.utils.AuthSessionHelper;
import com.example.ck_mobile.utils.Constants;
import com.example.ck_mobile.utils.MoneyUtils;

/**
 * T40: mua vé phim / đặt phòng khách sạn. Loại dịch vụ lấy từ extra EXTRA_SERVICE_TYPE
 * (Constants.TX_MOVIE hoặc Constants.TX_HOTEL).
 */
public class ServiceOrderActivity extends BaseSecureActivity {

    private static final int MAX_QTY = 10;

    private ActivityServiceOrderBinding b;
    private final ServiceOrderRepository repo = new ServiceOrderRepository();
    private String type;
    private ServiceItem selected;
    private int quantity = 1;

    @Override
    protected String getRequiredRole() {
        return AuthSessionHelper.ROLE_CUSTOMER;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityServiceOrderBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        b.toolbar.setNavigationOnClickListener(v -> finish());

        type = Constants.TX_HOTEL.equals(getIntent().getStringExtra(Constants.EXTRA_SERVICE_TYPE))
                ? Constants.TX_HOTEL : Constants.TX_MOVIE;
        boolean hotel = Constants.TX_HOTEL.equals(type);
        b.toolbar.setTitle(hotel ? "Đặt phòng khách sạn" : "Mua vé xem phim");
        b.tvQtyLabel.setText(hotel ? "Số đêm" : "Số vé");

        b.rvItems.setLayoutManager(new LinearLayoutManager(this));
        b.rvItems.setAdapter(new ServiceItemAdapter(ServiceCatalog.forType(type), item -> {
            selected = item;
            refreshTotal();
        }));

        b.btnMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                refreshTotal();
            }
        });
        b.btnPlus.setOnClickListener(v -> {
            if (quantity < MAX_QTY) {
                quantity++;
                refreshTotal();
            }
        });
        b.btnPay.setOnClickListener(v -> confirmPay());
        refreshTotal();
    }

    private void refreshTotal() {
        b.tvQty.setText(String.valueOf(quantity));
        b.tvTotal.setText(selected == null
                ? "Chọn một mục để tiếp tục"
                : "Tổng: " + MoneyUtils.formatVnd(selected.unitPrice * quantity));
        b.btnPay.setEnabled(selected != null);
    }

    private void confirmPay() {
        if (selected == null || auth.getCurrentUser() == null) return;
        long total = selected.unitPrice * quantity;
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận thanh toán")
                .setMessage(selected.title + "\n" + quantity + " " + selected.unitLabel
                        + "\nTổng tiền: " + MoneyUtils.formatVnd(total)
                        + "\nTrừ từ tài khoản thanh toán của bạn.")
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Thanh toán", (d, w) -> pay())
                .show();
    }

    private void pay() {
        b.progress.setVisibility(View.VISIBLE);
        b.btnPay.setEnabled(false);
        repo.pay(auth.getCurrentUser().getUid(), selected, quantity,
                new RepoCallback<ServiceOrderRepository.OrderResult>() {
                    @Override
                    public void onSuccess(ServiceOrderRepository.OrderResult r) {
                        b.progress.setVisibility(View.GONE);
                        new AlertDialog.Builder(ServiceOrderActivity.this)
                                .setTitle("Thanh toán thành công")
                                .setMessage("Mã đơn: " + r.orderId
                                        + "\nĐã thanh toán: " + MoneyUtils.formatVnd(r.total)
                                        + "\nSố dư còn lại: " + MoneyUtils.formatVnd(r.balanceAfter))
                                .setCancelable(false)
                                .setPositiveButton("Đóng", (d, w) -> finish())
                                .show();
                    }

                    @Override
                    public void onError(String message) {
                        b.progress.setVisibility(View.GONE);
                        b.btnPay.setEnabled(true);
                        Toast.makeText(ServiceOrderActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
