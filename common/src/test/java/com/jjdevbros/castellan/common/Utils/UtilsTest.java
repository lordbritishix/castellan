package com.jjdevbros.castellan.common.Utils;

import com.jjdevbros.castellan.common.utils.Utils;
import org.junit.Test;

import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by lordbritishix on 13/09/15.
 */
public class UtilsTest {
    @Test
    public void testIndexForDateProducesCorrectIndex() {
        assertThat(Utils.indexForDate(LocalDate.of(2015, 9, 13)), is("nxlog-20150913"));
        assertThat(Utils.indexForDate(LocalDate.of(2015, 1, 1)), is("nxlog-20150101"));
    }
}
