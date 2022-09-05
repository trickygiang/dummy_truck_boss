package com.hontoka.truckboss.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TruckMapperTest {

    private TruckMapper truckMapper;

    @BeforeEach
    public void setUp() {
        truckMapper = new TruckMapperImpl();
    }
}
