package com.jjdevbros.castellan.reportgenerator.generator;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.jjdevbros.castellan.common.database.JsonGroupLookup;
import com.jjdevbros.castellan.common.model.InactivePeriod;
import com.jjdevbros.castellan.common.model.NormalizedEventId;
import com.jjdevbros.castellan.common.model.NormalizedEventModel;
import com.jjdevbros.castellan.common.model.NormalizedSession;
import com.jjdevbros.castellan.common.model.SessionPeriod;
import com.jjdevbros.castellan.reportgenerator.report.UserReport;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by lordbritishix on 05/09/15.
 *
 * Generates a user report
 */
@Slf4j
public class UserReportGenerator {
    private final JsonGroupLookup lookup;
    private final long inactivityThresholdInSeconds;

    @Inject
    public UserReportGenerator(JsonGroupLookup lookup, @Named("inactive.threshold") long inactivityThresholdInSeconds) {
        this.lookup = lookup;
        this.inactivityThresholdInSeconds = inactivityThresholdInSeconds;
    }

    public UserReport generateUserReport(String userName, NormalizedSession events, SessionPeriod period) {
        UserReport.UserReportBuilder builder = UserReport.builder()
                .userName(userName)
                .group(lookup.getGroupForName(userName))
                .period(period)
                .sourceEvents(events.getEvents().stream().map(e -> e.getEventModel()).collect(Collectors.toList()));

        List<String> errorDescriptions = Lists.newArrayList();

        Optional<List<NormalizedEventModel>> inScopeEvents = getInScopeEvents(events.getEvents());

        if (!inScopeEvents.isPresent()) {
            errorDescriptions.add("Unable to find the start time or end time in the session");
        }

        Optional<Instant> startTime = computeStartTime(inScopeEvents.orElse(Lists.newArrayList()));
        Optional<Instant> endTime = computeEndTime(inScopeEvents.orElse(Lists.newArrayList()));

        List<InactivePeriod> inactivePeriods =
                getInactivityActivityPeriods(inScopeEvents.orElse(Lists.newArrayList()), inactivityThresholdInSeconds);
        Optional<Duration> workDuration = Optional.empty();

        Duration inactivityDuration = computeInactivityDuration(inactivePeriods);

        if (startTime.isPresent() && endTime.isPresent()) {
            workDuration = Optional.of(Duration.between(startTime.get(), endTime.get()));
        }
        else {
            if (!startTime.isPresent()) {
                errorDescriptions.add("Unable to compute the start time in the session");
            }

            if (!endTime.isPresent()) {
                errorDescriptions.add("Unable to compute the end time in the session");
            }
        }

        Optional<Duration> activityDuration = Optional.empty();

        if (workDuration.isPresent()) {
            activityDuration = Optional.of(workDuration.get().minus(inactivityDuration));
        }

        if (errorDescriptions.size() > 0) {
            String errors = errorDescriptions.stream().collect(Collectors.joining("; "));
            builder.hasErrors(true);
            builder.errorDescription(errors);
            log.error("Unable to generate user report for user: {} for events {} for period {} with errors {}",
                        userName, events, period.toString(), errors);
        }

        return builder
                .startTime(startTime.orElse(null))
                .endTime(endTime.orElse(null))
                .inactivePeriods(inactivePeriods)
                .inactivityDuration(inactivityDuration)
                .workDuration(workDuration.orElse(Duration.ofHours(0L)))
                .activityDuration(activityDuration.orElse(Duration.ofHours(0L)))
                .build();
    }

