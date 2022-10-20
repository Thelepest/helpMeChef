package it.marcobiasone.web.rest;

import it.marcobiasone.domain.MyConfig;
import it.marcobiasone.repository.MyConfigRepository;
import it.marcobiasone.service.MyConfigQueryService;
import it.marcobiasone.service.MyConfigService;
import it.marcobiasone.service.criteria.MyConfigCriteria;
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
 * REST controller for managing {@link it.marcobiasone.domain.MyConfig}.
 */
@RestController
@RequestMapping("/api")
public class MyConfigResource {

    private final Logger log = LoggerFactory.getLogger(MyConfigResource.class);

    private static final String ENTITY_NAME = "myConfig";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MyConfigService myConfigService;

    private final MyConfigRepository myConfigRepository;

    private final MyConfigQueryService myConfigQueryService;

    public MyConfigResource(
        MyConfigService myConfigService,
        MyConfigRepository myConfigRepository,
        MyConfigQueryService myConfigQueryService
    ) {
        this.myConfigService = myConfigService;
        this.myConfigRepository = myConfigRepository;
        this.myConfigQueryService = myConfigQueryService;
    }

    /**
     * {@code POST  /my-configs} : Create a new myConfig.
     *
     * @param myConfig the myConfig to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new myConfig, or with status {@code 400 (Bad Request)} if the myConfig has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/my-configs")
    public ResponseEntity<MyConfig> createMyConfig(@Valid @RequestBody MyConfig myConfig) throws URISyntaxException {
        log.debug("REST request to save MyConfig : {}", myConfig);
        if (myConfig.getId() != null) {
            throw new BadRequestAlertException("A new myConfig cannot already have an ID", ENTITY_NAME, "idexists");
        }
        MyConfig result = myConfigService.save(myConfig);
        return ResponseEntity
            .created(new URI("/api/my-configs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /my-configs/:id} : Updates an existing myConfig.
     *
     * @param id the id of the myConfig to save.
     * @param myConfig the myConfig to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated myConfig,
     * or with status {@code 400 (Bad Request)} if the myConfig is not valid,
     * or with status {@code 500 (Internal Server Error)} if the myConfig couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/my-configs/{id}")
    public ResponseEntity<MyConfig> updateMyConfig(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MyConfig myConfig
    ) throws URISyntaxException {
        log.debug("REST request to update MyConfig : {}, {}", id, myConfig);
        if (myConfig.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, myConfig.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!myConfigRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        MyConfig result = myConfigService.update(myConfig);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, myConfig.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /my-configs/:id} : Partial updates given fields of an existing myConfig, field will ignore if it is null
     *
     * @param id the id of the myConfig to save.
     * @param myConfig the myConfig to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated myConfig,
     * or with status {@code 400 (Bad Request)} if the myConfig is not valid,
     * or with status {@code 404 (Not Found)} if the myConfig is not found,
     * or with status {@code 500 (Internal Server Error)} if the myConfig couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/my-configs/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MyConfig> partialUpdateMyConfig(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MyConfig myConfig
    ) throws URISyntaxException {
        log.debug("REST request to partial update MyConfig partially : {}, {}", id, myConfig);
        if (myConfig.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, myConfig.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!myConfigRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MyConfig> result = myConfigService.partialUpdate(myConfig);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, myConfig.getId().toString())
        );
    }

    /**
     * {@code GET  /my-configs} : get all the myConfigs.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of myConfigs in body.
     */
    @GetMapping("/my-configs")
    public ResponseEntity<List<MyConfig>> getAllMyConfigs(MyConfigCriteria criteria) {
        log.debug("REST request to get MyConfigs by criteria: {}", criteria);
        List<MyConfig> entityList = myConfigQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /my-configs/count} : count all the myConfigs.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/my-configs/count")
    public ResponseEntity<Long> countMyConfigs(MyConfigCriteria criteria) {
        log.debug("REST request to count MyConfigs by criteria: {}", criteria);
        return ResponseEntity.ok().body(myConfigQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /my-configs/:id} : get the "id" myConfig.
     *
     * @param id the id of the myConfig to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the myConfig, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/my-configs/{id}")
    public ResponseEntity<MyConfig> getMyConfig(@PathVariable Long id) {
        log.debug("REST request to get MyConfig : {}", id);
        Optional<MyConfig> myConfig = myConfigService.findOne(id);
        return ResponseUtil.wrapOrNotFound(myConfig);
    }

    /**
     * {@code DELETE  /my-configs/:id} : delete the "id" myConfig.
     *
     * @param id the id of the myConfig to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/my-configs/{id}")
    public ResponseEntity<Void> deleteMyConfig(@PathVariable Long id) {
        log.debug("REST request to delete MyConfig : {}", id);
        myConfigService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
