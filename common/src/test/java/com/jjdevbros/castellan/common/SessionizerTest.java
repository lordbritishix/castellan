package com.jjdevbros.castellan.common;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.*;
import org.junit.runner.*;
import org.mockito.runners.*;
import com.google.common.collect.ImmutableList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class SessionizerTest {
    private Sessionizer fixture;

    @Before
    public void setup() {
        fixture = new Sessionizer();
    }

    @Test
    @Ignore
    public void testSessionizeReturnsSessionizedEventList() {
        LocalDate date = LocalDate.of(2015, 9, 3);

        //Reference date: 9/3/2015 8:00:00

        EventModel event0 = EventModel.builder().eventId(WindowsLogEventId.LOG_OUT)
                            .eventName("Logout")
                            .timestamp(Instant.parse("2015-09-02T14:15:30.00Z").toEpochMilli())
                            .userName("Jim")
                            .build();


        EventModel event1 = EventModel.builder().eventId(WindowsLogEventId.LOG_IN)
                            .eventName("Login")
                            .timestamp(Instant.parse("2015-09-03T08:00:00.00Z").toEpochMilli())
                            .userName("Jim")
                            .build();

        EventModel event2 = EventModel.builder().eventId(WindowsLogEventId.LOG_OUT)
                .eventName("Logout")
                .timestamp(Instant.parse("2015-09-03T16:00:00.00Z").toEpochMilli())
                .userName("Jim")
                .build();

        List<EventModel> events = ImmutableList.of(event0, event1, event2);
        Map<String, List<EventModel>> session = fixture.getDailySessionizedEvents(events, date);

        assertTrue(session.containsKey("Jim"));

        List<EventModel> sessionizedEvents = session.get("Jim");
        List<EventModel> expectedEvents = ImmutableList.of(event1, event2);
        assertThat(sessionizedEvents, containsInAnyOrder(expectedEvents));
    }
}
