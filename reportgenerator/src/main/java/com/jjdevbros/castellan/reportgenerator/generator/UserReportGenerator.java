package com.jjdevbros.castellan.reportgenerator.generator;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.jjdevbros.castellan.common.model.InactivePeriod;
import com.jjdevbros.castellan.common.model.NormalizedEventId;
import com.jjdevbros.castellan.common.model.NormalizedEventModel;
import com.jjdevbros.castellan.common.model.NormalizedSession;
import com.jjdevbros.castellan.common.model.SessionPeriod;
import com.jjdevbros.castellan.reportgenerator.report.UserReport;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by lordbritishix on 05/09/15.
 *
 * Generates a user report
 */
@Slf4j
public class UserReportGenerator {
    public UserReport generateUserReport(String userName, NormalizedSession events, SessionPeriod period) {
        UserReport.UserReportBuilder builder = UserReport.builder()
                .userName(userName)
                .period(period)
                .sourceEvents(events.getEvents().stream().map(e -> e.getEventModel()).collect(Collectors.toList()));
        List<String> errorDescriptions = Lists.newArrayList();
        Optional<Instant> startTime = computeStartTime(events);
        Optional<Instant> endTime = computeEndTime(events);

        List<InactivePeriod> inactivePeriods = getInactivityActivityPeriods(events);
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
            builder.hasErrors(true);
            builder.errorDescription(errorDescriptions.stream().collect(Collectors.joining("; ")));
            log.error("Unable to generate user report for user: {} for events {} for period {}",
                        userName, events, period.toString());
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

    /**
     * Start time: the first active event in the list of events
     * Returns -1 if there is no Active event in the list of events
     */
    @VisibleForTesting
    Optional<Instant> computeStartTime(NormalizedSession session) {
        return Optional.ofNullable(session.getEvents().stream()
                .sorted()
                .filter(e -> e.getEventId().equals(NormalizedEventId.ACTIVE))
                .findFirst()
                .map(e -> Instant.ofEpochMilli(e.getEventModel().getTimestamp()))
                .orElse(null));

    }

    /**
     * Time of the first "Inactive" event after the last "Active" event in the list of events
     */
    @VisibleForTesting
    Optional<Instant> computeEndTime(NormalizedSession events) {
        List<NormalizedEventModel> baseEvents = events.getEvents().stream().sorted().collect(Collectors.toList());

        List<NormalizedEventModel> activeEvents = baseEvents.stream()
                .filter(e -> e.getEventId().equals(NormalizedEventId.ACTIVE))
                .collect(Collectors.toList());

        if (activeEvents.size() <= 0) {
            return Optional.empty();
        }

        NormalizedEventModel lastEvent = activeEvents.get(activeEvents.size() - 1);

        List<NormalizedEventModel> sublist = baseEvents.subList(baseEvents.indexOf(lastEvent), baseEvents.size());

        return Optional.ofNullable(sublist.stream()
                .filter(e -> e.getEventId().equals(NormalizedEventId.INACTIVE))
                .findFirst()
                .map(e -> Instant.ofEpochMilli(e.getEventModel().getTimestamp()))
                .orElse(null));
    }

    /**
     * Returns a list of periods of activity from the list.
     *
     * Activity period is defined as the period between the first active event followed by the first inactive event
     * after the active event
     */
    @VisibleForTesting
    List<InactivePeriod> getInactivityActivityPeriods(NormalizedSession events) {
        if (events.isHasErrors()) {
            return ImmutableList.of();
        }

        List<InactivePeriod> inactivePeriods = Lists.newArrayList();
        Stack<NormalizedEventModel> stack = new Stack<>();
        boolean firstActiveFound = false;

        for (NormalizedEventModel event : events.getEvents()) {
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

        return inactivePeriods;
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
