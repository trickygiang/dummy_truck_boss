package com.hontoka.truckboss.repository;

import com.hontoka.truckboss.domain.Truck;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Truck entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TruckRepository extends JpaRepository<Truck, Long> {}
