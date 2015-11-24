package com.jjdevbros.castellan.console;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.eclipse.birt.core.exception.BirtException;
import org.slf4j.bridge.SLF4JBridgeHandler;
import com.beust.jcommander.IDefaultProvider;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.jjdevbros.castellan.common.guice.AttendanceReportModule;
import com.jjdevbros.castellan.common.model.SessionType;
import com.jjdevbros.castellan.reportgenerator.ReportGenerator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by lordbritishix on 12/09/15.
 */
@Slf4j
public class App {
    private static final IDefaultProvider DEFAULT_PROVIDER = s -> {
        if (s.equals("-m")) {
            return LocalDate.now().withDayOfMonth(1).minus(1L, ChronoUnit.MONTHS).toString();
        }
        else if (s.equals("-d")) {
            return LocalDate.now().minus(1L, ChronoUnit.DAYS).toString();
        }

        return null;
    };

    @Parameters(commandDescription = "Generates a monthly report")
    private static class CommandGenerateMonthlyReport {
        @Parameter(names = "-m", description = "Month from which the report will be generated, in yyyy-MM-dd. "
                + "If not provided, it will generate a report for the last month's data set")
        private String from = "";
    }

    @Parameters(commandDescription = "Generates a daily report")
    private static class CommandGenerateDailyReport {
        @Parameter(names = "-d", description = "Day from which the report will be generated, in yyyy-MM-dd. "
                + "If not provided, it will generate a report for yesterday's data set")
        private String from = "";
    }


    private static class ReportGeneratorParams {
        @Parameter(names = {"-c"},
                description = "Location of the config file", required = true)
        @Getter
        private String configFile;

        @Parameter(names = "-help", description = "Prints the help text", help = true)
        @Getter
        private boolean help;
    }

    public static void main(String[] args)
            throws ExecutionException, InterruptedException, IOException, BirtException {
        ReportGeneratorParams params = new ReportGeneratorParams();
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        Logger.getLogger("global").setLevel(Level.INFO);

        JCommander jc = new JCommander(params);
        jc.setDefaultProvider(DEFAULT_PROVIDER);
        CommandGenerateMonthlyReport monthlyReport = new CommandGenerateMonthlyReport();
        CommandGenerateDailyReport dailyReport = new CommandGenerateDailyReport();

        jc.addCommand("monthly", monthlyReport);
        jc.addCommand("daily", dailyReport);
        jc.parse(args);

        if (params.isHelp()) {
            jc.usage();
            return;
        }

        Injector injector = Guice.createInjector(new AttendanceReportModule(params.getConfigFile()));
        ReportGenerator generator = injector.getInstance(ReportGenerator.class);

        Path path;

        String p = jc.getParsedCommand();

        if (p.equals("monthly")) {
            LocalDate dateParam = LocalDate.parse(monthlyReport.from).withDayOfMonth(1);
            log.info("Generating monthly report for reporting period: {}", dateParam.toString());
            path = generator.generateReport(dateParam, SessionType.MONTHLY);
        }
        else if (p.equals("daily")) {
            LocalDate dateParam = LocalDate.parse(dailyReport.from);
            log.info("Generating daily report for reporting period: {}", dateParam.toString());
            path = generator.generateReport(dateParam, SessionType.DAILY);
        }
        else {
            log.error("Invalid report type provided.");
            jc.usage();
            return;
        }

        if (path != null) {
            log.info("Report is at: {}", path.toString());
        }
        else {
            log.info("Nothing to generate!");
        }
    }
}
