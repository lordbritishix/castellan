package com.jjdevbros.castellan.reportgenerator.renderer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.jjdevbros.castellan.common.model.EventModel;
import com.jjdevbros.castellan.common.model.SessionPeriod;
import com.jjdevbros.castellan.common.model.WindowsLogEventId;
import com.jjdevbros.castellan.common.specification.DailyReportSpecification;
import com.jjdevbros.castellan.common.specification.MonthlyReportSpecification;
import com.jjdevbros.castellan.reportgenerator.generator.AttendanceReportGenerator;
import com.jjdevbros.castellan.reportgenerator.report.AttendanceReport;
import org.eclipse.birt.core.exception.BirtException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.hamcrest.core.Is.is;

public class ExcelFileRendererTest {
    private ExcelFileRenderer renderer;

    @Before
    public void setup() {
        renderer = new ExcelFileRenderer();
    }

    @Test
    public void testDaily() throws IOException, BirtException {
        List<EventModel> events = ImmutableList.of(
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-02T08:00:00.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.SCREEN_LOCK, "2015-09-02T09:00:00.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.SCREEN_UNLOCK, "2015-09-02T10:00:00.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.SCREEN_LOCK, "2015-09-02T12:00:00.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.SCREEN_UNLOCK, "2015-09-02T13:30:00.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-02T18:00:00.00Z", "Jim"),

                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-02T10:00:00.00Z", "Jeff"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-02T18:00:00.00Z", "Jeff"),

                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-02T12:00:00.00Z", "Jen"),
                buildTestEvent(WindowsLogEventId.SCREENSAVER_ACTIVE, "2015-09-02T14:00:00.00Z", "Jen"),
                buildTestEvent(WindowsLogEventId.SCREENSAVER_INACTIVE, "2015-09-02T14:30:00.00Z", "Jen"),
                buildTestEvent(WindowsLogEventId.SCREENSAVER_ACTIVE, "2015-09-02T15:00:00.00Z", "Jen"),
                buildTestEvent(WindowsLogEventId.SCREENSAVER_INACTIVE, "2015-09-02T15:30:00.00Z", "Jen"),
                buildTestEvent(WindowsLogEventId.SCREENSAVER_ACTIVE, "2015-09-02T16:00:00.00Z", "Jen"),
                buildTestEvent(WindowsLogEventId.SCREENSAVER_INACTIVE, "2015-09-02T16:30:00.00Z", "Jen"),
                buildTestEvent(WindowsLogEventId.SCREENSAVER_ACTIVE, "2015-09-02T17:00:00.00Z", "Jen"),
                buildTestEvent(WindowsLogEventId.SCREENSAVER_INACTIVE, "2015-09-02T17:30:00.00Z", "Jen"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-02T18:00:00.00Z", "Jen"),

                //Error
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-02T10:00:00.00Z", "Marcus"),

                //Error
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-02T10:00:00.00Z", "Jared"),


                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-03T10:00:00.00Z", "Jeff"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-03T18:00:00.00Z", "Jeff")
        );

        SessionPeriod period = new SessionPeriod(LocalDate.of(2015, 9, 2), LocalDate.of(2015, 9, 2));

