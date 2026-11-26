package com.example.ck_mobile.utils;

import com.example.ck_mobile.model.MortgageInstallment;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public final class FinancialCalculator {

    public static final double DEMAND_DEPOSIT_RATE = 0.2; // 0.2%/năm khi rút trước hạn

    private FinancialCalculator() {
    }

    /**
     * Định dạng số tiền kiểu Việt Nam: 45.850.000
     */
    public static String formatCurrencyNumber(double amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator('.');
        DecimalFormat formatter = new DecimalFormat("#,##0", symbols);
        return formatter.format(Math.round(amount));
    }

    /**
     * Định dạng số tiền kèm đơn vị: 45.850.000 đ
     */
    public static String formatVnd(double amount) {
        return formatCurrencyNumber(amount) + " đ";
    }

    /**
     * Định dạng số tiền có dấu +/-: +5.000.000 đ hoặc -2.500.000 đ
     */
    public static String formatSignedVnd(double amount) {
        String sign = amount >= 0 ? "+" : "-";
        return sign + formatCurrencyNumber(Math.abs(amount)) + " đ";
    }

    // ==================== CÔNG THỨC TIẾT KIỆM (SAVING) ====================

    /**
     * Tiền lãi dự tính cuối kỳ: Gốc * (Lãi suất % / 100) * (Số tháng / 12)
     */
    public static double calculateSavingProjectedInterest(double principal, double annualRatePercent, int termMonths) {
        if (principal <= 0 || annualRatePercent <= 0 || termMonths <= 0) return 0;
        return Math.round(principal * (annualRatePercent / 100.0) * (termMonths / 12.0));
    }

    /**
     * Tiền lãi tích lũy hiện tại theo số tháng đã gửi
     */
    public static double calculateSavingAccruedInterest(double principal, double annualRatePercent, int elapsedMonths) {
        if (principal <= 0 || annualRatePercent <= 0 || elapsedMonths <= 0) return 0;
        return Math.round(principal * (annualRatePercent / 100.0) * (elapsedMonths / 12.0));
    }

    /**
     * Lợi tức ước tính mỗi tháng
     */
    public static double calculateSavingMonthlyProfit(double principal, double annualRatePercent) {
        if (principal <= 0 || annualRatePercent <= 0) return 0;
        return Math.round(principal * (annualRatePercent / 100.0) / 12.0);
    }

    /**
     * Tổng tiền thực nhận khi tất toán trước hạn (áp dụng lãi không kỳ hạn 0.2%/năm)
     */
    public static double calculatePrematureWithdrawalPayout(double principal, int elapsedDays) {
        if (principal <= 0) return 0;
        if (elapsedDays <= 0) return principal;
        double demandInterest = Math.round(principal * (DEMAND_DEPOSIT_RATE / 100.0) * (elapsedDays / 365.0));
        return principal + demandInterest;
    }

    // ==================== CÔNG THỨC VAY THẾ CHẤP (MORTGAGE) ====================

    /**
     * Tính tỷ lệ LTV (%) = (Khoản vay hoặc dư nợ / Giá trị định giá tài sản) * 100
     */
    public static double calculateLtvPercent(double loanAmount, double collateralValuation) {
        if (collateralValuation <= 0 || loanAmount <= 0) return 0;
        return Math.round((loanAmount / collateralValuation) * 1000.0) / 10.0;
    }

    /**
     * Tính phần trăm tiến độ đã trả nợ gốc
     */
    public static int calculateRepaymentProgressPercent(double initialPrincipal, double remainingPrincipal) {
        if (initialPrincipal <= 0) return 0;
        double paid = Math.max(0, initialPrincipal - remainingPrincipal);
        return (int) Math.round((paid / initialPrincipal) * 100.0);
    }

    /**
     * Sinh bảng lịch trả nợ N kỳ theo phương pháp dư nợ giảm dần:
     * - Gốc trả đều hàng tháng = initialPrincipal / totalPeriods
     * - Lãi hàng tháng = dư nợ đầu kỳ * (annualRatePercent / 100 / 12)
     */
    public static List<MortgageInstallment> generateAmortizationSchedule(
            double initialPrincipal,
            double annualRatePercent,
            int totalPeriods,
            int paidPeriods,
            int startYear,
            int startMonth
    ) {
        List<MortgageInstallment> list = new ArrayList<>();
        if (initialPrincipal <= 0 || totalPeriods <= 0) return list;

        double monthlyPrincipalBase = Math.round(initialPrincipal / totalPeriods);
        double monthlyRate = (annualRatePercent / 100.0) / 12.0;
        double currentRemaining = initialPrincipal;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, startYear);
        cal.set(Calendar.MONTH, startMonth - 1); // 0-indexed
        cal.set(Calendar.DAY_OF_MONTH, 5);

        for (int period = 1; period <= totalPeriods; period++) {
            double principalThisPeriod = (period == totalPeriods)
                    ? currentRemaining
                    : Math.min(monthlyPrincipalBase, currentRemaining);

            double interestThisPeriod = Math.round(currentRemaining * monthlyRate);
            double totalThisPeriod = principalThisPeriod + interestThisPeriod;
            double remainingAfter = Math.max(0, currentRemaining - principalThisPeriod);

            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            String dueDate = String.format(Locale.US, "05/%02d/%d", month, year);

            MortgageInstallment item = new MortgageInstallment();
            item.setPeriodNumber(period);
            item.setTotalPeriods(totalPeriods);
            item.setYear(year);
            item.setDueDate(dueDate);
            item.setPrincipalAmount(principalThisPeriod);
            item.setInterestAmount(interestThisPeriod);
            item.setTotalAmount(totalThisPeriod);
            item.setRemainingBalance(remainingAfter);
            item.setReceiptCode("MTG-" + period);

            if (period <= paidPeriods) {
                item.setStatus(MortgageInstallment.STATUS_PAID);
            } else if (period == paidPeriods + 1) {
                item.setStatus(MortgageInstallment.STATUS_DUE);
            } else {
                item.setStatus(MortgageInstallment.STATUS_UPCOMING);
            }

            list.add(item);
            currentRemaining = remainingAfter;
            cal.add(Calendar.MONTH, 1);
        }

        return list;
    }
}