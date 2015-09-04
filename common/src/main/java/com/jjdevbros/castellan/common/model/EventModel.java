package com.jjdevbros.castellan.common.model;

import lombok.Data;

import java.util.Map;

/**
 * Created by lordbritishix on 04/09/15.
 */
@Data
public class EventModel {
    /**
     * A unique id identifying an event
     */
    private long eventId;

    /**
     * A user-friendly description of the event
     */
    private String eventName;

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
}
