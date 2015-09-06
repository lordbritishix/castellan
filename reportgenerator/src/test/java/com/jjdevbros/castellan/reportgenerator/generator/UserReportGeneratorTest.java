package com.jjdevbros.castellan.reportgenerator.generator;

import com.google.common.collect.Lists;
import com.jjdevbros.castellan.common.EventModel;
import com.jjdevbros.castellan.common.NormalizedEventId;
import com.jjdevbros.castellan.common.NormalizedEventModel;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by lordbritishix on 05/09/15.
 */
public class UserReportGeneratorTest {
    private UserReportGenerator fixture;

    @Before
    public void setup() {
        fixture = new UserReportGenerator();
    }

    @Test
    public void testComputeStartTimeReturnsTimeForHappyCase1() {
        List<NormalizedEventModel> events = Lists.newArrayList();
        events.add(buildTestEvent(Instant.parse("2015-09-02T14:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T08:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T12:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T10:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));

        assertThat(fixture.computeStartTime(events), is(Instant.parse("2015-09-02T08:15:30.00Z").toEpochMilli()));
    }

    @Test
    public void testComputeStartTimeReturnsTimeForHappyCase2() {
        List<NormalizedEventModel> events = Lists.newArrayList();
        events.add(buildTestEvent(Instant.parse("2015-09-02T08:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));

        assertThat(fixture.computeStartTime(events), is(Instant.parse("2015-09-02T08:15:30.00Z").toEpochMilli()));
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

        assertThat(fixture.computeStartTime(events), is(Instant.parse("2015-09-02T08:15:30.00Z").toEpochMilli()));
    }

    @Test
    public void testComputeStartTimeReturnsErrorForEventsWithNoActiveTime() {
        List<NormalizedEventModel> events = Lists.newArrayList();
        events.add(buildTestEvent(Instant.parse("2015-09-02T08:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));

        assertThat(fixture.computeStartTime(events), is(-1L));
    }

    @Test
    public void testComputeEndTimeReturnsTimeForHappyCase1() {
        List<NormalizedEventModel> events = Lists.newArrayList();
        events.add(buildTestEvent(Instant.parse("2015-09-02T14:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T08:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T12:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T10:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));

        assertThat(fixture.computeEndTime(events, -1L), is(Instant.parse("2015-09-02T14:15:30.00Z").toEpochMilli()));
    }

    @Test
    public void testComputeEndTimeReturnsTimeForHappyCase2() {
        List<NormalizedEventModel> events = Lists.newArrayList();
        events.add(buildTestEvent(Instant.parse("2015-09-02T08:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T10:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));

        assertThat(fixture.computeEndTime(events, -1L), is(Instant.parse("2015-09-02T10:15:30.00Z").toEpochMilli()));
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

        assertThat(fixture.computeEndTime(events, -1L), is(Instant.parse("2015-09-02T14:15:30.00Z").toEpochMilli()));
    }

    @Test
    public void testComputeEndTimeReturnsDefaultTimeForValidListWithoutInactive() {
        List<NormalizedEventModel> events = Lists.newArrayList();
        events.add(buildTestEvent(Instant.parse("2015-09-02T08:15:30.00Z").toEpochMilli(), NormalizedEventId.ACTIVE));

        long now = Instant.now().toEpochMilli();
        assertThat(fixture.computeEndTime(events, now), is(now));
    }

    @Test
    public void testComputeEndTimeReturnsErrorForInvalidList() {
        List<NormalizedEventModel> events = Lists.newArrayList();
        events.add(buildTestEvent(Instant.parse("2015-09-02T08:15:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));
        events.add(buildTestEvent(Instant.parse("2015-09-02T08:17:30.00Z").toEpochMilli(), NormalizedEventId.INACTIVE));

        long now = Instant.now().toEpochMilli();
        assertThat(fixture.computeEndTime(events, now), is(-1L));
    }

    private NormalizedEventModel buildTestEvent(long timeStamp, NormalizedEventId id) {
        return NormalizedEventModel.builder()
                .eventId(id)
                .eventModel(EventModel.builder().timestamp(timeStamp).build())
                .build();
    }

}
