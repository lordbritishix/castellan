package com.jjdevbros.castellan.reportgenerator;

import com.google.common.collect.ImmutableList;
import com.jjdevbros.castellan.common.EventModel;
import com.jjdevbros.castellan.common.SessionPeriod;
import com.jjdevbros.castellan.common.WindowsLogEventId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class SessionizerTest {
    private Sessionizer fixture;

    @Before
    public void setup() {
        fixture = new Sessionizer();
    }

    @Test
    public void testSessionizeReturnsSessionizedEventList1() {
        EventModel event0 = EventModel.builder().eventId(WindowsLogEventId.LOG_OUT)
                            .timestamp(Instant.parse("2015-09-02T14:15:30.00Z").toEpochMilli())
                            .userName("Jim")
                            .build();


        EventModel event1 = EventModel.builder().eventId(WindowsLogEventId.LOG_IN)
                            .timestamp(Instant.parse("2015-09-03T08:00:00.00Z").toEpochMilli())
                            .userName("Jim")
                            .build();

        EventModel event2 = EventModel.builder().eventId(WindowsLogEventId.LOG_OUT)
                            .timestamp(Instant.parse("2015-09-03T16:00:00.00Z").toEpochMilli())
                            .userName("Jim")
                            .build();

        EventModel event3 = EventModel.builder().eventId(WindowsLogEventId.LOG_IN)
                            .timestamp(Instant.parse("2015-09-05T16:00:00.00Z").toEpochMilli())
                            .userName("Jim")
                            .build();

        List<EventModel> events = ImmutableList.of(event3, event2, event1, event0);
        SessionPeriod period = new SessionPeriod(LocalDate.of(2015, 9, 3), LocalDate.of(2015, 9, 4));
        Map<String, List<EventModel>> session = fixture.getSessionizedEventsForPeriod(events, period);

        Assert.assertTrue(session.containsKey("Jim"));
        Assert.assertEquals(session.get("Jim"), ImmutableList.of(event1, event2));
    }

    @Test
    public void testSessionizeReturnsSessionizedEventList2() {
        EventModel event0 = EventModel.builder().eventId(WindowsLogEventId.LOG_OUT)
                .timestamp(Instant.parse("2015-09-02T14:15:30.00Z").toEpochMilli())
                .userName("Jim")
                .build();


        EventModel event1 = EventModel.builder().eventId(WindowsLogEventId.LOG_IN)
                .timestamp(Instant.parse("2015-09-03T08:00:00.00Z").toEpochMilli())
                .userName("Jim")
                .build();

        EventModel event2 = EventModel.builder().eventId(WindowsLogEventId.SCREEN_LOCK)
                .timestamp(Instant.parse("2015-09-03T10:00:00.00Z").toEpochMilli())
                .userName("Jim")
                .build();

        EventModel event3 = EventModel.builder().eventId(WindowsLogEventId.SCREEN_UNLOCK)
                .timestamp(Instant.parse("2015-09-03T11:00:00.00Z").toEpochMilli())
                .userName("Jim")
                .build();

        EventModel event4 = EventModel.builder().eventId(WindowsLogEventId.USER_INACTIVE)
                .timestamp(Instant.parse("2015-09-03T12:00:00.00Z").toEpochMilli())
                .userName("Jim")
                .build();

        EventModel event5 = EventModel.builder().eventId(WindowsLogEventId.USER_ACTIVE)
                .timestamp(Instant.parse("2015-09-03T13:00:00.00Z").toEpochMilli())
                .userName("Jim")
                .build();

        EventModel event6 = EventModel.builder().eventId(WindowsLogEventId.LOG_OUT)
                .timestamp(Instant.parse("2015-09-03T18:00:00.00Z").toEpochMilli())
                .userName("Jim")
                .build();

        EventModel event7 = EventModel.builder().eventId(WindowsLogEventId.LOG_IN)
                .timestamp(Instant.parse("2015-09-05T16:00:00.00Z").toEpochMilli())
                .userName("Jim")
                .build();


        List<EventModel> events = ImmutableList.of(event3, event2, event4, event5, event7, event1, event6, event0);
        SessionPeriod period = new SessionPeriod(LocalDate.of(2015, 9, 3), LocalDate.of(2015, 9, 4));
        Map<String, List<EventModel>> session = fixture.getSessionizedEventsForPeriod(events, period);

        Assert.assertTrue(session.containsKey("Jim"));
        Assert.assertEquals(session.get("Jim"), ImmutableList.of(event1, event2, event3, event4, event5, event6));
    }

    @Test
    public void testSessionizeReturnsSessionizedEventListForEventsOutsideSession() {
        EventModel event0 = EventModel.builder().eventId(WindowsLogEventId.LOG_OUT)
                .timestamp(Instant.parse("2015-09-02T14:15:30.00Z").toEpochMilli())
                .userName("Jim")
                .build();


        EventModel event1 = EventModel.builder().eventId(WindowsLogEventId.LOG_IN)
                .timestamp(Instant.parse("2015-09-06T08:00:00.00Z").toEpochMilli())
                .userName("Jim")
                .build();

        EventModel event2 = EventModel.builder().eventId(WindowsLogEventId.LOG_OUT)
                .timestamp(Instant.parse("2015-09-06T16:00:00.00Z").toEpochMilli())
                .userName("Jim")
                .build();

        List<EventModel> events = ImmutableList.of(event2, event1, event0);
        SessionPeriod period = new SessionPeriod(LocalDate.of(2015, 9, 3), LocalDate.of(2015, 9, 4));
        Map<String, List<EventModel>> session = fixture.getSessionizedEventsForPeriod(events, period);

        Assert.assertFalse(session.containsKey("Jim"));
    }

    @Test
    public void testSessionizeReturnsSessionizedEventListForDifferentUsers() {
        EventModel event1 = EventModel.builder().eventId(WindowsLogEventId.LOG_IN)
                .timestamp(Instant.parse("2015-09-03T08:00:00.00Z").toEpochMilli())
                .userName("Jim")
                .build();

        EventModel event2 = EventModel.builder().eventId(WindowsLogEventId.LOG_OUT)
                .timestamp(Instant.parse("2015-09-03T16:00:00.00Z").toEpochMilli())
                .userName("Jim")
                .build();

        EventModel event3 = EventModel.builder().eventId(WindowsLogEventId.LOG_IN)
                .timestamp(Instant.parse("2015-09-03T20:00:00.00Z").toEpochMilli())
                .userName("Jim")
                .build();

        EventModel event4 = EventModel.builder().eventId(WindowsLogEventId.LOG_IN)
                .timestamp(Instant.parse("2015-09-03T08:00:00.00Z").toEpochMilli())
                .userName("Jeff")
                .build();

        EventModel event5 = EventModel.builder().eventId(WindowsLogEventId.LOG_OUT)
                .timestamp(Instant.parse("2015-09-03T16:00:00.00Z").toEpochMilli())
                .userName("Jeff")
                .build();

        EventModel event6 = EventModel.builder().eventId(WindowsLogEventId.LOG_IN)
                .timestamp(Instant.parse("2015-09-03T20:00:00.00Z").toEpochMilli())
                .userName("Jeff")
                .build();

        EventModel event7 = EventModel.builder().eventId(WindowsLogEventId.LOG_IN)
                .timestamp(Instant.parse("2015-09-01T08:00:00.00Z").toEpochMilli())
                .userName("Jen")
                .build();

        EventModel event8 = EventModel.builder().eventId(WindowsLogEventId.LOG_OUT)
                .timestamp(Instant.parse("2015-09-01T16:00:00.00Z").toEpochMilli())
                .userName("Jen")
                .build();

        EventModel event9 = EventModel.builder().eventId(WindowsLogEventId.LOG_IN)
                .timestamp(Instant.parse("2015-09-01T16:00:00.00Z").toEpochMilli())
                .userName("Jen")
                .build();


        List<EventModel> events =
                ImmutableList.of(event3, event2, event1, event6, event5, event4, event9, event8, event7);

        SessionPeriod period = new SessionPeriod(LocalDate.of(2015, 9, 3), LocalDate.of(2015, 9, 4));
        Map<String, List<EventModel>> session = fixture.getSessionizedEventsForPeriod(events, period);

        Assert.assertTrue(session.containsKey("Jim"));
        Assert.assertEquals(session.get("Jim"), ImmutableList.of(event1, event2, event3));

        Assert.assertTrue(session.containsKey("Jeff"));
        Assert.assertEquals(session.get("Jeff"), ImmutableList.of(event4, event5, event6));

        Assert.assertFalse(session.containsKey("Jen"));
    }

}
