package com.jjdevbros.castellan.reportgenerator.renderer;

import java.io.IOException;
import java.nio.file.Path;
import com.jjdevbros.castellan.reportgenerator.report.AttendanceReport;

/**
 * Created by lordbritishix on 06/09/15.
 */
public interface FileRenderer {
    void render(AttendanceReport report, Path path) throws IOException;
}
