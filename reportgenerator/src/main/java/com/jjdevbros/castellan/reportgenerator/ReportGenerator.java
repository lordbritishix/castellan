package com.jjdevbros.castellan.reportgenerator;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
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
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.concurrent.ExecutionException;

/**
 * Created by lordbritishix on 12/09/15.
 */
@Slf4j
@Singleton
public class ReportGenerator {
    private final AttendanceReportStore reportStore;
    private final ExcelFileRenderer renderer;
    private final AttendanceReportGenerator generator;
    private final String reportingPath;

    @Inject
    public ReportGenerator(AttendanceReportStore reportStore, ExcelFileRenderer renderer,
                           AttendanceReportGenerator generator, @Named("reporting.path") String reportingPath) {
        this.reportStore = reportStore;
        this.renderer = renderer;
        this.generator = generator;
        this.reportingPath = reportingPath;
    }

    public Path generateReport(LocalDate period, SessionType sessionType)
            throws ExecutionException, InterruptedException, IOException, BirtException {

        switch(sessionType) {
            case DAILY:
                return generateDailyReport(period, Paths.get(reportingPath));

            case MONTHLY:
                return generateMonthlyReport(period, Paths.get(reportingPath));
        }

        return null;
    }

    private Path generateMonthlyReport(LocalDate period, Path path)
            throws ExecutionException, InterruptedException, IOException, BirtException {
        LocalDate start = LocalDate.of(period.getYear(), period.getMonthValue(), 1);
        LocalDate end = start.plusMonths(1L).minusDays(1L);
        SessionPeriod sessionPeriod = new SessionPeriod(start, end);

        return generateReport(sessionPeriod, new MonthlyReportSpecification(), path);
    }

    private Path generateDailyReport(LocalDate period, Path path)
            throws ExecutionException, InterruptedException, IOException, BirtException {
        SessionPeriod sessionPeriod = new SessionPeriod(period, period);

        return generateReport(sessionPeriod, new DailyReportSpecification(), path);
    }

    private Path generateReport(SessionPeriod sessionPeriod, ReportSpecification specification, Path path)
            throws ExecutionException, InterruptedException, IOException, BirtException {
        Instant now = Instant.now();
        AttendanceReport report = generator.generateAttendanceReport(
                reportStore.getEvents(sessionPeriod), sessionPeriod);

        DateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");

        String name = String.format("%s_%s.pdf", specification.getFileNamePrefix(),
                            formatter.format(Date.from(Instant.now())));

        Path tempFile = Files.createFile(Paths.get(path.toString(), name));

        log.info("Generating report type: {} for report id: {} for the period: {} to: {}",
                specification.getFileNamePrefix(), report.getReportId().toString(),
                sessionPeriod.toString(), tempFile.toString());

        boolean ret = renderer.write(report, tempFile, specification);

        if (!ret) {
            Files.delete(tempFile);
            tempFile = null;
            log.info("Nothing to generate! Elapsed time: {} ms", Duration.between(now, Instant.now()).toMillis());
        }
        else {
            log.info("Report generated! Elapsed time: {} ms", Duration.between(now, Instant.now()).toMillis());
        }

        return tempFile;
    }

}
