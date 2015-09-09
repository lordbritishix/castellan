package com.jjdevbros.castellan.reportgenerator.renderer;

import com.jjdevbros.castellan.reportgenerator.report.AttendanceReport;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class ExcelFileRendererTest {
    @Test
    @Ignore
    public void test() throws IOException {
        ExcelFileRenderer renderer = new ExcelFileRenderer();
        Path dir = Paths.get("/home/lordbritishix/tmp");
        renderer.render(AttendanceReport.builder().reportId(UUID.randomUUID()).build(), Files.createTempFile(dir, "report", ".pdf"));
    }
}
