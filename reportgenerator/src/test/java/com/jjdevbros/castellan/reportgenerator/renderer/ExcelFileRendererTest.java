package com.jjdevbros.castellan.reportgenerator.renderer;

import com.google.common.collect.ImmutableList;
import com.jjdevbros.castellan.common.EventModel;
import com.jjdevbros.castellan.common.SessionPeriod;
import com.jjdevbros.castellan.common.WindowsLogEventId;
import com.jjdevbros.castellan.reportgenerator.generator.AttendanceReportGenerator;
import com.jjdevbros.castellan.reportgenerator.report.AttendanceReport;
import org.eclipse.birt.core.exception.BirtException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public class ExcelFileRendererTest {
    private ExcelFileRenderer renderer;

    @Before
    public void setup() {
        renderer = new ExcelFileRenderer();
    }

    @Test
    @Ignore
    public void test() throws IOException, BirtException {
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

        Path dir = Paths.get("/home/lordbritishix/tmp");
        renderer.write(createAttendanceReport(events), Files.createTempFile(dir, "report", ".pdf"));
    }

    @Test
    @Ignore
    public void testMonthly() throws IOException, BirtException {
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

        Path dir = Paths.get("/home/lordbritishix/tmp");
        renderer.write(createAttendanceReport(events), Files.createTempFile(dir, "report", ".pdf"));
    }

    private AttendanceReport createAttendanceReport(List<EventModel> events) throws IOException {
        AttendanceReportGenerator generator = new AttendanceReportGenerator();
        SessionPeriod period = new SessionPeriod(LocalDate.of(2015, 9, 1), LocalDate.of(2015, 9, 30));

        return generator.generateAttendanceReport(events, period);
    }

    private EventModel buildTestEvent(WindowsLogEventId eventId, String timestamp, String username) {
        return EventModel.builder().eventId(eventId)
                .timestamp(Instant.parse(timestamp).toEpochMilli())
                .userName(username)
                .build();
    }

}
