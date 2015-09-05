package com.jjdevbros.castellan.common;

import lombok.Data;

/**
 * Created by lordbritishix on 05/09/15.
 */
@Data
public class NormalizedEventModel {
    private NormalizedEventId eventId;
    private EventModel eventModel;
}
