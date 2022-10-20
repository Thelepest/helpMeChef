package it.marcobiasone.service;

import it.marcobiasone.domain.Pantry;
import it.marcobiasone.repository.PantryRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Pantry}.
 */
@Service
@Transactional
public class PantryService {

    private final Logger log = LoggerFactory.getLogger(PantryService.class);

    private final PantryRepository pantryRepository;

    public PantryService(PantryRepository pantryRepository) {
        this.pantryRepository = pantryRepository;
    }

    /**
     * Save a pantry.
     *
     * @param pantry the entity to save.
     * @return the persisted entity.
     */
    public Pantry save(Pantry pantry) {
        log.debug("Request to save Pantry : {}", pantry);
        return pantryRepository.save(pantry);
    }

    /**
     * Update a pantry.
     *
     * @param pantry the entity to save.
     * @return the persisted entity.
     */
    public Pantry update(Pantry pantry) {
        log.debug("Request to save Pantry : {}", pantry);
        return pantryRepository.save(pantry);
    }

    /**
     * Partially update a pantry.
     *
     * @param pantry the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Pantry> partialUpdate(Pantry pantry) {
        log.debug("Request to partially update Pantry : {}", pantry);

        return pantryRepository
            .findById(pantry.getId())
            .map(existingPantry -> {
                if (pantry.getName() != null) {
                    existingPantry.setName(pantry.getName());
                }
                if (pantry.getActive() != null) {
                    existingPantry.setActive(pantry.getActive());
                }
                if (pantry.getDescription() != null) {
                    existingPantry.setDescription(pantry.getDescription());
                }
                if (pantry.getCreatedAt() != null) {
                    existingPantry.setCreatedAt(pantry.getCreatedAt());
                }

                return existingPantry;
            })
            .map(pantryRepository::save);
    }

    /**
     * Get all the pantries.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Pantry> findAll() {
        log.debug("Request to get all Pantries");
        return pantryRepository.findAllWithEagerRelationships();
    }

    /**
     * Get all the pantries with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<Pantry> findAllWithEagerRelationships(Pageable pageable) {
        return pantryRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one pantry by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Pantry> findOne(Long id) {
        log.debug("Request to get Pantry : {}", id);
        return pantryRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the pantry by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Pantry : {}", id);
        pantryRepository.deleteById(id);
    }
}
