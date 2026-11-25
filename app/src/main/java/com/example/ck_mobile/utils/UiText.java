package com.example.ck_mobile.utils;

/** Chuyển mã nghiệp vụ sang nhãn tiếng Việt hiển thị. */
public final class UiText {

    private UiText() {
    }

    public static String kycLabel(String status) {
        if (status == null) return "eKYC: Chưa đăng ký";
        switch (status) {
            case Constants.KYC_PENDING:
                return "eKYC: Chờ duyệt";
            case Constants.KYC_APPROVED:
                return "eKYC: Đã xác thực";
            case Constants.KYC_REJECTED:
                return "eKYC: Bị từ chối";
            default:
                return "eKYC: Chưa đăng ký";
        }
    }

    public static String txTypeLabel(String type) {
        if (type == null) return "Khác";
        switch (type) {
            case Constants.TX_DEPOSIT:
                return "Nạp tiền";
            case Constants.TX_WITHDRAW:
                return "Rút tiền";
            case Constants.TX_TRANSFER_OUT:
                return "Chuyển đi";
            case Constants.TX_TRANSFER_IN:
                return "Nhận tiền";
            case Constants.TX_BILL:
                return "Thanh toán hóa đơn";
            case Constants.TX_MOVIE:
                return "Vé phim";
            case Constants.TX_HOTEL:
                return "Khách sạn";
            case Constants.TX_INTEREST:
                return "Lãi suất";
            default:
                return "Khác";
        }
    }

    public static String accountTypeLabel(String type) {
        if (type == null) return "";
        switch (type) {
            case Constants.ACC_CHECKING:
                return "Thanh toán";
            case Constants.ACC_SAVING:
                return "Tiết kiệm";
            case Constants.ACC_MORTGAGE:
                return "Vay thế chấp";
            default:
                return type;
        }
    }
}
