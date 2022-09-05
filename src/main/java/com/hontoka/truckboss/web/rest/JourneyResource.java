package com.hontoka.truckboss.web.rest;

import com.hontoka.truckboss.repository.JourneyRepository;
import com.hontoka.truckboss.service.JourneyService;
import com.hontoka.truckboss.service.dto.JourneyDTO;
import com.hontoka.truckboss.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.hontoka.truckboss.domain.Journey}.
 */
@RestController
@RequestMapping("/api")
public class JourneyResource {

    private final Logger log = LoggerFactory.getLogger(JourneyResource.class);

    private static final String ENTITY_NAME = "journey";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final JourneyService journeyService;

    private final JourneyRepository journeyRepository;

    public JourneyResource(JourneyService journeyService, JourneyRepository journeyRepository) {
        this.journeyService = journeyService;
        this.journeyRepository = journeyRepository;
    }

    /**
     * {@code POST  /journeys} : Create a new journey.
     *
     * @param journeyDTO the journeyDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new journeyDTO, or with status {@code 400 (Bad Request)} if the journey has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/journeys")
    public ResponseEntity<JourneyDTO> createJourney(@RequestBody JourneyDTO journeyDTO) throws URISyntaxException {
        log.debug("REST request to save Journey : {}", journeyDTO);
        if (journeyDTO.getId() != null) {
            throw new BadRequestAlertException("A new journey cannot already have an ID", ENTITY_NAME, "idexists");
        }
        JourneyDTO result = journeyService.save(journeyDTO);
        return ResponseEntity
            .created(new URI("/api/journeys/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /journeys/:id} : Updates an existing journey.
     *
     * @param id the id of the journeyDTO to save.
     * @param journeyDTO the journeyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated journeyDTO,
     * or with status {@code 400 (Bad Request)} if the journeyDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the journeyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/journeys/{id}")
    public ResponseEntity<JourneyDTO> updateJourney(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody JourneyDTO journeyDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Journey : {}, {}", id, journeyDTO);
        if (journeyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, journeyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!journeyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        JourneyDTO result = journeyService.save(journeyDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, journeyDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /journeys/:id} : Partial updates given fields of an existing journey, field will ignore if it is null
     *
     * @param id the id of the journeyDTO to save.
     * @param journeyDTO the journeyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated journeyDTO,
     * or with status {@code 400 (Bad Request)} if the journeyDTO is not valid,
     * or with status {@code 404 (Not Found)} if the journeyDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the journeyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/journeys/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<JourneyDTO> partialUpdateJourney(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody JourneyDTO journeyDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Journey partially : {}, {}", id, journeyDTO);
        if (journeyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, journeyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!journeyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<JourneyDTO> result = journeyService.partialUpdate(journeyDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, journeyDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /journeys} : get all the journeys.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of journeys in body.
     */
    @GetMapping("/journeys")
    public ResponseEntity<List<JourneyDTO>> getAllJourneys(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Journeys");
        Page<JourneyDTO> page = journeyService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /journeys/:id} : get the "id" journey.
     *
     * @param id the id of the journeyDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the journeyDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/journeys/{id}")
    public ResponseEntity<JourneyDTO> getJourney(@PathVariable Long id) {
        log.debug("REST request to get Journey : {}", id);
        Optional<JourneyDTO> journeyDTO = journeyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(journeyDTO);
    }

    /**
     * {@code DELETE  /journeys/:id} : delete the "id" journey.
     *
     * @param id the id of the journeyDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/journeys/{id}")
    public ResponseEntity<Void> deleteJourney(@PathVariable Long id) {
        log.debug("REST request to delete Journey : {}", id);
        journeyService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
