package com.jjdevbros.castellan.reportgenerator;

import com.google.common.annotations.VisibleForTesting;
import com.jjdevbros.castellan.common.EventModel;
import com.jjdevbros.castellan.common.NormalizedEventId;
import com.jjdevbros.castellan.common.NormalizedEventModel;
import com.jjdevbros.castellan.common.SessionPeriod;
import org.apache.commons.lang3.tuple.Pair;

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
    @VisibleForTesting
    Map<String, List<NormalizedEventModel>> getSessionizedEventsForPeriodWithoutSpillOver(List<EventModel> events,
                                                                                       SessionPeriod sessionPeriod) {
        return events.stream().filter(e -> sessionPeriod.isInSession(e.getTimestamp()))
                                .sorted()
                                .map(q -> normalizeEvent(q))
                                .collect(Collectors.groupingBy(p -> p.getEventModel().getUserName()));
    }

    /**
     * Produces a daily-grouped sessionized list of events:
     *
     * Session Period, Map< User Name, List<Events> >
     *
     */
    public List<Pair<SessionPeriod, Map<String, List<NormalizedEventModel>>>>
        sessionizeAndNormalize(List<EventModel> events, SessionPeriod sessionPeriod) {
        SessionPeriodSpliterator spliterator = new SessionPeriodSpliterator();

        List<SessionPeriod> splits = spliterator.splitDaily(sessionPeriod);
        return splits.stream().map(s ->
                Pair.of(s, getSessionizedEventsForPeriodWithoutSpillOver(events, s))).collect(Collectors.toList());
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
