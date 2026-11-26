package com.example.ck_mobile.chat;

import android.os.Handler;
import android.os.Looper;

import com.example.ck_mobile.BuildConfig;
import com.example.ck_mobile.model.ChatMessage;
import com.example.ck_mobile.repository.RepoCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HƯỚNG A (T53): chatbot LLM qua Gemini REST API. Khóa API và tên model lấy từ local.properties
 * (GEMINI_API_KEY, GEMINI_MODEL) thông qua BuildConfig; không commit khóa lên Git.
 */
public class GeminiChatEngine implements ChatEngine {

    private static final String SYSTEM_PROMPT =
            "Bạn là trợ lý ảo của ngân hàng số TDT Bank (ứng dụng di động, đồ án sinh viên). "
                    + "Chỉ trả lời các câu hỏi về dịch vụ ngân hàng trong ứng dụng: xem số dư, lịch sử giao dịch, "
                    + "chuyển tiền, nạp/rút tiền, thanh toán hóa đơn điện nước, nạp tiền điện thoại, mua vé phim, "
                    + "đặt khách sạn, lãi suất tiết kiệm, chi nhánh, eKYC và bảo mật. "
                    + "Trả lời bằng tiếng Việt, ngắn gọn (tối đa 4 câu), lịch sự. "
                    + "KHÔNG bịa số dư, lãi suất hay dữ liệu cá nhân: nếu cần số liệu thật, hãy hướng người dùng mở chức năng tương ứng. "
                    + "KHÔNG bao giờ yêu cầu mật khẩu hay mã OTP. Từ chối lịch sự các câu hỏi ngoài lĩnh vực ngân hàng. "
                    + "Nếu có một chức năng phù hợp, kết thúc câu trả lời bằng đúng một thẻ [[ACTION:MÃ]] với MÃ thuộc: "
                    + "HISTORY, TRANSFER, DEPOSIT_WITHDRAW, BILL, TOPUP_PHONE, MOVIE, HOTEL, BRANCHES, ACCOUNTS, KYC, RATES.";

    private static final Pattern ACTION_TAG = Pattern.compile("\\[\\[ACTION:([A-Z_]+)\\]\\]");
    private static final int MAX_HISTORY = 10;
    private static final int TIMEOUT_MS = 15000;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler main = new Handler(Looper.getMainLooper());

    public static boolean isConfigured() {
        return BuildConfig.GEMINI_API_KEY != null && !BuildConfig.GEMINI_API_KEY.trim().isEmpty();
    }

    @Override
    public void reply(String userInput, List<ChatMessage> history, RepoCallback<BotReply> cb) {
        if (!isConfigured()) {
            cb.onError("Chưa cấu hình GEMINI_API_KEY.");
            return;
        }
        final List<ChatMessage> snapshot = new ArrayList<>(history);
        executor.execute(() -> {
            try {
                String raw = call(snapshot, userInput);
                BotReply reply = parse(raw);
                main.post(() -> cb.onSuccess(reply));
            } catch (Exception e) {
                String msg = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
                main.post(() -> cb.onError(msg));
            }
        });
    }

    private String call(List<ChatMessage> history, String userInput) throws Exception {
        URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models/"
                + BuildConfig.GEMINI_MODEL + ":generateContent");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(TIMEOUT_MS);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestProperty("x-goog-api-key", BuildConfig.GEMINI_API_KEY);

            byte[] body = buildBody(history, userInput).toString().getBytes(StandardCharsets.UTF_8);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(body);
            }

            int code = conn.getResponseCode();
            if (code != HttpURLConnection.HTTP_OK) {
                throw new IOException("Gemini HTTP " + code + " " + readAll(conn.getErrorStream()));
            }
            return extractText(readAll(conn.getInputStream()));
        } finally {
            conn.disconnect();
        }
    }

    private JSONObject buildBody(List<ChatMessage> history, String userInput) throws Exception {
        // Lấy tối đa MAX_HISTORY tin gần nhất, gộp các tin liên tiếp cùng vai, bắt đầu bằng vai "user"
        List<String[]> turns = new ArrayList<>(); // {role, text}
        int start = Math.max(0, history.size() - MAX_HISTORY);
        for (int i = start; i < history.size(); i++) {
            ChatMessage m = history.get(i);
            addTurn(turns, m.isFromUser() ? "user" : "model", m.getText());
        }
        addTurn(turns, "user", userInput);
        while (!turns.isEmpty() && !"user".equals(turns.get(0)[0])) turns.remove(0);

        JSONArray contents = new JSONArray();
        for (String[] t : turns) {
            contents.put(new JSONObject()
                    .put("role", t[0])
                    .put("parts", new JSONArray().put(new JSONObject().put("text", t[1]))));
        }
        return new JSONObject()
                .put("system_instruction", new JSONObject()
                        .put("parts", new JSONArray().put(new JSONObject().put("text", SYSTEM_PROMPT))))
                .put("contents", contents)
                .put("generationConfig", new JSONObject().put("temperature", 0.4).put("maxOutputTokens", 1024));
    }

    private static void addTurn(List<String[]> turns, String role, String text) {
        if (text == null || text.trim().isEmpty()) return;
        if (!turns.isEmpty() && turns.get(turns.size() - 1)[0].equals(role)) {
            String[] last = turns.get(turns.size() - 1);
            last[1] = last[1] + "\n" + text;
        } else {
            turns.add(new String[]{role, text});
        }
    }

    private static String extractText(String json) throws Exception {
        JSONArray candidates = new JSONObject(json).optJSONArray("candidates");
        if (candidates == null || candidates.length() == 0) throw new IOException("Gemini không trả về nội dung.");
        JSONArray parts = candidates.getJSONObject(0).optJSONObject("content") == null
                ? null : candidates.getJSONObject(0).getJSONObject("content").optJSONArray("parts");
        if (parts == null) throw new IOException("Gemini không trả về nội dung.");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length(); i++) sb.append(parts.getJSONObject(i).optString("text", ""));
        String text = sb.toString().trim();
        if (text.isEmpty()) throw new IOException("Gemini trả về nội dung rỗng.");
        return text;
    }

    /** Tách thẻ [[ACTION:X]] khỏi nội dung để tạo nút deep link. Thẻ lạ bị bỏ qua. */
    static BotReply parse(String raw) {
        String target = null;
        Matcher m = ACTION_TAG.matcher(raw);
        while (m.find()) {
            if (ChatActions.isKnown(m.group(1))) {
                target = m.group(1);
                break;
            }
        }
        String clean = ACTION_TAG.matcher(raw).replaceAll("").trim();
        return new BotReply(clean, target);
    }

    private static String readAll(InputStream in) throws IOException {
        if (in == null) return "";
        StringBuilder sb = new StringBuilder();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = r.readLine()) != null) sb.append(line).append('\n');
        }
        return sb.toString();
    }
}
