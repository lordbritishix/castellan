package com.jjdevbros.castellan.common.database;

import com.jjdevbros.castellan.common.model.EventModel;
import com.jjdevbros.castellan.common.model.SessionPeriod;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by lordbritishix on 12/09/15.
 */
public interface AttendanceReportStore {
    List<EventModel> getEvents(SessionPeriod period) throws ExecutionException, InterruptedException;
}
