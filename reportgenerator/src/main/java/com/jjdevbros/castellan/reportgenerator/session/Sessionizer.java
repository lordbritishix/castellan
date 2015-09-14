package com.jjdevbros.castellan.reportgenerator.session;

import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.jjdevbros.castellan.common.utils.Constants;
import com.jjdevbros.castellan.common.model.EventModel;
import com.jjdevbros.castellan.common.model.NormalizedEventId;
import com.jjdevbros.castellan.common.model.NormalizedEventModel;
import com.jjdevbros.castellan.common.model.NormalizedSession;
import com.jjdevbros.castellan.common.model.SessionPeriod;
import com.jjdevbros.castellan.common.model.WindowsLogEventId;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Sessionizer {
    /**
     * Produces a daily-grouped sessionized list of events:
     *
     * Session Period, Map< User Name, List<Events> >
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
     * 2. Clean up events pass 1 (Events between the inactive - active period are cleaned up)
     * 3. Clean up events pass 2
     * 4. Normalizes the events
     * 5. Groups the events by user
     */
    public List<NormalizedSession>
        getSessionizedEventsForPeriodWithoutSpillOver(List<EventModel> events, SessionPeriod sessionPeriod) {

        List<EventModel> cleanedUpEvents = cleanUpEvents(events);

        Map<String, List<EventModel>> processedEvents =
                cleanedUpEvents.stream()
                .filter(e -> sessionPeriod.isInSession(e.getTimestamp()))
                .filter(e -> Constants.SUPPORTED_STATES.contains(e.getEventId()))
                .sorted()
                .collect(Collectors.groupingBy(p -> p.getUserName()));

        List<NormalizedSession> normalizedSessions = Lists.newArrayList();

        for (String key : processedEvents.keySet()) {
            List<EventModel> event = processedEvents.get(key);

            normalizedSessions.add(NormalizedSession.builder()
                    .hasErrors(false)
                    .events(event.stream().map(e -> normalizeEvent(e)).collect(Collectors.toList()))
                    .errorDescription("")
                    .userName(key).build());

        }

        return normalizedSessions;
    }

    private List<EventModel> cleanUpEvents(List<EventModel> events) {
        //Events between the inactive - active period are cleaned up
        List<EventModel> cleanedUpEvents = cleanupEventsBetween(events,
                WindowsLogEventId.SCREEN_LOCK, WindowsLogEventId.SCREEN_UNLOCK);

        cleanedUpEvents = cleanupEventsBetween(cleanedUpEvents,
                WindowsLogEventId.LOG_OUT, WindowsLogEventId.LOG_IN);

        cleanedUpEvents = cleanupEventsBetween(cleanedUpEvents,
                WindowsLogEventId.SCREENSAVER_ACTIVE, WindowsLogEventId.SCREENSAVER_INACTIVE);

        //Duplicate events are cleaned up

        return cleanedUpEvents.stream().distinct().collect(Collectors.toList());
    }

    /**
     * Returns a list of screen lock / unlock sessions:
     *
     * e.g.
     * SL, SU = SL, SU
     * SL, LI, SU = SL, SU
     */
    @VisibleForTesting
    List<EventModel> cleanupEventsBetween(List<EventModel> events,
                                          WindowsLogEventId startEvent, WindowsLogEventId endEvent) {
        List<EventModel> cleanedUpList = Lists.newArrayList(events.stream().sorted().collect(Collectors.toList()));
        List<EventModel> toBeRemoved = Lists.newArrayList();
        Stack<EventModel> stack = new Stack<>();

        boolean slFound = false;
        for (EventModel event : events) {
            if (event.getEventId() ==  startEvent) {
                slFound = true;
                stack.clear();
            }
            else if (event.getEventId() ==  endEvent) {
                if (slFound) {
                    toBeRemoved.addAll(stack.stream().collect(Collectors.toList()));
                    stack.clear();
                }
                slFound = false;
            }
            else {
                if (slFound) {
                    stack.push(event);
                }
            }
        }

        stack.clear();
        cleanedUpList.removeAll(toBeRemoved);
        return cleanedUpList;
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
            case SCREENSAVER_ACTIVE:
            case LOG_OUT:
                builder.eventId(NormalizedEventId.INACTIVE);
                break;

            case LOG_IN:
            case SCREEN_UNLOCK:
            case SCREENSAVER_INACTIVE:
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
