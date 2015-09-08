package com.jjdevbros.castellan.reportgenerator.session;

import com.google.common.collect.ImmutableList;
import com.jjdevbros.castellan.common.EventModel;
import com.jjdevbros.castellan.common.NormalizedEventId;
import com.jjdevbros.castellan.common.NormalizedEventModel;
import com.jjdevbros.castellan.common.NormalizedSession;
import com.jjdevbros.castellan.common.SessionPeriod;
import com.jjdevbros.castellan.common.WindowsLogEventId;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class SessionizerTest {
    private Sessionizer fixture;

    @Before
    public void setup() {
        fixture = new Sessionizer();
    }

    private EventModel createEvent(WindowsLogEventId eventId, String timestamp, String username) {
        return EventModel.builder().eventId(eventId)
                .timestamp(Instant.parse(timestamp).toEpochMilli())
                .userName(username)
                .build();
    }

    @Test
    public void testSessionizeReturnsSessionizedEventListForUnrecognizedEvents() {
        EventModel event0 = EventModel.builder().eventId(WindowsLogEventId.LOG_OUT)
                .timestamp(Instant.parse("2015-09-02T14:15:30.00Z").toEpochMilli())
                .userName("Jim")
                .build();

        EventModel event1 = EventModel.builder().eventId(WindowsLogEventId.NETWORK_CONNECTED)
                .timestamp(Instant.parse("2015-09-03T08:00:00.00Z").toEpochMilli())
                .userName("Jim")
                .build();

        EventModel event2 = EventModel.builder().eventId(WindowsLogEventId.NETWORK_DISCONNECTED)
                .timestamp(Instant.parse("2015-09-03T16:00:00.00Z").toEpochMilli())
                .userName("Jim")
                .build();

        EventModel event3 = EventModel.builder().eventId(WindowsLogEventId.LOG_IN)
                .timestamp(Instant.parse("2015-09-05T16:00:00.00Z").toEpochMilli())
                .userName("Jim")
                .build();

        List<EventModel> events = ImmutableList.of(event3, event2, event1, event0);
        SessionPeriod period = new SessionPeriod(LocalDate.of(2015, 9, 1), LocalDate.of(2015, 9, 6));
        List<NormalizedSession> session =
                fixture.getSessionizedEventsForPeriodWithoutSpillOver(events, period);

        assertEquals(session.get(0).getEvents().stream().map(e -> e.getEventModel()).collect(Collectors.toList()),
                ImmutableList.of(event0, event3));
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
        List<NormalizedSession> session =
                fixture.getSessionizedEventsForPeriodWithoutSpillOver(events, period);

        assertEquals(session.get(0).getEvents().stream().map(e -> e.getEventModel()).collect(Collectors.toList()),
                ImmutableList.of(event1, event2));
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

        EventModel event4 = EventModel.builder().eventId(WindowsLogEventId.SCREENSAVER_ACTIVE)
                .timestamp(Instant.parse("2015-09-03T12:00:00.00Z").toEpochMilli())
                .userName("Jim")
                .build();

        EventModel event5 = EventModel.builder().eventId(WindowsLogEventId.SCREENSAVER_INACTIVE)
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
        List<NormalizedSession> session =
                fixture.getSessionizedEventsForPeriodWithoutSpillOver(events, period);

        assertEquals(session.get(0).getEvents().stream().map(e -> e.getEventModel()).collect(Collectors.toList()),
                ImmutableList.of(event1, event2, event3, event4, event5, event6));
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
        List<NormalizedSession> session =
                fixture.getSessionizedEventsForPeriodWithoutSpillOver(events, period);

        assertThat(session.size(), is(0));
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
        List<NormalizedSession> session =
                fixture.getSessionizedEventsForPeriodWithoutSpillOver(events, period);

        NormalizedSession jim = session.stream().filter(e -> e.getUserName().equals("Jim")).findFirst().get();
        assertEquals(jim.getEvents().stream().map(e -> e.getEventModel()).collect(Collectors.toList()),
                        ImmutableList.of(event1, event2, event3));

        NormalizedSession jeff = session.stream().filter(e -> e.getUserName().equals("Jeff")).findFirst().get();
        assertEquals(jeff.getEvents().stream().map(e -> e.getEventModel()).collect(Collectors.toList()),
                        ImmutableList.of(event4, event5, event6));

        assertFalse(session.stream().filter(e -> e.getUserName().equals("Jen")).findFirst().isPresent());
    }

    @Test
    public void testSessionizeDailyReturnsDailySessionizedEventList1() {
        EventModel event0 = EventModel.builder().eventId(WindowsLogEventId.LOG_OUT)
                .timestamp(Instant.parse("2015-09-02T14:15:30.00Z").toEpochMilli())
                .userName("Jim")
                .build();


        EventModel event1 = EventModel.builder().eventId(WindowsLogEventId.LOG_IN)
                .timestamp(Instant.parse("2015-09-03T16:00:00.00Z").toEpochMilli())
                .userName("Jim")
                .build();

        EventModel event2 = EventModel.builder().eventId(WindowsLogEventId.LOG_IN)
                .timestamp(Instant.parse("2015-09-04T16:00:00.00Z").toEpochMilli())
                .userName("Jim")
                .build();

        EventModel event3 = EventModel.builder().eventId(WindowsLogEventId.LOG_IN)
                .timestamp(Instant.parse("2015-09-05T16:00:00.00Z").toEpochMilli())
                .userName("Jim")
                .build();

        EventModel event4 = EventModel.builder().eventId(WindowsLogEventId.LOG_IN)
                .timestamp(Instant.parse("2015-09-06T16:00:00.00Z").toEpochMilli())
                .userName("Jim")
                .build();

        EventModel event5 = EventModel.builder().eventId(WindowsLogEventId.LOG_IN)
                .timestamp(Instant.parse("2015-09-06T16:00:00.00Z").toEpochMilli())
                .userName("John")
                .build();

        EventModel event6 = EventModel.builder().eventId(WindowsLogEventId.LOG_OUT)
                .timestamp(Instant.parse("2015-09-06T19:00:00.00Z").toEpochMilli())
                .userName("John")
                .build();


        List<EventModel> events = ImmutableList.of(event3, event2, event1, event0, event6, event5, event4);
        SessionPeriod period = new SessionPeriod(LocalDate.of(2015, 9, 3), LocalDate.of(2015, 9, 8));

        List<Pair<SessionPeriod, List<NormalizedSession>>> session = fixture.sessionizeAndNormalize(events, period);

        assertThat(session.size(), is(6));
        assertTrue(session.get(0).getKey().equals(new SessionPeriod(LocalDate.of(2015, 9, 3), LocalDate.of(2015, 9, 3))));
        assertTrue(session.get(0).getValue().stream().filter(e -> e.getUserName().equals("Jim")).findFirst().get().getEvents().get(0).getEventModel().equals(event1));
        assertTrue(session.get(1).getKey().equals(new SessionPeriod(LocalDate.of(2015, 9, 4), LocalDate.of(2015, 9, 4))));
        assertTrue(session.get(1).getValue().stream().filter(e -> e.getUserName().equals("Jim")).findFirst().get().getEvents().get(0).getEventModel().equals(event2));
        assertTrue(session.get(2).getKey().equals(new SessionPeriod(LocalDate.of(2015, 9, 5), LocalDate.of(2015, 9, 5))));
        assertTrue(session.get(2).getValue().stream().filter(e -> e.getUserName().equals("Jim")).findFirst().get().getEvents().get(0).getEventModel().equals(event3));
        assertTrue(session.get(3).getKey().equals(new SessionPeriod(LocalDate.of(2015, 9, 6), LocalDate.of(2015, 9, 6))));
        assertTrue(session.get(3).getValue().stream().filter(e -> e.getUserName().equals("Jim")).findFirst().get().getEvents().get(0).getEventModel().equals(event4));
        assertTrue(session.get(3).getValue().stream().filter(e -> e.getUserName().equals("John")).findFirst().get().getEvents().get(0).getEventModel().equals(event5));
        assertTrue(session.get(3).getValue().stream().filter(e -> e.getUserName().equals("John")).findFirst().get().getEvents().get(1).getEventModel().equals(event6));
    }

    @Test
    public void testNormalizeNormalizesEventCorrectly() {
        NormalizedEventModel event = fixture.normalizeEvent(EventModel.builder().eventId(WindowsLogEventId.LOG_OUT)
                .timestamp(Instant.parse("2015-09-02T14:15:30.00Z").toEpochMilli())
                .userName("Jim")
                .build());

        assertThat(event.getEventId(), is(NormalizedEventId.INACTIVE));

        event = fixture.normalizeEvent(EventModel.builder().eventId(WindowsLogEventId.LOG_IN)
                .timestamp(Instant.parse("2015-09-02T14:15:30.00Z").toEpochMilli())
                .userName("Jim")
                .build());

        assertThat(event.getEventId(), is(NormalizedEventId.ACTIVE));

        event = fixture.normalizeEvent(EventModel.builder().eventId(WindowsLogEventId.SCREEN_LOCK)
                .timestamp(Instant.parse("2015-09-02T14:15:30.00Z").toEpochMilli())
                .userName("Jim")
                .build());

        assertThat(event.getEventId(), is(NormalizedEventId.INACTIVE));

        event = fixture.normalizeEvent(EventModel.builder().eventId(WindowsLogEventId.SCREEN_UNLOCK)
                .timestamp(Instant.parse("2015-09-02T14:15:30.00Z").toEpochMilli())
                .userName("Jim")
                .build());

        assertThat(event.getEventId(), is(NormalizedEventId.ACTIVE));

        event = fixture.normalizeEvent(EventModel.builder().eventId(WindowsLogEventId.SCREENSAVER_INACTIVE)
                .timestamp(Instant.parse("2015-09-02T14:15:30.00Z").toEpochMilli())
                .userName("Jim")
                .build());

        assertThat(event.getEventId(), is(NormalizedEventId.ACTIVE));

        event = fixture.normalizeEvent(EventModel.builder().eventId(WindowsLogEventId.SCREENSAVER_ACTIVE)
                .timestamp(Instant.parse("2015-09-02T14:15:30.00Z").toEpochMilli())
                .userName("Jim")
                .build());

        assertThat(event.getEventId(), is(NormalizedEventId.INACTIVE));
    }

    @Test
    public void testCleanupScreenLockUnlockSession1() {
        List<EventModel> events = ImmutableList.of(
                createEvent(WindowsLogEventId.SCREEN_LOCK, "2015-09-02T14:15:30.00Z", "Jim"),
                createEvent(WindowsLogEventId.LOG_IN, "2015-09-02T14:15:30.00Z", "Jim"),
                createEvent(WindowsLogEventId.SCREEN_UNLOCK, "2015-09-02T14:15:30.00Z", "Jim")
        );

        List<EventModel> cleanedUpEvents = fixture.cleanupEventsBetween(events,
                WindowsLogEventId.SCREEN_LOCK, WindowsLogEventId.SCREEN_UNLOCK);
        assertThat(cleanedUpEvents.size(), is(2));
        assertThat(cleanedUpEvents.get(0), is(events.get(0)));
        assertThat(cleanedUpEvents.get(1), is(events.get(2)));
    }

    @Test
    public void testCleanupScreenLockUnlockSession2() {
        List<EventModel> events = ImmutableList.of(
                createEvent(WindowsLogEventId.SCREEN_LOCK, "2015-09-02T14:15:30.00Z", "Jim"),
                createEvent(WindowsLogEventId.LOG_IN, "2015-09-02T14:15:30.00Z", "Jim"),
                createEvent(WindowsLogEventId.SCREEN_UNLOCK, "2015-09-02T14:15:30.00Z", "Jim"),
                createEvent(WindowsLogEventId.SCREEN_LOCK, "2015-09-02T15:15:30.00Z", "Jim"),
                createEvent(WindowsLogEventId.LOG_IN, "2015-09-02T15:15:30.00Z", "Jim"),
                createEvent(WindowsLogEventId.SCREEN_UNLOCK, "2015-09-02T15:15:30.00Z", "Jim")
        );

        List<EventModel> cleanedUpEvents = fixture.cleanupEventsBetween(events,
                WindowsLogEventId.SCREEN_LOCK, WindowsLogEventId.SCREEN_UNLOCK);
        assertThat(cleanedUpEvents.size(), is(4));
        assertThat(cleanedUpEvents.get(0), is(events.get(0)));
        assertThat(cleanedUpEvents.get(1), is(events.get(2)));
        assertThat(cleanedUpEvents.get(2), is(events.get(3)));
        assertThat(cleanedUpEvents.get(3), is(events.get(5)));
    }

    @Test
    public void testCleanupScreenLockUnlockSession3() {
        List<EventModel> events = ImmutableList.of(
                createEvent(WindowsLogEventId.SCREEN_LOCK, "2015-09-02T14:15:30.00Z", "Jim"),
                createEvent(WindowsLogEventId.LOG_OUT, "2015-09-02T14:15:30.00Z", "Jim"),
                createEvent(WindowsLogEventId.LOG_IN, "2015-09-02T14:15:30.00Z", "Jim"),
                createEvent(WindowsLogEventId.SCREEN_LOCK, "2015-09-02T15:15:30.00Z", "Jim"),
                createEvent(WindowsLogEventId.LOG_IN, "2015-09-02T15:15:30.00Z", "Jim"),
                createEvent(WindowsLogEventId.SCREEN_UNLOCK, "2015-09-02T15:15:30.00Z", "Jim")
        );

        List<EventModel> cleanedUpEvents = fixture.cleanupEventsBetween(events,
                WindowsLogEventId.SCREEN_LOCK, WindowsLogEventId.SCREEN_UNLOCK);
        assertThat(cleanedUpEvents.size(), is(5));
        assertThat(cleanedUpEvents.get(3), is(events.get(3)));
        assertThat(cleanedUpEvents.get(4), is(events.get(5)));
    }

    @Test
    public void testCleanupScreenLockUnlockSession4() {
        List<EventModel> events = ImmutableList.of(
                createEvent(WindowsLogEventId.SCREEN_LOCK, "2015-09-02T14:15:30.00Z", "Jim"),
                createEvent(WindowsLogEventId.SCREENSAVER_ACTIVE, "2015-09-02T14:15:30.00Z", "Jim"),
                createEvent(WindowsLogEventId.SCREENSAVER_INACTIVE, "2015-09-02T14:15:30.00Z", "Jim"),
                createEvent(WindowsLogEventId.SCREENSAVER_ACTIVE, "2015-09-02T14:15:30.00Z", "Jim"),
                createEvent(WindowsLogEventId.SCREENSAVER_INACTIVE, "2015-09-02T14:15:30.00Z", "Jim"),
                createEvent(WindowsLogEventId.SCREENSAVER_ACTIVE, "2015-09-02T14:15:30.00Z", "Jim"),
                createEvent(WindowsLogEventId.SCREENSAVER_INACTIVE, "2015-09-02T14:15:30.00Z", "Jim"),
                createEvent(WindowsLogEventId.SCREEN_UNLOCK, "2015-09-02T15:15:30.00Z", "Jim")
        );

        List<EventModel> cleanedUpEvents = fixture.cleanupEventsBetween(events,
                WindowsLogEventId.SCREEN_LOCK, WindowsLogEventId.SCREEN_UNLOCK);
        assertThat(cleanedUpEvents.size(), is(2));
        assertThat(cleanedUpEvents.get(0), is(events.get(0)));
        assertThat(cleanedUpEvents.get(1), is(events.get(7)));
    }

    @Test
    public void testCleanupScreenLockUnlockSession5() {
        List<EventModel> events = ImmutableList.of(
                createEvent(WindowsLogEventId.SCREEN_UNLOCK, "2015-09-02T14:15:30.00Z", "Jim"),
                createEvent(WindowsLogEventId.LOG_IN, "2015-09-02T14:15:30.00Z", "Jim"),
                createEvent(WindowsLogEventId.SCREEN_LOCK, "2015-09-02T14:15:30.00Z", "Jim")
        );

        List<EventModel> cleanedUpEvents = fixture.cleanupEventsBetween(events,
                WindowsLogEventId.SCREEN_LOCK, WindowsLogEventId.SCREEN_UNLOCK);
        assertThat(cleanedUpEvents.size(), is(3));
    }

    @Test
    public void testCleanupScreenLockUnlockSession6() {
        List<EventModel> events = ImmutableList.of(
                createEvent(WindowsLogEventId.SCREEN_UNLOCK, "2015-09-02T14:15:30.00Z", "Jim")
        );

        List<EventModel> cleanedUpEvents = fixture.cleanupEventsBetween(events,
                WindowsLogEventId.SCREEN_LOCK, WindowsLogEventId.SCREEN_UNLOCK);
        assertThat(cleanedUpEvents.size(), is(1));
    }

    @Test
    public void testCleanupScreenLockUnlockSession7() {
        List<EventModel> events = ImmutableList.of(
                createEvent(WindowsLogEventId.LOG_IN, "2015-09-02T14:15:30.00Z", "Jim")
        );

        List<EventModel> cleanedUpEvents = fixture.cleanupEventsBetween(events,
                WindowsLogEventId.SCREEN_LOCK, WindowsLogEventId.SCREEN_UNLOCK);
        assertThat(cleanedUpEvents.size(), is(1));
    }


}
