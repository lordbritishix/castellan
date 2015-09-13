package com.jjdevbros.castellan.common.model;

import com.jjdevbros.castellan.common.model.SessionPeriod;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void testGetDurationReturnsCorrectDuration() {
        assertThat(fixture.getDaysInBetween(), is(5L));
    }

}
