package com.jjdevbros.castellan.common.serializer;

import com.jjdevbros.castellan.common.model.EventModel;
import com.jjdevbros.castellan.common.model.WindowsLogEventId;
import org.elasticsearch.search.SearchHit;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by lordbritishix on 13/09/15.
 */
public class SearchHitToEventModelSerializer {
    private static final DateFormat DF = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    static {
        DF.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public EventModel serialize(SearchHit searchHit) throws ParseException {
        Map<String, Object> hit = searchHit.getSource();

        if (!isValid(hit)) {
            throw new UnsupportedOperationException("Unable to parse - invalid event: "
                        + searchHit.getSourceAsString());
        }

        return EventModel.builder()
                .hostName(hit.get("Hostname").toString())
                .eventId(WindowsLogEventId.fromEventId(Integer.parseInt(hit.get("EventID").toString())))
                .userName(hit.get("TargetUserName").toString())
                .timestamp(
                        DF.parse(hit.get("EventTime").toString()).toInstant().toEpochMilli())
                .build();
    }

    private boolean isValid(Map<String, Object> hit) {
        if (hit.containsKey("EventID")) {
            WindowsLogEventId id = WindowsLogEventId.fromEventId(Integer.parseInt(hit.get("EventID").toString()));

            if (id == null) {
                return false;
            }
        }

        return hit.containsKey("Hostname")
            && hit.containsKey("EventID")
            && hit.containsKey("TargetUserName")
            && hit.containsKey("EventTime");
    }
}
