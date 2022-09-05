package com.hontoka.truckboss.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.hontoka.truckboss.IntegrationTest;
import com.hontoka.truckboss.domain.Truck;
import com.hontoka.truckboss.repository.TruckRepository;
import com.hontoka.truckboss.service.dto.TruckDTO;
import com.hontoka.truckboss.service.mapper.TruckMapper;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link TruckResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TruckResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_MODEL = "AAAAAAAAAA";
    private static final String UPDATED_MODEL = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_REGISTRATION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_REGISTRATION_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Integer DEFAULT_MONTH_DUE = 1;
    private static final Integer UPDATED_MONTH_DUE = 2;

    private static final String ENTITY_API_URL = "/api/trucks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TruckRepository truckRepository;

    @Autowired
    private TruckMapper truckMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTruckMockMvc;

    private Truck truck;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Truck createEntity(EntityManager em) {
        Truck truck = new Truck()
            .code(DEFAULT_CODE)
            .model(DEFAULT_MODEL)
            .registrationDate(DEFAULT_REGISTRATION_DATE)
            .monthDue(DEFAULT_MONTH_DUE);
        return truck;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Truck createUpdatedEntity(EntityManager em) {
        Truck truck = new Truck()
            .code(UPDATED_CODE)
            .model(UPDATED_MODEL)
            .registrationDate(UPDATED_REGISTRATION_DATE)
            .monthDue(UPDATED_MONTH_DUE);
        return truck;
    }

    @BeforeEach
    public void initTest() {
        truck = createEntity(em);
    }

    @Test
    @Transactional
    void createTruck() throws Exception {
        int databaseSizeBeforeCreate = truckRepository.findAll().size();
        // Create the Truck
        TruckDTO truckDTO = truckMapper.toDto(truck);
        restTruckMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(truckDTO)))
            .andExpect(status().isCreated());

        // Validate the Truck in the database
        List<Truck> truckList = truckRepository.findAll();
        assertThat(truckList).hasSize(databaseSizeBeforeCreate + 1);
        Truck testTruck = truckList.get(truckList.size() - 1);
        assertThat(testTruck.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testTruck.getModel()).isEqualTo(DEFAULT_MODEL);
        assertThat(testTruck.getRegistrationDate()).isEqualTo(DEFAULT_REGISTRATION_DATE);
        assertThat(testTruck.getMonthDue()).isEqualTo(DEFAULT_MONTH_DUE);
    }

    @Test
    @Transactional
    void createTruckWithExistingId() throws Exception {
        // Create the Truck with an existing ID
        truck.setId(1L);
        TruckDTO truckDTO = truckMapper.toDto(truck);

        int databaseSizeBeforeCreate = truckRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTruckMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(truckDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Truck in the database
        List<Truck> truckList = truckRepository.findAll();
        assertThat(truckList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTrucks() throws Exception {
        // Initialize the database
        truckRepository.saveAndFlush(truck);

        // Get all the truckList
        restTruckMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(truck.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].model").value(hasItem(DEFAULT_MODEL)))
            .andExpect(jsonPath("$.[*].registrationDate").value(hasItem(DEFAULT_REGISTRATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].monthDue").value(hasItem(DEFAULT_MONTH_DUE)));
    }

    @Test
    @Transactional
    void getTruck() throws Exception {
        // Initialize the database
        truckRepository.saveAndFlush(truck);

        // Get the truck
        restTruckMockMvc
            .perform(get(ENTITY_API_URL_ID, truck.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(truck.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.model").value(DEFAULT_MODEL))
            .andExpect(jsonPath("$.registrationDate").value(DEFAULT_REGISTRATION_DATE.toString()))
            .andExpect(jsonPath("$.monthDue").value(DEFAULT_MONTH_DUE));
    }

    @Test
    @Transactional
    void getNonExistingTruck() throws Exception {
        // Get the truck
        restTruckMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTruck() throws Exception {
        // Initialize the database
        truckRepository.saveAndFlush(truck);

        int databaseSizeBeforeUpdate = truckRepository.findAll().size();

        // Update the truck
        Truck updatedTruck = truckRepository.findById(truck.getId()).get();
        // Disconnect from session so that the updates on updatedTruck are not directly saved in db
        em.detach(updatedTruck);
        updatedTruck.code(UPDATED_CODE).model(UPDATED_MODEL).registrationDate(UPDATED_REGISTRATION_DATE).monthDue(UPDATED_MONTH_DUE);
        TruckDTO truckDTO = truckMapper.toDto(updatedTruck);

        restTruckMockMvc
            .perform(
                put(ENTITY_API_URL_ID, truckDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(truckDTO))
            )
            .andExpect(status().isOk());

        // Validate the Truck in the database
        List<Truck> truckList = truckRepository.findAll();
        assertThat(truckList).hasSize(databaseSizeBeforeUpdate);
        Truck testTruck = truckList.get(truckList.size() - 1);
        assertThat(testTruck.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testTruck.getModel()).isEqualTo(UPDATED_MODEL);
        assertThat(testTruck.getRegistrationDate()).isEqualTo(UPDATED_REGISTRATION_DATE);
        assertThat(testTruck.getMonthDue()).isEqualTo(UPDATED_MONTH_DUE);
    }

    @Test
    @Transactional
    void putNonExistingTruck() throws Exception {
        int databaseSizeBeforeUpdate = truckRepository.findAll().size();
        truck.setId(count.incrementAndGet());

        // Create the Truck
        TruckDTO truckDTO = truckMapper.toDto(truck);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTruckMockMvc
            .perform(
                put(ENTITY_API_URL_ID, truckDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(truckDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Truck in the database
        List<Truck> truckList = truckRepository.findAll();
        assertThat(truckList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTruck() throws Exception {
        int databaseSizeBeforeUpdate = truckRepository.findAll().size();
        truck.setId(count.incrementAndGet());

        // Create the Truck
        TruckDTO truckDTO = truckMapper.toDto(truck);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTruckMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(truckDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Truck in the database
        List<Truck> truckList = truckRepository.findAll();
        assertThat(truckList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTruck() throws Exception {
        int databaseSizeBeforeUpdate = truckRepository.findAll().size();
        truck.setId(count.incrementAndGet());

        // Create the Truck
        TruckDTO truckDTO = truckMapper.toDto(truck);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTruckMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(truckDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Truck in the database
        List<Truck> truckList = truckRepository.findAll();
        assertThat(truckList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTruckWithPatch() throws Exception {
        // Initialize the database
        truckRepository.saveAndFlush(truck);

        int databaseSizeBeforeUpdate = truckRepository.findAll().size();

        // Update the truck using partial update
        Truck partialUpdatedTruck = new Truck();
        partialUpdatedTruck.setId(truck.getId());

        partialUpdatedTruck.registrationDate(UPDATED_REGISTRATION_DATE);

        restTruckMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTruck.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTruck))
            )
            .andExpect(status().isOk());

        // Validate the Truck in the database
        List<Truck> truckList = truckRepository.findAll();
        assertThat(truckList).hasSize(databaseSizeBeforeUpdate);
        Truck testTruck = truckList.get(truckList.size() - 1);
        assertThat(testTruck.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testTruck.getModel()).isEqualTo(DEFAULT_MODEL);
        assertThat(testTruck.getRegistrationDate()).isEqualTo(UPDATED_REGISTRATION_DATE);
        assertThat(testTruck.getMonthDue()).isEqualTo(DEFAULT_MONTH_DUE);
    }

    @Test
    @Transactional
    void fullUpdateTruckWithPatch() throws Exception {
        // Initialize the database
        truckRepository.saveAndFlush(truck);

        int databaseSizeBeforeUpdate = truckRepository.findAll().size();

        // Update the truck using partial update
        Truck partialUpdatedTruck = new Truck();
        partialUpdatedTruck.setId(truck.getId());

        partialUpdatedTruck.code(UPDATED_CODE).model(UPDATED_MODEL).registrationDate(UPDATED_REGISTRATION_DATE).monthDue(UPDATED_MONTH_DUE);

        restTruckMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTruck.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTruck))
            )
            .andExpect(status().isOk());

        // Validate the Truck in the database
        List<Truck> truckList = truckRepository.findAll();
        assertThat(truckList).hasSize(databaseSizeBeforeUpdate);
        Truck testTruck = truckList.get(truckList.size() - 1);
        assertThat(testTruck.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testTruck.getModel()).isEqualTo(UPDATED_MODEL);
        assertThat(testTruck.getRegistrationDate()).isEqualTo(UPDATED_REGISTRATION_DATE);
        assertThat(testTruck.getMonthDue()).isEqualTo(UPDATED_MONTH_DUE);
    }

    @Test
    @Transactional
    void patchNonExistingTruck() throws Exception {
        int databaseSizeBeforeUpdate = truckRepository.findAll().size();
        truck.setId(count.incrementAndGet());

        // Create the Truck
        TruckDTO truckDTO = truckMapper.toDto(truck);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTruckMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, truckDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(truckDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Truck in the database
        List<Truck> truckList = truckRepository.findAll();
        assertThat(truckList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTruck() throws Exception {
        int databaseSizeBeforeUpdate = truckRepository.findAll().size();
        truck.setId(count.incrementAndGet());

        // Create the Truck
        TruckDTO truckDTO = truckMapper.toDto(truck);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTruckMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(truckDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Truck in the database
        List<Truck> truckList = truckRepository.findAll();
        assertThat(truckList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTruck() throws Exception {
        int databaseSizeBeforeUpdate = truckRepository.findAll().size();
        truck.setId(count.incrementAndGet());

        // Create the Truck
        TruckDTO truckDTO = truckMapper.toDto(truck);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTruckMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(truckDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Truck in the database
        List<Truck> truckList = truckRepository.findAll();
        assertThat(truckList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTruck() throws Exception {
        // Initialize the database
        truckRepository.saveAndFlush(truck);

        int databaseSizeBeforeDelete = truckRepository.findAll().size();

        // Delete the truck
        restTruckMockMvc
            .perform(delete(ENTITY_API_URL_ID, truck.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Truck> truckList = truckRepository.findAll();
        assertThat(truckList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
