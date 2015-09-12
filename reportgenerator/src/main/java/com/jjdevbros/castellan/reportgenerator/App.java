package com.jjdevbros.castellan.reportgenerator;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.jjdevbros.castellan.common.SessionType;
import com.jjdevbros.castellan.reportgenerator.renderer.ExcelFileRenderer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Optional;

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

        @Parameter(names = {"-d"},
                    description = "Date of the reporting period. "
                                    + "Provide as yyyy-mm-dd. If ommitted, gets the day today")
        @Getter
        private String reportDate;

        @Parameter(names = "--help", help = true)
        @Getter
        private boolean help;
    }

    public static void main(String[] args) {
        ReportGeneratorParams params = new ReportGeneratorParams();

        new JCommander(params, args);

        if (params.isHelp()) {
            log.info("-t Type of report to generate. Provide as daily or monthly");
            log.info("-d Date of the reporting period. Provide as yyyy-mm-dd. If ommitted, gets the day today");
            return;
        }

        ExcelFileRenderer renderer = new ExcelFileRenderer();
        ReportGenerator generator = new ReportGenerator();

        LocalDate dateParam = Optional.ofNullable(
                params.getReportDate()).map(d -> LocalDate.parse(d)).orElse(LocalDate.now());

        if (params.getReportType().equals("daily")) {
            generator.generateReport(dateParam, SessionType.DAILY);
        }
        else if (params.getReportType().equals("monthly")) {
            generator.generateReport(dateParam, SessionType.MONTHLY);
        }
        else {
            log.error("Invalid report type provided. Provide as daily or monthly.");
        }

    }

}
