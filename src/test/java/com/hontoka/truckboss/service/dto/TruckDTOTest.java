package com.hontoka.truckboss.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.hontoka.truckboss.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TruckDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TruckDTO.class);
        TruckDTO truckDTO1 = new TruckDTO();
        truckDTO1.setId(1L);
        TruckDTO truckDTO2 = new TruckDTO();
        assertThat(truckDTO1).isNotEqualTo(truckDTO2);
        truckDTO2.setId(truckDTO1.getId());
        assertThat(truckDTO1).isEqualTo(truckDTO2);
        truckDTO2.setId(2L);
        assertThat(truckDTO1).isNotEqualTo(truckDTO2);
        truckDTO1.setId(null);
        assertThat(truckDTO1).isNotEqualTo(truckDTO2);
    }
}
