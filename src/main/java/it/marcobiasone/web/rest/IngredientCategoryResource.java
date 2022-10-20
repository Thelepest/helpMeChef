package it.marcobiasone.web.rest;

import it.marcobiasone.domain.IngredientCategory;
import it.marcobiasone.repository.IngredientCategoryRepository;
import it.marcobiasone.service.IngredientCategoryQueryService;
import it.marcobiasone.service.IngredientCategoryService;
import it.marcobiasone.service.criteria.IngredientCategoryCriteria;
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
 * REST controller for managing {@link it.marcobiasone.domain.IngredientCategory}.
 */
@RestController
@RequestMapping("/api")
public class IngredientCategoryResource {

    private final Logger log = LoggerFactory.getLogger(IngredientCategoryResource.class);

    private static final String ENTITY_NAME = "ingredientCategory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final IngredientCategoryService ingredientCategoryService;

    private final IngredientCategoryRepository ingredientCategoryRepository;

    private final IngredientCategoryQueryService ingredientCategoryQueryService;

    public IngredientCategoryResource(
        IngredientCategoryService ingredientCategoryService,
        IngredientCategoryRepository ingredientCategoryRepository,
        IngredientCategoryQueryService ingredientCategoryQueryService
    ) {
        this.ingredientCategoryService = ingredientCategoryService;
        this.ingredientCategoryRepository = ingredientCategoryRepository;
        this.ingredientCategoryQueryService = ingredientCategoryQueryService;
    }

    /**
     * {@code POST  /ingredient-categories} : Create a new ingredientCategory.
     *
     * @param ingredientCategory the ingredientCategory to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ingredientCategory, or with status {@code 400 (Bad Request)} if the ingredientCategory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/ingredient-categories")
    public ResponseEntity<IngredientCategory> createIngredientCategory(@Valid @RequestBody IngredientCategory ingredientCategory)
        throws URISyntaxException {
        log.debug("REST request to save IngredientCategory : {}", ingredientCategory);
        if (ingredientCategory.getId() != null) {
            throw new BadRequestAlertException("A new ingredientCategory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        IngredientCategory result = ingredientCategoryService.save(ingredientCategory);
        return ResponseEntity
            .created(new URI("/api/ingredient-categories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /ingredient-categories/:id} : Updates an existing ingredientCategory.
     *
     * @param id the id of the ingredientCategory to save.
     * @param ingredientCategory the ingredientCategory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ingredientCategory,
     * or with status {@code 400 (Bad Request)} if the ingredientCategory is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ingredientCategory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/ingredient-categories/{id}")
    public ResponseEntity<IngredientCategory> updateIngredientCategory(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody IngredientCategory ingredientCategory
    ) throws URISyntaxException {
        log.debug("REST request to update IngredientCategory : {}, {}", id, ingredientCategory);
        if (ingredientCategory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ingredientCategory.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ingredientCategoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        IngredientCategory result = ingredientCategoryService.update(ingredientCategory);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ingredientCategory.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /ingredient-categories/:id} : Partial updates given fields of an existing ingredientCategory, field will ignore if it is null
     *
     * @param id the id of the ingredientCategory to save.
     * @param ingredientCategory the ingredientCategory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ingredientCategory,
     * or with status {@code 400 (Bad Request)} if the ingredientCategory is not valid,
     * or with status {@code 404 (Not Found)} if the ingredientCategory is not found,
     * or with status {@code 500 (Internal Server Error)} if the ingredientCategory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/ingredient-categories/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<IngredientCategory> partialUpdateIngredientCategory(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody IngredientCategory ingredientCategory
    ) throws URISyntaxException {
        log.debug("REST request to partial update IngredientCategory partially : {}, {}", id, ingredientCategory);
        if (ingredientCategory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ingredientCategory.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ingredientCategoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<IngredientCategory> result = ingredientCategoryService.partialUpdate(ingredientCategory);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ingredientCategory.getId().toString())
        );
    }

    /**
     * {@code GET  /ingredient-categories} : get all the ingredientCategories.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of ingredientCategories in body.
     */
    @GetMapping("/ingredient-categories")
    public ResponseEntity<List<IngredientCategory>> getAllIngredientCategories(IngredientCategoryCriteria criteria) {
        log.debug("REST request to get IngredientCategories by criteria: {}", criteria);
        List<IngredientCategory> entityList = ingredientCategoryQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /ingredient-categories/count} : count all the ingredientCategories.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/ingredient-categories/count")
    public ResponseEntity<Long> countIngredientCategories(IngredientCategoryCriteria criteria) {
        log.debug("REST request to count IngredientCategories by criteria: {}", criteria);
        return ResponseEntity.ok().body(ingredientCategoryQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /ingredient-categories/:id} : get the "id" ingredientCategory.
     *
     * @param id the id of the ingredientCategory to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ingredientCategory, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/ingredient-categories/{id}")
    public ResponseEntity<IngredientCategory> getIngredientCategory(@PathVariable Long id) {
        log.debug("REST request to get IngredientCategory : {}", id);
        Optional<IngredientCategory> ingredientCategory = ingredientCategoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(ingredientCategory);
    }

    /**
     * {@code DELETE  /ingredient-categories/:id} : delete the "id" ingredientCategory.
     *
     * @param id the id of the ingredientCategory to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/ingredient-categories/{id}")
    public ResponseEntity<Void> deleteIngredientCategory(@PathVariable Long id) {
        log.debug("REST request to delete IngredientCategory : {}", id);
        ingredientCategoryService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
