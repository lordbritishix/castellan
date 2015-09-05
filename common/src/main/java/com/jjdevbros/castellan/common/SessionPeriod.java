package com.jjdevbros.castellan.common;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class SessionPeriod {
    private static final ZoneId UTC = ZoneId.of("UTC");
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public SessionPeriod(LocalDate startTime, LocalDate endTime) {

        this.startTime = startTime.atStartOfDay();
        this.endTime = endTime.atStartOfDay().plus(1L, ChronoUnit.DAYS);
    }

    /**
     * Provided time is in session if:
     *
     * It is equal to or after the start time
     * It is less than the end time
     */
    public boolean isInSession(long time) {
        LocalDateTime compare = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), UTC);

        return compare.isAfter(startTime.minus(1L, ChronoUnit.MILLIS)) && compare.isBefore(endTime);
    }
}
