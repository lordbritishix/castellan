package com.jjdevbros.castellan.common.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Created by lordbritishix on 04/09/15.
 */
public class DailySession {
    private final LocalDateTime startDate;

    public DailySession(long forDate) {
        LocalDate date = LocalDate.ofEpochDay(forDate / 86400000L);
        startDate = date.atStartOfDay();
    }

    public boolean isInSession(long date) {
        ChronoLocalDateTime d = ChronoLocalDateTime.from(Instant.ofEpochMilli(date));
        return startDate.isAfter(d) && startDate.plus(1L, ChronoUnit.DAYS).isBefore(d);
    }
}
