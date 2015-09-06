package com.jjdevbros.castellan.common;

import lombok.Builder;
import lombok.Data;

/**
 * Created by lordbritishix on 05/09/15.
 */
@Data
@Builder
public class NormalizedEventModel {
    /**
     * Normalized event id
     */
    private NormalizedEventId eventId;

    /**
     * Reference to the event model
     */
    private EventModel eventModel;

    /**
     * If it is marked as invalid in the context of the session
     */
    private boolean invalid;
}
