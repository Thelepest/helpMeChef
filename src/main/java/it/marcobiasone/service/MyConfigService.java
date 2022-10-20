package it.marcobiasone.service;

import it.marcobiasone.domain.MyConfig;
import it.marcobiasone.repository.MyConfigRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link MyConfig}.
 */
@Service
@Transactional
public class MyConfigService {

    private final Logger log = LoggerFactory.getLogger(MyConfigService.class);

    private final MyConfigRepository myConfigRepository;

    public MyConfigService(MyConfigRepository myConfigRepository) {
        this.myConfigRepository = myConfigRepository;
    }

    /**
     * Save a myConfig.
     *
     * @param myConfig the entity to save.
     * @return the persisted entity.
     */
    public MyConfig save(MyConfig myConfig) {
        log.debug("Request to save MyConfig : {}", myConfig);
        return myConfigRepository.save(myConfig);
    }

    /**
     * Update a myConfig.
     *
     * @param myConfig the entity to save.
     * @return the persisted entity.
     */
    public MyConfig update(MyConfig myConfig) {
        log.debug("Request to save MyConfig : {}", myConfig);
        return myConfigRepository.save(myConfig);
    }

    /**
     * Partially update a myConfig.
     *
     * @param myConfig the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<MyConfig> partialUpdate(MyConfig myConfig) {
        log.debug("Request to partially update MyConfig : {}", myConfig);

        return myConfigRepository
            .findById(myConfig.getId())
            .map(existingMyConfig -> {
                if (myConfig.getMcKey() != null) {
                    existingMyConfig.setMcKey(myConfig.getMcKey());
                }
                if (myConfig.getMcValue() != null) {
                    existingMyConfig.setMcValue(myConfig.getMcValue());
                }

                return existingMyConfig;
            })
            .map(myConfigRepository::save);
    }

    /**
     * Get all the myConfigs.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<MyConfig> findAll() {
        log.debug("Request to get all MyConfigs");
        return myConfigRepository.findAll();
    }

    /**
     * Get one myConfig by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<MyConfig> findOne(Long id) {
        log.debug("Request to get MyConfig : {}", id);
        return myConfigRepository.findById(id);
    }

    /**
     * Delete the myConfig by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete MyConfig : {}", id);
        myConfigRepository.deleteById(id);
    }
}
