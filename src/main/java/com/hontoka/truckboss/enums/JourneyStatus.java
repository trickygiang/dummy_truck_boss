package com.hontoka.truckboss.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JourneyStatus {
    DRAFT(0), STARTED(1), ON_GOING(10), FINISHED(20), CANCELED(30);

    private final Integer statusId;
}
