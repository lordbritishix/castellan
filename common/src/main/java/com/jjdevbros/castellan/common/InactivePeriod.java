package com.jjdevbros.castellan.common;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Created by lordbritishix on 05/09/15.
 */
@Data
@AllArgsConstructor
public class InactivePeriod {
    private LocalDateTime start;
    private LocalDateTime end;
}
