package it.marcobiasone.web.rest;

import it.marcobiasone.domain.UncompatibleIRCategory;
import it.marcobiasone.repository.UncompatibleIRCategoryRepository;
import it.marcobiasone.service.UncompatibleIRCategoryQueryService;
import it.marcobiasone.service.UncompatibleIRCategoryService;
import it.marcobiasone.service.criteria.UncompatibleIRCategoryCriteria;
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
 * REST controller for managing {@link it.marcobiasone.domain.UncompatibleIRCategory}.
 */
@RestController
@RequestMapping("/api")
public class UncompatibleIRCategoryResource {

    private final Logger log = LoggerFactory.getLogger(UncompatibleIRCategoryResource.class);

    private static final String ENTITY_NAME = "uncompatibleIRCategory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UncompatibleIRCategoryService uncompatibleIRCategoryService;

    private final UncompatibleIRCategoryRepository uncompatibleIRCategoryRepository;

    private final UncompatibleIRCategoryQueryService uncompatibleIRCategoryQueryService;

    public UncompatibleIRCategoryResource(
        UncompatibleIRCategoryService uncompatibleIRCategoryService,
        UncompatibleIRCategoryRepository uncompatibleIRCategoryRepository,
        UncompatibleIRCategoryQueryService uncompatibleIRCategoryQueryService
    ) {
        this.uncompatibleIRCategoryService = uncompatibleIRCategoryService;
        this.uncompatibleIRCategoryRepository = uncompatibleIRCategoryRepository;
        this.uncompatibleIRCategoryQueryService = uncompatibleIRCategoryQueryService;
    }

    /**
     * {@code POST  /uncompatible-ir-categories} : Create a new uncompatibleIRCategory.
     *
     * @param uncompatibleIRCategory the uncompatibleIRCategory to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new uncompatibleIRCategory, or with status {@code 400 (Bad Request)} if the uncompatibleIRCategory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/uncompatible-ir-categories")
    public ResponseEntity<UncompatibleIRCategory> createUncompatibleIRCategory(@RequestBody UncompatibleIRCategory uncompatibleIRCategory)
        throws URISyntaxException {
        log.debug("REST request to save UncompatibleIRCategory : {}", uncompatibleIRCategory);
        if (uncompatibleIRCategory.getId() != null) {
            throw new BadRequestAlertException("A new uncompatibleIRCategory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UncompatibleIRCategory result = uncompatibleIRCategoryService.save(uncompatibleIRCategory);
        return ResponseEntity
            .created(new URI("/api/uncompatible-ir-categories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /uncompatible-ir-categories/:id} : Updates an existing uncompatibleIRCategory.
     *
     * @param id the id of the uncompatibleIRCategory to save.
     * @param uncompatibleIRCategory the uncompatibleIRCategory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated uncompatibleIRCategory,
     * or with status {@code 400 (Bad Request)} if the uncompatibleIRCategory is not valid,
     * or with status {@code 500 (Internal Server Error)} if the uncompatibleIRCategory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/uncompatible-ir-categories/{id}")
    public ResponseEntity<UncompatibleIRCategory> updateUncompatibleIRCategory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody UncompatibleIRCategory uncompatibleIRCategory
    ) throws URISyntaxException {
        log.debug("REST request to update UncompatibleIRCategory : {}, {}", id, uncompatibleIRCategory);
        if (uncompatibleIRCategory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, uncompatibleIRCategory.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!uncompatibleIRCategoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        UncompatibleIRCategory result = uncompatibleIRCategoryService.update(uncompatibleIRCategory);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, uncompatibleIRCategory.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /uncompatible-ir-categories/:id} : Partial updates given fields of an existing uncompatibleIRCategory, field will ignore if it is null
     *
     * @param id the id of the uncompatibleIRCategory to save.
     * @param uncompatibleIRCategory the uncompatibleIRCategory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated uncompatibleIRCategory,
     * or with status {@code 400 (Bad Request)} if the uncompatibleIRCategory is not valid,
     * or with status {@code 404 (Not Found)} if the uncompatibleIRCategory is not found,
     * or with status {@code 500 (Internal Server Error)} if the uncompatibleIRCategory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/uncompatible-ir-categories/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UncompatibleIRCategory> partialUpdateUncompatibleIRCategory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody UncompatibleIRCategory uncompatibleIRCategory
    ) throws URISyntaxException {
        log.debug("REST request to partial update UncompatibleIRCategory partially : {}, {}", id, uncompatibleIRCategory);
        if (uncompatibleIRCategory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, uncompatibleIRCategory.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!uncompatibleIRCategoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UncompatibleIRCategory> result = uncompatibleIRCategoryService.partialUpdate(uncompatibleIRCategory);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, uncompatibleIRCategory.getId().toString())
        );
    }

    /**
     * {@code GET  /uncompatible-ir-categories} : get all the uncompatibleIRCategories.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of uncompatibleIRCategories in body.
     */
    @GetMapping("/uncompatible-ir-categories")
    public ResponseEntity<List<UncompatibleIRCategory>> getAllUncompatibleIRCategories(UncompatibleIRCategoryCriteria criteria) {
        log.debug("REST request to get UncompatibleIRCategories by criteria: {}", criteria);
        List<UncompatibleIRCategory> entityList = uncompatibleIRCategoryQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /uncompatible-ir-categories/count} : count all the uncompatibleIRCategories.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/uncompatible-ir-categories/count")
    public ResponseEntity<Long> countUncompatibleIRCategories(UncompatibleIRCategoryCriteria criteria) {
        log.debug("REST request to count UncompatibleIRCategories by criteria: {}", criteria);
        return ResponseEntity.ok().body(uncompatibleIRCategoryQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /uncompatible-ir-categories/:id} : get the "id" uncompatibleIRCategory.
     *
     * @param id the id of the uncompatibleIRCategory to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the uncompatibleIRCategory, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/uncompatible-ir-categories/{id}")
    public ResponseEntity<UncompatibleIRCategory> getUncompatibleIRCategory(@PathVariable Long id) {
        log.debug("REST request to get UncompatibleIRCategory : {}", id);
        Optional<UncompatibleIRCategory> uncompatibleIRCategory = uncompatibleIRCategoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(uncompatibleIRCategory);
    }

    /**
     * {@code DELETE  /uncompatible-ir-categories/:id} : delete the "id" uncompatibleIRCategory.
     *
     * @param id the id of the uncompatibleIRCategory to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/uncompatible-ir-categories/{id}")
    public ResponseEntity<Void> deleteUncompatibleIRCategory(@PathVariable Long id) {
        log.debug("REST request to delete UncompatibleIRCategory : {}", id);
        uncompatibleIRCategoryService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
