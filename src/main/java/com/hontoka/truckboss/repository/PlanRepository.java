package com.hontoka.truckboss.repository;

import com.hontoka.truckboss.domain.Plan;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Plan entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {}
