package com.jjdevbros.castellan.reportgenerator.serializer;

import com.google.common.collect.ImmutableList;
import com.jjdevbros.castellan.common.EventModel;
import com.jjdevbros.castellan.common.SessionPeriod;
import com.jjdevbros.castellan.common.WindowsLogEventId;
import com.jjdevbros.castellan.reportgenerator.generator.AttendanceReportGenerator;
import com.jjdevbros.castellan.reportgenerator.report.AttendanceReport;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by lordbritishix on 06/09/15.
 */
public class JsonWriterTest {
    private AttendanceReportGenerator generator = new AttendanceReportGenerator();
    private JsonWriter fixture;

    @Before
    public void setup() {
        fixture = new JsonWriter();
    }

    @Test
    public void generateJsonReturnsCorrectJsonForHappyCase() throws IOException {
        List<EventModel> events = ImmutableList.of(
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-02T08:15:30.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.SCREEN_LOCK, "2015-09-02T09:15:30.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.SCREEN_UNLOCK, "2015-09-02T10:15:30.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-02T18:15:30.00Z", "Jim"),

                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-02T08:15:30.00Z", "Jeff"),
                buildTestEvent(WindowsLogEventId.SCREEN_LOCK, "2015-09-02T09:15:30.00Z", "Jeff"),
                buildTestEvent(WindowsLogEventId.SCREEN_UNLOCK, "2015-09-02T10:15:30.00Z", "Jeff"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-02T18:15:30.00Z", "Jeff"),

                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-03T08:15:30.00Z", "Jen"),
                buildTestEvent(WindowsLogEventId.SCREEN_LOCK, "2015-09-03T09:15:30.00Z", "Jen"),
                buildTestEvent(WindowsLogEventId.SCREEN_UNLOCK, "2015-09-03T10:15:30.00Z", "Jen"),
                buildTestEvent(WindowsLogEventId.SCREEN_LOCK, "2015-09-03T11:15:30.00Z", "Jen"),
                buildTestEvent(WindowsLogEventId.SCREEN_UNLOCK, "2015-09-03T12:15:30.00Z", "Jen"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-03T18:15:30.00Z", "Jen")
        );

        SessionPeriod period = new SessionPeriod(LocalDate.of(2015, 9, 2), LocalDate.of(2015, 9, 3));

        AttendanceReport attendanceReport = generator.generateAttendanceReport(events, period);
        String json = fixture.serialize(attendanceReport);
        System.out.println(json);
    }

    @Test
    public void generateJsonReturnsCorrectJsonForInvalidCase() throws IOException {
        List<EventModel> events = ImmutableList.of(
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-02T08:15:30.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.SCREEN_LOCK, "2015-09-02T09:15:30.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.USER_ACTIVE, "2015-09-02T09:30:30.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-02T18:15:30.00Z", "Jim")
        );

        SessionPeriod period = new SessionPeriod(LocalDate.of(2015, 9, 2), LocalDate.of(2015, 9, 3));

        AttendanceReport attendanceReport = generator.generateAttendanceReport(events, period);
        String json = fixture.serialize(attendanceReport);
        System.out.println(json);
    }


    private EventModel buildTestEvent(WindowsLogEventId eventId, String timestamp, String username) {
        return EventModel.builder().eventId(eventId)
                .timestamp(Instant.parse(timestamp).toEpochMilli())
                .userName(username)
                .build();
    }
}
