package com.example.ck_mobile.chat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ChatActionsTest {

    @Test
    public void everyKnownTargetHasLabel() {
        String[] targets = {ChatActions.HISTORY, ChatActions.TRANSFER, ChatActions.DEPOSIT_WITHDRAW,
                ChatActions.BILL, ChatActions.TOPUP_PHONE, ChatActions.MOVIE, ChatActions.HOTEL,
                ChatActions.BRANCHES, ChatActions.ACCOUNTS, ChatActions.KYC, ChatActions.RATES};
        for (String t : targets) {
            assertTrue(t, ChatActions.isKnown(t));
        }
    }

    @Test
    public void unknownTargetIsRejected() {
        assertFalse(ChatActions.isKnown("NOPE"));
        assertFalse(ChatActions.isKnown(null));
        assertNull(ChatActions.label("NOPE"));
    }

    @Test
    public void botReplyKeepsKnownActionAndDropsUnknown() {
        BotReply ok = new BotReply("x", ChatActions.RATES);
        assertEquals(ChatActions.RATES, ok.actionTarget);
        assertEquals("Xem lãi suất tiết kiệm", ok.actionLabel);
        assertNull(new BotReply("x", "NOPE").actionLabel);
    }
}
