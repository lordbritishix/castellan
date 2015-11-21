package com.jjdevbros.castellan.reportgenerator.generator;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.jjdevbros.castellan.common.database.JsonGroupLookup;
import com.jjdevbros.castellan.common.model.EventModel;
import com.jjdevbros.castellan.common.model.SessionPeriod;
import com.jjdevbros.castellan.common.model.WindowsLogEventId;
import com.jjdevbros.castellan.reportgenerator.report.AttendanceReport;
import com.jjdevbros.castellan.reportgenerator.report.UserReport;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Created by lordbritishix on 06/09/15.
 */
public class AttendanceReportGeneratorTest {
    private AttendanceReportGenerator fixture;

    @Before
    public void setup() {
        fixture = new AttendanceReportGenerator(new UserReportGenerator(new JsonGroupLookup(null), 10), ImmutableSet.of("ExcludeMe"));
    }

    @Test
    public void testInactivityThresholdExcludesExcludedEventFromComputation() {
        List<EventModel> events = ImmutableList.of(
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-13T17:00:00.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-13T18:00:00.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-13T18:00:10.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-13T18:00:25.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-13T18:00:30.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-13T19:00:00.00Z", "Jim")
        );

        SessionPeriod period = new SessionPeriod(LocalDate.of(2015, 9, 13), LocalDate.of(2015, 9, 13));

        AttendanceReport attendanceReport = fixture.generateAttendanceReport(events, period);
        List<UserReport> userReports =
                getUserReportsForSingleDay(attendanceReport.getUserReports(), LocalDate.of(2015, 9, 13));

        assertThat(userReports.size(), is(1));
        assertThat(userReports.get(0).getInactivityDuration(), is(Duration.ofHours(0L)));
        assertThat(userReports.get(0).getWorkDuration(),
                is(Duration.between(Instant.parse("2015-09-13T17:00:00.00Z"),
                                    Instant.parse("2015-09-13T19:00:00.00Z"))));
        assertThat(userReports.get(0).getActivityDuration(),
                is(Duration.between(Instant.parse("2015-09-13T17:00:00.00Z"),
                        Instant.parse("2015-09-13T19:00:00.00Z"))));
    }

    @Test
    public void testExcludeUserExcludesUserIfOnList1() {
        List<EventModel> events = ImmutableList.of(
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-13T12:11:00.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-13T18:02:00.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-13T18:08:00.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-13T12:02:00.00Z", "ExcludeMe"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-13T18:02:00.00Z", "ExcludeMe")
                );

        SessionPeriod period = new SessionPeriod(LocalDate.of(2015, 9, 13), LocalDate.of(2015, 9, 13));

        AttendanceReport attendanceReport = fixture.generateAttendanceReport(events, period);
        List<UserReport> userReports =
                getUserReportsForSingleDay(attendanceReport.getUserReports(), LocalDate.of(2015, 9, 13));

