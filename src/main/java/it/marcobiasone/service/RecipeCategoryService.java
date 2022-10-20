package it.marcobiasone.service;

import it.marcobiasone.domain.RecipeCategory;
import it.marcobiasone.repository.RecipeCategoryRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link RecipeCategory}.
 */
@Service
@Transactional
public class RecipeCategoryService {

    private final Logger log = LoggerFactory.getLogger(RecipeCategoryService.class);

    private final RecipeCategoryRepository recipeCategoryRepository;

    public RecipeCategoryService(RecipeCategoryRepository recipeCategoryRepository) {
        this.recipeCategoryRepository = recipeCategoryRepository;
    }

    /**
     * Save a recipeCategory.
     *
     * @param recipeCategory the entity to save.
     * @return the persisted entity.
     */
    public RecipeCategory save(RecipeCategory recipeCategory) {
        log.debug("Request to save RecipeCategory : {}", recipeCategory);
        return recipeCategoryRepository.save(recipeCategory);
    }

    /**
     * Update a recipeCategory.
     *
     * @param recipeCategory the entity to save.
     * @return the persisted entity.
     */
    public RecipeCategory update(RecipeCategory recipeCategory) {
        log.debug("Request to save RecipeCategory : {}", recipeCategory);
        return recipeCategoryRepository.save(recipeCategory);
    }

    /**
     * Partially update a recipeCategory.
     *
     * @param recipeCategory the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<RecipeCategory> partialUpdate(RecipeCategory recipeCategory) {
        log.debug("Request to partially update RecipeCategory : {}", recipeCategory);

        return recipeCategoryRepository
            .findById(recipeCategory.getId())
            .map(existingRecipeCategory -> {
                if (recipeCategory.getName() != null) {
                    existingRecipeCategory.setName(recipeCategory.getName());
                }
                if (recipeCategory.getImage() != null) {
                    existingRecipeCategory.setImage(recipeCategory.getImage());
                }
                if (recipeCategory.getImageContentType() != null) {
                    existingRecipeCategory.setImageContentType(recipeCategory.getImageContentType());
                }
                if (recipeCategory.getDescription() != null) {
                    existingRecipeCategory.setDescription(recipeCategory.getDescription());
                }

                return existingRecipeCategory;
            })
            .map(recipeCategoryRepository::save);
    }

    /**
     * Get all the recipeCategories.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<RecipeCategory> findAll() {
        log.debug("Request to get all RecipeCategories");
        return recipeCategoryRepository.findAll();
    }

    /**
     * Get one recipeCategory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<RecipeCategory> findOne(Long id) {
        log.debug("Request to get RecipeCategory : {}", id);
        return recipeCategoryRepository.findById(id);
    }

    /**
     * Delete the recipeCategory by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete RecipeCategory : {}", id);
        recipeCategoryRepository.deleteById(id);
    }
}
