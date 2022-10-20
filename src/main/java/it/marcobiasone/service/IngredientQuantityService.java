package it.marcobiasone.service;

import it.marcobiasone.domain.IngredientQuantity;
import it.marcobiasone.repository.IngredientQuantityRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link IngredientQuantity}.
 */
@Service
@Transactional
public class IngredientQuantityService {

    private final Logger log = LoggerFactory.getLogger(IngredientQuantityService.class);

    private final IngredientQuantityRepository ingredientQuantityRepository;

    public IngredientQuantityService(IngredientQuantityRepository ingredientQuantityRepository) {
        this.ingredientQuantityRepository = ingredientQuantityRepository;
    }

    /**
     * Save a ingredientQuantity.
     *
     * @param ingredientQuantity the entity to save.
     * @return the persisted entity.
     */
    public IngredientQuantity save(IngredientQuantity ingredientQuantity) {
        log.debug("Request to save IngredientQuantity : {}", ingredientQuantity);
        return ingredientQuantityRepository.save(ingredientQuantity);
    }

    /**
     * Update a ingredientQuantity.
     *
     * @param ingredientQuantity the entity to save.
     * @return the persisted entity.
     */
    public IngredientQuantity update(IngredientQuantity ingredientQuantity) {
        log.debug("Request to save IngredientQuantity : {}", ingredientQuantity);
        return ingredientQuantityRepository.save(ingredientQuantity);
    }

    /**
     * Partially update a ingredientQuantity.
     *
     * @param ingredientQuantity the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<IngredientQuantity> partialUpdate(IngredientQuantity ingredientQuantity) {
        log.debug("Request to partially update IngredientQuantity : {}", ingredientQuantity);

        return ingredientQuantityRepository
            .findById(ingredientQuantity.getId())
            .map(existingIngredientQuantity -> {
                return existingIngredientQuantity;
            })
            .map(ingredientQuantityRepository::save);
    }

    /**
     * Get all the ingredientQuantities.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<IngredientQuantity> findAll() {
        log.debug("Request to get all IngredientQuantities");
        return ingredientQuantityRepository.findAllWithEagerRelationships();
    }

    /**
     * Get all the ingredientQuantities with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<IngredientQuantity> findAllWithEagerRelationships(Pageable pageable) {
        return ingredientQuantityRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one ingredientQuantity by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<IngredientQuantity> findOne(Long id) {
        log.debug("Request to get IngredientQuantity : {}", id);
        return ingredientQuantityRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the ingredientQuantity by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete IngredientQuantity : {}", id);
        ingredientQuantityRepository.deleteById(id);
    }
}
