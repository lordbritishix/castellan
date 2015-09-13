package com.jjdevbros.castellan.common.model;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

/**
 * Created by lordbritishix on 06/09/15.
 */
@Builder
@Data
public class NormalizedSession {
    @Singular
    private List<NormalizedEventModel> events;

    private String userName;

    private boolean hasErrors;

    private String errorDescription;
}
