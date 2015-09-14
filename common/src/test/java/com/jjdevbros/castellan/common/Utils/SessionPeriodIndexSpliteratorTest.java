package com.jjdevbros.castellan.common.Utils;

import com.jjdevbros.castellan.common.model.SessionPeriod;
import com.jjdevbros.castellan.common.utils.SessionPeriodIndexSpliterator;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by lordbritishix on 13/09/15.
 */
public class SessionPeriodIndexSpliteratorTest {
    private SessionPeriodIndexSpliterator fixture;

    @Before
    public void setup() {
        fixture = new SessionPeriodIndexSpliterator();
    }

    @Test
    public void testSpliteratorSplitsIntoCorrectIndexes1() {
        SessionPeriod period = new SessionPeriod(LocalDate.of(2015, 9, 13), LocalDate.of(2015, 9, 15));
        List<String> indexes = fixture.splitDaily(period);

        assertThat(indexes.size(), is(3));
        assertThat(indexes.get(0), is("nxlog-20150913"));
        assertThat(indexes.get(1), is("nxlog-20150914"));
        assertThat(indexes.get(2), is("nxlog-20150915"));
    }

    @Test
    public void testSpliteratorSplitsIntoCorrectIndexes2() {
        SessionPeriod period = new SessionPeriod(LocalDate.of(2015, 9, 13), LocalDate.of(2015, 9, 13));
        List<String> indexes = fixture.splitDaily(period);

        assertThat(indexes.size(), is(1));
        assertThat(indexes.get(0), is("nxlog-20150913"));
    }

    @Test
    public void testSpliteratorSplitsIntoCorrectIndexes3() {
        SessionPeriod period = new SessionPeriod(LocalDate.of(2015, 9, 30), LocalDate.of(2015, 10, 1));
        List<String> indexes = fixture.splitDaily(period);

        assertThat(indexes.size(), is(2));
        assertThat(indexes.get(0), is("nxlog-20150930"));
        assertThat(indexes.get(1), is("nxlog-20151001"));
    }

}
