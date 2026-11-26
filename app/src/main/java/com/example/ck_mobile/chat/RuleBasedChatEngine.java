package com.example.ck_mobile.chat;

import android.os.Handler;
import android.os.Looper;

import com.example.ck_mobile.model.ChatMessage;
import com.example.ck_mobile.model.InterestRate;
import com.example.ck_mobile.model.TransactionRecord;
import com.example.ck_mobile.repository.RateRepository;
import com.example.ck_mobile.repository.RepoCallback;
import com.example.ck_mobile.repository.TransactionRepository;
import com.example.ck_mobile.utils.Constants;
import com.example.ck_mobile.utils.MoneyUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * HƯỚNG B (T47): chatbot rule-based/intent. Ổn định, không cần mạng ngoài Firebase và không cần API key.
 * Một số ý định (số dư, lãi suất, tổng kết tháng) đọc dữ liệu thật từ Firestore.
 */
public class RuleBasedChatEngine implements ChatEngine {

    private final Handler main = new Handler(Looper.getMainLooper());

    @Override
    public void reply(String userInput, List<ChatMessage> history, RepoCallback<BotReply> cb) {
        ChatIntent intent = IntentMatcher.match(userInput);
        switch (intent) {
            case BALANCE:
                replyBalance(cb);
                return;
            case INTEREST_RATE:
                replyRates(cb);
                return;
            case TRANSACTIONS:
                replyMonthSummary(cb);
                return;
            default:
                post(cb, staticReply(intent));
        }
    }

    /** Trả lời tĩnh theo ý định (không cần dữ liệu). */
    static BotReply staticReply(ChatIntent intent) {
        switch (intent) {
            case GREETING:
                return new BotReply("Xin chào! Tôi là trợ lý TDT Bank. Tôi có thể giúp bạn xem số dư, lịch sử giao dịch, "
                        + "chuyển tiền, thanh toán hóa đơn, mua vé phim, đặt khách sạn, tra lãi suất và tìm chi nhánh.", null);
            case HELP:
                return new BotReply("Bạn có thể hỏi tôi: \"số dư của tôi\", \"lịch sử giao dịch\", \"chuyển tiền\", "
                        + "\"nạp tiền điện thoại\", \"thanh toán tiền điện\", \"mua vé phim\", \"đặt khách sạn\", "
                        + "\"lãi suất tiết kiệm\", \"chi nhánh gần nhất\".", null);
            case TRANSFER:
                return new BotReply("Bạn có thể chuyển tiền nội bộ hoặc liên ngân hàng. Nhấn nút bên dưới để mở màn hình chuyển tiền.",
                        ChatActions.TRANSFER);
            case DEPOSIT_WITHDRAW:
                return new BotReply("Bạn có thể nạp/rút tiền tại màn hình Nạp - Rút. Nhấn nút bên dưới để mở.",
                        ChatActions.DEPOSIT_WITHDRAW);
            case BILL:
                return new BotReply("Bạn có thể thanh toán tiền điện, nước ngay trong ứng dụng. Nhấn nút bên dưới để bắt đầu.",
                        ChatActions.BILL);
            case TOPUP_PHONE:
                return new BotReply("Bạn có thể nạp tiền điện thoại nhanh bằng số dư tài khoản. Nhấn nút bên dưới để nạp.",
                        ChatActions.TOPUP_PHONE);
            case MOVIE:
                return new BotReply("Bạn muốn xem phim? Chọn suất chiếu và thanh toán trực tiếp từ tài khoản.",
                        ChatActions.MOVIE);
            case HOTEL:
                return new BotReply("Bạn có thể đặt phòng khách sạn và thanh toán ngay trong ứng dụng.",
                        ChatActions.HOTEL);
            case BRANCHES:
                return new BotReply("Bạn có thể xem các chi nhánh và đường đi trên bản đồ.", ChatActions.BRANCHES);
            case KYC:
                return new BotReply("eKYC là bước xác thực sinh trắc học do nhân viên ngân hàng hỗ trợ tại quầy. "
                        + "Bạn có thể xem trạng thái hồ sơ của mình bên dưới.", ChatActions.KYC);
            case SECURITY:
                return new BotReply("Để an toàn: không chia sẻ mật khẩu hoặc mã OTP cho bất kỳ ai, kể cả nhân viên ngân hàng. "
                        + "Giao dịch chuyển tiền cần xác thực OTP. Nếu nghi ngờ bị lừa đảo, hãy liên hệ hotline ngay.", null);
            default:
                return new BotReply("Xin lỗi, tôi chưa hiểu câu hỏi. Bạn thử hỏi về số dư, lịch sử giao dịch, chuyển tiền, "
                        + "hóa đơn, vé phim, khách sạn, lãi suất hoặc chi nhánh nhé.", null);
        }
    }

