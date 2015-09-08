package com.jjdevbros.castellan.reportgenerator.renderer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import com.jjdevbros.castellan.reportgenerator.report.AttendanceReport;
import com.jjdevbros.castellan.reportgenerator.serializer.JsonWriter;

/**
 * Created by lordbritishix on 06/09/15.
 */
public class JsonFileRenderer implements FileRenderer {
    @Override
    public void render(AttendanceReport report, Path path) throws IOException {
        JsonWriter writer = new JsonWriter();
        String json = writer.serialize(report);
        Files.write(path, json.getBytes(StandardCharsets.UTF_8));
    }
}
