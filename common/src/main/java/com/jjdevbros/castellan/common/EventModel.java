package com.jjdevbros.castellan.common;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * Created by lordbritishix on 04/09/15.
 */
@Data
@Builder
@EqualsAndHashCode
public class EventModel implements Comparable {
    /**
     * A unique id identifying an event
     */
    private WindowsLogEventId eventId;

    /**
     * When the event occurred, in UTC
     */
    private long timestamp;

    /**
     * The currently logged user name
     */
    private String userName;

    /**
     * Some other information about the event
     */
    private Map rawData;

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof  EventModel)) {
            throw new IllegalArgumentException();
        }

        EventModel e = (EventModel) o;

        return Long.compare(getTimestamp(), e.getTimestamp());
    }
}
