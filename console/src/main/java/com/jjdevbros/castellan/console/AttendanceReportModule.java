package com.jjdevbros.castellan.console;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.name.Names;
import com.jjdevbros.castellan.common.database.AttendanceReportElasticStore;
import com.jjdevbros.castellan.common.database.AttendanceReportStore;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

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
            log.error("Unable to read the properties file");
            throw new RuntimeException(e);
        }

    }

    private void bindSettings(Binder binder) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(configFile))) {
            Properties prop = new Properties();
            prop.load(reader);
            binder.bind(String.class)
                    .annotatedWith(Names.named("elastic.hostname"))
                    .toInstance(prop.getProperty("elastic.hostname"));
            binder.bind(Integer.class)
                    .annotatedWith(Names.named("elastic.port"))
                    .toInstance(Integer.parseInt(prop.getProperty("elastic.port")));
            binder.bind(String.class)
                    .annotatedWith(Names.named("elastic.clustername"))
                    .toInstance(prop.getProperty("elastic.clustername"));
        }
    }
}
