package it.marcobiasone.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import it.marcobiasone.IntegrationTest;
import it.marcobiasone.domain.IngredientCategory;
import it.marcobiasone.repository.IngredientCategoryRepository;
import it.marcobiasone.service.criteria.IngredientCategoryCriteria;
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
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link IngredientCategoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class IngredientCategoryResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/ingredient-categories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private IngredientCategoryRepository ingredientCategoryRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restIngredientCategoryMockMvc;

    private IngredientCategory ingredientCategory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static IngredientCategory createEntity(EntityManager em) {
        IngredientCategory ingredientCategory = new IngredientCategory()
            .name(DEFAULT_NAME)
            .image(DEFAULT_IMAGE)
            .imageContentType(DEFAULT_IMAGE_CONTENT_TYPE)
            .description(DEFAULT_DESCRIPTION);
        return ingredientCategory;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static IngredientCategory createUpdatedEntity(EntityManager em) {
        IngredientCategory ingredientCategory = new IngredientCategory()
            .name(UPDATED_NAME)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .description(UPDATED_DESCRIPTION);
        return ingredientCategory;
    }

    @BeforeEach
    public void initTest() {
        ingredientCategory = createEntity(em);
    }

    @Test
    @Transactional
    void createIngredientCategory() throws Exception {
        int databaseSizeBeforeCreate = ingredientCategoryRepository.findAll().size();
        // Create the IngredientCategory
        restIngredientCategoryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ingredientCategory))
            )
            .andExpect(status().isCreated());

        // Validate the IngredientCategory in the database
        List<IngredientCategory> ingredientCategoryList = ingredientCategoryRepository.findAll();
        assertThat(ingredientCategoryList).hasSize(databaseSizeBeforeCreate + 1);
        IngredientCategory testIngredientCategory = ingredientCategoryList.get(ingredientCategoryList.size() - 1);
        assertThat(testIngredientCategory.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testIngredientCategory.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testIngredientCategory.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
        assertThat(testIngredientCategory.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createIngredientCategoryWithExistingId() throws Exception {
        // Create the IngredientCategory with an existing ID
        ingredientCategory.setId(1L);

        int databaseSizeBeforeCreate = ingredientCategoryRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restIngredientCategoryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ingredientCategory))
            )
            .andExpect(status().isBadRequest());

        // Validate the IngredientCategory in the database
        List<IngredientCategory> ingredientCategoryList = ingredientCategoryRepository.findAll();
        assertThat(ingredientCategoryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = ingredientCategoryRepository.findAll().size();
        // set the field null
        ingredientCategory.setName(null);

        // Create the IngredientCategory, which fails.

        restIngredientCategoryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ingredientCategory))
            )
            .andExpect(status().isBadRequest());

        List<IngredientCategory> ingredientCategoryList = ingredientCategoryRepository.findAll();
        assertThat(ingredientCategoryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllIngredientCategories() throws Exception {
        // Initialize the database
        ingredientCategoryRepository.saveAndFlush(ingredientCategory);

        // Get all the ingredientCategoryList
        restIngredientCategoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ingredientCategory.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getIngredientCategory() throws Exception {
        // Initialize the database
        ingredientCategoryRepository.saveAndFlush(ingredientCategory);

        // Get the ingredientCategory
        restIngredientCategoryMockMvc
            .perform(get(ENTITY_API_URL_ID, ingredientCategory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ingredientCategory.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.imageContentType").value(DEFAULT_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.image").value(Base64Utils.encodeToString(DEFAULT_IMAGE)))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getIngredientCategoriesByIdFiltering() throws Exception {
        // Initialize the database
        ingredientCategoryRepository.saveAndFlush(ingredientCategory);

        Long id = ingredientCategory.getId();

        defaultIngredientCategoryShouldBeFound("id.equals=" + id);
        defaultIngredientCategoryShouldNotBeFound("id.notEquals=" + id);

        defaultIngredientCategoryShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultIngredientCategoryShouldNotBeFound("id.greaterThan=" + id);

        defaultIngredientCategoryShouldBeFound("id.lessThanOrEqual=" + id);
        defaultIngredientCategoryShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllIngredientCategoriesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        ingredientCategoryRepository.saveAndFlush(ingredientCategory);

        // Get all the ingredientCategoryList where name equals to DEFAULT_NAME
        defaultIngredientCategoryShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the ingredientCategoryList where name equals to UPDATED_NAME
        defaultIngredientCategoryShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllIngredientCategoriesByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ingredientCategoryRepository.saveAndFlush(ingredientCategory);

        // Get all the ingredientCategoryList where name not equals to DEFAULT_NAME
        defaultIngredientCategoryShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the ingredientCategoryList where name not equals to UPDATED_NAME
        defaultIngredientCategoryShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllIngredientCategoriesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        ingredientCategoryRepository.saveAndFlush(ingredientCategory);

        // Get all the ingredientCategoryList where name in DEFAULT_NAME or UPDATED_NAME
        defaultIngredientCategoryShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the ingredientCategoryList where name equals to UPDATED_NAME
        defaultIngredientCategoryShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllIngredientCategoriesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        ingredientCategoryRepository.saveAndFlush(ingredientCategory);

        // Get all the ingredientCategoryList where name is not null
        defaultIngredientCategoryShouldBeFound("name.specified=true");

        // Get all the ingredientCategoryList where name is null
        defaultIngredientCategoryShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllIngredientCategoriesByNameContainsSomething() throws Exception {
        // Initialize the database
        ingredientCategoryRepository.saveAndFlush(ingredientCategory);

        // Get all the ingredientCategoryList where name contains DEFAULT_NAME
        defaultIngredientCategoryShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the ingredientCategoryList where name contains UPDATED_NAME
        defaultIngredientCategoryShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllIngredientCategoriesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        ingredientCategoryRepository.saveAndFlush(ingredientCategory);

        // Get all the ingredientCategoryList where name does not contain DEFAULT_NAME
        defaultIngredientCategoryShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the ingredientCategoryList where name does not contain UPDATED_NAME
        defaultIngredientCategoryShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllIngredientCategoriesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        ingredientCategoryRepository.saveAndFlush(ingredientCategory);

        // Get all the ingredientCategoryList where description equals to DEFAULT_DESCRIPTION
        defaultIngredientCategoryShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the ingredientCategoryList where description equals to UPDATED_DESCRIPTION
        defaultIngredientCategoryShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllIngredientCategoriesByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ingredientCategoryRepository.saveAndFlush(ingredientCategory);

        // Get all the ingredientCategoryList where description not equals to DEFAULT_DESCRIPTION
        defaultIngredientCategoryShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the ingredientCategoryList where description not equals to UPDATED_DESCRIPTION
        defaultIngredientCategoryShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllIngredientCategoriesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        ingredientCategoryRepository.saveAndFlush(ingredientCategory);

        // Get all the ingredientCategoryList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultIngredientCategoryShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the ingredientCategoryList where description equals to UPDATED_DESCRIPTION
        defaultIngredientCategoryShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllIngredientCategoriesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        ingredientCategoryRepository.saveAndFlush(ingredientCategory);

        // Get all the ingredientCategoryList where description is not null
        defaultIngredientCategoryShouldBeFound("description.specified=true");

        // Get all the ingredientCategoryList where description is null
        defaultIngredientCategoryShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllIngredientCategoriesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        ingredientCategoryRepository.saveAndFlush(ingredientCategory);

        // Get all the ingredientCategoryList where description contains DEFAULT_DESCRIPTION
        defaultIngredientCategoryShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the ingredientCategoryList where description contains UPDATED_DESCRIPTION
        defaultIngredientCategoryShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllIngredientCategoriesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        ingredientCategoryRepository.saveAndFlush(ingredientCategory);

        // Get all the ingredientCategoryList where description does not contain DEFAULT_DESCRIPTION
        defaultIngredientCategoryShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the ingredientCategoryList where description does not contain UPDATED_DESCRIPTION
        defaultIngredientCategoryShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultIngredientCategoryShouldBeFound(String filter) throws Exception {
        restIngredientCategoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ingredientCategory.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));

        // Check, that the count call also returns 1
        restIngredientCategoryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultIngredientCategoryShouldNotBeFound(String filter) throws Exception {
        restIngredientCategoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restIngredientCategoryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingIngredientCategory() throws Exception {
        // Get the ingredientCategory
        restIngredientCategoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewIngredientCategory() throws Exception {
        // Initialize the database
        ingredientCategoryRepository.saveAndFlush(ingredientCategory);

        int databaseSizeBeforeUpdate = ingredientCategoryRepository.findAll().size();

        // Update the ingredientCategory
        IngredientCategory updatedIngredientCategory = ingredientCategoryRepository.findById(ingredientCategory.getId()).get();
        // Disconnect from session so that the updates on updatedIngredientCategory are not directly saved in db
        em.detach(updatedIngredientCategory);
        updatedIngredientCategory
            .name(UPDATED_NAME)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .description(UPDATED_DESCRIPTION);

        restIngredientCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedIngredientCategory.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedIngredientCategory))
            )
            .andExpect(status().isOk());

        // Validate the IngredientCategory in the database
        List<IngredientCategory> ingredientCategoryList = ingredientCategoryRepository.findAll();
        assertThat(ingredientCategoryList).hasSize(databaseSizeBeforeUpdate);
        IngredientCategory testIngredientCategory = ingredientCategoryList.get(ingredientCategoryList.size() - 1);
        assertThat(testIngredientCategory.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testIngredientCategory.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testIngredientCategory.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
        assertThat(testIngredientCategory.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void putNonExistingIngredientCategory() throws Exception {
        int databaseSizeBeforeUpdate = ingredientCategoryRepository.findAll().size();
        ingredientCategory.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIngredientCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ingredientCategory.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ingredientCategory))
            )
            .andExpect(status().isBadRequest());

        // Validate the IngredientCategory in the database
        List<IngredientCategory> ingredientCategoryList = ingredientCategoryRepository.findAll();
        assertThat(ingredientCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchIngredientCategory() throws Exception {
        int databaseSizeBeforeUpdate = ingredientCategoryRepository.findAll().size();
        ingredientCategory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIngredientCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ingredientCategory))
            )
            .andExpect(status().isBadRequest());

        // Validate the IngredientCategory in the database
        List<IngredientCategory> ingredientCategoryList = ingredientCategoryRepository.findAll();
        assertThat(ingredientCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamIngredientCategory() throws Exception {
        int databaseSizeBeforeUpdate = ingredientCategoryRepository.findAll().size();
        ingredientCategory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIngredientCategoryMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ingredientCategory))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the IngredientCategory in the database
        List<IngredientCategory> ingredientCategoryList = ingredientCategoryRepository.findAll();
        assertThat(ingredientCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateIngredientCategoryWithPatch() throws Exception {
        // Initialize the database
        ingredientCategoryRepository.saveAndFlush(ingredientCategory);

        int databaseSizeBeforeUpdate = ingredientCategoryRepository.findAll().size();

        // Update the ingredientCategory using partial update
        IngredientCategory partialUpdatedIngredientCategory = new IngredientCategory();
        partialUpdatedIngredientCategory.setId(ingredientCategory.getId());

        partialUpdatedIngredientCategory.description(UPDATED_DESCRIPTION);

        restIngredientCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedIngredientCategory.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedIngredientCategory))
            )
            .andExpect(status().isOk());

        // Validate the IngredientCategory in the database
        List<IngredientCategory> ingredientCategoryList = ingredientCategoryRepository.findAll();
        assertThat(ingredientCategoryList).hasSize(databaseSizeBeforeUpdate);
        IngredientCategory testIngredientCategory = ingredientCategoryList.get(ingredientCategoryList.size() - 1);
        assertThat(testIngredientCategory.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testIngredientCategory.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testIngredientCategory.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
        assertThat(testIngredientCategory.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateIngredientCategoryWithPatch() throws Exception {
        // Initialize the database
        ingredientCategoryRepository.saveAndFlush(ingredientCategory);

        int databaseSizeBeforeUpdate = ingredientCategoryRepository.findAll().size();

        // Update the ingredientCategory using partial update
        IngredientCategory partialUpdatedIngredientCategory = new IngredientCategory();
        partialUpdatedIngredientCategory.setId(ingredientCategory.getId());

        partialUpdatedIngredientCategory
            .name(UPDATED_NAME)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .description(UPDATED_DESCRIPTION);

        restIngredientCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedIngredientCategory.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedIngredientCategory))
            )
            .andExpect(status().isOk());

        // Validate the IngredientCategory in the database
        List<IngredientCategory> ingredientCategoryList = ingredientCategoryRepository.findAll();
        assertThat(ingredientCategoryList).hasSize(databaseSizeBeforeUpdate);
        IngredientCategory testIngredientCategory = ingredientCategoryList.get(ingredientCategoryList.size() - 1);
        assertThat(testIngredientCategory.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testIngredientCategory.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testIngredientCategory.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
        assertThat(testIngredientCategory.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingIngredientCategory() throws Exception {
        int databaseSizeBeforeUpdate = ingredientCategoryRepository.findAll().size();
        ingredientCategory.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIngredientCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ingredientCategory.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ingredientCategory))
            )
            .andExpect(status().isBadRequest());

        // Validate the IngredientCategory in the database
        List<IngredientCategory> ingredientCategoryList = ingredientCategoryRepository.findAll();
        assertThat(ingredientCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchIngredientCategory() throws Exception {
        int databaseSizeBeforeUpdate = ingredientCategoryRepository.findAll().size();
        ingredientCategory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIngredientCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ingredientCategory))
            )
            .andExpect(status().isBadRequest());

        // Validate the IngredientCategory in the database
        List<IngredientCategory> ingredientCategoryList = ingredientCategoryRepository.findAll();
        assertThat(ingredientCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamIngredientCategory() throws Exception {
        int databaseSizeBeforeUpdate = ingredientCategoryRepository.findAll().size();
        ingredientCategory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIngredientCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ingredientCategory))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the IngredientCategory in the database
        List<IngredientCategory> ingredientCategoryList = ingredientCategoryRepository.findAll();
        assertThat(ingredientCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteIngredientCategory() throws Exception {
        // Initialize the database
        ingredientCategoryRepository.saveAndFlush(ingredientCategory);

        int databaseSizeBeforeDelete = ingredientCategoryRepository.findAll().size();

        // Delete the ingredientCategory
        restIngredientCategoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, ingredientCategory.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<IngredientCategory> ingredientCategoryList = ingredientCategoryRepository.findAll();
        assertThat(ingredientCategoryList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
