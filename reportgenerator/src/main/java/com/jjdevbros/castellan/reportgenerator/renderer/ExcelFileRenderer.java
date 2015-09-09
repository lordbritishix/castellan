package com.jjdevbros.castellan.reportgenerator.renderer;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EXCELRenderOption;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import com.jjdevbros.castellan.reportgenerator.report.AttendanceReport;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by lordbritishix on 06/09/15.
 */
@Slf4j
public class ExcelFileRenderer implements FileRenderer {
    @Override
    public void render(AttendanceReport report, Path path) throws IOException {
        log.info("Generating report: " + path.getFileName().toString());

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (InputStream in = Files.newInputStream(
                    Paths.get("/home/jim.quitevis/src/freelancer_castellan/reportgenerator/src/main/resources/"
                            + "birt-reports/attendance_monthly.rptdesign"))) {
                EngineConfig config = new EngineConfig();
                Platform.startup();
                IReportEngineFactory factory = (IReportEngineFactory) Platform.createFactoryObject(
                        IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
                IReportEngine engine = factory.createReportEngine(config);

                IReportRunnable design = engine.openReportDesign(in);
                IRunAndRenderTask runAndRenderTask = engine.createRunAndRenderTask(design);
                runAndRenderTask.setParameterValue("reportSource", "sample_monthly.json");
                EXCELRenderOption options = new EXCELRenderOption();
                options.setOutputFormat("xls");
                options.setOutputStream(baos);
                runAndRenderTask.setRenderOption(options);

                Instant start = Instant.now();
                runAndRenderTask.run();
                Duration runDuration = Duration.between(start, Instant.now());

                List<?> errors = runAndRenderTask.getErrors();
                errors.stream().forEach(e -> {
                    log.error("An error was encountered while generating the report: {}", e.toString());
                });

                runAndRenderTask.close();

                log.info("Generating report complete with duration: " + runDuration.toString());
            } catch (EngineException e) {
                throw new RuntimeException(e);
            } catch (BirtException e) {
                throw new RuntimeException(e);
            }

            try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(path))) {
                byte[] data = baos.toByteArray();
                out.write(data, 0, data.length);
            }


        }

    }
}
