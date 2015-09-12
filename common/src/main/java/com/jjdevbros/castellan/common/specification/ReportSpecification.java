package com.jjdevbros.castellan.common.specification;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by lordbritishix on 12/09/15.
 */
@AllArgsConstructor
public abstract class ReportSpecification {
    @Getter
    private String reportTemplateName;

    @Getter
    private String fileNamePrefix;
}
