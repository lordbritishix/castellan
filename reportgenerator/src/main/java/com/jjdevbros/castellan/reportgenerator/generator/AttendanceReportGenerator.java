package com.jjdevbros.castellan.reportgenerator.generator;

import com.jjdevbros.castellan.common.EventModel;
import com.jjdevbros.castellan.common.NormalizedEventModel;
import com.jjdevbros.castellan.common.SessionPeriod;
import com.jjdevbros.castellan.reportgenerator.report.AttendanceReport;
import com.jjdevbros.castellan.reportgenerator.session.Sessionizer;
import org.apache.commons.lang3.tuple.Pair;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by lordbritishix on 06/09/15.
 */
public class AttendanceReportGenerator {
    public AttendanceReport generateAttendanceReport(List<EventModel> events, SessionPeriod sessionPeriod) {
        AttendanceReport.AttendanceReportBuilder builder = AttendanceReport.builder()
                .reportedGeneratedAt(Instant.now())
                .period(sessionPeriod)
                .reportId(UUID.randomUUID());

        Sessionizer sessionizer = new Sessionizer();
        List<Pair<SessionPeriod, Map<String, List<NormalizedEventModel>>>> report =
                                                            sessionizer.sessionizeAndNormalize(events, sessionPeriod);

        builder.userReports(report.stream().map(session ->
                        session.getValue().entrySet().stream().map(p -> {
                            UserReportGenerator generator = new UserReportGenerator();
                            return generator.generateUserReport(p.getKey(), p.getValue(), session.getKey());
                        }).collect(Collectors.toList())
        ).flatMap(e -> e.stream()).collect(Collectors.groupingBy(p -> p.getPeriod())));

        return builder.build();
    }
}
