package com.jjdevbros.castellan.reportgenerator;

import com.google.inject.Inject;
import com.jjdevbros.castellan.common.database.AttendanceReportStore;
import com.jjdevbros.castellan.common.model.SessionPeriod;
import com.jjdevbros.castellan.common.model.SessionType;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.concurrent.ExecutionException;

/**
 * Created by lordbritishix on 12/09/15.
 */
@Slf4j
public class ReportGenerator {
    private AttendanceReportStore reportStore;

    @Inject
    public ReportGenerator(AttendanceReportStore reportStore) {
        this.reportStore = reportStore;
    }

    public boolean generateReport(LocalDate period, SessionType sessionType)
                        throws ExecutionException, InterruptedException {

        switch(sessionType) {
            case DAILY:
                generateDailyReport(period);
                break;

            case MONTHLY:
                generateMonthlyReport(period);
                break;
        }

        return true;
    }

    private void generateMonthlyReport(LocalDate period) throws ExecutionException, InterruptedException {
        LocalDate start = LocalDate.of(period.getYear(), period.getMonthValue(), 1);
        LocalDate end = start.plusMonths(1L).minusDays(1L);
        SessionPeriod sessionPeriod = new SessionPeriod(start, end);

        generateReport(sessionPeriod);
    }

    private void generateDailyReport(LocalDate period) throws ExecutionException, InterruptedException {
        SessionPeriod sessionPeriod = new SessionPeriod(period, period);

        generateReport(sessionPeriod);
    }

    private void generateReport(SessionPeriod sessionPeriod) throws ExecutionException, InterruptedException {
        Instant now = Instant.now();
        log.info("Generating report for the period: " + sessionPeriod.toString());
        //Todo: Hookup ES, feed to AttendanceReport, then pass to ExcelFileRenderer

        reportStore.getEvents(sessionPeriod.getStartTime().toLocalDate());

        log.info("Report generated! Elapsed time: {} ms", Duration.between(now, Instant.now()).toMillis());
    }

}
