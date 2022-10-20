package it.marcobiasone.web.rest;

import it.marcobiasone.domain.IngredientQuantity;
import it.marcobiasone.repository.IngredientQuantityRepository;
import it.marcobiasone.service.IngredientQuantityQueryService;
import it.marcobiasone.service.IngredientQuantityService;
import it.marcobiasone.service.criteria.IngredientQuantityCriteria;
import it.marcobiasone.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link it.marcobiasone.domain.IngredientQuantity}.
 */
@RestController
@RequestMapping("/api")
public class IngredientQuantityResource {

    private final Logger log = LoggerFactory.getLogger(IngredientQuantityResource.class);

    private static final String ENTITY_NAME = "ingredientQuantity";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final IngredientQuantityService ingredientQuantityService;

    private final IngredientQuantityRepository ingredientQuantityRepository;

    private final IngredientQuantityQueryService ingredientQuantityQueryService;

    public IngredientQuantityResource(
        IngredientQuantityService ingredientQuantityService,
        IngredientQuantityRepository ingredientQuantityRepository,
        IngredientQuantityQueryService ingredientQuantityQueryService
    ) {
        this.ingredientQuantityService = ingredientQuantityService;
        this.ingredientQuantityRepository = ingredientQuantityRepository;
        this.ingredientQuantityQueryService = ingredientQuantityQueryService;
    }

    /**
     * {@code POST  /ingredient-quantities} : Create a new ingredientQuantity.
     *
     * @param ingredientQuantity the ingredientQuantity to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ingredientQuantity, or with status {@code 400 (Bad Request)} if the ingredientQuantity has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/ingredient-quantities")
    public ResponseEntity<IngredientQuantity> createIngredientQuantity(@RequestBody IngredientQuantity ingredientQuantity)
        throws URISyntaxException {
        log.debug("REST request to save IngredientQuantity : {}", ingredientQuantity);
        if (ingredientQuantity.getId() != null) {
            throw new BadRequestAlertException("A new ingredientQuantity cannot already have an ID", ENTITY_NAME, "idexists");
        }
        IngredientQuantity result = ingredientQuantityService.save(ingredientQuantity);
        return ResponseEntity
            .created(new URI("/api/ingredient-quantities/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /ingredient-quantities/:id} : Updates an existing ingredientQuantity.
     *
     * @param id the id of the ingredientQuantity to save.
     * @param ingredientQuantity the ingredientQuantity to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ingredientQuantity,
     * or with status {@code 400 (Bad Request)} if the ingredientQuantity is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ingredientQuantity couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/ingredient-quantities/{id}")
    public ResponseEntity<IngredientQuantity> updateIngredientQuantity(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody IngredientQuantity ingredientQuantity
    ) throws URISyntaxException {
        log.debug("REST request to update IngredientQuantity : {}, {}", id, ingredientQuantity);
        if (ingredientQuantity.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ingredientQuantity.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ingredientQuantityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        IngredientQuantity result = ingredientQuantityService.update(ingredientQuantity);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ingredientQuantity.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /ingredient-quantities/:id} : Partial updates given fields of an existing ingredientQuantity, field will ignore if it is null
     *
     * @param id the id of the ingredientQuantity to save.
     * @param ingredientQuantity the ingredientQuantity to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ingredientQuantity,
     * or with status {@code 400 (Bad Request)} if the ingredientQuantity is not valid,
     * or with status {@code 404 (Not Found)} if the ingredientQuantity is not found,
     * or with status {@code 500 (Internal Server Error)} if the ingredientQuantity couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/ingredient-quantities/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<IngredientQuantity> partialUpdateIngredientQuantity(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody IngredientQuantity ingredientQuantity
    ) throws URISyntaxException {
        log.debug("REST request to partial update IngredientQuantity partially : {}, {}", id, ingredientQuantity);
        if (ingredientQuantity.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ingredientQuantity.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ingredientQuantityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<IngredientQuantity> result = ingredientQuantityService.partialUpdate(ingredientQuantity);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ingredientQuantity.getId().toString())
        );
    }

    /**
     * {@code GET  /ingredient-quantities} : get all the ingredientQuantities.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of ingredientQuantities in body.
     */
    @GetMapping("/ingredient-quantities")
    public ResponseEntity<List<IngredientQuantity>> getAllIngredientQuantities(IngredientQuantityCriteria criteria) {
        log.debug("REST request to get IngredientQuantities by criteria: {}", criteria);
        List<IngredientQuantity> entityList = ingredientQuantityQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /ingredient-quantities/count} : count all the ingredientQuantities.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/ingredient-quantities/count")
    public ResponseEntity<Long> countIngredientQuantities(IngredientQuantityCriteria criteria) {
        log.debug("REST request to count IngredientQuantities by criteria: {}", criteria);
        return ResponseEntity.ok().body(ingredientQuantityQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /ingredient-quantities/:id} : get the "id" ingredientQuantity.
     *
     * @param id the id of the ingredientQuantity to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ingredientQuantity, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/ingredient-quantities/{id}")
    public ResponseEntity<IngredientQuantity> getIngredientQuantity(@PathVariable Long id) {
        log.debug("REST request to get IngredientQuantity : {}", id);
        Optional<IngredientQuantity> ingredientQuantity = ingredientQuantityService.findOne(id);
        return ResponseUtil.wrapOrNotFound(ingredientQuantity);
    }

    /**
     * {@code DELETE  /ingredient-quantities/:id} : delete the "id" ingredientQuantity.
     *
     * @param id the id of the ingredientQuantity to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/ingredient-quantities/{id}")
    public ResponseEntity<Void> deleteIngredientQuantity(@PathVariable Long id) {
        log.debug("REST request to delete IngredientQuantity : {}", id);
        ingredientQuantityService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
