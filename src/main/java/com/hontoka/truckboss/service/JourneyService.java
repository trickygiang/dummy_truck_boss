package com.hontoka.truckboss.service;

import com.hontoka.truckboss.domain.Journey;
import com.hontoka.truckboss.repository.JourneyRepository;
import com.hontoka.truckboss.service.dto.JourneyDTO;
import com.hontoka.truckboss.service.mapper.JourneyMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Journey}.
 */
@Service
@Transactional
public class JourneyService {

    private final Logger log = LoggerFactory.getLogger(JourneyService.class);

    private final JourneyRepository journeyRepository;

    private final JourneyMapper journeyMapper;

    public JourneyService(JourneyRepository journeyRepository, JourneyMapper journeyMapper) {
        this.journeyRepository = journeyRepository;
        this.journeyMapper = journeyMapper;
    }

    /**
     * Save a journey.
     *
     * @param journeyDTO the entity to save.
     * @return the persisted entity.
     */
    public JourneyDTO save(JourneyDTO journeyDTO) {
        log.debug("Request to save Journey : {}", journeyDTO);
        Journey journey = journeyMapper.toEntity(journeyDTO);
        journey = journeyRepository.save(journey);
        return journeyMapper.toDto(journey);
    }

    /**
     * Partially update a journey.
     *
     * @param journeyDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<JourneyDTO> partialUpdate(JourneyDTO journeyDTO) {
        log.debug("Request to partially update Journey : {}", journeyDTO);

        return journeyRepository
            .findById(journeyDTO.getId())
            .map(existingJourney -> {
                journeyMapper.partialUpdate(existingJourney, journeyDTO);

                return existingJourney;
            })
            .map(journeyRepository::save)
            .map(journeyMapper::toDto);
    }

    /**
     * Get all the journeys.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<JourneyDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Journeys");
        return journeyRepository.findAll(pageable).map(journeyMapper::toDto);
    }

    /**
     * Get one journey by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<JourneyDTO> findOne(Long id) {
        log.debug("Request to get Journey : {}", id);
        return journeyRepository.findById(id).map(journeyMapper::toDto);
    }

    /**
     * Delete the journey by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Journey : {}", id);
        journeyRepository.deleteById(id);
    }
}
