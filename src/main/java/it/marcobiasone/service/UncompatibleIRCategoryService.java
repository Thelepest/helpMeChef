package it.marcobiasone.service;

import it.marcobiasone.domain.UncompatibleIRCategory;
import it.marcobiasone.repository.UncompatibleIRCategoryRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link UncompatibleIRCategory}.
 */
@Service
@Transactional
public class UncompatibleIRCategoryService {

    private final Logger log = LoggerFactory.getLogger(UncompatibleIRCategoryService.class);

    private final UncompatibleIRCategoryRepository uncompatibleIRCategoryRepository;

    public UncompatibleIRCategoryService(UncompatibleIRCategoryRepository uncompatibleIRCategoryRepository) {
        this.uncompatibleIRCategoryRepository = uncompatibleIRCategoryRepository;
    }

    /**
     * Save a uncompatibleIRCategory.
     *
     * @param uncompatibleIRCategory the entity to save.
     * @return the persisted entity.
     */
    public UncompatibleIRCategory save(UncompatibleIRCategory uncompatibleIRCategory) {
        log.debug("Request to save UncompatibleIRCategory : {}", uncompatibleIRCategory);
        return uncompatibleIRCategoryRepository.save(uncompatibleIRCategory);
    }

    /**
     * Update a uncompatibleIRCategory.
     *
     * @param uncompatibleIRCategory the entity to save.
     * @return the persisted entity.
     */
    public UncompatibleIRCategory update(UncompatibleIRCategory uncompatibleIRCategory) {
        log.debug("Request to save UncompatibleIRCategory : {}", uncompatibleIRCategory);
        return uncompatibleIRCategoryRepository.save(uncompatibleIRCategory);
    }

    /**
     * Partially update a uncompatibleIRCategory.
     *
     * @param uncompatibleIRCategory the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<UncompatibleIRCategory> partialUpdate(UncompatibleIRCategory uncompatibleIRCategory) {
        log.debug("Request to partially update UncompatibleIRCategory : {}", uncompatibleIRCategory);

        return uncompatibleIRCategoryRepository
            .findById(uncompatibleIRCategory.getId())
            .map(existingUncompatibleIRCategory -> {
                return existingUncompatibleIRCategory;
            })
            .map(uncompatibleIRCategoryRepository::save);
    }

    /**
     * Get all the uncompatibleIRCategories.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<UncompatibleIRCategory> findAll() {
        log.debug("Request to get all UncompatibleIRCategories");
        return uncompatibleIRCategoryRepository.findAllWithEagerRelationships();
    }

    /**
     * Get all the uncompatibleIRCategories with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<UncompatibleIRCategory> findAllWithEagerRelationships(Pageable pageable) {
        return uncompatibleIRCategoryRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one uncompatibleIRCategory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<UncompatibleIRCategory> findOne(Long id) {
        log.debug("Request to get UncompatibleIRCategory : {}", id);
        return uncompatibleIRCategoryRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the uncompatibleIRCategory by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete UncompatibleIRCategory : {}", id);
        uncompatibleIRCategoryRepository.deleteById(id);
    }
}
