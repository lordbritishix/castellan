package com.jjdevbros.castellan.injector;

import java.io.IOException;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.elasticsearch.action.index.IndexResponse;
import com.google.inject.Inject;
import com.jjdevbros.castellan.common.elasticclient.ElasticClient;
import com.jjdevbros.castellan.common.model.EventModel;
import com.jjdevbros.castellan.common.model.WindowsLogEventId;
import com.jjdevbros.castellan.common.serializer.EventModelToXContentBuilderSerializer;
import com.jjdevbros.castellan.common.utils.Constants;
import com.jjdevbros.castellan.common.utils.Utils;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by jim.quitevis on 23/11/15.
 */
@Slf4j
public class EventWriter {
    private final ElasticClient client;
    private final EventModelToXContentBuilderSerializer serializer;

    @Inject
    public EventWriter(ElasticClient client) {
        this.client = client;
        this.serializer = new EventModelToXContentBuilderSerializer();
    }

    public IndexResponse writeEvent(String username, WindowsLogEventId eventId, Optional<LocalDateTime> time)
            throws IOException {
        LocalDateTime timeToUse = time.orElse(LocalDateTime.now());
        String index = Utils.indexForDate(timeToUse.toLocalDate());
        EventModel model =
                EventModel.builder()
                .eventId(eventId)
                .userName(username)
                .hostName(InetAddress.getLocalHost().getHostName())
                .timestamp(timeToUse.toInstant(ZoneOffset.UTC).toEpochMilli())
                .build();

        log.debug("Injecting: {}", model.toString());

        return client.getClient()
                .prepareIndex(index, Constants.ES_TYPE)
                .setSource(serializer.serialize(model))
                .execute()
                .actionGet();

    }
}
