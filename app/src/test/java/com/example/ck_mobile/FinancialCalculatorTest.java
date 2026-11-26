package com.example.ck_mobile;

import com.example.ck_mobile.model.MortgageInstallment;
import com.example.ck_mobile.utils.FinancialCalculator;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FinancialCalculatorTest {

    @Test
    public void testFormatCurrency() {
        assertEquals("45.850.000", FinancialCalculator.formatCurrencyNumber(45_850_000));
        assertEquals("45.850.000 đ", FinancialCalculator.formatVnd(45_850_000));
        assertEquals("+5.000.000 đ", FinancialCalculator.formatSignedVnd(5_000_000));
        assertEquals("-2.500.000 đ", FinancialCalculator.formatSignedVnd(-2_500_000));
    }

    @Test
    public void testSavingCalculations() {
        double principal = 30_000_000;
        double rate = 5.8;

        assertEquals(1_740_000, FinancialCalculator.calculateSavingProjectedInterest(principal, rate, 12), 0.01);
        assertEquals(435_000, FinancialCalculator.calculateSavingAccruedInterest(principal, rate, 3), 0.01);
        assertEquals(145_000, FinancialCalculator.calculateSavingMonthlyProfit(principal, rate), 0.01);

        // Tất toán trước hạn 90 ngày với lãi không kỳ hạn 0.2%/năm
        double expectedDemandInterest = Math.round(principal * (0.2 / 100.0) * (90.0 / 365.0));
        assertEquals(principal + expectedDemandInterest,
                FinancialCalculator.calculatePrematureWithdrawalPayout(principal, 90), 0.01);

        // Biên <= 0
        assertEquals(0, FinancialCalculator.calculateSavingProjectedInterest(0, rate, 12), 0.01);
        assertEquals(principal, FinancialCalculator.calculatePrematureWithdrawalPayout(principal, 0), 0.01);
    }

    @Test
    public void testMortgageLtvAndProgress() {
        assertEquals(32.4, FinancialCalculator.calculateLtvPercent(600_000_000, 1_850_000_000), 0.01);
        assertEquals(0.0, FinancialCalculator.calculateLtvPercent(600_000_000, 0), 0.01);

        assertEquals(30, FinancialCalculator.calculateRepaymentProgressPercent(600_000_000, 420_000_000));
        assertEquals(0, FinancialCalculator.calculateRepaymentProgressPercent(0, 0));
    }

    @Test
    public void testAmortizationSchedule120Periods() {
        List<MortgageInstallment> schedule = FinancialCalculator.generateAmortizationSchedule(
                600_000_000,
                9.8,
                120,
                36,
                2023,
                12
        );

        assertEquals(120, schedule.size());

        // Kỳ 1: gốc 5.000.000, lãi 600.000.000 * 9.8% / 12 = 4.900.000
        MortgageInstallment p1 = schedule.get(0);
        assertEquals(1, p1.getPeriodNumber());
        assertEquals(5_000_000, p1.getPrincipalAmount(), 0.01);
        assertEquals(4_900_000, p1.getInterestAmount(), 0.01);
        assertEquals(9_900_000, p1.getTotalAmount(), 0.01);
        assertEquals(MortgageInstallment.STATUS_PAID, p1.getStatus());

        // Kỳ 36 đã trả, kỳ 37 đến hạn, kỳ 38 sắp tới
        assertEquals(MortgageInstallment.STATUS_PAID, schedule.get(35).getStatus());
        assertEquals(MortgageInstallment.STATUS_DUE, schedule.get(36).getStatus());
        assertEquals(MortgageInstallment.STATUS_UPCOMING, schedule.get(37).getStatus());

        // Kỳ cuối 120 dư nợ về 0 và tổng gốc trả khớp 600.000.000
        MortgageInstallment p120 = schedule.get(119);
        assertEquals(0, p120.getRemainingBalance(), 0.01);

        double totalPrincipalPaid = 0;
        for (MortgageInstallment item : schedule) {
            totalPrincipalPaid += item.getPrincipalAmount();
        }
        assertEquals(600_000_000, totalPrincipalPaid, 0.01);
        assertTrue(p1.getInterestAmount() > p120.getInterestAmount());
    }
}