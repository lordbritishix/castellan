package com.jjdevbros.castellan.reportgenerator.report;

import com.jjdevbros.castellan.common.SessionPeriod;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by lordbritishix on 05/09/15.
 */
@Data
@Builder
public class AttendanceReport {
    private UUID reportId;

    private Instant reportedGeneratedAt;

    private SessionPeriod period;

    @Singular
    private Map<SessionPeriod, List<UserReport>> userReports;
}
