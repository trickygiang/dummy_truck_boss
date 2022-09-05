package com.hontoka.truckboss.service;

import com.hontoka.truckboss.domain.Plan;
import com.hontoka.truckboss.repository.PlanRepository;
import com.hontoka.truckboss.service.dto.PlanDTO;
import com.hontoka.truckboss.service.mapper.PlanMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Plan}.
 */
@Service
@Transactional
public class PlanService {

    private final Logger log = LoggerFactory.getLogger(PlanService.class);

    private final PlanRepository planRepository;

    private final PlanMapper planMapper;

    public PlanService(PlanRepository planRepository, PlanMapper planMapper) {
        this.planRepository = planRepository;
        this.planMapper = planMapper;
    }

    /**
     * Save a plan.
     *
     * @param planDTO the entity to save.
     * @return the persisted entity.
     */
    public PlanDTO save(PlanDTO planDTO) {
        log.debug("Request to save Plan : {}", planDTO);
        Plan plan = planMapper.toEntity(planDTO);
        plan = planRepository.save(plan);
        return planMapper.toDto(plan);
    }

    /**
     * Partially update a plan.
     *
     * @param planDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PlanDTO> partialUpdate(PlanDTO planDTO) {
        log.debug("Request to partially update Plan : {}", planDTO);

        return planRepository
            .findById(planDTO.getId())
            .map(existingPlan -> {
                planMapper.partialUpdate(existingPlan, planDTO);

                return existingPlan;
            })
            .map(planRepository::save)
            .map(planMapper::toDto);
    }

    /**
     * Get all the plans.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PlanDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Plans");
        return planRepository.findAll(pageable).map(planMapper::toDto);
    }

    /**
     * Get one plan by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PlanDTO> findOne(Long id) {
        log.debug("Request to get Plan : {}", id);
        return planRepository.findById(id).map(planMapper::toDto);
    }

    /**
     * Delete the plan by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Plan : {}", id);
        planRepository.deleteById(id);
    }
}
