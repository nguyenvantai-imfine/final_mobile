package com.example.ck_mobile.ui.customer;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.ck_mobile.databinding.ActivityKycStatusBinding;
import com.example.ck_mobile.model.UserProfile;
import com.example.ck_mobile.repository.CustomerRepository;
import com.example.ck_mobile.repository.RepoCallback;
import com.example.ck_mobile.ui.base.BaseSecureActivity;
import com.example.ck_mobile.utils.AuthSessionHelper;
import com.example.ck_mobile.utils.Constants;
import com.example.ck_mobile.utils.UiText;

/** T34: Customer xem trạng thái eKYC của chính mình. */
public class KycStatusActivity extends BaseSecureActivity {

    @Override
    protected String getRequiredRole() {
        return AuthSessionHelper.ROLE_CUSTOMER;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityKycStatusBinding b = ActivityKycStatusBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        b.toolbar.setNavigationOnClickListener(v -> finish());

        if (auth.getCurrentUser() == null) return;
        b.progress.setVisibility(View.VISIBLE);
        new CustomerRepository().getCustomer(auth.getCurrentUser().getUid(), new RepoCallback<UserProfile>() {
            @Override
            public void onSuccess(UserProfile u) {
                b.progress.setVisibility(View.GONE);
                String s = u.getKycStatus() == null ? Constants.KYC_NONE : u.getKycStatus();
                b.tvStatus.setText(UiText.kycLabel(s).replace("eKYC: ", ""));
                switch (s) {
                    case Constants.KYC_APPROVED:
                        b.tvDetail.setText("Hồ sơ sinh trắc học của bạn đã được nhân viên ngân hàng xác thực.");
                        break;
                    case Constants.KYC_PENDING:
                        b.tvDetail.setText("Hồ sơ đã được ghi nhận và đang chờ nhân viên ngân hàng duyệt.");
                        break;
                    case Constants.KYC_REJECTED:
                        b.tvDetail.setText("Hồ sơ bị từ chối. Vui lòng đến quầy giao dịch để đăng ký lại.");
                        break;
                    default:
                        b.tvDetail.setText("Bạn chưa đăng ký sinh trắc học. Vui lòng đến quầy để nhân viên hỗ trợ.");
                }
            }

            @Override
            public void onError(String message) {
                b.progress.setVisibility(View.GONE);
                b.tvDetail.setText(message);
            }
        });
    }
}