    private void replyBalance(RepoCallback<BotReply> cb) {
        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        if (u == null) {
            post(cb, new BotReply("Vui lòng đăng nhập lại để xem số dư.", null));
            return;
        }
        FirebaseFirestore.getInstance().collection(Constants.COL_ACCOUNTS)
                .whereEqualTo("ownerUid", u.getUid()).get()
                .addOnSuccessListener(qs -> {
                    if (qs.isEmpty()) {
                        post(cb, new BotReply("Bạn chưa có tài khoản nào.", null));
                        return;
                    }
                    StringBuilder sb = new StringBuilder("Số dư các tài khoản của bạn:");
                    for (DocumentSnapshot d : qs.getDocuments()) {
                        Long bal = d.getLong("balance");
                        String type = d.getString("type");
                        sb.append("\n• ").append(com.example.ck_mobile.utils.UiText.accountTypeLabel(type))
                                .append(": ").append(MoneyUtils.formatVnd(bal == null ? 0 : bal));
                    }
                    post(cb, new BotReply(sb.toString(), ChatActions.ACCOUNTS));
                })
                .addOnFailureListener(e -> post(cb, new BotReply("Không lấy được số dư lúc này, vui lòng thử lại.", null)));
    }

    private void replyRates(RepoCallback<BotReply> cb) {
        new RateRepository().loadRates(new RepoCallback<List<InterestRate>>() {
            @Override
            public void onSuccess(List<InterestRate> rates) {
                StringBuilder sb = new StringBuilder("Lãi suất tiết kiệm hiện tại (%/năm):");
                for (InterestRate r : rates) {
                    sb.append(String.format(Locale.US, "\n• %d tháng: %.2f%%", r.getTermMonths(), r.getRate()));
                }
                post(cb, new BotReply(sb.toString(), ChatActions.ACCOUNTS));
            }

            @Override
            public void onError(String message) {
                post(cb, new BotReply("Không tra được lãi suất lúc này, vui lòng thử lại.", null));
            }
        });
    }

    private void replyMonthSummary(RepoCallback<BotReply> cb) {
        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        if (u == null) {
            post(cb, new BotReply("Vui lòng đăng nhập lại để xem giao dịch.", null));
            return;
        }
        final String thisMonth = MoneyUtils.monthKey(Calendar.getInstance().getTime());
        new TransactionRepository().loadByOwner(u.getUid(), new RepoCallback<List<TransactionRecord>>() {
            @Override
            public void onSuccess(List<TransactionRecord> list) {
                int count = 0;
                long in = 0, out = 0;
                for (TransactionRecord t : list) {
                    if (!thisMonth.equals(MoneyUtils.monthKey(t.getCreatedAt()))) continue;
                    count++;
                    if (t.isCredit()) in += t.getAmount();
                    else out += t.getAmount();
                }
                String text = count == 0
                        ? "Tháng này bạn chưa có giao dịch nào."
                        : String.format(Locale.US, "Tháng này bạn có %d giao dịch:\n• Tiền vào: %s\n• Tiền ra: %s",
                        count, MoneyUtils.formatVnd(in), MoneyUtils.formatVnd(out));
                post(cb, new BotReply(text, ChatActions.HISTORY));
            }

            @Override
            public void onError(String message) {
                post(cb, new BotReply("Không lấy được lịch sử giao dịch lúc này.", ChatActions.HISTORY));
            }
        });
    }

    private void post(RepoCallback<BotReply> cb, BotReply reply) {
        main.post(() -> cb.onSuccess(reply));
    }
}
