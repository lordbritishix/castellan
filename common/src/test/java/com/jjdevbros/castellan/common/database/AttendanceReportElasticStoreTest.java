package com.jjdevbros.castellan.common.database;

import com.jjdevbros.castellan.common.elasticclient.ElasticClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by lordbritishix on 13/09/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class AttendanceReportElasticStoreTest {
    private AttendanceReportElasticStore fixture;

    @Mock
    ElasticClient elasticClient;

    @Before
    public void AttendanceReportElasticStoreTest() {
        fixture = new AttendanceReportElasticStore(elasticClient);
    }

    @Test
    public void testIndexForDateProducesCorrectIndex() {
        assertThat(fixture.indexForDate(LocalDate.of(2015, 9, 13)), is("nxlog-20150913"));
        assertThat(fixture.indexForDate(LocalDate.of(2015, 1, 1)), is("nxlog-20150101"));
    }
}
