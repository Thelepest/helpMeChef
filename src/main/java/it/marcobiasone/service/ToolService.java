package it.marcobiasone.service;

import it.marcobiasone.domain.Tool;
import it.marcobiasone.repository.ToolRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Tool}.
 */
@Service
@Transactional
public class ToolService {

    private final Logger log = LoggerFactory.getLogger(ToolService.class);

    private final ToolRepository toolRepository;

    public ToolService(ToolRepository toolRepository) {
        this.toolRepository = toolRepository;
    }

    /**
     * Save a tool.
     *
     * @param tool the entity to save.
     * @return the persisted entity.
     */
    public Tool save(Tool tool) {
        log.debug("Request to save Tool : {}", tool);
        return toolRepository.save(tool);
    }

    /**
     * Update a tool.
     *
     * @param tool the entity to save.
     * @return the persisted entity.
     */
    public Tool update(Tool tool) {
        log.debug("Request to save Tool : {}", tool);
        return toolRepository.save(tool);
    }

    /**
     * Partially update a tool.
     *
     * @param tool the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Tool> partialUpdate(Tool tool) {
        log.debug("Request to partially update Tool : {}", tool);

        return toolRepository
            .findById(tool.getId())
            .map(existingTool -> {
                if (tool.getName() != null) {
                    existingTool.setName(tool.getName());
                }
                if (tool.getDescription() != null) {
                    existingTool.setDescription(tool.getDescription());
                }

                return existingTool;
            })
            .map(toolRepository::save);
    }

    /**
     * Get all the tools.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Tool> findAll() {
        log.debug("Request to get all Tools");
        return toolRepository.findAllWithEagerRelationships();
    }

    /**
     * Get all the tools with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<Tool> findAllWithEagerRelationships(Pageable pageable) {
        return toolRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one tool by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Tool> findOne(Long id) {
        log.debug("Request to get Tool : {}", id);
        return toolRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the tool by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Tool : {}", id);
        toolRepository.deleteById(id);
    }
}
