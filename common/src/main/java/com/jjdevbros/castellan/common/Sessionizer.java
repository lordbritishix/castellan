package com.jjdevbros.castellan.common;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import com.google.common.collect.Maps;

public class Sessionizer {
    /**
     * Sessionizes a list of event, returning a list containing the username and a list of sessionized EventModel
     *
     * See https://docs.google.com/spreadsheets/d/1EmQpUtoFDs72c8ZHn4GvC8PnoaedbSv1n9adUEBUqjU/edit#gid=1409220163
     * for the definition of a session
     *
     * @param events List of events that will be sessionized
     * @return
     */
    public Map<String, List<EventModel>> getDailySessionizedEvents(List<EventModel> events, LocalDate date) {
        Map<String, List<EventModel>> sessionizedEvents = Maps.newLinkedHashMap();

        sessionizedEvents.entrySet().stream();

        return sessionizedEvents;
    }
}
