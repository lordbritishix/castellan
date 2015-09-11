package com.jjdevbros.castellan.reportgenerator.renderer;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EXCELRenderOption;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import com.google.common.collect.Lists;
import com.jjdevbros.castellan.reportgenerator.report.AttendanceReport;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by lordbritishix on 06/09/15.
 */
@Slf4j
public class ExcelFileRenderer {
    public void write(AttendanceReport report, Path outputPath) throws IOException, BirtException {
        log.info("Generating report: " + outputPath.toString());
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("birt-reports/attendance_monthly.rptdesign").getFile());
        List<?> errors = Lists.newArrayList();

        byte[] data = null;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             InputStream in = new FileInputStream(file)) {

            JsonFileRenderer jsonFileRenderer = new JsonFileRenderer();
            Path jsonOutput = Files.createTempFile("attendanceReport", ".json");
            jsonFileRenderer.write(report, jsonOutput);

            log.info("Reading data source from: " + jsonOutput);

            EngineConfig config = new EngineConfig();
            Platform.startup();
            IReportEngineFactory factory = (IReportEngineFactory) Platform.createFactoryObject(
                    IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
            IReportEngine engine = factory.createReportEngine(config);

            IReportRunnable design = engine.openReportDesign(in);
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

            log.info("Generating report complete with duration: " + runDuration.toString());
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
    }
}
