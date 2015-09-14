package com.jjdevbros.castellan.reportgenerator;

import com.google.inject.Inject;
import com.jjdevbros.castellan.common.database.AttendanceReportStore;
import com.jjdevbros.castellan.common.model.SessionPeriod;
import com.jjdevbros.castellan.common.model.SessionType;
import com.jjdevbros.castellan.common.specification.DailyReportSpecification;
import com.jjdevbros.castellan.common.specification.MonthlyReportSpecification;
import com.jjdevbros.castellan.common.specification.ReportSpecification;
import com.jjdevbros.castellan.reportgenerator.generator.AttendanceReportGenerator;
import com.jjdevbros.castellan.reportgenerator.renderer.ExcelFileRenderer;
import com.jjdevbros.castellan.reportgenerator.report.AttendanceReport;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.birt.core.exception.BirtException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private ExcelFileRenderer renderer;

    @Inject
    public ReportGenerator(AttendanceReportStore reportStore, ExcelFileRenderer renderer) {
        this.reportStore = reportStore;
        this.renderer = renderer;
    }

    public boolean generateReport(LocalDate period, SessionType sessionType)
            throws ExecutionException, InterruptedException, IOException, BirtException {

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

    private void generateMonthlyReport(LocalDate period)
            throws ExecutionException, InterruptedException, IOException, BirtException {
        LocalDate start = LocalDate.of(period.getYear(), period.getMonthValue(), 1);
        LocalDate end = start.plusMonths(1L).minusDays(1L);
        SessionPeriod sessionPeriod = new SessionPeriod(start, end);

        generateReport(sessionPeriod, new MonthlyReportSpecification());
    }

    private void generateDailyReport(LocalDate period)
            throws ExecutionException, InterruptedException, IOException, BirtException {
        SessionPeriod sessionPeriod = new SessionPeriod(period, period);

        generateReport(sessionPeriod, new DailyReportSpecification());
    }

    private void generateReport(SessionPeriod sessionPeriod, ReportSpecification specification)
            throws ExecutionException, InterruptedException, IOException, BirtException {
        Instant now = Instant.now();
        AttendanceReportGenerator generator = new AttendanceReportGenerator();

        log.info("Generating report for the period: " + sessionPeriod.toString());

        AttendanceReport report = generator.generateAttendanceReport(
                reportStore.getEvents(sessionPeriod), sessionPeriod);

        Path tempFile = Files.createTempFile(
                            Paths.get(System.getProperty("user.home")),
                            specification.getFileNamePrefix() + "_", ".pdf");
        boolean ret = renderer.write(report, tempFile, specification);

        if (!ret) {
            Files.delete(tempFile);
            log.info("Nothing to generate! Elapsed time: {} ms", Duration.between(now, Instant.now()).toMillis());
        }
        else {
            log.info("Report generated! Elapsed time: {} ms", Duration.between(now, Instant.now()).toMillis());
        }
    }

}