    @VisibleForTesting
    /**
     * In-scope events are events between the first active time and the last inactive time, inclusive
     */
    Optional<List<NormalizedEventModel>> getInScopeEvents(List<NormalizedEventModel> events) {
        List<NormalizedEventModel> activeEvents = events.stream()
                .filter(e -> e.getEventId().equals(NormalizedEventId.ACTIVE))
                .sorted()
                .collect(Collectors.toList());

        List<NormalizedEventModel> inactiveEvents = events.stream()
                .filter(e -> e.getEventId().equals(NormalizedEventId.INACTIVE))
                .sorted()
                .collect(Collectors.toList());

        if ((activeEvents.size() <= 0) || (inactiveEvents.size() <= 0)) {
            return Optional.empty();
        }

        NormalizedEventModel startEvent = activeEvents.get(0);
        NormalizedEventModel endEvent = inactiveEvents.get(inactiveEvents.size() - 1);

        return Optional.of(events.stream()
                .filter(
                    p -> p.getEventModel().getTimestamp() >= startEvent.getEventModel().getTimestamp()
                    && p.getEventModel().getTimestamp() <= endEvent.getEventModel().getTimestamp())
                .collect(Collectors.toList()));
    }

    /**
     * Start time: the first active event in the list of events
     * Returns -1 if there is no Active event in the list of events
     */
    @VisibleForTesting
    Optional<Instant> computeStartTime(List<NormalizedEventModel> events) {
        return Optional.ofNullable(events.stream()
                .sorted()
                .filter(e -> e.getEventId().equals(NormalizedEventId.ACTIVE))
                .findFirst()
                .map(e -> Instant.ofEpochMilli(e.getEventModel().getTimestamp()))
                .orElse(null));

    }

    /**
     * Last inactive event. Active event must be present
     */
    @VisibleForTesting
    Optional<Instant> computeEndTime(List<NormalizedEventModel> events) {
        if (events.stream().filter(e -> e.getEventId().equals(NormalizedEventId.ACTIVE)).count() <= 0) {
            return Optional.empty();
        }

        List<NormalizedEventModel> inactiveEvents = events.stream()
                .filter(e -> e.getEventId().equals(NormalizedEventId.INACTIVE))
                .sorted()
                .collect(Collectors.toList());

        if (inactiveEvents.size() <= 0) {
            return Optional.empty();
        }

        return Optional.of(
                Instant.ofEpochMilli(inactiveEvents.get(inactiveEvents.size() - 1).getEventModel().getTimestamp()));
    }

    /**
     * Returns a list of periods of activity from the list.
     *
     * Activity period is defined as the period between the first active event followed by the first inactive event
     * after the active event
     */
    @VisibleForTesting
    List<InactivePeriod> getInactivityActivityPeriods(
            List<NormalizedEventModel> events, long threshold) {
        List<InactivePeriod> inactivePeriods = Lists.newArrayList();
        Stack<NormalizedEventModel> stack = new Stack<>();
        boolean firstActiveFound = false;

        for (NormalizedEventModel event : events) {
            if (event.getEventId() == NormalizedEventId.ACTIVE) {
                firstActiveFound = true;
            }

            if (!firstActiveFound) {
                continue;
            }

            if ((event.getEventId() == NormalizedEventId.INACTIVE)  && stack.isEmpty()) {
                stack.push(event);
            }
            else if ((event.getEventId() == NormalizedEventId.ACTIVE) && !stack.isEmpty()) {
                NormalizedEventModel inactive = stack.pop();
                inactivePeriods.add(new InactivePeriod(
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(inactive.getEventModel().getTimestamp()),
                            ZoneOffset.UTC),
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(event.getEventModel().getTimestamp()),
                            ZoneOffset.UTC)));
            }

        }

        return inactivePeriods.stream()
                .filter(p -> p.getDuration().compareTo(
                        Duration.of(threshold, ChronoUnit.SECONDS)) > 0)
                .collect(Collectors.toList());
    }

    @VisibleForTesting
    Duration computeInactivityDuration(List<InactivePeriod> inactivePeriods) {
        long sumOfInactivePeriodsInMillis =
                inactivePeriods.stream()
                        .map(e -> Duration.between(e.getStart(), e.getEnd()))
                        .mapToLong(d -> d.toMillis()).summaryStatistics().getSum();

        return Duration.ofMillis(sumOfInactivePeriodsInMillis);
    }
}
