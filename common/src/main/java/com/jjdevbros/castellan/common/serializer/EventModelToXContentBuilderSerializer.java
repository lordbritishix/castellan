package com.jjdevbros.castellan.common.serializer;

import java.io.IOException;
import java.util.Date;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import com.jjdevbros.castellan.common.model.EventModel;
import com.jjdevbros.castellan.common.utils.Constants;

/**
 * Created by jim.quitevis on 23/11/15.
 */
public class EventModelToXContentBuilderSerializer {
    public XContentBuilder serialize(EventModel eventModel) throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder().startObject()
                .field("Hostname", eventModel.getHostName())
                .field("EventID", String.valueOf(eventModel.getEventId().getCode()))
                .field("UserID", eventModel.getUserName())
                .field("EventTime", Constants.ES_EVENT_DATE_FORMATTER.format(
                        new Date(eventModel.getTimestamp())))
                .endObject();

        return builder;
    }
}
