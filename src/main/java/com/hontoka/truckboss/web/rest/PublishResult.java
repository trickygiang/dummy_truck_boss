package com.hontoka.truckboss.web.rest;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
class PublishResult {

    public final String topic;
    public final int partition;
    public final long offset;
    public final Instant timestamp;
}
