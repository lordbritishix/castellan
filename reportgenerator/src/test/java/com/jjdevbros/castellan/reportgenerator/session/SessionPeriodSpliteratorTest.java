package com.jjdevbros.castellan.reportgenerator.session;

import com.jjdevbros.castellan.common.SessionPeriod;
import com.jjdevbros.castellan.reportgenerator.session.SessionPeriodSpliterator;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by lordbritishix on 05/09/15.
 */
public class SessionPeriodSpliteratorTest {
    private SessionPeriodSpliterator fixture;

    @Before
    public void setup() {
        fixture = new SessionPeriodSpliterator();
    }

    @Test
    public void testSplitDailyReturnsCorrectValuesForNormalRange() {
        SessionPeriod period = new SessionPeriod(LocalDate.of(2015, 9, 3), LocalDate.of(2015, 9, 6));
        List<SessionPeriod> splits = fixture.splitDaily(period);

        assertThat(splits.size(), is(4));

        assertTrue(splits.get(0).equals(new SessionPeriod(LocalDate.of(2015, 9, 3), LocalDate.of(2015, 9, 3))));
        assertTrue(splits.get(1).equals(new SessionPeriod(LocalDate.of(2015, 9, 4), LocalDate.of(2015, 9, 4))));
        assertTrue(splits.get(2).equals(new SessionPeriod(LocalDate.of(2015, 9, 5), LocalDate.of(2015, 9, 5))));
        assertTrue(splits.get(3).equals(new SessionPeriod(LocalDate.of(2015, 9, 6), LocalDate.of(2015, 9, 6))));
    }

    @Test
    public void testSplitDailyReturnsCorrectValuesForOneElement() {
        SessionPeriod period = new SessionPeriod(LocalDate.of(2015, 9, 3), LocalDate.of(2015, 9, 3));
        List<SessionPeriod> splits = fixture.splitDaily(period);

        assertThat(splits.size(), is(1));
        assertTrue(splits.get(0).equals(new SessionPeriod(LocalDate.of(2015, 9, 3), LocalDate.of(2015, 9, 3))));
    }

}
