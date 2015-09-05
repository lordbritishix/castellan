package com.jjdevbros.castellan.reportgenerator;

import com.google.common.collect.Lists;
import com.jjdevbros.castellan.common.EventModel;
import com.jjdevbros.castellan.common.NormalizedEventModel;
import com.jjdevbros.castellan.common.SessionPeriod;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Sessionizer {
    /**
     * Sessionizes a list of events (without spill-over)
     *
     * See https://docs.google.com/spreadsheets/d/1EmQpUtoFDs72c8ZHn4GvC8PnoaedbSv1n9adUEBUqjU/edit#gid=1409220163
     * for the definition of a session
     */
    public Map<String, List<EventModel>> getSessionizedEventsForPeriodWithoutSpillOver(List<EventModel> events,
                                                                                       SessionPeriod sessionPeriod) {
        return events.stream().filter(e -> sessionPeriod.isInSession(e.getTimestamp()))
                                .sorted()
                                .collect(Collectors.groupingBy(p -> p.getUserName()));
    }

    /**
     * Normalization procedure:
     *
     * Convert to Inactive:
     *   LogOut
     *   Idle
     *   ScreenLock
     *
     * Convert to Active:
     *    LogIn
     *    NotIdle
     *    ScreenUnlock
     *
     * Tag invalid data
     */
    public List<NormalizedEventModel> normalize(List<EventModel> events) {
        List<NormalizedEventModel> normalizedEvents = Lists.newArrayList();

        return normalizedEvents;
    }
}
