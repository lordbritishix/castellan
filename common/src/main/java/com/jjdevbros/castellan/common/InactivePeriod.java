package com.jjdevbros.castellan.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
}
