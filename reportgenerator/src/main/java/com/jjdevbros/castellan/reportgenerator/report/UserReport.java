package com.jjdevbros.castellan.reportgenerator.report;

import com.jjdevbros.castellan.common.InactivePeriod;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

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
    private long startTime;

    /**
     * Time the user finished working
     */
    private long endTime;

    /**
     * List of inactive periods for this user
     */
    @Singular
    private List<InactivePeriod> inactivePeriods;
}
