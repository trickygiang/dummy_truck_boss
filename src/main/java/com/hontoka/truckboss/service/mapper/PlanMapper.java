package com.hontoka.truckboss.service.mapper;

import com.hontoka.truckboss.domain.Plan;
import com.hontoka.truckboss.service.dto.PlanDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Plan} and its DTO {@link PlanDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface PlanMapper extends EntityMapper<PlanDTO, Plan> {
    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PlanDTO toDtoId(Plan plan);
}
