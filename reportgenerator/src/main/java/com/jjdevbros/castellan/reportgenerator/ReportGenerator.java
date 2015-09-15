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
import java.time.LocalDateTime;
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

    public Path generateReport(LocalDate period, SessionType sessionType)
            throws ExecutionException, InterruptedException, IOException, BirtException {

        switch(sessionType) {
            case DAILY:
                return generateDailyReport(period);

            case MONTHLY:
                return generateMonthlyReport(period);
        }

        return null;
    }

    private Path generateMonthlyReport(LocalDate period)
            throws ExecutionException, InterruptedException, IOException, BirtException {
        LocalDate start = LocalDate.of(period.getYear(), period.getMonthValue(), 1);
        LocalDate end = start.plusMonths(1L).minusDays(1L);
        SessionPeriod sessionPeriod = new SessionPeriod(start, end);

        return generateReport(sessionPeriod, new MonthlyReportSpecification());
    }

    private Path generateDailyReport(LocalDate period)
            throws ExecutionException, InterruptedException, IOException, BirtException {
        SessionPeriod sessionPeriod = new SessionPeriod(period, period);

        return generateReport(sessionPeriod, new DailyReportSpecification());
    }

    private Path generateReport(SessionPeriod sessionPeriod, ReportSpecification specification)
            throws ExecutionException, InterruptedException, IOException, BirtException {
        Instant now = Instant.now();
        AttendanceReportGenerator generator = new AttendanceReportGenerator();

        AttendanceReport report = generator.generateAttendanceReport(
                reportStore.getEvents(sessionPeriod), sessionPeriod);

        log.info("Generating report type: {} for report id: {} for the period: {}",
                specification.getFileNamePrefix(), report.getReportId().toString(), sessionPeriod.toString());

        String name = String.format("%s_%s.pdf", specification.getFileNamePrefix(), LocalDateTime.now().toString());
        Path tempFile = Files.createFile(Paths.get(System.getProperty("user.home") + "/" + name));
        boolean ret = renderer.write(report, tempFile, specification);

        if (!ret) {
            Files.delete(tempFile);
            log.info("Nothing to generate! Elapsed time: {} ms", Duration.between(now, Instant.now()).toMillis());
        }
        else {
            log.info("Report generated! Elapsed time: {} ms", Duration.between(now, Instant.now()).toMillis());
        }

        return tempFile;
    }

}
