package com.jjdevbros.castellan.reportgenerator.report;

import com.jjdevbros.castellan.common.InactivePeriod;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Created by lordbritishix on 05/09/15.
 */
@Data
@Builder
public class UserReport {
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
     * List of inactive periods for this user
     */
    @Singular
    private List<InactivePeriod> inactivePeriods;
}