        assertThat(userReports.size(), is(1));
        assertThat(userReports.get(0).getUserName(), is("Jim"));
        assertThat(userReports.get(0).getInactivityDuration(), is(Duration.ofHours(0L)));
        assertThat(userReports.get(0).getWorkDuration(),
                is(Duration.between(Instant.parse("2015-09-13T18:02:00.00Z"), Instant.parse("2015-09-13T18:08:00.00Z"))));
    }

    @Test
    public void testExcludeUserExcludesUserIfOnList2() {
        List<EventModel> events = ImmutableList.of(
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-13T12:02:00.00Z", "ExcludeMe"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-13T18:02:00.00Z", "ExcludeMe")
        );

        SessionPeriod period = new SessionPeriod(LocalDate.of(2015, 9, 13), LocalDate.of(2015, 9, 13));

        AttendanceReport attendanceReport = fixture.generateAttendanceReport(events, period);
        List<UserReport> userReports =
                getUserReportsForSingleDay(attendanceReport.getUserReports(), LocalDate.of(2015, 9, 13));

        assertTrue(userReports == null);
    }

    @Test
    public void testInactiveActiveInactive() {
        List<EventModel> events = ImmutableList.of(
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-13T12:11:00.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-13T18:02:00.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-13T18:08:00.00Z", "Jim")
        );

        SessionPeriod period = new SessionPeriod(LocalDate.of(2015, 9, 13), LocalDate.of(2015, 9, 13));

        AttendanceReport attendanceReport = fixture.generateAttendanceReport(events, period);
        List<UserReport> userReports =
                getUserReportsForSingleDay(attendanceReport.getUserReports(), LocalDate.of(2015, 9, 13));

        assertThat(userReports.get(0).getInactivityDuration(), is(Duration.ofHours(0L)));
        assertThat(userReports.get(0).getWorkDuration(),
            is(Duration.between(Instant.parse("2015-09-13T18:02:00.00Z"), Instant.parse("2015-09-13T18:08:00.00Z"))));
    }

    @Test
    public void generateAttendanceReportForOneMonth() {
        List<EventModel> events = ImmutableList.of(
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-02T08:15:30.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-02T18:15:30.00Z", "Jim")
        );

        SessionPeriod period = new SessionPeriod(LocalDate.of(2015, 9, 1), LocalDate.of(2015, 9, 30));

        AttendanceReport attendanceReport = fixture.generateAttendanceReport(events, period);

        assertThat(attendanceReport.getPeriod(), is(period));
        assertThat(attendanceReport.getReportedGeneratedAt(), notNullValue());
        assertThat(attendanceReport.getUserReports().size(), is(1));
    }


    @Test
    public void generateAttendanceReportReturnsCorrectReportForMultipleLogin() {
        List<EventModel> events = ImmutableList.of(
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-02T08:15:00.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-02T09:15:00.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-02T18:15:00.00Z", "Jim")
        );

        SessionPeriod period = new SessionPeriod(LocalDate.of(2015, 9, 2), LocalDate.of(2015, 9, 2));

        AttendanceReport attendanceReport = fixture.generateAttendanceReport(events, period);

        assertThat(attendanceReport.getUserReports().size(), is(1));

        List<UserReport> userReports =
                getUserReportsForSingleDay(attendanceReport.getUserReports(), LocalDate.of(2015, 9, 2));

        assertThat(userReports.get(0).getStartTime(), is(Instant.parse("2015-09-02T08:15:00.00Z")));
        assertThat(userReports.get(0).getActivityDuration(), is(Duration.ofHours(10L)));
    }

    @Test
    public void generateAttendanceReportReturnsCorrectReportForMultipleLogout() {
        List<EventModel> events = ImmutableList.of(
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-02T08:15:00.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-02T18:15:00.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-02T20:15:00.00Z", "Jim")
        );

        SessionPeriod period = new SessionPeriod(LocalDate.of(2015, 9, 2), LocalDate.of(2015, 9, 2));

        AttendanceReport attendanceReport = fixture.generateAttendanceReport(events, period);

        assertThat(attendanceReport.getUserReports().size(), is(1));

        List<UserReport> userReports =
                getUserReportsForSingleDay(attendanceReport.getUserReports(), LocalDate.of(2015, 9, 2));

        assertThat(userReports.get(0).getEndTime(), is(Instant.parse("2015-09-02T20:15:00.00Z")));
        assertThat(userReports.get(0).getActivityDuration(), is(Duration.ofHours(12L)));
    }


    @Test
    public void generateAttendanceReportReturnsCorrectReportForEventsWithNoStartEvent() {
        List<EventModel> events = ImmutableList.of(
                buildTestEvent(WindowsLogEventId.SCREEN_LOCK, "2015-09-02T08:15:40.00Z", "Jim")
        );

        SessionPeriod period = new SessionPeriod(LocalDate.of(2015, 9, 2), LocalDate.of(2015, 9, 2));

        AttendanceReport attendanceReport = fixture.generateAttendanceReport(events, period);

        assertThat(attendanceReport.getUserReports().size(), is(1));

        List<UserReport> userReports =
                getUserReportsForSingleDay(attendanceReport.getUserReports(), LocalDate.of(2015, 9, 2));
        assertThat(userReports.get(0).isHasErrors(), is(true));
    }

    @Test
    public void generateAttendanceReportReturnsCorrectReportForEventsWithEndEventNotLast() {
        List<EventModel> events = ImmutableList.of(
                buildTestEvent(WindowsLogEventId.SCREEN_UNLOCK, "2015-09-02T08:15:40.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.SCREEN_LOCK, "2015-09-02T10:15:40.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.SCREEN_UNLOCK, "2015-09-02T12:15:40.00Z", "Jim")
        );

        SessionPeriod period = new SessionPeriod(LocalDate.of(2015, 9, 2), LocalDate.of(2015, 9, 2));

        AttendanceReport attendanceReport = fixture.generateAttendanceReport(events, period);

        assertThat(attendanceReport.getUserReports().size(), is(1));

        List<UserReport> userReports =
                getUserReportsForSingleDay(attendanceReport.getUserReports(), LocalDate.of(2015, 9, 2));
        assertThat(userReports.get(0).isHasErrors(), is(false));
        assertThat(userReports.get(0).getWorkDuration(), is(Duration.ofHours(2)));
        assertThat(userReports.get(0).getActivityDuration(), is(Duration.ofHours(2)));
        assertThat(userReports.get(0).getInactivityDuration(), is(Duration.ofHours(0)));
    }

    @Test
    @Ignore
    public void generateAttendanceReportReturnsCorrectReportForInvalidEvents2() {
        List<EventModel> events = ImmutableList.of(
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-02T08:15:30.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.SCREEN_LOCK, "2015-09-02T09:15:30.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.SCREENSAVER_INACTIVE, "2015-09-02T10:15:30.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-02T18:15:40.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-03T08:15:30.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-03T08:15:35.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.SCREENSAVER_INACTIVE, "2015-09-03T18:15:40.00Z", "Jim"));

        SessionPeriod period = new SessionPeriod(LocalDate.of(2015, 9, 2), LocalDate.of(2015, 9, 3));

        AttendanceReport attendanceReport = fixture.generateAttendanceReport(events, period);

        assertThat(attendanceReport.getUserReports().size(), is(2));

        List<UserReport> userReportDay1 =
                getUserReportsForSingleDay(attendanceReport.getUserReports(), LocalDate.of(2015, 9, 2));
        assertThat(userReportDay1.get(0).isHasErrors(), is(true));

        List<UserReport> userReportDay2 =
                getUserReportsForSingleDay(attendanceReport.getUserReports(), LocalDate.of(2015, 9, 3));
        assertThat(userReportDay2.get(0).isHasErrors(), is(true));
    }


    @Test
    public void generateAttendanceReportReturnsCorrectReportForHappyCase1() {
        List<EventModel> events = ImmutableList.of(
            buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-02T08:15:30.00Z", "Jim"),
            buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-02T18:15:30.00Z", "Jim")
        );

        SessionPeriod period = new SessionPeriod(LocalDate.of(2015, 9, 2), LocalDate.of(2015, 9, 2));

        AttendanceReport attendanceReport = fixture.generateAttendanceReport(events, period);

        assertThat(attendanceReport.getPeriod(), is(period));
        assertThat(attendanceReport.getReportedGeneratedAt(), notNullValue());
        assertThat(attendanceReport.getUserReports().size(), is(1));

        UserReport userReport = attendanceReport.getUserReports().get(period).get(0);
        assertThat(userReport.getUserName(), is("Jim"));
    }

    @Test
    public void generateAttendanceReportReturnsCorrectReportForHappyCase2() {
        List<EventModel> events = ImmutableList.of(
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-02T08:15:30.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-02T18:15:30.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-02T08:15:30.00Z", "Jeff"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-02T18:15:30.00Z", "Jeff"),
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-02T08:15:30.00Z", "Jen"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-02T18:15:30.00Z", "Jen")
        );

        SessionPeriod period = new SessionPeriod(LocalDate.of(2015, 9, 2), LocalDate.of(2015, 9, 2));

        AttendanceReport attendanceReport = fixture.generateAttendanceReport(events, period);

        assertThat(attendanceReport.getPeriod(), is(period));
        assertThat(attendanceReport.getReportedGeneratedAt(), notNullValue());
        assertThat(attendanceReport.getUserReports().size(), is(1));
        assertThat(attendanceReport.getUserReports().get(period).size(), is(3));

        assertThat(getReport(attendanceReport.getUserReports().get(period), "Jim").getUserName(), is("Jim"));
        assertThat(getReport(attendanceReport.getUserReports().get(period), "Jeff").getUserName(), is("Jeff"));
        assertThat(getReport(attendanceReport.getUserReports().get(period), "Jen").getUserName(), is("Jen"));
    }

    @Test
    public void generateAttendanceReportReturnsCorrectReportForMultipleDaysOnePerson() {
        List<EventModel> events = ImmutableList.of(
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-02T08:15:30.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-02T18:15:30.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-03T08:15:30.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-03T18:15:30.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-04T08:15:30.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-04T18:15:30.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-05T08:15:30.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-05T18:15:30.00Z", "Jim")

        );

        SessionPeriod period = new SessionPeriod(LocalDate.of(2015, 9, 2), LocalDate.of(2015, 9, 5));

        AttendanceReport attendanceReport = fixture.generateAttendanceReport(events, period);

        assertThat(attendanceReport.getPeriod(), is(period));
        assertThat(attendanceReport.getReportedGeneratedAt(), notNullValue());
        assertThat(attendanceReport.getUserReports().size(), is(4));

        assertThat(
            getUserReportsForSingleDay(attendanceReport.getUserReports(), LocalDate.of(2015, 9, 2)).get(0).getStartTime(),
            is(Instant.parse("2015-09-02T08:15:30.00Z")));

        assertThat(
                getUserReportsForSingleDay(attendanceReport.getUserReports(), LocalDate.of(2015, 9, 3)).get(0).getStartTime(),
                is(Instant.parse("2015-09-03T08:15:30.00Z")));

        assertThat(
                getUserReportsForSingleDay(attendanceReport.getUserReports(), LocalDate.of(2015, 9, 4)).get(0).getStartTime(),
                is(Instant.parse("2015-09-04T08:15:30.00Z")));

        assertThat(
                getUserReportsForSingleDay(attendanceReport.getUserReports(), LocalDate.of(2015, 9, 5)).get(0).getStartTime(),
                is(Instant.parse("2015-09-05T08:15:30.00Z")));
    }

    @Test
    public void generateAttendanceReportReturnsCorrectReportForMultipleDaysMultiplePerson() {
        List<EventModel> events = ImmutableList.of(
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-02T08:15:30.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-02T10:15:30.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-02T12:15:30.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-02T14:15:30.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-02T08:15:30.00Z", "Jeff"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-02T18:15:30.00Z", "Jeff"),

                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-03T08:15:30.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-03T18:15:30.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-03T08:15:30.00Z", "Jen"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-03T18:15:30.00Z", "Jen"),
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-03T20:15:30.00Z", "Jen"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-03T22:15:30.00Z", "Jen"),


                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-04T08:15:30.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-04T18:15:30.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-04T08:15:30.00Z", "Jacob"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-04T18:15:30.00Z", "Jacob"),

                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-05T08:15:30.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-05T18:15:30.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-05T08:15:30.00Z", "Jared"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-05T18:15:30.00Z", "Jared"),
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-05T22:15:30.00Z", "Jared"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-05T23:15:30.00Z", "Jared")
                );

        SessionPeriod period = new SessionPeriod(LocalDate.of(2015, 9, 2), LocalDate.of(2015, 9, 5));

        AttendanceReport attendanceReport = fixture.generateAttendanceReport(events, period);

        assertThat(attendanceReport.getPeriod(), is(period));
        assertThat(attendanceReport.getReportedGeneratedAt(), notNullValue());
        assertThat(attendanceReport.getUserReports().size(), is(4));

        assertThat(getUserReportsForSingleDay(attendanceReport.getUserReports(), LocalDate.of(2015, 9, 2)).size(), is(2));
        assertThat(getUserReportsForSingleDay(attendanceReport.getUserReports(), LocalDate.of(2015, 9, 3)).size(), is(2));
        assertThat(getUserReportsForSingleDay(attendanceReport.getUserReports(), LocalDate.of(2015, 9, 4)).size(), is(2));
        assertThat(getUserReportsForSingleDay(attendanceReport.getUserReports(), LocalDate.of(2015, 9, 5)).size(), is(2));
    }

    private UserReport getReport(List<UserReport> reports, String name) {
        return reports.stream().filter(r -> r.getUserName().equals(name)).findFirst().get();
    }

    private List<UserReport> getUserReportsForSingleDay(Map<SessionPeriod, List<UserReport>> reports,
                                                        LocalDate date) {
        return reports.get(new SessionPeriod(date, date));
    }


    private EventModel buildTestEvent(WindowsLogEventId eventId, String timestamp, String username) {
        return EventModel.builder().eventId(eventId)
                .timestamp(Instant.parse(timestamp).toEpochMilli())
                .userName(username)
                .build();
    }

}
