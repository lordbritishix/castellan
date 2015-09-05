package com.jjdevbros.castellan.reportgenerator;

import com.jjdevbros.castellan.common.EventModel;
import com.jjdevbros.castellan.common.SessionPeriod;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Sessionizer {
    /**
     * Sessionizes a list of events
     *
     * See https://docs.google.com/spreadsheets/d/1EmQpUtoFDs72c8ZHn4GvC8PnoaedbSv1n9adUEBUqjU/edit#gid=1409220163
     * for the definition of a session
     */
    public Map<String, List<EventModel>> getSessionizedEventsForPeriod(List<EventModel> events,
                                                                       SessionPeriod sessionPeriod) {
        return events.stream().filter(e -> sessionPeriod.isInSession(e.getTimestamp()))
                                .sorted()
                                .collect(Collectors.groupingBy(p -> p.getUserName()));
    }
}
