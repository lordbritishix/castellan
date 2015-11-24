package com.jjdevbros.castellan.common.utils;

import com.google.common.collect.ImmutableList;
import com.jjdevbros.castellan.common.model.WindowsLogEventId;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by lordbritishix on 06/09/15.
 */
public final class Constants {
    public static final List<WindowsLogEventId> SUPPORTED_STATES = ImmutableList.of(
            WindowsLogEventId.LOG_IN,
            WindowsLogEventId.LOG_OUT,
            WindowsLogEventId.SCREENSAVER_ACTIVE,
            WindowsLogEventId.SCREENSAVER_INACTIVE,
            WindowsLogEventId.SCREEN_LOCK,
            WindowsLogEventId.SHUTDOWN,
            WindowsLogEventId.SCREEN_UNLOCK);

    public static final String EN_DASH = "–";
    public static final String INDEX_PREFIX = "nxlog-";
    public static final SimpleDateFormat ES_INDEX_DATE_FORMATTER = new SimpleDateFormat("YYYYMMdd");
    public static final SimpleDateFormat ES_EVENT_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final String ES_TYPE = "eventlog";
}
