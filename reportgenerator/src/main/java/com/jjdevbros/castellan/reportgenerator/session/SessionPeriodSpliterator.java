package com.jjdevbros.castellan.reportgenerator.session;

import com.google.common.collect.Lists;
import com.jjdevbros.castellan.common.SessionPeriod;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Created by lordbritishix on 05/09/15.
 */
public class SessionPeriodSpliterator {
    /**
     * Splits a SessionPeriod into a list of 1-day Session Periods
     */
    public List<SessionPeriod> splitDaily(SessionPeriod period) {
        List<SessionPeriod> splits = Lists.newArrayList();
        int periodInDays = period.getDuration().getDays();

        LocalDateTime next = period.getStartTime().toLocalDate().atStartOfDay();
        for (int x = 0; x < periodInDays; ++x) {
            splits.add(new SessionPeriod(next.toLocalDate(), next.toLocalDate()));
            next = next.plus(1L, ChronoUnit.DAYS);
        }

        return splits;
    }
}
