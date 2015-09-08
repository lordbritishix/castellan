package com.jjdevbros.castellan.common;

import com.google.common.collect.ImmutableList;

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
            WindowsLogEventId.SCREEN_UNLOCK);

    public static final String EN_DASH = "–";
}
