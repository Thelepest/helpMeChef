package it.marcobiasone.web.rest;

import it.marcobiasone.domain.Quantity;
import it.marcobiasone.repository.QuantityRepository;
import it.marcobiasone.service.QuantityQueryService;
import it.marcobiasone.service.QuantityService;
import it.marcobiasone.service.criteria.QuantityCriteria;
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
 * REST controller for managing {@link it.marcobiasone.domain.Quantity}.
 */
@RestController
@RequestMapping("/api")
public class QuantityResource {

    private final Logger log = LoggerFactory.getLogger(QuantityResource.class);

    private static final String ENTITY_NAME = "quantity";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final QuantityService quantityService;

    private final QuantityRepository quantityRepository;

    private final QuantityQueryService quantityQueryService;

    public QuantityResource(
        QuantityService quantityService,
        QuantityRepository quantityRepository,
        QuantityQueryService quantityQueryService
    ) {
        this.quantityService = quantityService;
        this.quantityRepository = quantityRepository;
        this.quantityQueryService = quantityQueryService;
    }

    /**
     * {@code POST  /quantities} : Create a new quantity.
     *
     * @param quantity the quantity to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new quantity, or with status {@code 400 (Bad Request)} if the quantity has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/quantities")
    public ResponseEntity<Quantity> createQuantity(@Valid @RequestBody Quantity quantity) throws URISyntaxException {
        log.debug("REST request to save Quantity : {}", quantity);
        if (quantity.getId() != null) {
            throw new BadRequestAlertException("A new quantity cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Quantity result = quantityService.save(quantity);
        return ResponseEntity
            .created(new URI("/api/quantities/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /quantities/:id} : Updates an existing quantity.
     *
     * @param id the id of the quantity to save.
     * @param quantity the quantity to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated quantity,
     * or with status {@code 400 (Bad Request)} if the quantity is not valid,
     * or with status {@code 500 (Internal Server Error)} if the quantity couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/quantities/{id}")
    public ResponseEntity<Quantity> updateQuantity(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Quantity quantity
    ) throws URISyntaxException {
        log.debug("REST request to update Quantity : {}, {}", id, quantity);
        if (quantity.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, quantity.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!quantityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Quantity result = quantityService.update(quantity);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, quantity.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /quantities/:id} : Partial updates given fields of an existing quantity, field will ignore if it is null
     *
     * @param id the id of the quantity to save.
     * @param quantity the quantity to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated quantity,
     * or with status {@code 400 (Bad Request)} if the quantity is not valid,
     * or with status {@code 404 (Not Found)} if the quantity is not found,
     * or with status {@code 500 (Internal Server Error)} if the quantity couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/quantities/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Quantity> partialUpdateQuantity(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Quantity quantity
    ) throws URISyntaxException {
        log.debug("REST request to partial update Quantity partially : {}, {}", id, quantity);
        if (quantity.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, quantity.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!quantityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Quantity> result = quantityService.partialUpdate(quantity);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, quantity.getId().toString())
        );
    }

    /**
     * {@code GET  /quantities} : get all the quantities.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of quantities in body.
     */
    @GetMapping("/quantities")
    public ResponseEntity<List<Quantity>> getAllQuantities(QuantityCriteria criteria) {
        log.debug("REST request to get Quantities by criteria: {}", criteria);
        List<Quantity> entityList = quantityQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /quantities/count} : count all the quantities.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/quantities/count")
    public ResponseEntity<Long> countQuantities(QuantityCriteria criteria) {
        log.debug("REST request to count Quantities by criteria: {}", criteria);
        return ResponseEntity.ok().body(quantityQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /quantities/:id} : get the "id" quantity.
     *
     * @param id the id of the quantity to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the quantity, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/quantities/{id}")
    public ResponseEntity<Quantity> getQuantity(@PathVariable Long id) {
        log.debug("REST request to get Quantity : {}", id);
        Optional<Quantity> quantity = quantityService.findOne(id);
        return ResponseUtil.wrapOrNotFound(quantity);
    }

    /**
     * {@code DELETE  /quantities/:id} : delete the "id" quantity.
     *
     * @param id the id of the quantity to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/quantities/{id}")
    public ResponseEntity<Void> deleteQuantity(@PathVariable Long id) {
        log.debug("REST request to delete Quantity : {}", id);
        quantityService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
