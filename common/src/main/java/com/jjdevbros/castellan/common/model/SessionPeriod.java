package com.jjdevbros.castellan.common.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@EqualsAndHashCode
public class SessionPeriod implements Comparable {
    @Getter
    private LocalDateTime startTime;

    @Getter
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
        LocalDateTime compare = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneOffset.UTC);

        return compare.isAfter(startTime.minus(1L, ChronoUnit.MILLIS)) && compare.isBefore(endTime);
    }

    public long getDaysInBetween() {
        return ChronoUnit.DAYS.between(startTime.toLocalDate(), endTime.toLocalDate());
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof  SessionPeriod)) {
            throw new UnsupportedOperationException();
        }

        SessionPeriod e = (SessionPeriod) o;

        return startTime.compareTo(e.getStartTime());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append(startTime.toString());
        builder.append(" - ");
        builder.append(endTime.toString());

        return builder.toString();
    }
}
