package com.jjdevbros.castellan.reportgenerator.renderer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.*;

public class ExcelFileRendererTest {
    @Test
    @Ignore
    public void test() throws IOException {
        ExcelFileRenderer renderer = new ExcelFileRenderer();
        Path dir = Paths.get("/home/jim.quitevis");
        renderer.render(null, Files.createTempFile(dir, "report_", ".xlsx"));
    }
}
