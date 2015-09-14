package com.jjdevbros.castellan.common.database;

import com.google.inject.Inject;
import com.jjdevbros.castellan.common.elasticclient.ElasticClient;
import com.jjdevbros.castellan.common.model.EventModel;
import com.jjdevbros.castellan.common.serializer.SearchHitToEventModelSerializer;
import com.jjdevbros.castellan.common.utils.Utils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Created by lordbritishix on 12/09/15.
 */
public class AttendanceReportElasticStore implements AttendanceReportStore {
    //index name is nxlog-[YYYYmmdd]
    private static final String TYPE = "eventlog";

    private ElasticClient elasticClient;

    @Inject
    public AttendanceReportElasticStore(ElasticClient elasticClient) {
        this.elasticClient = elasticClient;
    }

    @Override
    public List<EventModel> getEvents(LocalDate date) throws ExecutionException, InterruptedException {
        SearchResponse response = elasticClient.getClient()
                                    .prepareSearch(Utils.indexForDate(date))
                                    .setTypes(TYPE)
                                    .setQuery(QueryBuilders.termsQuery(
                                                "EventID",
                                                    "4648",
                                                    "4647",
                                                    "4800",
                                                    "4801",
                                                    "4802",
                                                    "4803"))
                                    .execute()
                                    .actionGet();

        List<SearchHit> hits = Arrays.asList(response.getHits().getHits());

        SearchHitToEventModelSerializer serializer = new SearchHitToEventModelSerializer();

        return hits.stream().map(h -> {
            try {
                return serializer.serialize(h);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }
}

