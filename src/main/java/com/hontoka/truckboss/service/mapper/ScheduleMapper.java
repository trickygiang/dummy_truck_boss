package com.hontoka.truckboss.service.mapper;

import com.hontoka.truckboss.domain.Schedule;
import com.hontoka.truckboss.service.dto.ScheduleDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Schedule} and its DTO {@link ScheduleDTO}.
 */
@Mapper(componentModel = "spring", uses = { TruckMapper.class, PlanMapper.class })
public interface ScheduleMapper extends EntityMapper<ScheduleDTO, Schedule> {
    @Mapping(target = "truck", source = "truck", qualifiedByName = "id")
    @Mapping(target = "plan", source = "plan", qualifiedByName = "id")
    ScheduleDTO toDto(Schedule s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ScheduleDTO toDtoId(Schedule schedule);
}
