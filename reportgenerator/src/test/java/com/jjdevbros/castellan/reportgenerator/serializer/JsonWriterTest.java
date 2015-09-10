package com.jjdevbros.castellan.reportgenerator.serializer;

import com.google.common.collect.ImmutableList;
import com.jjdevbros.castellan.common.Constants;
import com.jjdevbros.castellan.common.EventModel;
import com.jjdevbros.castellan.common.SessionPeriod;
import com.jjdevbros.castellan.common.WindowsLogEventId;
import com.jjdevbros.castellan.reportgenerator.generator.AttendanceReportGenerator;
import com.jjdevbros.castellan.reportgenerator.report.AttendanceReport;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

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

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(json);
        assertThat(jsonNode.get("reportId").asText(), is(attendanceReport.getReportId().toString()));
        assertThat(jsonNode.get("sessionStart").asText(), is("2015-09-02T00:00:00 UTC"));
        assertThat(jsonNode.get("sessionEnd").asText(), is("2015-09-04T00:00:00 UTC"));

        assertThat(jsonNode.get("userReports").size(), is(2));
        assertThat(jsonNode.get("userReports").get(0).get("2015-09-02T00:00:00 UTC"), notNullValue());
        assertThat(jsonNode.get("userReports").get(1).get("2015-09-03T00:00:00 UTC"), notNullValue());
    }

    private EventModel buildTestEvent(WindowsLogEventId eventId, String timestamp, String username) {
        return EventModel.builder().eventId(eventId)
                .timestamp(Instant.parse(timestamp).toEpochMilli())
                .userName(username)
                .build();
    }
}
