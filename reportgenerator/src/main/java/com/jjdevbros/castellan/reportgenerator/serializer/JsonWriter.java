package com.jjdevbros.castellan.reportgenerator.serializer;

import com.jjdevbros.castellan.reportgenerator.report.AttendanceReport;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

/**
 * Created by lordbritishix on 06/09/15.
 *
 * Serializes a class to json
 */
public class JsonWriter {
    public String serialize(AttendanceReport report) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(report);
    }
}
