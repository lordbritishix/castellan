package com.jjdevbros.castellan.reportgenerator.renderer;

import com.google.common.collect.Lists;
import com.jjdevbros.castellan.reportgenerator.report.AttendanceReport;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EXCELRenderOption;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;

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

/**
 * Created by lordbritishix on 06/09/15.
 */
@Slf4j
public class ExcelFileRenderer implements FileRenderer {
    @Override
    public void render(AttendanceReport report, Path path) throws IOException {
        log.info("Generating report: " + path.getFileName().toString());
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("birt-reports/attendance_daily.rptdesign").getFile());
        List<?> errors = Lists.newArrayList();
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             InputStream in = new FileInputStream(file)) {
                EngineConfig config = new EngineConfig();
                Platform.startup();
                IReportEngineFactory factory = (IReportEngineFactory) Platform.createFactoryObject(
                        IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
                IReportEngine engine = factory.createReportEngine(config);

                IReportRunnable design = engine.openReportDesign(in);
                IRunAndRenderTask runAndRenderTask = engine.createRunAndRenderTask(design);
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

                if (errors.isEmpty()) {
                    try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(path))) {
                        byte[] data = baos.toByteArray();
                        out.write(data, 0, data.length);
                    }
                }

                log.info("Generating report complete with duration: " + runDuration.toString());
        } catch (EngineException e) {
            e.printStackTrace();
        } catch (BirtException e) {
            e.printStackTrace();
        }

    }
}
