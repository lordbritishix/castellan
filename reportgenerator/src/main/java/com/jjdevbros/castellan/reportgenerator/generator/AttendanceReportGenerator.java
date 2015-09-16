package com.jjdevbros.castellan.reportgenerator.generator;

import com.google.inject.Inject;
import com.jjdevbros.castellan.common.model.EventModel;
import com.jjdevbros.castellan.common.model.NormalizedSession;
import com.jjdevbros.castellan.common.model.SessionPeriod;
import com.jjdevbros.castellan.reportgenerator.report.AttendanceReport;
import com.jjdevbros.castellan.reportgenerator.session.Sessionizer;
import org.apache.commons.lang3.tuple.Pair;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by lordbritishix on 06/09/15.
 */
public class AttendanceReportGenerator {
    private final UserReportGenerator generator;

    @Inject
    public AttendanceReportGenerator(UserReportGenerator generator) {
        this.generator = generator;
    }

    public AttendanceReport generateAttendanceReport(List<EventModel> events, SessionPeriod sessionPeriod) {
        AttendanceReport.AttendanceReportBuilder builder = AttendanceReport.builder()
                .reportedGeneratedAt(Instant.now())
                .period(sessionPeriod)
                .reportId(UUID.randomUUID());
        Sessionizer sessionizer = new Sessionizer();
        List<Pair<SessionPeriod, List<NormalizedSession>>> report =
                                                        sessionizer.sessionizeAndNormalize(events, sessionPeriod);

        builder.userReports(report.stream().map(session ->
                            session.getValue().stream().map(p -> {
                                return generator.generateUserReport(p.getUserName(), p, session.getKey());
                            }).collect(Collectors.toList())
        ).flatMap(e -> e.stream()).collect(Collectors.groupingBy(p -> p.getPeriod())));

        return builder.build();
    }
}
