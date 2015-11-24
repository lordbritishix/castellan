package com.jjdevbros.castellan.reportgenerator.renderer;

import com.google.common.collect.Lists;
import com.jjdevbros.castellan.common.specification.ReportSpecification;
import com.jjdevbros.castellan.reportgenerator.report.AttendanceReport;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EXCELRenderOption;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by lordbritishix on 06/09/15.
 */
@Slf4j
public class ExcelFileRenderer {
    private boolean hasDataToReport(AttendanceReport report) {
        return report.getUserReports().keySet().stream()
                    .filter(k -> report.getPeriod().isInSession(
                            k.getStartTime().toInstant(ZoneOffset.UTC).toEpochMilli())).count() > 0;
    }

    public boolean write(AttendanceReport report, Path outputPath, ReportSpecification specification)
            throws IOException, BirtException {
        if (!hasDataToReport(report)) {
            return false;
        }

        log.debug("Generating report: " + outputPath.toString());
        List<?> errors = Lists.newArrayList();

        byte[] data = null;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             InputStream is = ClassLoader.getSystemResourceAsStream(specification.getReportTemplateName())) {

            JsonFileRenderer jsonFileRenderer = new JsonFileRenderer();
            Path jsonOutput = Files.createTempFile(specification.getFileNamePrefix() + "_", ".json");
            jsonFileRenderer.write(report, jsonOutput);

            log.debug("Reading data source from: " + jsonOutput);

            EngineConfig config = new EngineConfig();
            config.setLogConfig(null, Level.WARNING);
            Platform.startup(config);
            IReportEngineFactory factory = (IReportEngineFactory) Platform.createFactoryObject(
                    IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
            IReportEngine engine = factory.createReportEngine(config);
            IReportRunnable design = engine.openReportDesign(is);
            IRunAndRenderTask runAndRenderTask = engine.createRunAndRenderTask(design);
            runAndRenderTask.setParameterValue("dataSource", jsonOutput.toString());

            EXCELRenderOption options = new EXCELRenderOption();
            options.setOutputFormat("pdf");
            options.setOutputStream(baos);
            runAndRenderTask.setRenderOption(options);

            Instant start = Instant.now();
            runAndRenderTask.run();
            Duration runDuration = Duration.between(start, Instant.now());

            errors = runAndRenderTask.getErrors();
            errors.stream().forEach(e -> {
                log.error("An error was encountered while generating the report: {}", e.toString());
            });

            runAndRenderTask.close();
            Platform.shutdown();

            data = baos.toByteArray();

            log.debug("Generating report complete with duration: " + runDuration.toString());
        } catch (BirtException e) {
            log.error("An error was encountered while generating the report", e);
            throw e;
        } finally {
            if ((errors.isEmpty()) && (data != null)) {
                try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(outputPath))) {
                    out.write(data, 0, data.length);
                }
            }
        }

        return  true;
    }
}
