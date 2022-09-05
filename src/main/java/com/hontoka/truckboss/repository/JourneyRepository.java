package com.hontoka.truckboss.repository;

import com.hontoka.truckboss.domain.Journey;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Journey entity.
 */
@SuppressWarnings("unused")
@Repository
public interface JourneyRepository extends JpaRepository<Journey, Long> {}
