package com.example.ck_mobile.chat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class IntentMatcherTest {

    @Test
    public void matchesCommonBankingQuestions() {
        assertEquals(ChatIntent.BALANCE, IntentMatcher.match("Số dư của tôi còn bao nhiêu?"));
        assertEquals(ChatIntent.TRANSFER, IntentMatcher.match("Tôi muốn chuyển tiền cho bạn"));
        assertEquals(ChatIntent.TOPUP_PHONE, IntentMatcher.match("nạp tiền điện thoại 100k"));
        assertEquals(ChatIntent.BILL, IntentMatcher.match("Thanh toán tiền điện tháng này"));
        assertEquals(ChatIntent.MOVIE, IntentMatcher.match("Mua vé xem phim cuối tuần"));
        assertEquals(ChatIntent.HOTEL, IntentMatcher.match("đặt phòng khách sạn ở Đà Lạt"));
        assertEquals(ChatIntent.BRANCHES, IntentMatcher.match("Chi nhánh gần nhất ở đâu"));
        assertEquals(ChatIntent.INTEREST_RATE, IntentMatcher.match("Lãi suất tiết kiệm 12 tháng"));
        assertEquals(ChatIntent.TRANSACTIONS, IntentMatcher.match("Xem lịch sử giao dịch"));
        assertEquals(ChatIntent.DEPOSIT_WITHDRAW, IntentMatcher.match("rút tiền ở đâu"));
        assertEquals(ChatIntent.GREETING, IntentMatcher.match("Xin chào"));
    }

    @Test
    public void unknownAndEmptyInput() {
        assertEquals(ChatIntent.UNKNOWN, IntentMatcher.match("blah blah"));
        assertEquals(ChatIntent.UNKNOWN, IntentMatcher.match(""));
        assertEquals(ChatIntent.UNKNOWN, IntentMatcher.match(null));
    }

    @Test
    public void normalizeStripsDiacriticsAndPunctuation() {
        assertEquals("so du cua toi", IntentMatcher.normalize("Số dư của tôi?"));
        assertEquals("dat phong", IntentMatcher.normalize("  Đặt  phòng!! "));
    }

    @Test
    public void botReplyDropsUnknownAction() {
        assertNull(new BotReply("x", "NOT_A_TARGET").actionTarget);
        assertEquals("Xem lịch sử giao dịch", new BotReply("x", ChatActions.HISTORY).actionLabel);
    }
}
