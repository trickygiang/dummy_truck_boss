package com.hontoka.truckboss.service.mapper;

import com.hontoka.truckboss.domain.Journey;
import com.hontoka.truckboss.service.dto.JourneyDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Journey} and its DTO {@link JourneyDTO}.
 */
@Mapper(componentModel = "spring", uses = { ScheduleMapper.class })
public interface JourneyMapper extends EntityMapper<JourneyDTO, Journey> {
    @Mapping(target = "schedule", source = "schedule", qualifiedByName = "id")
    JourneyDTO toDto(Journey s);
}
