package com.jjdevbros.castellan.reportgenerator.serializer;

import com.google.common.collect.ImmutableList;
import com.jjdevbros.castellan.common.EventModel;
import com.jjdevbros.castellan.common.SessionPeriod;
import com.jjdevbros.castellan.common.WindowsLogEventId;
import com.jjdevbros.castellan.reportgenerator.generator.AttendanceReportGenerator;
import com.jjdevbros.castellan.reportgenerator.report.AttendanceReport;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by lordbritishix on 07/09/15.
 */
public class UseCasesTest {
    private AttendanceReportGenerator generator = new AttendanceReportGenerator();
    private JsonWriter fixture;

    @Before
    public void setup() {
        fixture = new JsonWriter();
    }

    @Test
    public void testScreensaverKicksInWithWorkstationLocking() throws IOException {
        List<EventModel> events = ImmutableList.of(
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-02T08:00:00.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.SCREENSAVER_ACTIVE, "2015-09-02T09:00:00.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.SCREENSAVER_INACTIVE, "2015-09-02T10:00:00.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.SCREEN_LOCK, "2015-09-02T10:00:00.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-02T10:05:00.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.SCREEN_UNLOCK, "2015-09-02T10:10:00.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-02T18:00:00.00Z", "Jim")
        );

        SessionPeriod period = new SessionPeriod(LocalDate.of(2015, 9, 2), LocalDate.of(2015, 9, 3));

        JsonNode report = generateAttendanceReport(events, period);
        JsonNode userReport = report.get("userReports").get(0).get("2015-09-02T00:00:00 UTC").get(0);

        assertThat(userReport.get("userName").asText(), is("Jim"));
        assertThat(userReport.get("startTime").asText(), is("2015-09-02T08:00:00 UTC"));
        assertThat(userReport.get("endTime").asText(), is("2015-09-02T18:00:00 UTC"));
        assertThat(userReport.get("inactivityDuration").asText(), is("1:10:00"));
        assertThat(userReport.get("activityDuration").asText(), is("8:50:00"));
        assertThat(userReport.get("workDuration").asText(), is("10:00:00"));
        assertThat(userReport.get("inactivePeriods").size(), is(2));
        assertThat(userReport.get("inactivePeriods").get(0).asText(), is("2015-09-02T09:00:00 UTC - 2015-09-02T10:00:00 UTC"));
        assertThat(userReport.get("inactivePeriods").get(1).asText(), is("2015-09-02T10:00:00 UTC - 2015-09-02T10:10:00 UTC"));
    }

    @Test
    public void testScreensaverKicksInWithoutWorkstationLocking() throws IOException {
        List<EventModel> events = ImmutableList.of(
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-02T08:00:00.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.SCREENSAVER_ACTIVE, "2015-09-02T09:00:00.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.SCREENSAVER_INACTIVE, "2015-09-02T10:00:00.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-02T18:00:00.00Z", "Jim")
        );

        SessionPeriod period = new SessionPeriod(LocalDate.of(2015, 9, 2), LocalDate.of(2015, 9, 3));

        JsonNode report = generateAttendanceReport(events, period);
        JsonNode userReport = report.get("userReports").get(0).get("2015-09-02T00:00:00 UTC").get(0);

        assertThat(userReport.get("userName").asText(), is("Jim"));
        assertThat(userReport.get("startTime").asText(), is("2015-09-02T08:00:00 UTC"));
        assertThat(userReport.get("endTime").asText(), is("2015-09-02T18:00:00 UTC"));
        assertThat(userReport.get("inactivityDuration").asText(), is("1:00:00"));
        assertThat(userReport.get("activityDuration").asText(), is("9:00:00"));
        assertThat(userReport.get("workDuration").asText(), is("10:00:00"));
        assertThat(userReport.get("inactivePeriods").size(), is(1));
        assertThat(userReport.get("inactivePeriods").get(0).asText(), is("2015-09-02T09:00:00 UTC - 2015-09-02T10:00:00 UTC"));
    }

    @Test
    public void testLogOnLogOff() throws IOException {
        List<EventModel> events = ImmutableList.of(
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-02T08:00:00.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-02T18:00:00.00Z", "Jim")
        );

        SessionPeriod period = new SessionPeriod(LocalDate.of(2015, 9, 2), LocalDate.of(2015, 9, 3));

        JsonNode report = generateAttendanceReport(events, period);
        JsonNode userReport = report.get("userReports").get(0).get("2015-09-02T00:00:00 UTC").get(0);

        assertThat(userReport.get("userName").asText(), is("Jim"));
        assertThat(userReport.get("startTime").asText(), is("2015-09-02T08:00:00 UTC"));
        assertThat(userReport.get("endTime").asText(), is("2015-09-02T18:00:00 UTC"));
        assertThat(userReport.get("inactivityDuration").asText(), is("0:00:00"));
        assertThat(userReport.get("activityDuration").asText(), is("10:00:00"));
        assertThat(userReport.get("workDuration").asText(), is("10:00:00"));
        assertThat(userReport.get("inactivePeriods").size(), is(0));
    }

