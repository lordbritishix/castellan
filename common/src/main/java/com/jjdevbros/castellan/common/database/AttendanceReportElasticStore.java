package com.jjdevbros.castellan.common.database;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.jjdevbros.castellan.common.elasticclient.ElasticClient;
import com.jjdevbros.castellan.common.model.EventModel;
import com.jjdevbros.castellan.common.model.SessionPeriod;
import com.jjdevbros.castellan.common.serializer.SearchHitToEventModelSerializer;
import com.jjdevbros.castellan.common.utils.SessionPeriodIndexSpliterator;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Created by lordbritishix on 12/09/15.
 */
@Slf4j
public class AttendanceReportElasticStore implements AttendanceReportStore {
    private static final int PAGE_SIZE = 100;
    private static final int PAGE_TIMEOUT = 60000;

    private ElasticClient elasticClient;

    @Inject
    public AttendanceReportElasticStore(ElasticClient elasticClient) {
        this.elasticClient = elasticClient;
    }

    @Override
    public List<EventModel> getEvents(SessionPeriod period) throws ExecutionException, InterruptedException {
        SessionPeriodIndexSpliterator indexSpliterator = new SessionPeriodIndexSpliterator();
        List<String> indices = indexSpliterator.splitDaily(period);

        String[] validIndices = indices.stream().filter(i ->
                                    elasticClient.getClient().admin().indices().exists(
                                            new IndicesExistsRequest(i)).actionGet().isExists()).toArray(String[]::new);

        SearchResponse response = elasticClient.getClient()
                                    .prepareSearch(validIndices)
                                    .setSearchType(SearchType.SCAN)
                                    .setScroll(new TimeValue(PAGE_SIZE))
                                    .setSize(PAGE_SIZE)
                                    .setQuery(QueryBuilders.termsQuery(
                                            "EventID",
                                            "4648",
                                            "4647",
                                            "4800",
                                            "4801",
                                            "4802",
                                            "4803",
                                            "1074"))
                                    .execute()
                                    .actionGet();

        List<EventModel> events = Lists.newArrayList();
        final SearchHitToEventModelSerializer serializer = new SearchHitToEventModelSerializer();

        log.debug("Fetching records");

        while (true) {
            List<SearchHit> hits = Arrays.asList(response.getHits().getHits());

            events.addAll(hits.stream().map(hit -> {
                try {
                    log.trace(hit.getSourceAsString());
                    return serializer.serialize(hit);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList()));

            response = elasticClient.getClient().prepareSearchScroll(
                    response.getScrollId()).setScroll(new TimeValue(PAGE_TIMEOUT)).execute().actionGet();

            int hitsLength = response.getHits().getHits().length;

            if (hitsLength <= 0) {
                log.debug("No more records found for this session");
                break;
            }

            log.debug("Found records for this page: {}", hitsLength);
        }

        log.debug("Done fetching records");

        return  events;
    }
}

