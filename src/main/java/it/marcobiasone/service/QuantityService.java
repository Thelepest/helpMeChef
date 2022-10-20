package it.marcobiasone.service;

import it.marcobiasone.domain.Quantity;
import it.marcobiasone.repository.QuantityRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Quantity}.
 */
@Service
@Transactional
public class QuantityService {

    private final Logger log = LoggerFactory.getLogger(QuantityService.class);

    private final QuantityRepository quantityRepository;

    public QuantityService(QuantityRepository quantityRepository) {
        this.quantityRepository = quantityRepository;
    }

    /**
     * Save a quantity.
     *
     * @param quantity the entity to save.
     * @return the persisted entity.
     */
    public Quantity save(Quantity quantity) {
        log.debug("Request to save Quantity : {}", quantity);
        return quantityRepository.save(quantity);
    }

    /**
     * Update a quantity.
     *
     * @param quantity the entity to save.
     * @return the persisted entity.
     */
    public Quantity update(Quantity quantity) {
        log.debug("Request to save Quantity : {}", quantity);
        return quantityRepository.save(quantity);
    }

    /**
     * Partially update a quantity.
     *
     * @param quantity the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Quantity> partialUpdate(Quantity quantity) {
        log.debug("Request to partially update Quantity : {}", quantity);

        return quantityRepository
            .findById(quantity.getId())
            .map(existingQuantity -> {
                if (quantity.getName() != null) {
                    existingQuantity.setName(quantity.getName());
                }
                if (quantity.getAmount() != null) {
                    existingQuantity.setAmount(quantity.getAmount());
                }
                if (quantity.getDescription() != null) {
                    existingQuantity.setDescription(quantity.getDescription());
                }

                return existingQuantity;
            })
            .map(quantityRepository::save);
    }

    /**
     * Get all the quantities.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Quantity> findAll() {
        log.debug("Request to get all Quantities");
        return quantityRepository.findAll();
    }

    /**
     * Get one quantity by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Quantity> findOne(Long id) {
        log.debug("Request to get Quantity : {}", id);
        return quantityRepository.findById(id);
    }

    /**
     * Delete the quantity by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Quantity : {}", id);
        quantityRepository.deleteById(id);
    }
}
