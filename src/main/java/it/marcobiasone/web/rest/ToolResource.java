package it.marcobiasone.web.rest;

import it.marcobiasone.domain.Tool;
import it.marcobiasone.repository.ToolRepository;
import it.marcobiasone.service.ToolQueryService;
import it.marcobiasone.service.ToolService;
import it.marcobiasone.service.criteria.ToolCriteria;
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
 * REST controller for managing {@link it.marcobiasone.domain.Tool}.
 */
@RestController
@RequestMapping("/api")
public class ToolResource {

    private final Logger log = LoggerFactory.getLogger(ToolResource.class);

    private static final String ENTITY_NAME = "tool";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ToolService toolService;

    private final ToolRepository toolRepository;

    private final ToolQueryService toolQueryService;

    public ToolResource(ToolService toolService, ToolRepository toolRepository, ToolQueryService toolQueryService) {
        this.toolService = toolService;
        this.toolRepository = toolRepository;
        this.toolQueryService = toolQueryService;
    }

    /**
     * {@code POST  /tools} : Create a new tool.
     *
     * @param tool the tool to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tool, or with status {@code 400 (Bad Request)} if the tool has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/tools")
    public ResponseEntity<Tool> createTool(@Valid @RequestBody Tool tool) throws URISyntaxException {
        log.debug("REST request to save Tool : {}", tool);
        if (tool.getId() != null) {
            throw new BadRequestAlertException("A new tool cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Tool result = toolService.save(tool);
        return ResponseEntity
            .created(new URI("/api/tools/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /tools/:id} : Updates an existing tool.
     *
     * @param id the id of the tool to save.
     * @param tool the tool to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tool,
     * or with status {@code 400 (Bad Request)} if the tool is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tool couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/tools/{id}")
    public ResponseEntity<Tool> updateTool(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Tool tool)
        throws URISyntaxException {
        log.debug("REST request to update Tool : {}, {}", id, tool);
        if (tool.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tool.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!toolRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Tool result = toolService.update(tool);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tool.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /tools/:id} : Partial updates given fields of an existing tool, field will ignore if it is null
     *
     * @param id the id of the tool to save.
     * @param tool the tool to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tool,
     * or with status {@code 400 (Bad Request)} if the tool is not valid,
     * or with status {@code 404 (Not Found)} if the tool is not found,
     * or with status {@code 500 (Internal Server Error)} if the tool couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/tools/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Tool> partialUpdateTool(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Tool tool
    ) throws URISyntaxException {
        log.debug("REST request to partial update Tool partially : {}, {}", id, tool);
        if (tool.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tool.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!toolRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Tool> result = toolService.partialUpdate(tool);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tool.getId().toString())
        );
    }

    /**
     * {@code GET  /tools} : get all the tools.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tools in body.
     */
    @GetMapping("/tools")
    public ResponseEntity<List<Tool>> getAllTools(ToolCriteria criteria) {
        log.debug("REST request to get Tools by criteria: {}", criteria);
        List<Tool> entityList = toolQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /tools/count} : count all the tools.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/tools/count")
    public ResponseEntity<Long> countTools(ToolCriteria criteria) {
        log.debug("REST request to count Tools by criteria: {}", criteria);
        return ResponseEntity.ok().body(toolQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /tools/:id} : get the "id" tool.
     *
     * @param id the id of the tool to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tool, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/tools/{id}")
    public ResponseEntity<Tool> getTool(@PathVariable Long id) {
        log.debug("REST request to get Tool : {}", id);
        Optional<Tool> tool = toolService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tool);
    }

    /**
     * {@code DELETE  /tools/:id} : delete the "id" tool.
     *
     * @param id the id of the tool to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/tools/{id}")
    public ResponseEntity<Void> deleteTool(@PathVariable Long id) {
        log.debug("REST request to delete Tool : {}", id);
        toolService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
