package com.jjdevbros.castellan.reportgenerator.generator;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.jjdevbros.castellan.common.database.JsonGroupLookup;
import com.jjdevbros.castellan.common.model.EventModel;
import com.jjdevbros.castellan.common.model.InactivePeriod;
import com.jjdevbros.castellan.common.model.NormalizedEventId;
import com.jjdevbros.castellan.common.model.NormalizedEventModel;
import com.jjdevbros.castellan.common.model.NormalizedSession;
import com.jjdevbros.castellan.common.model.SessionPeriod;
import com.jjdevbros.castellan.reportgenerator.report.UserReport;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Created by lordbritishix on 05/09/15.
 */
public class UserReportGeneratorTest {
    private UserReportGenerator fixture;

    @Before
    public void setup() {
        fixture = new UserReportGenerator(new JsonGroupLookup(null), 0);
    }

    @Test
    public void testGetActivityPeriodsExcludesPeriodsWithinThreshold1() {
        List<NormalizedEventModel> events = Lists.newArrayList();
        events.add(buildTestEvent(Instant.parse("2015-09-02T08:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T11:15:31.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T11:15:40.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T11:15:41.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T12:00:00.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T14:19:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));

        List<InactivePeriod> inactivePeriods =
                fixture.getInactivityActivityPeriods(events, 60);

        assertThat(inactivePeriods.size(), is(1));
    }

    @Test
    public void testGetActivityPeriodsExcludesPeriodsWithinThreshold2() {
        List<NormalizedEventModel> events = Lists.newArrayList();
        events.add(buildTestEvent(Instant.parse("2015-09-02T08:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T11:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T11:15:40.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T11:15:41.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T11:15:51.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T12:15:40.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T12:15:51.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));

        events.add(buildTestEvent(Instant.parse("2015-09-02T14:19:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));

        List<InactivePeriod> inactivePeriods =
                fixture.getInactivityActivityPeriods(events, 10);

        assertThat(inactivePeriods.size(), is(1));
    }

    @Test
    public void testGetActivityPeriodsExcludesPeriodsWithinThreshold3() {
        List<NormalizedEventModel> events = Lists.newArrayList();
        events.add(buildTestEvent(Instant.parse("2015-09-02T08:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T11:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T11:15:40.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T11:15:41.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T11:15:51.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T12:15:40.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T12:15:51.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T14:19:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));

        List<InactivePeriod> inactivePeriods =
                fixture.getInactivityActivityPeriods(events, 0);

        assertThat(inactivePeriods.size(), is(3));
    }

    @Test
    public void testGetActivityPeriodsExcludesPeriodsWithinThreshold4() {
        List<NormalizedEventModel> events = Lists.newArrayList();
        events.add(buildTestEvent(Instant.parse("2015-09-02T08:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T11:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T11:15:40.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T11:15:41.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T11:15:51.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T12:15:40.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T12:15:51.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T14:19:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));

        List<InactivePeriod> inactivePeriods =
                fixture.getInactivityActivityPeriods(events, 180);

        assertThat(inactivePeriods.size(), is(0));
    }

    @Test
    public void testGetInScopeEventsForHappyCase1() {
        List<NormalizedEventModel> events = Lists.newArrayList();
        events.add(buildTestEvent(Instant.parse("2015-09-02T08:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T10:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T12:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T14:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));

        List<NormalizedEventModel> inScopeEvents = fixture.getInScopeEvents(events).get();

        assertThat(inScopeEvents, is(events));
    }

    @Test
    public void testGetInScopeEventsForHappyCase2() {
        List<NormalizedEventModel> events = Lists.newArrayList();
        events.add(buildTestEvent(Instant.parse("2015-09-02T10:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T12:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T14:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));

        List<NormalizedEventModel> inScopeEvents = fixture.getInScopeEvents(events).get();

        assertThat(inScopeEvents, is(ImmutableList.of(
                buildTestEvent(Instant.parse("2015-09-02T12:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE),
                buildTestEvent(Instant.parse("2015-09-02T14:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE))));
    }

    @Test
    public void testGetInScopeEventsForHappyCase3() {
        List<NormalizedEventModel> events = Lists.newArrayList();
        events.add(buildTestEvent(Instant.parse("2015-09-02T10:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T12:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T14:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T16:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T18:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));


        List<NormalizedEventModel> inScopeEvents = fixture.getInScopeEvents(events).get();

        assertThat(inScopeEvents, is(ImmutableList.of(
                buildTestEvent(Instant.parse("2015-09-02T12:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE),
                buildTestEvent(Instant.parse("2015-09-02T14:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE),
                buildTestEvent(Instant.parse("2015-09-02T16:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE),
                buildTestEvent(Instant.parse("2015-09-02T18:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE))));
    }

    @Test
    public void testGetInScopeEventsWithoutActiveEvent() {
        List<NormalizedEventModel> events = Lists.newArrayList();
        events.add(buildTestEvent(Instant.parse("2015-09-02T10:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T14:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T16:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T18:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));

        assertThat(fixture.getInScopeEvents(events), is(Optional.empty()));
    }

    @Test
    public void testGetInScopeEventsWithoutInactiveEvent() {
        List<NormalizedEventModel> events = Lists.newArrayList();
        events.add(buildTestEvent(Instant.parse("2015-09-02T10:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T14:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));

        assertThat(fixture.getInScopeEvents(events), is(Optional.empty()));
    }

    @Test
    public void testGetInScopeEventsForEmptyList() {
        assertThat(fixture.getInScopeEvents(Lists.newArrayList()), is(Optional.empty()));
    }

    @Test
    public void testComputeStartTimeReturnsTimeForHappyCase1() {
        List<NormalizedEventModel> events = Lists.newArrayList();
        events.add(buildTestEvent(Instant.parse("2015-09-02T14:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T08:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T12:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T10:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));

        assertThat(fixture.computeStartTime(events).get(),
                    is(Instant.parse("2015-09-02T08:15:30.00Z")));
    }

    @Test
    public void testComputeStartTimeReturnsTimeForHappyCase2() {
        List<NormalizedEventModel> events = Lists.newArrayList();
        events.add(buildTestEvent(Instant.parse("2015-09-02T08:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));

        assertThat(fixture.computeStartTime(events).get(),
                    is(Instant.parse("2015-09-02T08:15:30.00Z")));
    }

    @Test
    public void testComputeStartTimeReturnsTimeForMultipleSameTime() {
        List<NormalizedEventModel> events = Lists.newArrayList();
        events.add(buildTestEvent(Instant.parse("2015-09-02T14:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T08:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T12:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T08:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T08:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T10:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));

        assertThat(fixture.computeStartTime(events).get(),
                is(Instant.parse("2015-09-02T08:15:30.00Z")));
    }

    @Test
    public void testComputeStartTimeReturnsErrorForEventsWithNoActiveTime() {
        List<NormalizedEventModel> events = Lists.newArrayList();
        events.add(buildTestEvent(Instant.parse("2015-09-02T08:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));

        assertFalse(fixture.computeStartTime(events).isPresent());
    }

    @Test
    public void testComputeEndTimeReturnsTimeForHappyCase1() {
        List<NormalizedEventModel> events = Lists.newArrayList();
        events.add(buildTestEvent(Instant.parse("2015-09-02T08:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T10:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T12:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T14:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        Collections.shuffle(events);
        assertThat(fixture.computeEndTime(events).get(),
                is(Instant.parse("2015-09-02T14:15:30.00Z")));
    }

    @Test
    public void testComputeEndTimeReturnsTimeForHappyCase2() {
        List<NormalizedEventModel> events = Lists.newArrayList();
        events.add(buildTestEvent(Instant.parse("2015-09-02T08:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T10:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));

        assertThat(fixture.computeEndTime(events).get(),
                is(Instant.parse("2015-09-02T10:15:30.00Z")));
    }

    @Test
    public void testComputeEndTimeReturnsTimeForHappyCase3() {
        List<NormalizedEventModel> events = Lists.newArrayList();
        events.add(buildTestEvent(Instant.parse("2015-09-02T06:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T08:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T10:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T11:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T12:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T15:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        Collections.shuffle(events);
        assertThat(fixture.computeEndTime(events).get(),
                is(Instant.parse("2015-09-02T11:15:30.00Z")));
    }


    @Test
    public void testComputeEndTimeReturnsTimeForMultipleLastInactiveTime() {
        List<NormalizedEventModel> events = Lists.newArrayList();
        events.add(buildTestEvent(Instant.parse("2015-09-02T08:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T16:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T10:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T12:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T14:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T14:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));

        assertThat(fixture.computeEndTime(events).get(),
                is(Instant.parse("2015-09-02T16:15:30.00Z")));
    }

    @Test
    public void testComputeEndTimeReturnsErrorForValidListWithoutInactive() {
        List<NormalizedEventModel> events = Lists.newArrayList();
        events.add(buildTestEvent(Instant.parse("2015-09-02T08:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));

        assertFalse(fixture.computeEndTime(events).isPresent());
    }

    @Test
    public void testComputeEndTimeReturnsErrorForInvalidList() {
        List<NormalizedEventModel> events = Lists.newArrayList();
        events.add(buildTestEvent(Instant.parse("2015-09-02T08:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T08:17:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));

        assertFalse(fixture.computeEndTime(events).isPresent());
    }

    @Test
    public void testGetActivityPeriodsReturnsCorrectPeriodsForHappyCase() {
        List<NormalizedEventModel> events = Lists.newArrayList();
        events.add(buildTestEvent(Instant.parse("2015-09-02T08:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T10:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T12:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T14:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));

        List<InactivePeriod> inactivePeriods =
                fixture.getInactivityActivityPeriods(events, 0);

        List<InactivePeriod> expectedInactivePeriods = ImmutableList.of(
                new InactivePeriod(LocalDateTime.ofInstant(Instant.parse("2015-09-02T10:15:30.00Z"), ZoneOffset.UTC),
                        LocalDateTime.ofInstant(Instant.parse("2015-09-02T12:15:30.00Z"), ZoneOffset.UTC)));

        assertTrue(inactivePeriods.equals(expectedInactivePeriods));
    }

    @Test
    public void testGetActivityPeriodsComputesFromFirstActivePeriod() {
        List<NormalizedEventModel> events = Lists.newArrayList();
        events.add(buildTestEvent(Instant.parse("2015-09-02T00:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T01:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T08:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T10:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T12:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T14:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));

        List<InactivePeriod> inactivePeriods =
                fixture.getInactivityActivityPeriods(events, 0);

        List<InactivePeriod> expectedInactivePeriods = ImmutableList.of(
                new InactivePeriod(LocalDateTime.ofInstant(Instant.parse("2015-09-02T10:15:30.00Z"), ZoneOffset.UTC),
                        LocalDateTime.ofInstant(Instant.parse("2015-09-02T12:15:30.00Z"), ZoneOffset.UTC)));

        assertTrue(inactivePeriods.equals(expectedInactivePeriods));
    }

    @Test
    public void testGetActivityPeriodsReturnsCorrectPeriodsForDuplicateCase() {
        List<NormalizedEventModel> events = Lists.newArrayList();
        events.add(buildTestEvent(Instant.parse("2015-09-02T08:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T08:20:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T08:25:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T10:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T10:25:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T12:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T12:30:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T14:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T16:30:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T18:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));


        List<InactivePeriod> inactivePeriods =
                fixture.getInactivityActivityPeriods(events, 0);

        List<InactivePeriod> expectedInactivePeriods = ImmutableList.of(
                new InactivePeriod(LocalDateTime.ofInstant(Instant.parse("2015-09-02T10:15:30.00Z"), ZoneOffset.UTC),
                        LocalDateTime.ofInstant(Instant.parse("2015-09-02T12:15:30.00Z"), ZoneOffset.UTC)),
                new InactivePeriod(LocalDateTime.ofInstant(Instant.parse("2015-09-02T14:15:30.00Z"), ZoneOffset.UTC),
                        LocalDateTime.ofInstant(Instant.parse("2015-09-02T16:30:30.00Z"), ZoneOffset.UTC))
        );

        assertTrue(inactivePeriods.equals(expectedInactivePeriods));
    }

    @Test
    public void testGetActivityPeriodsReturnsCorrectPeriodsForNoInactivityCase() {
        List<NormalizedEventModel> events = Lists.newArrayList();
        events.add(buildTestEvent(Instant.parse("2015-09-02T08:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T18:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));

        List<InactivePeriod> inactivePeriods =
                fixture.getInactivityActivityPeriods(events, 0);

        assertThat(inactivePeriods.size(), is(0));
    }

    @Test
    public void testGetActivityPeriodsReturnsCorrectPeriodsForInvalidDataCase1() {
        List<NormalizedEventModel> events = Lists.newArrayList();
        events.add(buildTestEvent(Instant.parse("2015-09-02T08:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));

        List<InactivePeriod> inactivePeriods =
                fixture.getInactivityActivityPeriods(events, 0);

        assertThat(inactivePeriods.size(), is(0));
    }

    @Test
    public void testGetActivityPeriodsReturnsCorrectPeriodsForInvalidDataCase2() {
        List<NormalizedEventModel> events = Lists.newArrayList();
        events.add(buildTestEvent(Instant.parse("2015-09-02T08:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));

        List<InactivePeriod> inactivePeriods =
                fixture.getInactivityActivityPeriods(events, 0);

        assertThat(inactivePeriods.size(), is(0));
    }

    @Test
    public void testGetActivityPeriodsReturnsCorrectPeriodsForWeirdCase() {
        List<NormalizedEventModel> events = Lists.newArrayList();
        events.add(buildTestEvent(Instant.parse("2015-09-02T08:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T10:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));

        List<InactivePeriod> inactivePeriods =
                fixture.getInactivityActivityPeriods(events, 0);

        assertThat(inactivePeriods.size(), is(0));
    }


    @Test
    public void testGetActivityPeriodsReturnsCorrectPeriodsForMultipleInactivityCase() {
        List<NormalizedEventModel> events = Lists.newArrayList();
        events.add(buildTestEvent(Instant.parse("2015-09-02T08:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T10:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T12:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T14:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T15:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T16:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T17:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T18:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));

        List<InactivePeriod> inactivePeriods =
                fixture.getInactivityActivityPeriods(events, 0);

        List<InactivePeriod> expectedInactivePeriods = ImmutableList.of(
                new InactivePeriod(LocalDateTime.ofInstant(Instant.parse("2015-09-02T10:15:30.00Z"), ZoneOffset.UTC),
                        LocalDateTime.ofInstant(Instant.parse("2015-09-02T12:15:30.00Z"), ZoneOffset.UTC)),
                new InactivePeriod(LocalDateTime.ofInstant(Instant.parse("2015-09-02T14:15:30.00Z"), ZoneOffset.UTC),
                        LocalDateTime.ofInstant(Instant.parse("2015-09-02T15:15:30.00Z"), ZoneOffset.UTC)),
                new InactivePeriod(LocalDateTime.ofInstant(Instant.parse("2015-09-02T16:15:30.00Z"), ZoneOffset.UTC),
                        LocalDateTime.ofInstant(Instant.parse("2015-09-02T17:15:30.00Z"), ZoneOffset.UTC))
        );

        assertTrue(inactivePeriods.equals(expectedInactivePeriods));
    }

    @Test
    public void testComputeInactivityDurationForHappyCase() {
        List<InactivePeriod> inactivePeriods = ImmutableList.of(
                new InactivePeriod(LocalDateTime.ofInstant(Instant.parse("2015-09-02T10:00:00.00Z"), ZoneOffset.UTC),
                                    LocalDateTime.ofInstant(Instant.parse("2015-09-02T11:00:00.00Z"), ZoneOffset.UTC)));

        //1 hour
        assertThat(fixture.computeInactivityDuration(inactivePeriods), is(Duration.of(3600000L, ChronoUnit.MILLIS)));
    }

    @Test
    public void testComputeInactivityDurationForMultipleCase1() {
        List<InactivePeriod> inactivePeriods = ImmutableList.of(
                new InactivePeriod(LocalDateTime.ofInstant(Instant.parse("2015-09-02T10:00:00.00Z"), ZoneOffset.UTC),
                        LocalDateTime.ofInstant(Instant.parse("2015-09-02T11:00:00.00Z"), ZoneOffset.UTC)),
                new InactivePeriod(LocalDateTime.ofInstant(Instant.parse("2015-09-02T12:00:00.00Z"), ZoneOffset.UTC),
                        LocalDateTime.ofInstant(Instant.parse("2015-09-02T13:00:00.00Z"), ZoneOffset.UTC)),
                new InactivePeriod(LocalDateTime.ofInstant(Instant.parse("2015-09-02T14:00:00.00Z"), ZoneOffset.UTC),
                        LocalDateTime.ofInstant(Instant.parse("2015-09-02T15:00:00.00Z"), ZoneOffset.UTC))
                );

        //3 hours
        assertThat(fixture.computeInactivityDuration(inactivePeriods), is(Duration.of(3600000L * 3, ChronoUnit.MILLIS)));
    }

    @Test
    public void testComputeInactivityDurationForMultipleCase2() {
        List<InactivePeriod> inactivePeriods = ImmutableList.of(
                new InactivePeriod(LocalDateTime.ofInstant(Instant.parse("2015-09-02T10:00:00.00Z"), ZoneOffset.UTC),
                        LocalDateTime.ofInstant(Instant.parse("2015-09-02T11:30:00.00Z"), ZoneOffset.UTC)),
                new InactivePeriod(LocalDateTime.ofInstant(Instant.parse("2015-09-02T12:00:00.00Z"), ZoneOffset.UTC),
                        LocalDateTime.ofInstant(Instant.parse("2015-09-02T13:30:00.00Z"), ZoneOffset.UTC)),
                new InactivePeriod(LocalDateTime.ofInstant(Instant.parse("2015-09-02T14:00:00.00Z"), ZoneOffset.UTC),
                        LocalDateTime.ofInstant(Instant.parse("2015-09-02T15:45:00.00Z"), ZoneOffset.UTC))
        );

        //4 hours, 45 minutes
        assertThat(fixture.computeInactivityDuration(inactivePeriods),
                is(Duration.of((3600000L * 4) + 2700000L, ChronoUnit.MILLIS)));
    }

    @Test
    public void testGenerateReportReturnsCorrectDurationsForHappyCase1() {
        List<NormalizedEventModel> events = Lists.newArrayList();
        events.add(buildTestEvent(Instant.parse("2015-09-02T08:00:00.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T18:00:00.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        SessionPeriod period = new SessionPeriod(LocalDate.now(), LocalDate.now());
        UserReport report = fixture.generateUserReport("Jim",
                NormalizedSession.builder().events(events).build(), period);

        assertThat(report.getWorkDuration(), is(Duration.of(10L, ChronoUnit.HOURS)));
        assertThat(report.getInactivityDuration(), is(Duration.of(0L, ChronoUnit.HOURS)));
        assertThat(report.getActivityDuration(), is(Duration.of(10L, ChronoUnit.HOURS)));
    }

    @Test
    public void testGenerateReportReturnsCorrectDurationsForHappyCase2() {
        List<NormalizedEventModel> events = Lists.newArrayList();
        events.add(buildTestEvent(Instant.parse("2015-09-02T08:00:00.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T10:00:00.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T12:00:00.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T14:00:00.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T16:00:00.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T18:00:00.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));

        SessionPeriod period = new SessionPeriod(LocalDate.now(), LocalDate.now());
        UserReport report = fixture.generateUserReport("Jim",
                NormalizedSession.builder().events(events).build(), period);

        assertThat(report.getWorkDuration(), is(Duration.of(10L, ChronoUnit.HOURS)));
        assertThat(report.getInactivityDuration(), is(Duration.of(4L, ChronoUnit.HOURS)));
        assertThat(report.getActivityDuration(), is(Duration.of(6L, ChronoUnit.HOURS)));
    }

    @Test
    public void testGenerateReportReturnsCorrectDurationsForNoInactiveEventCase() {
        List<NormalizedEventModel> events = Lists.newArrayList();
        events.add(buildTestEvent(Instant.parse("2015-09-02T00:00:00.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T02:00:00.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));

        SessionPeriod period = new SessionPeriod(LocalDate.of(2015, 9, 02), LocalDate.of(2015, 9, 02));
        UserReport report = fixture.generateUserReport("Jim",
                NormalizedSession.builder().events(events).build(), period);

        assertThat(report.getWorkDuration(), is(Duration.of(0L, ChronoUnit.HOURS)));
        assertThat(report.getInactivityDuration(), is(Duration.of(0L, ChronoUnit.HOURS)));
        assertThat(report.getActivityDuration(), is(Duration.of(0L, ChronoUnit.HOURS)));
    }

    @Test
    public void testGenerateReportReturnsCorrectDurationsForNoActiveEventCase() {
        List<NormalizedEventModel> events = Lists.newArrayList();
        events.add(buildTestEvent(Instant.parse("2015-09-02T00:00:00.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));

        SessionPeriod period = new SessionPeriod(LocalDate.of(2015, 9, 02), LocalDate.of(2015, 9, 02));
        UserReport report = fixture.generateUserReport("Jim",
                NormalizedSession.builder().events(events).build(), period);

        assertThat(report.getWorkDuration(), is(Duration.of(0L, ChronoUnit.HOURS)));
        assertThat(report.getInactivityDuration(), is(Duration.of(0L, ChronoUnit.HOURS)));
        assertThat(report.getActivityDuration(), is(Duration.of(0L, ChronoUnit.HOURS)));
    }

    @Test
    public void testGenerateReportReturnsCorrectDurationsForInvalidSessionCase() {
        SessionPeriod period = new SessionPeriod(LocalDate.of(2015, 9, 02), LocalDate.of(2015, 9, 02));
        UserReport report = fixture.generateUserReport("Jim",
                                        NormalizedSession.builder().hasErrors(true).build(), period);

        assertThat(report.isHasErrors(), is(true));
        assertThat(report.getStartTime(), nullValue());
        assertThat(report.getEndTime(), nullValue());
        assertThat(report.getWorkDuration(), is(Duration.ofHours(0L)));
        assertThat(report.getInactivityDuration(), is(Duration.ofHours(0L)));
        assertThat(report.getActivityDuration(), is(Duration.ofHours(0L)));
    }

    private NormalizedEventModel buildTestEvent(long timeStamp, NormalizedEventId id) {
        return NormalizedEventModel.builder()
                .eventId(id)
                .eventModel(EventModel.builder().timestamp(timeStamp).build())
                .build();
    }

}
