package com.jjdevbros.castellan.common.utils;

import com.google.common.collect.Lists;
import com.jjdevbros.castellan.common.model.SessionPeriod;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Created by lordbritishix on 05/09/15.
 */
public class SessionPeriodIndexSpliterator {
    public List<String> splitDaily(SessionPeriod sessionPeriod) {
        List<String> splits = Lists.newArrayList();
        long periodInDays = sessionPeriod.getDaysInBetween();

        LocalDateTime next = sessionPeriod.getStartTime().toLocalDate().atStartOfDay();
        for (int x = 0; x < periodInDays; ++x) {
            splits.add(Utils.indexForDate(next.toLocalDate()));
            next = next.plus(1L, ChronoUnit.DAYS);
        }

        return splits;
    }
}
