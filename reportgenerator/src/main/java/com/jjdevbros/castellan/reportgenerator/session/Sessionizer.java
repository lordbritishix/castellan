package com.jjdevbros.castellan.reportgenerator.session;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.jjdevbros.castellan.common.Constants;
import com.jjdevbros.castellan.common.EventModel;
import com.jjdevbros.castellan.common.NormalizedEventId;
import com.jjdevbros.castellan.common.NormalizedEventModel;
import com.jjdevbros.castellan.common.NormalizedSession;
import com.jjdevbros.castellan.common.SessionPeriod;
import com.jjdevbros.castellan.reportgenerator.validator.Validator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class Sessionizer {
    /**
     * Produces a daily-grouped sessionized list of events:
     *
     * Session Period, Map< User Name, List<Events> >
     *
     */
    public List<Pair<SessionPeriod, List<NormalizedSession>>>
                    sessionizeAndNormalize(List<EventModel> events, SessionPeriod sessionPeriod) {
        SessionPeriodSpliterator spliterator = new SessionPeriodSpliterator();

        List<SessionPeriod> splits = spliterator.splitDaily(sessionPeriod);
        return splits.stream()
                .map(s -> Pair.of(s, getSessionizedEventsForPeriodWithoutSpillOver(events, s)))
                .collect(Collectors.toList());
    }

    /**
     * Sessionizes an event:
     *
     * 1. Filters for events that are within session
     * 2. Normalizes the events
     * 3. Groups the events by user
     *
     * See https://docs.google.com/spreadsheets/d/1EmQpUtoFDs72c8ZHn4GvC8PnoaedbSv1n9adUEBUqjU/edit#gid=1409220163
     * for the definition of a session
     */
    public List<NormalizedSession>
        getSessionizedEventsForPeriodWithoutSpillOver(List<EventModel> events, SessionPeriod sessionPeriod) {

        Map<String, List<EventModel>> processedEvents =
            events.stream()
                .filter(e -> sessionPeriod.isInSession(e.getTimestamp()))
                .filter(e -> Constants.SUPPORTED_STATES.contains(e.getEventId()))
                .sorted()
                .collect(Collectors.groupingBy(p -> p.getUserName()));

        List<NormalizedSession> normalizedSessions = Lists.newArrayList();

        Validator validator = new Validator();
        for (String key : processedEvents.keySet()) {
            List<EventModel> event = processedEvents.get(key);

            boolean isValid = validator.isValid(event);

            if (!isValid) {
                log.warn("Invalid event detected for: {}", event.toString());
            }

            normalizedSessions.add(NormalizedSession.builder()
                    .hasErrors(!isValid)
                    .events(event.stream().map(e -> normalizeEvent(e)).collect(Collectors.toList()))
                    .userName(key).build());

        }

        return normalizedSessions;
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
    @VisibleForTesting
    NormalizedEventModel normalizeEvent(EventModel eventModel) {
        NormalizedEventModel.NormalizedEventModelBuilder builder = NormalizedEventModel.builder();
        builder.eventModel(eventModel);

        switch(eventModel.getEventId()) {
            case SCREEN_LOCK:
            case USER_INACTIVE:
            case LOG_OUT:
                builder.eventId(NormalizedEventId.INACTIVE);
                break;

            case LOG_IN:
            case SCREEN_UNLOCK:
            case USER_ACTIVE:
                builder.eventId(NormalizedEventId.ACTIVE);
                break;

            case NETWORK_CONNECTED:
            case NETWORK_DISCONNECTED:
                builder.eventId(NormalizedEventId.ACTIVE);
                break;
        }

        return builder.build();
    }
}
