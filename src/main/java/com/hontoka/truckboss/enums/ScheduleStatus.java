package com.hontoka.truckboss.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ScheduleStatus {
    DRAFT(0),
    PLANNED(1),
    RE_PLANNED(1),
    APPROVED(10),
    FINISHED(20),
    CANCELED(30),
    REJECTED(31);

    private final Integer statusId;
}
