package com.jjdevbros.castellan.reportgenerator.generator;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.jjdevbros.castellan.common.InactivePeriod;
import com.jjdevbros.castellan.common.NormalizedEventId;
import com.jjdevbros.castellan.common.NormalizedEventModel;
import com.jjdevbros.castellan.common.SessionPeriod;
import com.jjdevbros.castellan.reportgenerator.report.UserReport;

import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by lordbritishix on 05/09/15.
 *
 * Generates a user report
 */
public class UserReportGenerator {
    public UserReport generateUserReport(String userName, List<NormalizedEventModel> events, SessionPeriod period) {
        UserReport.UserReportBuilder builder = UserReport.builder();
        builder.userName(userName)
                .startTime(computeStartTime(events))
                .endTime(computeEndTime(events, period.getEndTime().toInstant(ZoneOffset.UTC).toEpochMilli()));

        return UserReport.builder().build();
    }

    /**
     * Start time: the first active event in the list of events
     * Returns -1 if there is no Active event in the list of events
     */
    @VisibleForTesting
    long computeStartTime(List<NormalizedEventModel> events) {
        return events.stream()
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
    long computeEndTime(List<NormalizedEventModel> events, long defaultEndTimeIfValid) {
        List<NormalizedEventModel> baseEvents = events.stream().sorted().collect(Collectors.toList());

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
     * Returns a list of inactivity periods from the list.
     *
     * Inactivity period is the difference between the time of the Inactive Event and the time of the last Active Event
     * that occured prior to the Inactive Event
     */
    List<InactivePeriod> computeInactivityPeriods(List<NormalizedEventModel> events) {
        return Lists.newArrayList();
    }

}
