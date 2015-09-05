package com.jjdevbros.castellan.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.junit.*;

import static org.junit.Assert.*;

public class SessionPeriodTest {
    private SessionPeriod fixture;
    private final static LocalDate START_TIME = LocalDate.of(2015, 9, 1);
    private final static LocalDate END_TIME = LocalDate.of(2015, 9, 5);

    @Before
    public void setup() {
        fixture = new SessionPeriod(START_TIME, END_TIME);
    }

    @Test
    public void testIsInSessioneturnsTrueIfDateIsInsideSession() {
        long time = LocalDateTime.of(2015, 9, 3, 10, 10, 10).toInstant(ZoneOffset.UTC).toEpochMilli();
        assertTrue(fixture.isInSession(time));
    }

    @Test
    public void testIsInSessionReturnsFalseIfDateIsBeforeSession() {
        long time = LocalDateTime.of(2015, 8, 3, 10, 10, 10).toInstant(ZoneOffset.UTC).toEpochMilli();
        assertFalse(fixture.isInSession(time));
    }

    @Test
    public void testIsInSessionReturnsFalseIfDateIsAfterSession() {
        long time = LocalDateTime.of(2015, 10, 3, 10, 10, 10).toInstant(ZoneOffset.UTC).toEpochMilli();
        assertFalse(fixture.isInSession(time));
    }

    @Test
    public void testIsInSessionReturnsFalseIfDateIsJustBeforeStartOfSession() {
        long time = LocalDateTime.of(2015, 8, 31, 23, 59, 59).toInstant(ZoneOffset.UTC).toEpochMilli();
        assertFalse(fixture.isInSession(time));
    }


    @Test
    public void testIsInSessionReturnsTrueIfDateIsStartOfSession() {
        long time = LocalDateTime.of(2015, 9, 1, 0, 0, 0).toInstant(ZoneOffset.UTC).toEpochMilli();
        assertTrue(fixture.isInSession(time));
    }

    @Test
    public void testIsInSessionReturnsTrueIfDateIsEndOfSession() {
        long time = LocalDateTime.of(2015, 9, 5, 23, 59, 59).toInstant(ZoneOffset.UTC).toEpochMilli();
        assertTrue(fixture.isInSession(time));
    }

    @Test
    public void testIsInSessionReturnsFalseIfDateIsJustAfterEndOfSession() {
        long time = LocalDateTime.of(2015, 9, 6, 0, 0, 0).toInstant(ZoneOffset.UTC).toEpochMilli();
        assertFalse(fixture.isInSession(time));
    }
}
