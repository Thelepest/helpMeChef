package it.marcobiasone.service;

import it.marcobiasone.domain.IngredientCategory;
import it.marcobiasone.repository.IngredientCategoryRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link IngredientCategory}.
 */
@Service
@Transactional
public class IngredientCategoryService {

    private final Logger log = LoggerFactory.getLogger(IngredientCategoryService.class);

    private final IngredientCategoryRepository ingredientCategoryRepository;

    public IngredientCategoryService(IngredientCategoryRepository ingredientCategoryRepository) {
        this.ingredientCategoryRepository = ingredientCategoryRepository;
    }

    /**
     * Save a ingredientCategory.
     *
     * @param ingredientCategory the entity to save.
     * @return the persisted entity.
     */
    public IngredientCategory save(IngredientCategory ingredientCategory) {
        log.debug("Request to save IngredientCategory : {}", ingredientCategory);
        return ingredientCategoryRepository.save(ingredientCategory);
    }

    /**
     * Update a ingredientCategory.
     *
     * @param ingredientCategory the entity to save.
     * @return the persisted entity.
     */
    public IngredientCategory update(IngredientCategory ingredientCategory) {
        log.debug("Request to save IngredientCategory : {}", ingredientCategory);
        return ingredientCategoryRepository.save(ingredientCategory);
    }

    /**
     * Partially update a ingredientCategory.
     *
     * @param ingredientCategory the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<IngredientCategory> partialUpdate(IngredientCategory ingredientCategory) {
        log.debug("Request to partially update IngredientCategory : {}", ingredientCategory);

        return ingredientCategoryRepository
            .findById(ingredientCategory.getId())
            .map(existingIngredientCategory -> {
                if (ingredientCategory.getName() != null) {
                    existingIngredientCategory.setName(ingredientCategory.getName());
                }
                if (ingredientCategory.getImage() != null) {
                    existingIngredientCategory.setImage(ingredientCategory.getImage());
                }
                if (ingredientCategory.getImageContentType() != null) {
                    existingIngredientCategory.setImageContentType(ingredientCategory.getImageContentType());
                }
                if (ingredientCategory.getDescription() != null) {
                    existingIngredientCategory.setDescription(ingredientCategory.getDescription());
                }

                return existingIngredientCategory;
            })
            .map(ingredientCategoryRepository::save);
    }

    /**
     * Get all the ingredientCategories.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<IngredientCategory> findAll() {
        log.debug("Request to get all IngredientCategories");
        return ingredientCategoryRepository.findAll();
    }

    /**
     * Get one ingredientCategory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<IngredientCategory> findOne(Long id) {
        log.debug("Request to get IngredientCategory : {}", id);
        return ingredientCategoryRepository.findById(id);
    }

    /**
     * Delete the ingredientCategory by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete IngredientCategory : {}", id);
        ingredientCategoryRepository.deleteById(id);
    }
}
