package com.example.ck_mobile.chat;

/** Mã đích deep link trong chatbot và nhãn nút hiển thị. */
public final class ChatActions {

    public static final String HISTORY = "HISTORY";
    public static final String TRANSFER = "TRANSFER";
    public static final String DEPOSIT_WITHDRAW = "DEPOSIT_WITHDRAW";
    public static final String BILL = "BILL";
    public static final String TOPUP_PHONE = "TOPUP_PHONE";
    public static final String MOVIE = "MOVIE";
    public static final String HOTEL = "HOTEL";
    public static final String BRANCHES = "BRANCHES";
    public static final String ACCOUNTS = "ACCOUNTS";
    public static final String KYC = "KYC";

    private ChatActions() {
    }

    public static boolean isKnown(String target) {
        return label(target) != null;
    }

    /** Nhãn nút cho mã đích; null nếu mã không hợp lệ. */
    public static String label(String target) {
        if (target == null) return null;
        switch (target) {
            case HISTORY:
                return "Xem lịch sử giao dịch";
            case TRANSFER:
                return "Chuyển tiền";
            case DEPOSIT_WITHDRAW:
                return "Nạp / rút tiền";
            case BILL:
                return "Thanh toán hóa đơn";
            case TOPUP_PHONE:
                return "Nạp tiền điện thoại";
            case MOVIE:
                return "Mua vé xem phim";
            case HOTEL:
                return "Đặt phòng khách sạn";
            case BRANCHES:
                return "Chi nhánh gần bạn";
            case ACCOUNTS:
                return "Quản lý tài khoản";
            case KYC:
                return "Trạng thái eKYC";
            default:
                return null;
        }
    }
}
