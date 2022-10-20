package it.marcobiasone.web.rest;

import it.marcobiasone.domain.Pantry;
import it.marcobiasone.repository.PantryRepository;
import it.marcobiasone.service.PantryQueryService;
import it.marcobiasone.service.PantryService;
import it.marcobiasone.service.criteria.PantryCriteria;
import it.marcobiasone.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link it.marcobiasone.domain.Pantry}.
 */
@RestController
@RequestMapping("/api")
public class PantryResource {

    private final Logger log = LoggerFactory.getLogger(PantryResource.class);

    private static final String ENTITY_NAME = "pantry";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PantryService pantryService;

    private final PantryRepository pantryRepository;

    private final PantryQueryService pantryQueryService;

    public PantryResource(PantryService pantryService, PantryRepository pantryRepository, PantryQueryService pantryQueryService) {
        this.pantryService = pantryService;
        this.pantryRepository = pantryRepository;
        this.pantryQueryService = pantryQueryService;
    }

    /**
     * {@code POST  /pantries} : Create a new pantry.
     *
     * @param pantry the pantry to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pantry, or with status {@code 400 (Bad Request)} if the pantry has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/pantries")
    public ResponseEntity<Pantry> createPantry(@Valid @RequestBody Pantry pantry) throws URISyntaxException {
        log.debug("REST request to save Pantry : {}", pantry);
        if (pantry.getId() != null) {
            throw new BadRequestAlertException("A new pantry cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Pantry result = pantryService.save(pantry);
        return ResponseEntity
            .created(new URI("/api/pantries/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /pantries/:id} : Updates an existing pantry.
     *
     * @param id the id of the pantry to save.
     * @param pantry the pantry to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pantry,
     * or with status {@code 400 (Bad Request)} if the pantry is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pantry couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/pantries/{id}")
    public ResponseEntity<Pantry> updatePantry(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Pantry pantry
    ) throws URISyntaxException {
        log.debug("REST request to update Pantry : {}, {}", id, pantry);
        if (pantry.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pantry.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pantryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Pantry result = pantryService.update(pantry);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, pantry.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /pantries/:id} : Partial updates given fields of an existing pantry, field will ignore if it is null
     *
     * @param id the id of the pantry to save.
     * @param pantry the pantry to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pantry,
     * or with status {@code 400 (Bad Request)} if the pantry is not valid,
     * or with status {@code 404 (Not Found)} if the pantry is not found,
     * or with status {@code 500 (Internal Server Error)} if the pantry couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/pantries/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Pantry> partialUpdatePantry(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Pantry pantry
    ) throws URISyntaxException {
        log.debug("REST request to partial update Pantry partially : {}, {}", id, pantry);
        if (pantry.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pantry.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pantryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Pantry> result = pantryService.partialUpdate(pantry);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, pantry.getId().toString())
        );
    }

    /**
     * {@code GET  /pantries} : get all the pantries.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of pantries in body.
     */
    @GetMapping("/pantries")
    public ResponseEntity<List<Pantry>> getAllPantries(PantryCriteria criteria) {
        log.debug("REST request to get Pantries by criteria: {}", criteria);
        List<Pantry> entityList = pantryQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /pantries/count} : count all the pantries.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/pantries/count")
    public ResponseEntity<Long> countPantries(PantryCriteria criteria) {
        log.debug("REST request to count Pantries by criteria: {}", criteria);
        return ResponseEntity.ok().body(pantryQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /pantries/:id} : get the "id" pantry.
     *
     * @param id the id of the pantry to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pantry, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/pantries/{id}")
    public ResponseEntity<Pantry> getPantry(@PathVariable Long id) {
        log.debug("REST request to get Pantry : {}", id);
        Optional<Pantry> pantry = pantryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(pantry);
    }

    /**
     * {@code DELETE  /pantries/:id} : delete the "id" pantry.
     *
     * @param id the id of the pantry to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/pantries/{id}")
    public ResponseEntity<Void> deletePantry(@PathVariable Long id) {
        log.debug("REST request to delete Pantry : {}", id);
        pantryService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
