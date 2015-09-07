package com.jjdevbros.castellan.reportgenerator.validator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.jjdevbros.castellan.common.Constants;
import com.jjdevbros.castellan.common.EventModel;
import com.jjdevbros.castellan.common.WindowsLogEventId;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by lordbritishix on 06/09/15.
 */
public class Validator {
    private final Map<WindowsLogEventId, List<WindowsLogEventId>> STATE_MACHINE = Maps.newHashMap();

    public Validator() {
        STATE_MACHINE.put(
            WindowsLogEventId.LOG_IN,
            ImmutableList.of(WindowsLogEventId.LOG_OUT, WindowsLogEventId.SCREEN_LOCK, WindowsLogEventId.USER_INACTIVE)
        );

        STATE_MACHINE.put(
            WindowsLogEventId.LOG_OUT,
            ImmutableList.of(WindowsLogEventId.LOG_IN)
        );

        STATE_MACHINE.put(
            WindowsLogEventId.USER_INACTIVE,
            ImmutableList.of(WindowsLogEventId.USER_ACTIVE)
        );

        STATE_MACHINE.put(
            WindowsLogEventId.USER_ACTIVE,
            ImmutableList.of(WindowsLogEventId.SCREEN_LOCK, WindowsLogEventId.LOG_OUT, WindowsLogEventId.USER_INACTIVE)
        );

        STATE_MACHINE.put(
            WindowsLogEventId.SCREEN_LOCK,
            ImmutableList.of(WindowsLogEventId.SCREEN_UNLOCK, WindowsLogEventId.LOG_OUT)
        );

        STATE_MACHINE.put(
            WindowsLogEventId.SCREEN_UNLOCK,
            ImmutableList.of(WindowsLogEventId.SCREEN_LOCK, WindowsLogEventId.USER_INACTIVE, WindowsLogEventId.LOG_OUT)
        );
    }

    public boolean isValid(List<EventModel> events) {
        if (events.size() <= 0) {
            return true;
        }

        List<EventModel> sortedEvents = events.stream().sorted().collect(Collectors.toList());

        WindowsLogEventId previous = null;

        for (EventModel e : sortedEvents) {
            if (previous == null) {
                previous = e.getEventId();
                continue;
            }

            if (!Constants.SUPPORTED_STATES.contains(e.getEventId())) {
                continue;
            }

            List<WindowsLogEventId> allowableStates = STATE_MACHINE.get(previous);

            if (!allowableStates.contains(e.getEventId())) {
                return false;
            }

            previous = e.getEventId();
        }

        return true;
    }

}
