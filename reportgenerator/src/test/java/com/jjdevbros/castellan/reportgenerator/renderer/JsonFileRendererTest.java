package com.jjdevbros.castellan.reportgenerator.renderer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.jjdevbros.castellan.common.database.JsonGroupLookup;
import com.jjdevbros.castellan.common.model.EventModel;
import com.jjdevbros.castellan.common.model.SessionPeriod;
import com.jjdevbros.castellan.common.model.WindowsLogEventId;
import com.jjdevbros.castellan.reportgenerator.generator.AttendanceReportGenerator;
import com.jjdevbros.castellan.reportgenerator.generator.UserReportGenerator;
import com.jjdevbros.castellan.reportgenerator.report.AttendanceReport;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class JsonFileRendererTest {
    private JsonFileRenderer fixture;
    private AttendanceReportGenerator generator =
            new AttendanceReportGenerator(new UserReportGenerator(new JsonGroupLookup(null), 0), ImmutableSet.of());

    @Before
    public void setup() {
        fixture = new JsonFileRenderer();
    }

    @Test
    public void testRenderProducesJson() throws IOException {
        List<EventModel> events = ImmutableList.of(
                buildTestEvent(WindowsLogEventId.LOG_IN, "2015-09-02T08:00:00.00Z", "Jim"),
                buildTestEvent(WindowsLogEventId.LOG_OUT, "2015-09-02T18:00:00.00Z", "Jim")
        );

        SessionPeriod period = new SessionPeriod(LocalDate.of(2015, 9, 2), LocalDate.of(2015, 9, 2));
        AttendanceReport attendanceReport = generator.generateAttendanceReport(events, period);

        Path temp = Files.createTempFile("test_", null);
        fixture.write(attendanceReport, temp);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(temp.toFile());

        assertThat(node.get("reportId").asText(), is(attendanceReport.getReportId().toString()));
    }

    private EventModel buildTestEvent(WindowsLogEventId eventId, String timestamp, String username) {
        return EventModel.builder().eventId(eventId)
                .timestamp(Instant.parse(timestamp).toEpochMilli())
                .userName(username)
                .build();
    }

}
