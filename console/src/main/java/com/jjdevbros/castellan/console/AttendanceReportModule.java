package com.jjdevbros.castellan.console;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.name.Names;
import com.jjdevbros.castellan.common.database.AttendanceReportElasticStore;
import com.jjdevbros.castellan.common.database.AttendanceReportStore;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by lordbritishix on 12/09/15.
 */
@Slf4j
public class AttendanceReportModule implements Module {
    private String configFile;

    public AttendanceReportModule(String configFile) {
        this.configFile = configFile;
    }

    @Override
    public void configure(Binder binder) {
        binder.bind(AttendanceReportStore.class).to(AttendanceReportElasticStore.class);
        try {
            bindSettings(binder);
        } catch (IOException e) {
            log.error("Unable to read the config file");
            throw new RuntimeException(e);
        }

    }

    private void bindSettings(Binder binder) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode lookup = mapper.readTree(Paths.get(configFile).toFile());
        binder.bind(String.class)
                .annotatedWith(Names.named("elastic.hostname"))
                .toInstance(lookup.get("elastic.hostname").asText());
        binder.bind(Integer.class)
                .annotatedWith(Names.named("elastic.port"))
                .toInstance(lookup.get("elastic.port").asInt());
        binder.bind(String.class)
                .annotatedWith(Names.named("elastic.clustername"))
                .toInstance(lookup.get("elastic.clustername").asText());
        binder.bind(JsonNode.class)
                .annotatedWith(Names.named("lookup.json"))
                .toInstance(lookup.get("groups"));

    }
}
