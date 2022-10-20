package it.marcobiasone.web.rest;

import it.marcobiasone.domain.RecipeCategory;
import it.marcobiasone.repository.RecipeCategoryRepository;
import it.marcobiasone.service.RecipeCategoryQueryService;
import it.marcobiasone.service.RecipeCategoryService;
import it.marcobiasone.service.criteria.RecipeCategoryCriteria;
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
 * REST controller for managing {@link it.marcobiasone.domain.RecipeCategory}.
 */
@RestController
@RequestMapping("/api")
public class RecipeCategoryResource {

    private final Logger log = LoggerFactory.getLogger(RecipeCategoryResource.class);

    private static final String ENTITY_NAME = "recipeCategory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RecipeCategoryService recipeCategoryService;

    private final RecipeCategoryRepository recipeCategoryRepository;

    private final RecipeCategoryQueryService recipeCategoryQueryService;

    public RecipeCategoryResource(
        RecipeCategoryService recipeCategoryService,
        RecipeCategoryRepository recipeCategoryRepository,
        RecipeCategoryQueryService recipeCategoryQueryService
    ) {
        this.recipeCategoryService = recipeCategoryService;
        this.recipeCategoryRepository = recipeCategoryRepository;
        this.recipeCategoryQueryService = recipeCategoryQueryService;
    }

    /**
     * {@code POST  /recipe-categories} : Create a new recipeCategory.
     *
     * @param recipeCategory the recipeCategory to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new recipeCategory, or with status {@code 400 (Bad Request)} if the recipeCategory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/recipe-categories")
    public ResponseEntity<RecipeCategory> createRecipeCategory(@Valid @RequestBody RecipeCategory recipeCategory)
        throws URISyntaxException {
        log.debug("REST request to save RecipeCategory : {}", recipeCategory);
        if (recipeCategory.getId() != null) {
            throw new BadRequestAlertException("A new recipeCategory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        RecipeCategory result = recipeCategoryService.save(recipeCategory);
        return ResponseEntity
            .created(new URI("/api/recipe-categories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /recipe-categories/:id} : Updates an existing recipeCategory.
     *
     * @param id the id of the recipeCategory to save.
     * @param recipeCategory the recipeCategory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated recipeCategory,
     * or with status {@code 400 (Bad Request)} if the recipeCategory is not valid,
     * or with status {@code 500 (Internal Server Error)} if the recipeCategory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/recipe-categories/{id}")
    public ResponseEntity<RecipeCategory> updateRecipeCategory(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RecipeCategory recipeCategory
    ) throws URISyntaxException {
        log.debug("REST request to update RecipeCategory : {}, {}", id, recipeCategory);
        if (recipeCategory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, recipeCategory.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!recipeCategoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        RecipeCategory result = recipeCategoryService.update(recipeCategory);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, recipeCategory.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /recipe-categories/:id} : Partial updates given fields of an existing recipeCategory, field will ignore if it is null
     *
     * @param id the id of the recipeCategory to save.
     * @param recipeCategory the recipeCategory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated recipeCategory,
     * or with status {@code 400 (Bad Request)} if the recipeCategory is not valid,
     * or with status {@code 404 (Not Found)} if the recipeCategory is not found,
     * or with status {@code 500 (Internal Server Error)} if the recipeCategory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/recipe-categories/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<RecipeCategory> partialUpdateRecipeCategory(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RecipeCategory recipeCategory
    ) throws URISyntaxException {
        log.debug("REST request to partial update RecipeCategory partially : {}, {}", id, recipeCategory);
        if (recipeCategory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, recipeCategory.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!recipeCategoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RecipeCategory> result = recipeCategoryService.partialUpdate(recipeCategory);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, recipeCategory.getId().toString())
        );
    }

    /**
     * {@code GET  /recipe-categories} : get all the recipeCategories.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of recipeCategories in body.
     */
    @GetMapping("/recipe-categories")
    public ResponseEntity<List<RecipeCategory>> getAllRecipeCategories(RecipeCategoryCriteria criteria) {
        log.debug("REST request to get RecipeCategories by criteria: {}", criteria);
        List<RecipeCategory> entityList = recipeCategoryQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /recipe-categories/count} : count all the recipeCategories.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/recipe-categories/count")
    public ResponseEntity<Long> countRecipeCategories(RecipeCategoryCriteria criteria) {
        log.debug("REST request to count RecipeCategories by criteria: {}", criteria);
        return ResponseEntity.ok().body(recipeCategoryQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /recipe-categories/:id} : get the "id" recipeCategory.
     *
     * @param id the id of the recipeCategory to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the recipeCategory, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/recipe-categories/{id}")
    public ResponseEntity<RecipeCategory> getRecipeCategory(@PathVariable Long id) {
        log.debug("REST request to get RecipeCategory : {}", id);
        Optional<RecipeCategory> recipeCategory = recipeCategoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(recipeCategory);
    }

    /**
     * {@code DELETE  /recipe-categories/:id} : delete the "id" recipeCategory.
     *
     * @param id the id of the recipeCategory to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/recipe-categories/{id}")
    public ResponseEntity<Void> deleteRecipeCategory(@PathVariable Long id) {
        log.debug("REST request to delete RecipeCategory : {}", id);
        recipeCategoryService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
