package com.jjdevbros.castellan.reportgenerator.report;

import com.jjdevbros.castellan.common.SessionPeriod;
import com.jjdevbros.castellan.reportgenerator.serializer.AttendanceReportSerializer;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by lordbritishix on 05/09/15.
 *
 * Models the attendance report for a given period
 */
@Data
@Builder
@JsonSerialize(using = AttendanceReportSerializer.class)
public class AttendanceReport {
    private UUID reportId;

    private Instant reportedGeneratedAt;

    private SessionPeriod period;

    @Singular
    private Map<SessionPeriod, List<UserReport>> userReports;
}
