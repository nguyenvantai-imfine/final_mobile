package com.example.ck_mobile.chat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/** Kiểm thử việc tách thẻ [[ACTION:X]] khỏi câu trả lời của Gemini. */
public class GeminiReplyParseTest {

    @Test
    public void extractsKnownActionTagAndStripsIt() {
        BotReply r = GeminiChatEngine.parse("Bạn xem lịch sử tại đây. [[ACTION:HISTORY]]");
        assertEquals("Bạn xem lịch sử tại đây.", r.text);
        assertEquals(ChatActions.HISTORY, r.actionTarget);
    }

    @Test
    public void unknownTagIsStrippedButGivesNoButton() {
        BotReply r = GeminiChatEngine.parse("Xin chào [[ACTION:HACK_THE_PLANET]]");
        assertEquals("Xin chào", r.text);
        assertNull(r.actionTarget);
    }

    @Test
    public void plainTextHasNoAction() {
        BotReply r = GeminiChatEngine.parse("Tôi có thể giúp gì cho bạn?");
        assertEquals("Tôi có thể giúp gì cho bạn?", r.text);
        assertNull(r.actionTarget);
    }

    @Test
    public void firstKnownTagWins() {
        BotReply r = GeminiChatEngine.parse("A [[ACTION:BILL]] B [[ACTION:MOVIE]]");
        assertEquals(ChatActions.BILL, r.actionTarget);
    }
}
