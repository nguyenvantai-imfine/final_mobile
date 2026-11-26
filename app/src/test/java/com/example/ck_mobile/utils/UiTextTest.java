package com.example.ck_mobile.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UiTextTest {

    @Test
    public void kycLabels() {
        assertEquals("eKYC: Chưa đăng ký", UiText.kycLabel(null));
        assertEquals("eKYC: Chờ duyệt", UiText.kycLabel(Constants.KYC_PENDING));
        assertEquals("eKYC: Đã xác thực", UiText.kycLabel(Constants.KYC_APPROVED));
        assertEquals("eKYC: Bị từ chối", UiText.kycLabel(Constants.KYC_REJECTED));
    }

    @Test
    public void transactionAndAccountLabels() {
        assertEquals("Vé phim", UiText.txTypeLabel(Constants.TX_MOVIE));
        assertEquals("Khác", UiText.txTypeLabel("???"));
        assertEquals("Tiết kiệm", UiText.accountTypeLabel(Constants.ACC_SAVING));
    }
}
