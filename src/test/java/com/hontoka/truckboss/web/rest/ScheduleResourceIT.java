package com.hontoka.truckboss.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.hontoka.truckboss.IntegrationTest;
import com.hontoka.truckboss.domain.Schedule;
import com.hontoka.truckboss.repository.ScheduleRepository;
import com.hontoka.truckboss.service.dto.ScheduleDTO;
import com.hontoka.truckboss.service.mapper.ScheduleMapper;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ScheduleResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ScheduleResourceIT {

    private static final Integer DEFAULT_STATUS = 1;
    private static final Integer UPDATED_STATUS = 2;

    private static final String ENTITY_API_URL = "/api/schedules";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restScheduleMockMvc;

    private Schedule schedule;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Schedule createEntity(EntityManager em) {
        Schedule schedule = new Schedule().status(DEFAULT_STATUS);
        return schedule;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Schedule createUpdatedEntity(EntityManager em) {
        Schedule schedule = new Schedule().status(UPDATED_STATUS);
        return schedule;
    }

    @BeforeEach
    public void initTest() {
        schedule = createEntity(em);
    }

    @Test
    @Transactional
    void createSchedule() throws Exception {
        int databaseSizeBeforeCreate = scheduleRepository.findAll().size();
        // Create the Schedule
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);
        restScheduleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(scheduleDTO)))
            .andExpect(status().isCreated());

        // Validate the Schedule in the database
        List<Schedule> scheduleList = scheduleRepository.findAll();
        assertThat(scheduleList).hasSize(databaseSizeBeforeCreate + 1);
        Schedule testSchedule = scheduleList.get(scheduleList.size() - 1);
        assertThat(testSchedule.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void createScheduleWithExistingId() throws Exception {
        // Create the Schedule with an existing ID
        schedule.setId(1L);
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);

        int databaseSizeBeforeCreate = scheduleRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restScheduleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(scheduleDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Schedule in the database
        List<Schedule> scheduleList = scheduleRepository.findAll();
        assertThat(scheduleList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllSchedules() throws Exception {
        // Initialize the database
        scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList
        restScheduleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(schedule.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));
    }

    @Test
    @Transactional
    void getSchedule() throws Exception {
        // Initialize the database
        scheduleRepository.saveAndFlush(schedule);

        // Get the schedule
        restScheduleMockMvc
            .perform(get(ENTITY_API_URL_ID, schedule.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(schedule.getId().intValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS));
    }

    @Test
    @Transactional
    void getNonExistingSchedule() throws Exception {
        // Get the schedule
        restScheduleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewSchedule() throws Exception {
        // Initialize the database
        scheduleRepository.saveAndFlush(schedule);

        int databaseSizeBeforeUpdate = scheduleRepository.findAll().size();

        // Update the schedule
        Schedule updatedSchedule = scheduleRepository.findById(schedule.getId()).get();
        // Disconnect from session so that the updates on updatedSchedule are not directly saved in db
        em.detach(updatedSchedule);
        updatedSchedule.status(UPDATED_STATUS);
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(updatedSchedule);

        restScheduleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, scheduleDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(scheduleDTO))
            )
            .andExpect(status().isOk());

        // Validate the Schedule in the database
        List<Schedule> scheduleList = scheduleRepository.findAll();
        assertThat(scheduleList).hasSize(databaseSizeBeforeUpdate);
        Schedule testSchedule = scheduleList.get(scheduleList.size() - 1);
        assertThat(testSchedule.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void putNonExistingSchedule() throws Exception {
        int databaseSizeBeforeUpdate = scheduleRepository.findAll().size();
        schedule.setId(count.incrementAndGet());

        // Create the Schedule
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restScheduleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, scheduleDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(scheduleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Schedule in the database
        List<Schedule> scheduleList = scheduleRepository.findAll();
        assertThat(scheduleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSchedule() throws Exception {
        int databaseSizeBeforeUpdate = scheduleRepository.findAll().size();
        schedule.setId(count.incrementAndGet());

        // Create the Schedule
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScheduleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(scheduleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Schedule in the database
        List<Schedule> scheduleList = scheduleRepository.findAll();
        assertThat(scheduleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSchedule() throws Exception {
        int databaseSizeBeforeUpdate = scheduleRepository.findAll().size();
        schedule.setId(count.incrementAndGet());

        // Create the Schedule
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScheduleMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(scheduleDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Schedule in the database
        List<Schedule> scheduleList = scheduleRepository.findAll();
        assertThat(scheduleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateScheduleWithPatch() throws Exception {
        // Initialize the database
        scheduleRepository.saveAndFlush(schedule);

        int databaseSizeBeforeUpdate = scheduleRepository.findAll().size();

        // Update the schedule using partial update
        Schedule partialUpdatedSchedule = new Schedule();
        partialUpdatedSchedule.setId(schedule.getId());

        partialUpdatedSchedule.status(UPDATED_STATUS);

        restScheduleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSchedule.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSchedule))
            )
            .andExpect(status().isOk());

        // Validate the Schedule in the database
        List<Schedule> scheduleList = scheduleRepository.findAll();
        assertThat(scheduleList).hasSize(databaseSizeBeforeUpdate);
        Schedule testSchedule = scheduleList.get(scheduleList.size() - 1);
        assertThat(testSchedule.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateScheduleWithPatch() throws Exception {
        // Initialize the database
        scheduleRepository.saveAndFlush(schedule);

        int databaseSizeBeforeUpdate = scheduleRepository.findAll().size();

        // Update the schedule using partial update
        Schedule partialUpdatedSchedule = new Schedule();
        partialUpdatedSchedule.setId(schedule.getId());

        partialUpdatedSchedule.status(UPDATED_STATUS);

        restScheduleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSchedule.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSchedule))
            )
            .andExpect(status().isOk());

        // Validate the Schedule in the database
        List<Schedule> scheduleList = scheduleRepository.findAll();
        assertThat(scheduleList).hasSize(databaseSizeBeforeUpdate);
        Schedule testSchedule = scheduleList.get(scheduleList.size() - 1);
        assertThat(testSchedule.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingSchedule() throws Exception {
        int databaseSizeBeforeUpdate = scheduleRepository.findAll().size();
        schedule.setId(count.incrementAndGet());

        // Create the Schedule
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restScheduleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, scheduleDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(scheduleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Schedule in the database
        List<Schedule> scheduleList = scheduleRepository.findAll();
        assertThat(scheduleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSchedule() throws Exception {
        int databaseSizeBeforeUpdate = scheduleRepository.findAll().size();
        schedule.setId(count.incrementAndGet());

        // Create the Schedule
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScheduleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(scheduleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Schedule in the database
        List<Schedule> scheduleList = scheduleRepository.findAll();
        assertThat(scheduleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSchedule() throws Exception {
        int databaseSizeBeforeUpdate = scheduleRepository.findAll().size();
        schedule.setId(count.incrementAndGet());

        // Create the Schedule
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScheduleMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(scheduleDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Schedule in the database
        List<Schedule> scheduleList = scheduleRepository.findAll();
        assertThat(scheduleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSchedule() throws Exception {
        // Initialize the database
        scheduleRepository.saveAndFlush(schedule);

        int databaseSizeBeforeDelete = scheduleRepository.findAll().size();

        // Delete the schedule
        restScheduleMockMvc
            .perform(delete(ENTITY_API_URL_ID, schedule.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Schedule> scheduleList = scheduleRepository.findAll();
        assertThat(scheduleList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