        boolean ret = renderer.write(createAttendanceReport(events, period), Files.createTempFile("daily_", ".pdf"),
                new DailyReportSpecification());
        Assert.assertThat(ret, is(true));
    }

    @Test
    public void testEmptyDaily() throws IOException, BirtException {
        SessionPeriod period = new SessionPeriod(LocalDate.of(2015, 9, 2), LocalDate.of(2015, 9, 2));

        boolean ret =
            renderer.write(createAttendanceReport(ImmutableList.of(), period), Files.createTempFile("daily_", ".pdf"),
                            new DailyReportSpecification());
        Assert.assertThat(ret, is(false));
    }

    @Test
    public void testMonthly() throws IOException, BirtException {
        List<EventModel> events = Lists.newArrayList();

        // 9/1
        events.add(buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-02T08:00:00.00Z", "Jim"));
        events.add(buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-02T18:00:00.00Z", "Jim"));

        // 9/2
        events.add(buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-02T08:00:00.00Z", "Jim"));
        events.add(buildTestEvent(WindowsLogEventId.SCREEN_LOCK, "2015-09-02T09:00:00.00Z", "Jim"));
        events.add(buildTestEvent(WindowsLogEventId.SCREEN_UNLOCK, "2015-09-02T10:00:00.00Z", "Jim"));
        events.add(buildTestEvent(WindowsLogEventId.SCREEN_LOCK, "2015-09-02T12:00:00.00Z", "Jim"));
        events.add(buildTestEvent(WindowsLogEventId.SCREEN_UNLOCK, "2015-09-02T13:30:00.00Z", "Jim"));
        events.add(buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-02T18:00:00.00Z", "Jim"));

        events.add(buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-02T10:00:00.00Z", "Jeff"));
        events.add(buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-02T18:00:00.00Z", "Jeff"));

        events.add(buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-02T12:00:00.00Z", "Jen"));
        events.add(buildTestEvent(WindowsLogEventId.SCREENSAVER_ACTIVE, "2015-09-02T14:00:00.00Z", "Jen"));
        events.add(buildTestEvent(WindowsLogEventId.SCREENSAVER_INACTIVE, "2015-09-02T14:30:00.00Z", "Jen"));
        events.add(buildTestEvent(WindowsLogEventId.SCREENSAVER_ACTIVE, "2015-09-02T15:00:00.00Z", "Jen"));
        events.add(buildTestEvent(WindowsLogEventId.SCREENSAVER_INACTIVE, "2015-09-02T15:30:00.00Z", "Jen"));
        events.add(buildTestEvent(WindowsLogEventId.SCREENSAVER_ACTIVE, "2015-09-02T16:00:00.00Z", "Jen"));
        events.add(buildTestEvent(WindowsLogEventId.SCREENSAVER_INACTIVE, "2015-09-02T16:30:00.00Z", "Jen"));
        events.add(buildTestEvent(WindowsLogEventId.SCREENSAVER_ACTIVE, "2015-09-02T17:00:00.00Z", "Jen"));
        events.add(buildTestEvent(WindowsLogEventId.SCREENSAVER_INACTIVE, "2015-09-02T17:30:00.00Z", "Jen"));
        events.add(buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-02T18:00:00.00Z", "Jen"));

        //Error
        events.add(buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-02T10:00:00.00Z", "Marcus"));

        //Error
        events.add(buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-02T10:00:00.00Z", "Jared"));

        // 9/3
        events.add(buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-03T10:00:00.00Z", "Jeff"));
        events.add(buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-03T18:00:00.00Z", "Jeff"));

        // 9/4
        events.add(buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-04T12:00:00.00Z", "Julian"));
        events.add(buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-04T10:00:00.00Z", "Julian"));

        for (int x = 1; x < 31; ++x) {
            LocalDateTime start = LocalDateTime.of(2015, 9, x, 8, 0, 0);
            LocalDateTime end = LocalDateTime.of(2015, 9, x, 18, 0, 0);

            events.add(buildTestEvent(WindowsLogEventId.LOG_IN, start.toInstant(ZoneOffset.UTC), "James"));
            events.add(buildTestEvent(WindowsLogEventId.LOG_OUT, end.toInstant(ZoneOffset.UTC), "James"));
        }

        for (int x = 1; x < 31; ++x) {
            LocalDateTime start = LocalDateTime.of(2015, 9, x, 12, 0, 0);
            LocalDateTime end = LocalDateTime.of(2015, 9, x, 19, 0, 0);

            LocalDateTime idleStart = LocalDateTime.of(2015, 9, x, 14, 0, 0);
            LocalDateTime idleEnd = LocalDateTime.of(2015, 9, x, 14, 20, 0);

            events.add(buildTestEvent(WindowsLogEventId.LOG_IN, start.toInstant(ZoneOffset.UTC), "Johanna"));
            events.add(buildTestEvent(WindowsLogEventId.SCREENSAVER_ACTIVE, idleStart.toInstant(ZoneOffset.UTC), "Johanna"));
            events.add(buildTestEvent(WindowsLogEventId.SCREENSAVER_INACTIVE, idleEnd.toInstant(ZoneOffset.UTC), "Johanna"));
            events.add(buildTestEvent(WindowsLogEventId.LOG_OUT, end.toInstant(ZoneOffset.UTC), "Johanna"));
        }

        SessionPeriod period = new SessionPeriod(LocalDate.of(2015, 9, 1), LocalDate.of(2015, 9, 30));

        boolean ret = renderer.write(createAttendanceReport(events, period), Files.createTempFile("monthly_", ".pdf"),
                new MonthlyReportSpecification());

        Assert.assertThat(ret, is(true));
    }

    @Test
    public void testEmptyMonthly() throws IOException, BirtException {
        List<EventModel> events = Lists.newArrayList();

        // 9/1
        events.add(buildTestEvent(WindowsLogEventId.LOG_IN, "2015-10-02T08:00:00.00Z", "Jim"));
        events.add(buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-10-02T18:00:00.00Z", "Jim"));

        SessionPeriod period = new SessionPeriod(LocalDate.of(2015, 9, 1), LocalDate.of(2015, 9, 30));

        boolean ret = renderer.write(createAttendanceReport(events, period), Files.createTempFile("monthly_", ".pdf"),
                                        new MonthlyReportSpecification());

        Assert.assertThat(ret, is(false));
    }

    private AttendanceReport createAttendanceReport(List<EventModel> events, SessionPeriod period) throws IOException {
        AttendanceReportGenerator generator = new AttendanceReportGenerator();
        return generator.generateAttendanceReport(events, period);
    }

    private EventModel buildTestEvent(WindowsLogEventId eventId, String timestamp, String username) {
        return EventModel.builder().eventId(eventId)
                .timestamp(Instant.parse(timestamp).toEpochMilli())
                .userName(username)
                .build();
    }

    private EventModel buildTestEvent(WindowsLogEventId eventId, Instant instant, String username) {
        return EventModel.builder().eventId(eventId)
                .timestamp(instant.toEpochMilli())
                .userName(username)
                .build();
    }

}
