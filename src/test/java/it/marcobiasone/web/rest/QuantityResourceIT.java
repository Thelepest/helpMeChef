package it.marcobiasone.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import it.marcobiasone.IntegrationTest;
import it.marcobiasone.domain.Quantity;
import it.marcobiasone.repository.QuantityRepository;
import it.marcobiasone.service.criteria.QuantityCriteria;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link QuantityResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class QuantityResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Long DEFAULT_AMOUNT = 1L;
    private static final Long UPDATED_AMOUNT = 2L;
    private static final Long SMALLER_AMOUNT = 1L - 1L;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/quantities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private QuantityRepository quantityRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restQuantityMockMvc;

    private Quantity quantity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Quantity createEntity(EntityManager em) {
        Quantity quantity = new Quantity().name(DEFAULT_NAME).amount(DEFAULT_AMOUNT).description(DEFAULT_DESCRIPTION);
        return quantity;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Quantity createUpdatedEntity(EntityManager em) {
        Quantity quantity = new Quantity().name(UPDATED_NAME).amount(UPDATED_AMOUNT).description(UPDATED_DESCRIPTION);
        return quantity;
    }

    @BeforeEach
    public void initTest() {
        quantity = createEntity(em);
    }

    @Test
    @Transactional
    void createQuantity() throws Exception {
        int databaseSizeBeforeCreate = quantityRepository.findAll().size();
        // Create the Quantity
        restQuantityMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(quantity))
            )
            .andExpect(status().isCreated());

        // Validate the Quantity in the database
        List<Quantity> quantityList = quantityRepository.findAll();
        assertThat(quantityList).hasSize(databaseSizeBeforeCreate + 1);
        Quantity testQuantity = quantityList.get(quantityList.size() - 1);
        assertThat(testQuantity.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testQuantity.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testQuantity.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createQuantityWithExistingId() throws Exception {
        // Create the Quantity with an existing ID
        quantity.setId(1L);

        int databaseSizeBeforeCreate = quantityRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restQuantityMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(quantity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Quantity in the database
        List<Quantity> quantityList = quantityRepository.findAll();
        assertThat(quantityList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = quantityRepository.findAll().size();
        // set the field null
        quantity.setName(null);

        // Create the Quantity, which fails.

        restQuantityMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(quantity))
            )
            .andExpect(status().isBadRequest());

        List<Quantity> quantityList = quantityRepository.findAll();
        assertThat(quantityList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = quantityRepository.findAll().size();
        // set the field null
        quantity.setAmount(null);

        // Create the Quantity, which fails.

        restQuantityMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(quantity))
            )
            .andExpect(status().isBadRequest());

        List<Quantity> quantityList = quantityRepository.findAll();
        assertThat(quantityList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllQuantities() throws Exception {
        // Initialize the database
        quantityRepository.saveAndFlush(quantity);

        // Get all the quantityList
        restQuantityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(quantity.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getQuantity() throws Exception {
        // Initialize the database
        quantityRepository.saveAndFlush(quantity);

        // Get the quantity
        restQuantityMockMvc
            .perform(get(ENTITY_API_URL_ID, quantity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(quantity.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getQuantitiesByIdFiltering() throws Exception {
        // Initialize the database
        quantityRepository.saveAndFlush(quantity);

        Long id = quantity.getId();

        defaultQuantityShouldBeFound("id.equals=" + id);
        defaultQuantityShouldNotBeFound("id.notEquals=" + id);

        defaultQuantityShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultQuantityShouldNotBeFound("id.greaterThan=" + id);

        defaultQuantityShouldBeFound("id.lessThanOrEqual=" + id);
        defaultQuantityShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllQuantitiesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        quantityRepository.saveAndFlush(quantity);

        // Get all the quantityList where name equals to DEFAULT_NAME
        defaultQuantityShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the quantityList where name equals to UPDATED_NAME
        defaultQuantityShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllQuantitiesByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        quantityRepository.saveAndFlush(quantity);

        // Get all the quantityList where name not equals to DEFAULT_NAME
        defaultQuantityShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the quantityList where name not equals to UPDATED_NAME
        defaultQuantityShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllQuantitiesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        quantityRepository.saveAndFlush(quantity);

        // Get all the quantityList where name in DEFAULT_NAME or UPDATED_NAME
        defaultQuantityShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the quantityList where name equals to UPDATED_NAME
        defaultQuantityShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllQuantitiesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        quantityRepository.saveAndFlush(quantity);

        // Get all the quantityList where name is not null
        defaultQuantityShouldBeFound("name.specified=true");

        // Get all the quantityList where name is null
        defaultQuantityShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllQuantitiesByNameContainsSomething() throws Exception {
        // Initialize the database
        quantityRepository.saveAndFlush(quantity);

        // Get all the quantityList where name contains DEFAULT_NAME
        defaultQuantityShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the quantityList where name contains UPDATED_NAME
        defaultQuantityShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllQuantitiesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        quantityRepository.saveAndFlush(quantity);

        // Get all the quantityList where name does not contain DEFAULT_NAME
        defaultQuantityShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the quantityList where name does not contain UPDATED_NAME
        defaultQuantityShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllQuantitiesByAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        quantityRepository.saveAndFlush(quantity);

        // Get all the quantityList where amount equals to DEFAULT_AMOUNT
        defaultQuantityShouldBeFound("amount.equals=" + DEFAULT_AMOUNT);

        // Get all the quantityList where amount equals to UPDATED_AMOUNT
        defaultQuantityShouldNotBeFound("amount.equals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllQuantitiesByAmountIsNotEqualToSomething() throws Exception {
        // Initialize the database
        quantityRepository.saveAndFlush(quantity);

        // Get all the quantityList where amount not equals to DEFAULT_AMOUNT
        defaultQuantityShouldNotBeFound("amount.notEquals=" + DEFAULT_AMOUNT);

        // Get all the quantityList where amount not equals to UPDATED_AMOUNT
        defaultQuantityShouldBeFound("amount.notEquals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllQuantitiesByAmountIsInShouldWork() throws Exception {
        // Initialize the database
        quantityRepository.saveAndFlush(quantity);

        // Get all the quantityList where amount in DEFAULT_AMOUNT or UPDATED_AMOUNT
        defaultQuantityShouldBeFound("amount.in=" + DEFAULT_AMOUNT + "," + UPDATED_AMOUNT);

        // Get all the quantityList where amount equals to UPDATED_AMOUNT
        defaultQuantityShouldNotBeFound("amount.in=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllQuantitiesByAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        quantityRepository.saveAndFlush(quantity);

        // Get all the quantityList where amount is not null
        defaultQuantityShouldBeFound("amount.specified=true");

        // Get all the quantityList where amount is null
        defaultQuantityShouldNotBeFound("amount.specified=false");
    }

    @Test
    @Transactional
    void getAllQuantitiesByAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        quantityRepository.saveAndFlush(quantity);

        // Get all the quantityList where amount is greater than or equal to DEFAULT_AMOUNT
        defaultQuantityShouldBeFound("amount.greaterThanOrEqual=" + DEFAULT_AMOUNT);

        // Get all the quantityList where amount is greater than or equal to UPDATED_AMOUNT
        defaultQuantityShouldNotBeFound("amount.greaterThanOrEqual=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllQuantitiesByAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        quantityRepository.saveAndFlush(quantity);

        // Get all the quantityList where amount is less than or equal to DEFAULT_AMOUNT
        defaultQuantityShouldBeFound("amount.lessThanOrEqual=" + DEFAULT_AMOUNT);

        // Get all the quantityList where amount is less than or equal to SMALLER_AMOUNT
        defaultQuantityShouldNotBeFound("amount.lessThanOrEqual=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllQuantitiesByAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        quantityRepository.saveAndFlush(quantity);

        // Get all the quantityList where amount is less than DEFAULT_AMOUNT
        defaultQuantityShouldNotBeFound("amount.lessThan=" + DEFAULT_AMOUNT);

        // Get all the quantityList where amount is less than UPDATED_AMOUNT
        defaultQuantityShouldBeFound("amount.lessThan=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllQuantitiesByAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        quantityRepository.saveAndFlush(quantity);

        // Get all the quantityList where amount is greater than DEFAULT_AMOUNT
        defaultQuantityShouldNotBeFound("amount.greaterThan=" + DEFAULT_AMOUNT);

        // Get all the quantityList where amount is greater than SMALLER_AMOUNT
        defaultQuantityShouldBeFound("amount.greaterThan=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllQuantitiesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        quantityRepository.saveAndFlush(quantity);

        // Get all the quantityList where description equals to DEFAULT_DESCRIPTION
        defaultQuantityShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the quantityList where description equals to UPDATED_DESCRIPTION
        defaultQuantityShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllQuantitiesByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        quantityRepository.saveAndFlush(quantity);

        // Get all the quantityList where description not equals to DEFAULT_DESCRIPTION
        defaultQuantityShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the quantityList where description not equals to UPDATED_DESCRIPTION
        defaultQuantityShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllQuantitiesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        quantityRepository.saveAndFlush(quantity);

        // Get all the quantityList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultQuantityShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the quantityList where description equals to UPDATED_DESCRIPTION
        defaultQuantityShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllQuantitiesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        quantityRepository.saveAndFlush(quantity);

        // Get all the quantityList where description is not null
        defaultQuantityShouldBeFound("description.specified=true");

        // Get all the quantityList where description is null
        defaultQuantityShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllQuantitiesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        quantityRepository.saveAndFlush(quantity);

        // Get all the quantityList where description contains DEFAULT_DESCRIPTION
        defaultQuantityShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the quantityList where description contains UPDATED_DESCRIPTION
        defaultQuantityShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllQuantitiesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        quantityRepository.saveAndFlush(quantity);

        // Get all the quantityList where description does not contain DEFAULT_DESCRIPTION
        defaultQuantityShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the quantityList where description does not contain UPDATED_DESCRIPTION
        defaultQuantityShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultQuantityShouldBeFound(String filter) throws Exception {
        restQuantityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(quantity.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));

        // Check, that the count call also returns 1
        restQuantityMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultQuantityShouldNotBeFound(String filter) throws Exception {
        restQuantityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restQuantityMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingQuantity() throws Exception {
        // Get the quantity
        restQuantityMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewQuantity() throws Exception {
        // Initialize the database
        quantityRepository.saveAndFlush(quantity);

        int databaseSizeBeforeUpdate = quantityRepository.findAll().size();

        // Update the quantity
        Quantity updatedQuantity = quantityRepository.findById(quantity.getId()).get();
        // Disconnect from session so that the updates on updatedQuantity are not directly saved in db
        em.detach(updatedQuantity);
        updatedQuantity.name(UPDATED_NAME).amount(UPDATED_AMOUNT).description(UPDATED_DESCRIPTION);

        restQuantityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedQuantity.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedQuantity))
            )
            .andExpect(status().isOk());

        // Validate the Quantity in the database
        List<Quantity> quantityList = quantityRepository.findAll();
        assertThat(quantityList).hasSize(databaseSizeBeforeUpdate);
        Quantity testQuantity = quantityList.get(quantityList.size() - 1);
        assertThat(testQuantity.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testQuantity.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testQuantity.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void putNonExistingQuantity() throws Exception {
        int databaseSizeBeforeUpdate = quantityRepository.findAll().size();
        quantity.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuantityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, quantity.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(quantity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Quantity in the database
        List<Quantity> quantityList = quantityRepository.findAll();
        assertThat(quantityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchQuantity() throws Exception {
        int databaseSizeBeforeUpdate = quantityRepository.findAll().size();
        quantity.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuantityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(quantity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Quantity in the database
        List<Quantity> quantityList = quantityRepository.findAll();
        assertThat(quantityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamQuantity() throws Exception {
        int databaseSizeBeforeUpdate = quantityRepository.findAll().size();
        quantity.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuantityMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(quantity))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Quantity in the database
        List<Quantity> quantityList = quantityRepository.findAll();
        assertThat(quantityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateQuantityWithPatch() throws Exception {
        // Initialize the database
        quantityRepository.saveAndFlush(quantity);

        int databaseSizeBeforeUpdate = quantityRepository.findAll().size();

        // Update the quantity using partial update
        Quantity partialUpdatedQuantity = new Quantity();
        partialUpdatedQuantity.setId(quantity.getId());

        partialUpdatedQuantity.amount(UPDATED_AMOUNT).description(UPDATED_DESCRIPTION);

        restQuantityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQuantity.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedQuantity))
            )
            .andExpect(status().isOk());

        // Validate the Quantity in the database
        List<Quantity> quantityList = quantityRepository.findAll();
        assertThat(quantityList).hasSize(databaseSizeBeforeUpdate);
        Quantity testQuantity = quantityList.get(quantityList.size() - 1);
        assertThat(testQuantity.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testQuantity.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testQuantity.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateQuantityWithPatch() throws Exception {
        // Initialize the database
        quantityRepository.saveAndFlush(quantity);

        int databaseSizeBeforeUpdate = quantityRepository.findAll().size();

        // Update the quantity using partial update
        Quantity partialUpdatedQuantity = new Quantity();
        partialUpdatedQuantity.setId(quantity.getId());

        partialUpdatedQuantity.name(UPDATED_NAME).amount(UPDATED_AMOUNT).description(UPDATED_DESCRIPTION);

        restQuantityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQuantity.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedQuantity))
            )
            .andExpect(status().isOk());

        // Validate the Quantity in the database
        List<Quantity> quantityList = quantityRepository.findAll();
        assertThat(quantityList).hasSize(databaseSizeBeforeUpdate);
        Quantity testQuantity = quantityList.get(quantityList.size() - 1);
        assertThat(testQuantity.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testQuantity.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testQuantity.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingQuantity() throws Exception {
        int databaseSizeBeforeUpdate = quantityRepository.findAll().size();
        quantity.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuantityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, quantity.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(quantity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Quantity in the database
        List<Quantity> quantityList = quantityRepository.findAll();
        assertThat(quantityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchQuantity() throws Exception {
        int databaseSizeBeforeUpdate = quantityRepository.findAll().size();
        quantity.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuantityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(quantity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Quantity in the database
        List<Quantity> quantityList = quantityRepository.findAll();
        assertThat(quantityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamQuantity() throws Exception {
        int databaseSizeBeforeUpdate = quantityRepository.findAll().size();
        quantity.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuantityMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(quantity))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Quantity in the database
        List<Quantity> quantityList = quantityRepository.findAll();
        assertThat(quantityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteQuantity() throws Exception {
        // Initialize the database
        quantityRepository.saveAndFlush(quantity);

        int databaseSizeBeforeDelete = quantityRepository.findAll().size();

        // Delete the quantity
        restQuantityMockMvc
            .perform(delete(ENTITY_API_URL_ID, quantity.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Quantity> quantityList = quantityRepository.findAll();
        assertThat(quantityList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
