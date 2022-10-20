package it.marcobiasone.service;

import it.marcobiasone.domain.Recipe;
import it.marcobiasone.repository.RecipeRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Recipe}.
 */
@Service
@Transactional
public class RecipeService {

    private final Logger log = LoggerFactory.getLogger(RecipeService.class);

    private final RecipeRepository recipeRepository;

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    /**
     * Save a recipe.
     *
     * @param recipe the entity to save.
     * @return the persisted entity.
     */
    public Recipe save(Recipe recipe) {
        log.debug("Request to save Recipe : {}", recipe);
        return recipeRepository.save(recipe);
    }

    /**
     * Update a recipe.
     *
     * @param recipe the entity to save.
     * @return the persisted entity.
     */
    public Recipe update(Recipe recipe) {
        log.debug("Request to save Recipe : {}", recipe);
        return recipeRepository.save(recipe);
    }

    /**
     * Partially update a recipe.
     *
     * @param recipe the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Recipe> partialUpdate(Recipe recipe) {
        log.debug("Request to partially update Recipe : {}", recipe);

        return recipeRepository
            .findById(recipe.getId())
            .map(existingRecipe -> {
                if (recipe.getName() != null) {
                    existingRecipe.setName(recipe.getName());
                }
                if (recipe.getTime() != null) {
                    existingRecipe.setTime(recipe.getTime());
                }
                if (recipe.getDescription() != null) {
                    existingRecipe.setDescription(recipe.getDescription());
                }

                return existingRecipe;
            })
            .map(recipeRepository::save);
    }

    /**
     * Get all the recipes.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Recipe> findAll() {
        log.debug("Request to get all Recipes");
        return recipeRepository.findAllWithEagerRelationships();
    }

    /**
     * Get all the recipes with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<Recipe> findAllWithEagerRelationships(Pageable pageable) {
        return recipeRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one recipe by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Recipe> findOne(Long id) {
        log.debug("Request to get Recipe : {}", id);
        return recipeRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the recipe by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Recipe : {}", id);
        recipeRepository.deleteById(id);
    }
}