    @Test
    public void testScreenLockUnlock() throws IOException {
        List<EventModel> events = ImmutableList.of(
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-02T08:00:00.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.SCREEN_LOCK, "2015-09-02T10:00:00.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-02T10:05:00.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.SCREEN_UNLOCK, "2015-09-02T10:10:00.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-02T18:00:00.00Z", "Jim")
        );

        SessionPeriod period = new SessionPeriod(LocalDate.of(2015, 9, 2), LocalDate.of(2015, 9, 3));

        JsonNode report = generateAttendanceReport(events, period);
        JsonNode userReport = report.get("userReports").get(0).get("2015-09-02T00:00:00 UTC").get(0);

        assertThat(userReport.get("userName").asText(), is("Jim"));
        assertThat(userReport.get("startTime").asText(), is("2015-09-02T08:00:00 UTC"));
        assertThat(userReport.get("endTime").asText(), is("2015-09-02T18:00:00 UTC"));
        assertThat(userReport.get("inactivityDuration").asText(), is("0:10:00"));
        assertThat(userReport.get("activityDuration").asText(), is("9:50:00"));
        assertThat(userReport.get("workDuration").asText(), is("10:00:00"));
        assertThat(userReport.get("inactivePeriods").size(), is(1));
        assertThat(userReport.get("inactivePeriods").get(0).asText(), is("2015-09-02T10:00:00 UTC - 2015-09-02T10:10:00 UTC"));
    }

    @Test
    public void testPowerOffPowerOn() throws IOException {
        List<EventModel> events = ImmutableList.of(
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-02T08:00:00.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-02T10:00:00.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-02T12:00:00.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-02T18:00:00.00Z", "Jim")
        );

        SessionPeriod period = new SessionPeriod(LocalDate.of(2015, 9, 2), LocalDate.of(2015, 9, 3));

        JsonNode report = generateAttendanceReport(events, period);
        JsonNode userReport = report.get("userReports").get(0).get("2015-09-02T00:00:00 UTC").get(0);

        assertThat(userReport.get("userName").asText(), is("Jim"));
        assertThat(userReport.get("startTime").asText(), is("2015-09-02T08:00:00 UTC"));
        assertThat(userReport.get("endTime").asText(), is("2015-09-02T18:00:00 UTC"));
        assertThat(userReport.get("inactivityDuration").asText(), is("2:00:00"));
        assertThat(userReport.get("activityDuration").asText(), is("8:00:00"));
        assertThat(userReport.get("workDuration").asText(), is("10:00:00"));
        assertThat(userReport.get("inactivePeriods").size(), is(1));
        assertThat(userReport.get("inactivePeriods").get(0).asText(), is("2015-09-02T10:00:00 UTC - 2015-09-02T12:00:00 UTC"));
    }

    @Test
    public void testAbruptShutdown() throws IOException {
        List<EventModel> events = ImmutableList.of(
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-02T08:00:00.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-02T10:00:00.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-02T12:00:00.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-02T18:00:00.00Z", "Jim")
        );

        SessionPeriod period = new SessionPeriod(LocalDate.of(2015, 9, 2), LocalDate.of(2015, 9, 3));

        JsonNode report = generateAttendanceReport(events, period);
        JsonNode userReport = report.get("userReports").get(0).get("2015-09-02T00:00:00 UTC").get(0);

        assertThat(userReport.get("userName").asText(), is("Jim"));
        assertThat(userReport.get("startTime").asText(), is("2015-09-02T08:00:00 UTC"));
        assertThat(userReport.get("endTime").asText(), is("2015-09-02T18:00:00 UTC"));
        assertThat(userReport.get("inactivityDuration").asText(), is("0:00:00"));
        assertThat(userReport.get("activityDuration").asText(), is("10:00:00"));
        assertThat(userReport.get("workDuration").asText(), is("10:00:00"));
        assertThat(userReport.get("inactivePeriods").size(), is(0));
    }

    @Test
    public void testScreenLockThenRestart() throws IOException {
        List<EventModel> events = ImmutableList.of(
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-02T08:00:00.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.SCREEN_LOCK, "2015-09-02T10:00:00.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-02T10:05:00.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-02T10:10:00.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-02T18:00:00.00Z", "Jim")
        );

        SessionPeriod period = new SessionPeriod(LocalDate.of(2015, 9, 2), LocalDate.of(2015, 9, 3));

        JsonNode report = generateAttendanceReport(events, period);
        JsonNode userReport = report.get("userReports").get(0).get("2015-09-02T00:00:00 UTC").get(0);

        assertThat(userReport.get("userName").asText(), is("Jim"));
        assertThat(userReport.get("startTime").asText(), is("2015-09-02T08:00:00 UTC"));
        assertThat(userReport.get("endTime").asText(), is("2015-09-02T18:00:00 UTC"));
        assertThat(userReport.get("inactivityDuration").asText(), is("0:10:00"));
        assertThat(userReport.get("activityDuration").asText(), is("9:50:00"));
        assertThat(userReport.get("workDuration").asText(), is("10:00:00"));
        assertThat(userReport.get("inactivePeriods").size(), is(1));
        assertThat(userReport.get("inactivePeriods").get(0).asText(), is("2015-09-02T10:00:00 UTC - 2015-09-02T10:10:00 UTC"));
    }

    private JsonNode generateAttendanceReport(List<EventModel> events, SessionPeriod period) throws IOException {
        AttendanceReport attendanceReport = generator.generateAttendanceReport(events, period);
        String json = fixture.serialize(attendanceReport);

        System.out.println(json);

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(json);
    }

    private EventModel buildTestEvent(WindowsLogEventId eventId, String timestamp, String username) {
        return EventModel.builder().eventId(eventId)
                .timestamp(Instant.parse(timestamp).toEpochMilli())
                .userName(username)
                .build();
    }

}