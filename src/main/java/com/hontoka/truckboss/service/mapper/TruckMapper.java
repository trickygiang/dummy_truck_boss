package com.hontoka.truckboss.service.mapper;

import com.hontoka.truckboss.domain.Truck;
import com.hontoka.truckboss.service.dto.TruckDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Truck} and its DTO {@link TruckDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface TruckMapper extends EntityMapper<TruckDTO, Truck> {
    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TruckDTO toDtoId(Truck truck);
}
