package com.jjdevbros.castellan.reportgenerator.serializer;

import com.google.common.collect.Lists;
import com.jjdevbros.castellan.common.model.EventModel;
import com.jjdevbros.castellan.common.model.InactivePeriod;
import com.jjdevbros.castellan.common.model.SessionPeriod;
import com.jjdevbros.castellan.common.utils.Constants;
import com.jjdevbros.castellan.reportgenerator.report.AttendanceReport;
import com.jjdevbros.castellan.reportgenerator.report.UserReport;
import com.jjdevbros.castellan.reportgenerator.session.SessionPeriodSpliterator;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

/**
 * Created by lordbritishix on 06/09/15.
 *
 * Serializes an AttendanceReport object to json
 * It is formatted in a way that it gets written directly to the report without much transformation (ideally)
 */
public class AttendanceReportSerializer extends JsonSerializer<AttendanceReport> {
    private static final SimpleDateFormat SF = new SimpleDateFormat("MM/dd/yy h:mm:ss a");
    private static final SimpleDateFormat SF_INACTIVITY = new SimpleDateFormat("HH:mm:ss");

    public AttendanceReportSerializer() {
        SF.setTimeZone(TimeZone.getTimeZone("UTC"));
        SF_INACTIVITY.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public void serialize(AttendanceReport attendanceReport, JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("reportId", attendanceReport.getReportId().toString());
        writeInstant("reportedGeneratedAt", attendanceReport.getReportedGeneratedAt(), jsonGenerator);
        writeInstant("sessionStart",
                attendanceReport.getPeriod().getStartTime().toInstant(ZoneOffset.UTC), jsonGenerator);
        writeInstant("sessionEnd", attendanceReport.getPeriod().getEndTime().toInstant(ZoneOffset.UTC), jsonGenerator);
        writeUserReports(attendanceReport, jsonGenerator);

        jsonGenerator.writeEndObject();
    }


    private void writeUserReports(AttendanceReport attendanceReport, JsonGenerator jsonGenerator)
            throws IOException {
        jsonGenerator.writeArrayFieldStart("userReports");

        SessionPeriodSpliterator spliterator = new SessionPeriodSpliterator();
        List<SessionPeriod> sessions = spliterator.splitDaily(attendanceReport.getPeriod());

        Map<SessionPeriod, List<UserReport>> userReportsMap = new LinkedHashMap<>();

        for (SessionPeriod session : sessions) {
            userReportsMap.put(session, Lists.newArrayList());
        }

        for (SessionPeriod session : attendanceReport.getUserReports().keySet()) {
            List<UserReport> report = userReportsMap.get(session);
            if (report != null) {
                userReportsMap.put(session, attendanceReport.getUserReports().get(session));
            }
        }

        for (SessionPeriod key : userReportsMap.keySet()) {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("userReportSessionStart",
                    SF.format(Date.from(key.getStartTime().toInstant(ZoneOffset.UTC))));
            jsonGenerator.writeStringField("userReportSessionEnd",
                    SF.format(Date.from(key.getEndTime().toInstant(ZoneOffset.UTC))));
            jsonGenerator.writeArrayFieldStart("report");
            List<UserReport> userReports = userReportsMap.get(key);

            for (UserReport userReport : userReports) {
                writeUserReport(userReport, jsonGenerator);
            }
            jsonGenerator.writeEndArray();
            jsonGenerator.writeEndObject();
        }

        jsonGenerator.writeEndArray();
    }


    private void writeUserReport(UserReport userReport, JsonGenerator generator) throws IOException {
        generator.writeStartObject();
        generator.writeStringField("userName", userReport.getUserName());
        writeInstant("startTime", userReport.getStartTime(), generator);
        writeInstant("endTime", userReport.getEndTime(), generator);
        writeDuration("inactivityDuration", userReport.getInactivityDuration(), generator);
        writeDuration("activityDuration", userReport.getActivityDuration(), generator);
        writeDuration("workDuration", userReport.getWorkDuration(), generator);
        generator.writeBooleanField("hasErrors", userReport.isHasErrors());
        generator.writeStringField("errorDescription", userReport.getErrorDescription());
        generator.writeStringField("group", userReport.getGroup());
        generator.writeArrayFieldStart("inactivePeriods");
        for (InactivePeriod inactivePeriod : userReport.getInactivePeriods()) {
            writeInactivePeriod(inactivePeriod, generator);
        }
        generator.writeEndArray();

        generator.writeStringField("src", sourceEventsFormatter(userReport.getSourceEvents()));
        generator.writeEndObject();
    }

    private String sourceEventsFormatter(List<EventModel> events) {
        return events.stream()
                .map(p -> p.getEventId().toString() + " " + p.getUserName() + " "
                        + SF.format(Date.from(Instant.ofEpochMilli(p.getTimestamp()))))
                .collect(Collectors.joining("\t"));
    }


    private void writeInactivePeriod(InactivePeriod period, JsonGenerator generator) throws IOException {
        generator.writeStartObject();
        if (period == null) {
            generator.writeStringField("startInactivityPeriod", "");
            generator.writeStringField("endInactivityPeriod", "");
            generator.writeStringField("duration", "");
        }
        else {
            String formattedPeriod = String.format("%s - %s",
                    SF_INACTIVITY.format(Date.from(period.getStart().toInstant(ZoneOffset.UTC))),
                    SF_INACTIVITY.format(Date.from(period.getEnd().toInstant(ZoneOffset.UTC))));
            generator.writeStringField("period", formattedPeriod);
            generator.writeStringField("duration", toFormattedDuration(period.getDuration()));
        }
        generator.writeEndObject();
    }

    private void writeInstant(String key, Instant value, JsonGenerator generator) throws IOException {
        if (value != null) {
            generator.writeStringField(key, SF.format(Date.from(value)));
        }
        else {
            generator.writeStringField(key, "");
        }
    }

    private void writeDuration(String key, Duration value, JsonGenerator generator) throws IOException {
        if (value == null) {
            generator.writeStringField(key, Constants.EN_DASH);
        }
        else {
            generator.writeStringField(key, toFormattedDuration(value));
        }
    }

    private String toFormattedDuration(Duration duration) {
        long sec = duration.getSeconds();
        return String.format("%d:%02d:%02d", sec / 3600, (sec % 3600) / 60, (sec % 60));
    }


}
