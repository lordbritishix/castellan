package com.jjdevbros.castellan.common.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
     * Name of the host
     */
    private String hostName;

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof  EventModel)) {
            throw new UnsupportedOperationException();
        }

        EventModel e = (EventModel) o;

        return Long.compare(getTimestamp(), e.getTimestamp());
    }
}
