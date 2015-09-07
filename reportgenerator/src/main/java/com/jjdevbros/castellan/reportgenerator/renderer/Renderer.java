package com.jjdevbros.castellan.reportgenerator.renderer;

import com.jjdevbros.castellan.reportgenerator.report.AttendanceReport;

/**
 * Created by lordbritishix on 06/09/15.
 */
public interface Renderer {
    boolean render(AttendanceReport report);
}
