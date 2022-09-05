package com.hontoka.truckboss.service;

import com.hontoka.truckboss.config.KafkaProperties;
import com.hontoka.truckboss.domain.Journey;
import com.hontoka.truckboss.domain.Schedule;
import com.hontoka.truckboss.enums.JourneyStatus;
import com.hontoka.truckboss.enums.ScheduleStatus;
import com.hontoka.truckboss.repository.JourneyRepository;
import com.hontoka.truckboss.repository.ScheduleRepository;
import com.hontoka.truckboss.service.dto.JourneyDTO;
import com.hontoka.truckboss.service.dto.ScheduleDTO;
import com.hontoka.truckboss.service.mapper.JourneyMapper;
import com.hontoka.truckboss.service.mapper.ScheduleMapper;

import java.time.Instant;
import java.util.Optional;

import com.hontoka.truckboss.web.rest.errors.BadRequestAlertException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Schedule}.
 */
@Service
@Transactional
//@AllArgsConstructor
public class ScheduleService {

    private final Logger log = LoggerFactory.getLogger(ScheduleService.class);

    private static final String SCHEDULE_ENTITY_NAME = "schedule";

    private static final String REG_SCHEDULE_CHECK_TOPIC = "reg_schedule_check";

    private final ScheduleRepository scheduleRepository;

    private final JourneyRepository journeyRepository;

    private final ScheduleMapper scheduleMapper;

    private final JourneyMapper journeyMapper;

    private final KafkaProperties kafkaProperties;

    private final KafkaProducer<String, String> producer;

    public ScheduleService(ScheduleRepository scheduleRepository, JourneyRepository journeyRepository, ScheduleMapper scheduleMapper, JourneyMapper journeyMapper, KafkaProperties kafkaProperties) {
        this.scheduleRepository = scheduleRepository;
        this.journeyRepository = journeyRepository;
        this.scheduleMapper = scheduleMapper;
        this.journeyMapper = journeyMapper;
        this.kafkaProperties = kafkaProperties;
        this.producer = new KafkaProducer<>(kafkaProperties.getProducerProps());
    }

    public JourneyDTO startJourney(Long scheduleId) {
        Schedule schedule = scheduleRepository.getById(scheduleId);
        if (schedule == null) {
            throw new BadRequestAlertException("Invalid schedule", SCHEDULE_ENTITY_NAME, "schedule_invalid");
        }

        Journey journey = new Journey();
        journey.setSchedule(schedule);
        journey.setStartTime(Instant.now());
        journey.setStatus(JourneyStatus.STARTED.getStatusId());
        journey = journeyRepository.save(journey);

        return journeyMapper.toDto(journey);
    }

    /**
     * Save a schedule.
     *
     * @param scheduleDTO the entity to save.
     * @return the persisted entity.
     */
    public ScheduleDTO save(ScheduleDTO scheduleDTO) {
        log.debug("Request to save Schedule : {}", scheduleDTO);
        Schedule schedule = scheduleMapper.toEntity(scheduleDTO);
        schedule = scheduleRepository.save(schedule);
        return scheduleMapper.toDto(schedule);
    }

    public ScheduleDTO create(ScheduleDTO scheduleDTO) {
        log.debug("Request to create new Schedule : {}", scheduleDTO);
        Schedule schedule = scheduleMapper.toEntity(scheduleDTO);
        schedule.setStatus(ScheduleStatus.DRAFT.getStatusId());
        schedule = scheduleRepository.save(schedule);

        producer.send(
            new ProducerRecord<>(
                REG_SCHEDULE_CHECK_TOPIC, schedule.getId().toString(), schedule.getTruck().getId().toString()
            )
        );

        schedule.setStatus(ScheduleStatus.PLANNED.getStatusId());
        schedule = scheduleRepository.save(schedule);
        return scheduleMapper.toDto(schedule);
    }

    /**
     * Partially update a schedule.
     *
     * @param scheduleDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ScheduleDTO> partialUpdate(ScheduleDTO scheduleDTO) {
        log.debug("Request to partially update Schedule : {}", scheduleDTO);

        return scheduleRepository
            .findById(scheduleDTO.getId())
            .map(existingSchedule -> {
                scheduleMapper.partialUpdate(existingSchedule, scheduleDTO);

                return existingSchedule;
            })
            .map(scheduleRepository::save)
            .map(scheduleMapper::toDto);
    }

    /**
     * Get all the schedules.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ScheduleDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Schedules");
        return scheduleRepository.findAll(pageable).map(scheduleMapper::toDto);
    }

    /**
     * Get one schedule by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ScheduleDTO> findOne(Long id) {
        log.debug("Request to get Schedule : {}", id);
        return scheduleRepository.findById(id).map(scheduleMapper::toDto);
    }

    /**
     * Delete the schedule by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Schedule : {}", id);
        scheduleRepository.deleteById(id);
    }
}
