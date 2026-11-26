package com.example.ck_mobile.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MoneyUtilsTest {

    @Test
    public void formatsVndWithDotGrouping() {
        assertEquals("45.850.000 ₫", MoneyUtils.formatVnd(45_850_000L));
        assertEquals("0 ₫", MoneyUtils.formatVnd(0));
    }

    @Test
    public void monthKeyIsYearDashMonth() {
        Date d = new GregorianCalendar(2026, Calendar.NOVEMBER, 5).getTime();
        assertEquals("2026-11", MoneyUtils.monthKey(d));
        assertEquals("", MoneyUtils.monthKey(null));
    }

    @Test
    public void randomAccountNumberHasTenDigits() {
        for (int i = 0; i < 200; i++) {
            assertTrue(MoneyUtils.randomAccountNumber().matches("^[1-9]\\d{9}$"));
        }
    }
}
