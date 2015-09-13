package com.jjdevbros.castellan.common.elasticclient;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import lombok.Data;

/**
 * Created by lordbritishix on 12/09/15.
 */
@Data
public class ElasticConfig {
    private String hostName;
    private int port;
    private String clusterName;

    @Inject
    public ElasticConfig(@Named("elastic.hostname") String hostName,
                         @Named("elastic.port") int port,
                         @Named("elastic.clustername") String clusterName) {
        this.hostName = hostName;
        this.port = port;
        this.clusterName = clusterName;
    }
}
