package com.jjdevbros.castellan.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Created by lordbritishix on 05/09/15.
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode
public class InactivePeriod {
    private LocalDateTime start;
    private LocalDateTime end;

    public Duration getDuration() {
        return Duration.between(start, end);
    }
}
