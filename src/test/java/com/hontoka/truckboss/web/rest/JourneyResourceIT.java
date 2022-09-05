package com.hontoka.truckboss.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.hontoka.truckboss.IntegrationTest;
import com.hontoka.truckboss.domain.Journey;
import com.hontoka.truckboss.repository.JourneyRepository;
import com.hontoka.truckboss.service.dto.JourneyDTO;
import com.hontoka.truckboss.service.mapper.JourneyMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link JourneyResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class JourneyResourceIT {

    private static final Instant DEFAULT_START_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_STATUS = 1;
    private static final Integer UPDATED_STATUS = 2;

    private static final String ENTITY_API_URL = "/api/journeys";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private JourneyRepository journeyRepository;

    @Autowired
    private JourneyMapper journeyMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restJourneyMockMvc;

    private Journey journey;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Journey createEntity(EntityManager em) {
        Journey journey = new Journey().startTime(DEFAULT_START_TIME).endTime(DEFAULT_END_TIME).status(DEFAULT_STATUS);
        return journey;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Journey createUpdatedEntity(EntityManager em) {
        Journey journey = new Journey().startTime(UPDATED_START_TIME).endTime(UPDATED_END_TIME).status(UPDATED_STATUS);
        return journey;
    }

    @BeforeEach
    public void initTest() {
        journey = createEntity(em);
    }

    @Test
    @Transactional
    void createJourney() throws Exception {
        int databaseSizeBeforeCreate = journeyRepository.findAll().size();
        // Create the Journey
        JourneyDTO journeyDTO = journeyMapper.toDto(journey);
        restJourneyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(journeyDTO)))
            .andExpect(status().isCreated());

        // Validate the Journey in the database
        List<Journey> journeyList = journeyRepository.findAll();
        assertThat(journeyList).hasSize(databaseSizeBeforeCreate + 1);
        Journey testJourney = journeyList.get(journeyList.size() - 1);
        assertThat(testJourney.getStartTime()).isEqualTo(DEFAULT_START_TIME);
        assertThat(testJourney.getEndTime()).isEqualTo(DEFAULT_END_TIME);
        assertThat(testJourney.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void createJourneyWithExistingId() throws Exception {
        // Create the Journey with an existing ID
        journey.setId(1L);
        JourneyDTO journeyDTO = journeyMapper.toDto(journey);

        int databaseSizeBeforeCreate = journeyRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restJourneyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(journeyDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Journey in the database
        List<Journey> journeyList = journeyRepository.findAll();
        assertThat(journeyList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllJourneys() throws Exception {
        // Initialize the database
        journeyRepository.saveAndFlush(journey);

        // Get all the journeyList
        restJourneyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(journey.getId().intValue())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));
    }

    @Test
    @Transactional
    void getJourney() throws Exception {
        // Initialize the database
        journeyRepository.saveAndFlush(journey);

        // Get the journey
        restJourneyMockMvc
            .perform(get(ENTITY_API_URL_ID, journey.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(journey.getId().intValue()))
            .andExpect(jsonPath("$.startTime").value(DEFAULT_START_TIME.toString()))
            .andExpect(jsonPath("$.endTime").value(DEFAULT_END_TIME.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS));
    }

    @Test
    @Transactional
    void getNonExistingJourney() throws Exception {
        // Get the journey
        restJourneyMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewJourney() throws Exception {
        // Initialize the database
        journeyRepository.saveAndFlush(journey);

        int databaseSizeBeforeUpdate = journeyRepository.findAll().size();

        // Update the journey
        Journey updatedJourney = journeyRepository.findById(journey.getId()).get();
        // Disconnect from session so that the updates on updatedJourney are not directly saved in db
        em.detach(updatedJourney);
        updatedJourney.startTime(UPDATED_START_TIME).endTime(UPDATED_END_TIME).status(UPDATED_STATUS);
        JourneyDTO journeyDTO = journeyMapper.toDto(updatedJourney);

        restJourneyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, journeyDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(journeyDTO))
            )
            .andExpect(status().isOk());

        // Validate the Journey in the database
        List<Journey> journeyList = journeyRepository.findAll();
        assertThat(journeyList).hasSize(databaseSizeBeforeUpdate);
        Journey testJourney = journeyList.get(journeyList.size() - 1);
        assertThat(testJourney.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testJourney.getEndTime()).isEqualTo(UPDATED_END_TIME);
        assertThat(testJourney.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void putNonExistingJourney() throws Exception {
        int databaseSizeBeforeUpdate = journeyRepository.findAll().size();
        journey.setId(count.incrementAndGet());

        // Create the Journey
        JourneyDTO journeyDTO = journeyMapper.toDto(journey);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restJourneyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, journeyDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(journeyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Journey in the database
        List<Journey> journeyList = journeyRepository.findAll();
        assertThat(journeyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchJourney() throws Exception {
        int databaseSizeBeforeUpdate = journeyRepository.findAll().size();
        journey.setId(count.incrementAndGet());

        // Create the Journey
        JourneyDTO journeyDTO = journeyMapper.toDto(journey);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJourneyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(journeyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Journey in the database
        List<Journey> journeyList = journeyRepository.findAll();
        assertThat(journeyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamJourney() throws Exception {
        int databaseSizeBeforeUpdate = journeyRepository.findAll().size();
        journey.setId(count.incrementAndGet());

        // Create the Journey
        JourneyDTO journeyDTO = journeyMapper.toDto(journey);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJourneyMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(journeyDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Journey in the database
        List<Journey> journeyList = journeyRepository.findAll();
        assertThat(journeyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateJourneyWithPatch() throws Exception {
        // Initialize the database
        journeyRepository.saveAndFlush(journey);

        int databaseSizeBeforeUpdate = journeyRepository.findAll().size();

        // Update the journey using partial update
        Journey partialUpdatedJourney = new Journey();
        partialUpdatedJourney.setId(journey.getId());

        partialUpdatedJourney.startTime(UPDATED_START_TIME).endTime(UPDATED_END_TIME).status(UPDATED_STATUS);

        restJourneyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedJourney.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedJourney))
            )
            .andExpect(status().isOk());

        // Validate the Journey in the database
        List<Journey> journeyList = journeyRepository.findAll();
        assertThat(journeyList).hasSize(databaseSizeBeforeUpdate);
        Journey testJourney = journeyList.get(journeyList.size() - 1);
        assertThat(testJourney.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testJourney.getEndTime()).isEqualTo(UPDATED_END_TIME);
        assertThat(testJourney.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateJourneyWithPatch() throws Exception {
        // Initialize the database
        journeyRepository.saveAndFlush(journey);

        int databaseSizeBeforeUpdate = journeyRepository.findAll().size();

        // Update the journey using partial update
        Journey partialUpdatedJourney = new Journey();
        partialUpdatedJourney.setId(journey.getId());

        partialUpdatedJourney.startTime(UPDATED_START_TIME).endTime(UPDATED_END_TIME).status(UPDATED_STATUS);

        restJourneyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedJourney.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedJourney))
            )
            .andExpect(status().isOk());

        // Validate the Journey in the database
        List<Journey> journeyList = journeyRepository.findAll();
        assertThat(journeyList).hasSize(databaseSizeBeforeUpdate);
        Journey testJourney = journeyList.get(journeyList.size() - 1);
        assertThat(testJourney.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testJourney.getEndTime()).isEqualTo(UPDATED_END_TIME);
        assertThat(testJourney.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingJourney() throws Exception {
        int databaseSizeBeforeUpdate = journeyRepository.findAll().size();
        journey.setId(count.incrementAndGet());

        // Create the Journey
        JourneyDTO journeyDTO = journeyMapper.toDto(journey);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restJourneyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, journeyDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(journeyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Journey in the database
        List<Journey> journeyList = journeyRepository.findAll();
        assertThat(journeyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchJourney() throws Exception {
        int databaseSizeBeforeUpdate = journeyRepository.findAll().size();
        journey.setId(count.incrementAndGet());

        // Create the Journey
        JourneyDTO journeyDTO = journeyMapper.toDto(journey);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJourneyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(journeyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Journey in the database
        List<Journey> journeyList = journeyRepository.findAll();
        assertThat(journeyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamJourney() throws Exception {
        int databaseSizeBeforeUpdate = journeyRepository.findAll().size();
        journey.setId(count.incrementAndGet());

        // Create the Journey
        JourneyDTO journeyDTO = journeyMapper.toDto(journey);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJourneyMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(journeyDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Journey in the database
        List<Journey> journeyList = journeyRepository.findAll();
        assertThat(journeyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteJourney() throws Exception {
        // Initialize the database
        journeyRepository.saveAndFlush(journey);

        int databaseSizeBeforeDelete = journeyRepository.findAll().size();

        // Delete the journey
        restJourneyMockMvc
            .perform(delete(ENTITY_API_URL_ID, journey.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Journey> journeyList = journeyRepository.findAll();
        assertThat(journeyList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
