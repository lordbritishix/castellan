package com.jjdevbros.castellan.reportgenerator.validator;

import com.google.common.collect.ImmutableList;
import com.jjdevbros.castellan.common.EventModel;
import com.jjdevbros.castellan.common.WindowsLogEventId;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by lordbritishix on 06/09/15.
 */
public class ValidatorTest {
    private Validator fixture;

    @Before
    public void setup() {
        fixture = new Validator();
    }

    @Test
    public void testValidateDetectsValidEvents1() {
        List<EventModel> events = ImmutableList.of(
                EventModel.builder().eventId(WindowsLogEventId.LOG_IN).build(),
                EventModel.builder().eventId(WindowsLogEventId.SCREEN_LOCK).build(),
                EventModel.builder().eventId(WindowsLogEventId.SCREEN_UNLOCK).build(),
                EventModel.builder().eventId(WindowsLogEventId.USER_INACTIVE).build(),
                EventModel.builder().eventId(WindowsLogEventId.USER_ACTIVE).build(),
                EventModel.builder().eventId(WindowsLogEventId.LOG_OUT).build());
        assertThat(fixture.isValid(events), is(true));
    }

    @Test
    public void testValidateDetectsValidEvents2() {
        List<EventModel> events = ImmutableList.of(
                EventModel.builder().eventId(WindowsLogEventId.LOG_IN).build()
        );

        assertThat(fixture.isValid(events), is(true));
    }

    @Test
    public void testValidateDetectsValidEvents3() {
        List<EventModel> events = ImmutableList.of();

        assertThat(fixture.isValid(events), is(true));
    }

    @Test
    public void testValidateDetectsValidEvents4() {
        List<EventModel> events = ImmutableList.of(
                EventModel.builder().eventId(WindowsLogEventId.LOG_OUT).build(),
                EventModel.builder().eventId(WindowsLogEventId.LOG_IN).build()
        );

        assertThat(fixture.isValid(events), is(true));
    }


    @Test
    public void testValidateDetectsInvalidEvents1() {
        List<EventModel> events = ImmutableList.of(
                EventModel.builder().eventId(WindowsLogEventId.LOG_IN).build(),
                EventModel.builder().eventId(WindowsLogEventId.SCREEN_UNLOCK).build(),
                EventModel.builder().eventId(WindowsLogEventId.SCREEN_LOCK).build());

        assertThat(fixture.isValid(events), is(false));
    }

    @Test
    public void testValidateDetectsInvalidEvents2() {
        List<EventModel> events = ImmutableList.of(
                EventModel.builder().eventId(WindowsLogEventId.LOG_IN).build(),
                EventModel.builder().eventId(WindowsLogEventId.SCREEN_LOCK).build(),
                EventModel.builder().eventId(WindowsLogEventId.USER_ACTIVE).build());

        assertThat(fixture.isValid(events), is(false));
    }

    @Test
    public void testValidateDetectsInvalidEvents3() {
        List<EventModel> events = ImmutableList.of(
                EventModel.builder().eventId(WindowsLogEventId.LOG_IN).build(),
                EventModel.builder().eventId(WindowsLogEventId.LOG_OUT).build(),
                EventModel.builder().eventId(WindowsLogEventId.USER_ACTIVE).build());

        assertThat(fixture.isValid(events), is(false));
    }

    @Test
    public void testValidateDetectsInvalidEvents4() {
        List<EventModel> events = ImmutableList.of(
                EventModel.builder().eventId(WindowsLogEventId.LOG_OUT).build(),
                EventModel.builder().eventId(WindowsLogEventId.USER_ACTIVE).build());

        assertThat(fixture.isValid(events), is(false));
    }

    @Test
    public void testValidateDetectsInvalidEvents5() {
        List<EventModel> events = ImmutableList.of(
                EventModel.builder().eventId(WindowsLogEventId.LOG_IN).build(),
                EventModel.builder().eventId(WindowsLogEventId.SCREEN_LOCK).build(),
                EventModel.builder().eventId(WindowsLogEventId.USER_INACTIVE).build());

        assertThat(fixture.isValid(events), is(false));
    }

    @Test
    public void testValidateIgnoresUnknownEvents() {
        List<EventModel> events = ImmutableList.of(
                EventModel.builder().eventId(WindowsLogEventId.LOG_IN).build(),
                EventModel.builder().eventId(WindowsLogEventId.NETWORK_CONNECTED).build(),
                EventModel.builder().eventId(WindowsLogEventId.NETWORK_DISCONNECTED).build(),
                EventModel.builder().eventId(WindowsLogEventId.LOG_OUT).build(),
                EventModel.builder().eventId(WindowsLogEventId.NETWORK_CONNECTED).build(),
                EventModel.builder().eventId(WindowsLogEventId.NETWORK_DISCONNECTED).build());

        assertThat(fixture.isValid(events), is(true));
    }

}

