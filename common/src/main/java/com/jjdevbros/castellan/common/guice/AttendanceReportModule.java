package com.jjdevbros.castellan.common.guice;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Set;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import com.google.common.collect.Sets;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.jjdevbros.castellan.common.database.AttendanceReportElasticStore;
import com.jjdevbros.castellan.common.database.AttendanceReportStore;
import lombok.extern.slf4j.Slf4j;

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

        log.debug("Dumping settings: {}", lookup.toString());

        binder.bindConstant().annotatedWith(
                Names.named("elastic.hostname")).to(lookup.get("elastic.hostname").asText());
        binder.bindConstant().annotatedWith(
                Names.named("elastic.port")).to(lookup.get("elastic.port").asInt());
        binder.bindConstant().annotatedWith(
                Names.named("elastic.clustername")).to(lookup.get("elastic.clustername").asText());
        binder.bindConstant().annotatedWith(
                Names.named("reporting.path")).to(lookup.get("reporting.path").asText());
        binder.bindConstant().annotatedWith(
                Names.named("inactive.threshold")).to(lookup.get("inactive.threshold").asLong());
        binder.bind(JsonNode.class)
                .annotatedWith(Names.named("lookup.list"))
                .toInstance(lookup.get("groups"));

        Set<String> excludeList = Sets.newHashSet();
        lookup.get("exclude").forEach(p -> {
            excludeList.add(p.asText());
        });

        binder.bind(new TypeLiteral<Set<String>>() { })
                .annotatedWith(Names.named("exclude.list"))
                .toInstance(excludeList);

    }
}
