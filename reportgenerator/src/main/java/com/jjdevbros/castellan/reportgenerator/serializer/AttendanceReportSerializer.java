package com.jjdevbros.castellan.reportgenerator.serializer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import com.jjdevbros.castellan.common.Constants;
import com.jjdevbros.castellan.common.EventModel;
import com.jjdevbros.castellan.common.InactivePeriod;
import com.jjdevbros.castellan.common.SessionPeriod;
import com.jjdevbros.castellan.reportgenerator.report.AttendanceReport;
import com.jjdevbros.castellan.reportgenerator.report.UserReport;

/**
 * Created by lordbritishix on 06/09/15.
 *
 * Serializes an AttendanceReport object to json
 * It is formatted in a way that it gets written directly to the report without much transformation (ideally)
 */
public class AttendanceReportSerializer extends JsonSerializer<AttendanceReport> {
    private static final SimpleDateFormat SF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss z");

    public AttendanceReportSerializer() {
        SF.setTimeZone(TimeZone.getTimeZone("UTC"));
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

        jsonGenerator.writeArrayFieldStart("sessions");

        attendanceReport.getUserReports().keySet().forEach(s -> {
                    try {
                        jsonGenerator.writeString(SF.format(Date.from(s.getStartTime().toInstant(ZoneOffset.UTC))));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
        jsonGenerator.writeEndArray();


        jsonGenerator.writeArrayFieldStart("userReports");
        Map<SessionPeriod, List<UserReport>> reports = attendanceReport.getUserReports();

        for (SessionPeriod key : reports.keySet()) {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeFieldName(
                    SF.format(Date.from(key.getStartTime().toInstant(ZoneOffset.UTC))));
            jsonGenerator.writeStartArray();

            List<UserReport> userReports = reports.get(key);

            for (UserReport userReport : userReports) {
                writeUserReport(userReport, jsonGenerator);
            }

            jsonGenerator.writeEndArray();
            jsonGenerator.writeEndObject();
        }

        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
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
        generator.writeArrayFieldStart("inactivePeriods");
        for (InactivePeriod inactivePeriod : userReport.getInactivePeriods()) {
            writeInactivePeriod(inactivePeriod, generator);
        }
        generator.writeEndArray();
        generator.writeArrayFieldStart("inactivePeriodDurations");
        for (InactivePeriod inactivePeriod : userReport.getInactivePeriods()) {
            writeDuration(inactivePeriod, generator);
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
        if (period == null) {
            generator.writeString(Constants.EN_DASH);
        }
        else {
            String formattedPeriod = String.format("%s - %s",
                    SF.format(Date.from(period.getStart().toInstant(ZoneOffset.UTC))),
                    SF.format(Date.from(period.getEnd().toInstant(ZoneOffset.UTC))));
            generator.writeString(formattedPeriod);
        }
    }

    private void writeInstant(String key, Instant value, JsonGenerator generator) throws IOException {
        if (value != null) {
            generator.writeStringField(key, SF.format(Date.from(value)));
        }
        else {
            generator.writeStringField(key, Constants.EN_DASH);
        }
    }

    private void writeDuration(String key, Duration value, JsonGenerator generator) throws IOException {
        if (value == null) {
            generator.writeStringField(key, Constants.EN_DASH);
        }
        else {
            long sec = value.getSeconds();
            String duration = String.format("%d:%02d:%02d", sec / 3600, (sec % 3600) / 60, (sec % 60));
            generator.writeStringField(key, duration);
        }
    }

    private void writeDuration(InactivePeriod period, JsonGenerator generator) throws IOException {
        if (period == null) {
            generator.writeString(Constants.EN_DASH);
        }
        else {
            long sec = period.getDuration().getSeconds();
            String duration = String.format("%d:%02d:%02d", sec / 3600, (sec % 3600) / 60, (sec % 60));
            generator.writeString(duration);
        }
    }


}
