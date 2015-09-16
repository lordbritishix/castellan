package com.jjdevbros.castellan.console;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.jjdevbros.castellan.common.model.SessionType;
import com.jjdevbros.castellan.reportgenerator.ReportGenerator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.birt.core.exception.BirtException;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;


/**
 * Created by lordbritishix on 12/09/15.
 */
@Slf4j
public class App {
    private static class ReportGeneratorParams {
        @Parameter(names = {"-t"},
                description = "Type of report to generate. Provide as daily or monthly", required = true)
        @Getter
        private String reportType;

        @Parameter(names = {"-c"},
                description = "Location of the config file.", required = true)
        @Getter
        private String configFile;

        @Parameter(names = {"-d"},
                description = "Date of the reporting period. "
                        + "Provide as yyyy-mm-dd. If ommitted, gets the day today")
        @Getter
        private String reportDate;

        @Parameter(names = "--help", help = true)
        @Getter
        private boolean help;
    }

    public static void main(String[] args)
            throws ExecutionException, InterruptedException, IOException, BirtException {
        ReportGeneratorParams params = new ReportGeneratorParams();
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        Logger.getLogger("global").setLevel(Level.FINEST);

        new JCommander(params, args);

        if (params.isHelp()) {
            System.out.println(
                    "-t Type of report to generate. Provide as daily or monthly. Required.");
            System.out.println(
                    "-c Location of the config file. Required.");
            System.out.println(
                    "-d Date of the reporting period. Provide as yyyy-mm-dd. If ommitted, gets the day today");
            return;
        }

        Injector injector = Guice.createInjector(new AttendanceReportModule(params.getConfigFile()));

        ReportGenerator generator = injector.getInstance(ReportGenerator.class);

        LocalDate dateParam = Optional.ofNullable(
                params.getReportDate()).map(d -> LocalDate.parse(d)).orElse(LocalDate.now());

        Instant now = Instant.now();
        Path path = null;
        if (params.getReportType().equals("daily")) {
            System.out.println("Generating report...");
            path = generator.generateReport(dateParam, SessionType.DAILY);
            System.out.println("Report generated! Elapsed time: "
                    + Duration.between(now, Instant.now()).toMillis() + " ms");
        }
        else if (params.getReportType().equals("monthly")) {
            System.out.println("Generating report...");
            path = generator.generateReport(dateParam, SessionType.MONTHLY);
            System.out.println("Report generated! Elapsed time: "
                    + Duration.between(now, Instant.now()).toMillis() + " ms");
        }
        else {
            log.error("Invalid report type provided. Provide as daily or monthly.");
        }

        if (path != null) {
            System.out.println("Report is at: " + path.toString());
        }
        else {
            System.out.println("Nothing to generate!");
        }
    }

}
