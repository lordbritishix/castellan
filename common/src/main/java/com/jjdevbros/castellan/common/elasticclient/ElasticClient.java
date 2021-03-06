package com.jjdevbros.castellan.common.elasticclient;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

/**
 * Created by lordbritishix on 12/09/15.
 */
@Slf4j
public class ElasticClient {
    private Client client;
    private String hostName;
    private int port;
    private String clusterName;

    @Inject
    public ElasticClient(ElasticConfig config) {
        this.hostName = config.getHostName();
        this.port = config.getPort();
        this.clusterName = config.getClusterName();
        Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", clusterName).build();

        log.info("Connecting to elastic using config: {}", config.toString());

        client = new TransportClient(settings)
                        .addTransportAddress(new InetSocketTransportAddress(this.hostName, this.port));
    }

    public Client getClient() {
        return client;
    }

    public void close() {
        client.close();
    }
}

