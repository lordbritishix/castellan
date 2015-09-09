package com.jjdevbros.castellan.reportgenerator.report;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import com.jjdevbros.castellan.common.EventModel;
import com.jjdevbros.castellan.common.InactivePeriod;
import com.jjdevbros.castellan.common.SessionPeriod;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

/**
 * Created by lordbritishix on 05/09/15.
 *
 * Models the user report
 */
@Data
@Builder
public class UserReport implements Comparable {
    private SessionPeriod period;

    /**
     * Name of the user
     */
    private String userName;

    /**
     * Time the user started working
     */
    private Instant startTime;

    /**
     * Time the user finished working
     */
    private Instant endTime;

    /**
     * Duration of inactivity
     */
    private Duration inactivityDuration;

    /**
     * Duration of activity
     */
    private Duration activityDuration;

    /**
     * Duration between end time and start time
     */
    private Duration workDuration;

    /**
     * Flag indicating errors in the data
     */
    private boolean hasErrors;

    /**
     * Description of the error
     */
    private String errorDescription;

    /**
     * List of inactive periods for this user
     */
    @Singular
    private List<InactivePeriod> inactivePeriods;

    /**
     * Store raw events for debugging
     */
    @Singular
    private List<EventModel> sourceEvents;

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof UserReport)) {
            throw new UnsupportedOperationException();
        }

        UserReport u = (UserReport) o;
        return startTime.compareTo(u.getStartTime());
    }
}
