package com.example.ck_mobile.chat;

import java.text.Normalizer;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Nhận diện ý định theo từ khóa (HƯỚNG B - rule-based). Không phụ thuộc Android để dễ kiểm thử.
 * Văn bản được bỏ dấu tiếng Việt, chuyển chữ thường, bỏ ký tự đặc biệt rồi so khớp theo CỤM TỪ.
 * Thứ tự khai báo = độ ưu tiên (ý định cụ thể trước ý định chung).
 */
public final class IntentMatcher {

    private static final Map<ChatIntent, String[]> KEYWORDS = new LinkedHashMap<>();

    static {
        KEYWORDS.put(ChatIntent.TOPUP_PHONE, new String[]{"dien thoai", "nap the", "nap dt", "topup", "top up", "sim"});
        KEYWORDS.put(ChatIntent.BILL, new String[]{"hoa don", "tien dien", "tien nuoc", "dien nuoc", "thanh toan dien", "thanh toan nuoc"});
        KEYWORDS.put(ChatIntent.MOVIE, new String[]{"ve phim", "xem phim", "rap phim", "cinema", "phim"});
        KEYWORDS.put(ChatIntent.HOTEL, new String[]{"khach san", "dat phong", "hotel", "nghi duong"});
        KEYWORDS.put(ChatIntent.BRANCHES, new String[]{"chi nhanh", "atm", "phong giao dich", "gan nhat", "dia chi ngan hang"});
        KEYWORDS.put(ChatIntent.INTEREST_RATE, new String[]{"lai suat", "tiet kiem", "gui tiet kiem", "ky han"});
        KEYWORDS.put(ChatIntent.KYC, new String[]{"ekyc", "xac thuc", "sinh trac", "khuon mat"});
        KEYWORDS.put(ChatIntent.SECURITY, new String[]{"otp", "mat khau", "bao mat", "quen mat khau", "bi lua", "lua dao"});
        KEYWORDS.put(ChatIntent.BALANCE, new String[]{"so du", "con bao nhieu tien", "balance", "co bao nhieu tien"});
        KEYWORDS.put(ChatIntent.TRANSACTIONS, new String[]{"lich su", "giao dich", "sao ke", "thang nay", "da tieu", "tieu bao nhieu"});
        KEYWORDS.put(ChatIntent.TRANSFER, new String[]{"chuyen tien", "chuyen khoan", "chuyen"});
        KEYWORDS.put(ChatIntent.DEPOSIT_WITHDRAW, new String[]{"nap tien", "rut tien", "gui tien", "nap", "rut"});
        KEYWORDS.put(ChatIntent.HELP, new String[]{"giup", "huong dan", "lam duoc gi", "chuc nang", "help", "tro giup"});
        KEYWORDS.put(ChatIntent.GREETING, new String[]{"xin chao", "chao", "hello", "hi", "alo"});
    }

    private IntentMatcher() {
    }

    public static ChatIntent match(String input) {
        String text = normalize(input);
        if (text.isEmpty()) return ChatIntent.UNKNOWN;
        String padded = " " + text + " ";
        for (Map.Entry<ChatIntent, String[]> e : KEYWORDS.entrySet()) {
            for (String kw : e.getValue()) {
                if (padded.contains(" " + kw + " ")) return e.getKey();
            }
        }
        return ChatIntent.UNKNOWN;
    }

    /** "Số dư của tôi?" -> "so du cua toi". */
    public static String normalize(String input) {
        if (input == null) return "";
        String s = input.trim().toLowerCase(Locale.ROOT).replace('đ', 'd');
        s = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
        s = s.replaceAll("[^a-z0-9]+", " ").trim();
        return s;
    }
}
