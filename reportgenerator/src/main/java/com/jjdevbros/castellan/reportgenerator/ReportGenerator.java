package com.jjdevbros.castellan.reportgenerator;

import com.jjdevbros.castellan.common.SessionPeriod;
import com.jjdevbros.castellan.common.SessionType;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

/**
 * Created by lordbritishix on 12/09/15.
 */
@Slf4j
public class ReportGenerator {
    public boolean generateReport(LocalDate period, SessionType sessionType) {

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

    private void generateMonthlyReport(LocalDate period) {
        LocalDate start = LocalDate.of(period.getYear(), period.getMonthValue(), 1);
        LocalDate end = start.plusMonths(1L).minusDays(1L);
        SessionPeriod sessionPeriod = new SessionPeriod(start, end);

        //Todo: Hookup ES, feed to AttendanceReport, then pass to ExcelFileRenderer

        log.info("Generating report for the period: " + sessionPeriod.toString());
    }

    private void generateDailyReport(LocalDate period) {
        SessionPeriod sessionPeriod = new SessionPeriod(period, period);

        //Todo: Hookup ES, feed to AttendanceReport, then pass to ExcelFileRenderer

        log.info("Generating report for the period: " + sessionPeriod.toString());
    }

}
