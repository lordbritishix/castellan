package com.jjdevbros.castellan.common.utils;

import java.sql.Date;
import java.time.LocalDate;

/**
 * Created by lordbritishix on 13/09/15.
 */
public class Utils {
    public static String indexForDate(LocalDate date) {
        return Constants.INDEX_PREFIX + Constants.INDEX_FORMATTER.format(Date.valueOf(date));
    }

}
