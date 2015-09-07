package com.jjdevbros.castellan.reportgenerator.generator;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.jjdevbros.castellan.common.InactivePeriod;
import com.jjdevbros.castellan.common.NormalizedEventId;
import com.jjdevbros.castellan.common.NormalizedEventModel;
import com.jjdevbros.castellan.common.NormalizedSession;
import com.jjdevbros.castellan.common.SessionPeriod;
import com.jjdevbros.castellan.reportgenerator.report.UserReport;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * Created by lordbritishix on 05/09/15.
 *
 * Generates a user report
 */
public class UserReportGenerator {
    public UserReport generateUserReport(String userName, NormalizedSession events, SessionPeriod period) {
        UserReport.UserReportBuilder builder = UserReport.builder()
                .userName(userName)
                .period(period);

        if (events.isHasErrors()) {
            return builder.hasErrors(true).build();
        }

        Instant startTime = Instant.ofEpochMilli(computeStartTime(events));
        Instant endTime = Instant.ofEpochMilli(
                computeEndTime(events, period.getEndTime().toInstant(ZoneOffset.UTC).toEpochMilli()));
        List<InactivePeriod> inactivePeriods = getInactivityActivityPeriods(events);
        Duration workDuration = Duration.between(startTime, endTime);
        Duration inactivityDuration = computeInactivityDuration(inactivePeriods);

        return builder
                .startTime(startTime)
                .endTime(endTime)
                .inactivePeriods(inactivePeriods)
                .inactivityDuration(inactivityDuration)
                .workDuration(workDuration)
                .activityDuration(workDuration.minus(inactivityDuration))
                .build();
    }

    /**
     * Start time: the first active event in the list of events
     * Returns -1 if there is no Active event in the list of events
     */
    @VisibleForTesting
    long computeStartTime(NormalizedSession session) {
        if (session.isHasErrors()) {
            return -1;
        }

        return session.getEvents().stream()
                .sorted()
                .filter(e -> e.getEventId().equals(NormalizedEventId.ACTIVE))
                .findFirst()
                .map(e -> e.getEventModel().getTimestamp())
                .orElse(-1L)
                .longValue();
    }

    /**
     * Time of the first "Inactive" event after the last "Active" event in the list of events
     */
    @VisibleForTesting
    long computeEndTime(NormalizedSession events, long defaultEndTimeIfValid) {
        if (events.isHasErrors()) {
            return -1;
        }

        List<NormalizedEventModel> baseEvents = events.getEvents().stream().sorted().collect(Collectors.toList());

        List<NormalizedEventModel> activeEvents = baseEvents.stream()
                .filter(e -> e.getEventId().equals(NormalizedEventId.ACTIVE))
                .collect(Collectors.toList());

        if (activeEvents.size() <= 0) {
            return -1L;
        }

        NormalizedEventModel lastEvent = activeEvents.get(activeEvents.size() - 1);

        List<NormalizedEventModel> sublist = baseEvents.subList(baseEvents.indexOf(lastEvent), baseEvents.size());

        return sublist.stream()
                .filter(e -> e.getEventId().equals(NormalizedEventId.INACTIVE))
                        .findFirst().map(e -> e.getEventModel().getTimestamp())
                        .orElse(defaultEndTimeIfValid);
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
        for (NormalizedEventModel event : events.getEvents()) {
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
