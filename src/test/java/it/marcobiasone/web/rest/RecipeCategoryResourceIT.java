package it.marcobiasone.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import it.marcobiasone.IntegrationTest;
import it.marcobiasone.domain.RecipeCategory;
import it.marcobiasone.repository.RecipeCategoryRepository;
import it.marcobiasone.service.criteria.RecipeCategoryCriteria;
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
 * Integration tests for the {@link RecipeCategoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RecipeCategoryResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/recipe-categories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RecipeCategoryRepository recipeCategoryRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRecipeCategoryMockMvc;

    private RecipeCategory recipeCategory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RecipeCategory createEntity(EntityManager em) {
        RecipeCategory recipeCategory = new RecipeCategory()
            .name(DEFAULT_NAME)
            .image(DEFAULT_IMAGE)
            .imageContentType(DEFAULT_IMAGE_CONTENT_TYPE)
            .description(DEFAULT_DESCRIPTION);
        return recipeCategory;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RecipeCategory createUpdatedEntity(EntityManager em) {
        RecipeCategory recipeCategory = new RecipeCategory()
            .name(UPDATED_NAME)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .description(UPDATED_DESCRIPTION);
        return recipeCategory;
    }

    @BeforeEach
    public void initTest() {
        recipeCategory = createEntity(em);
    }

    @Test
    @Transactional
    void createRecipeCategory() throws Exception {
        int databaseSizeBeforeCreate = recipeCategoryRepository.findAll().size();
        // Create the RecipeCategory
        restRecipeCategoryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(recipeCategory))
            )
            .andExpect(status().isCreated());

        // Validate the RecipeCategory in the database
        List<RecipeCategory> recipeCategoryList = recipeCategoryRepository.findAll();
        assertThat(recipeCategoryList).hasSize(databaseSizeBeforeCreate + 1);
        RecipeCategory testRecipeCategory = recipeCategoryList.get(recipeCategoryList.size() - 1);
        assertThat(testRecipeCategory.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testRecipeCategory.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testRecipeCategory.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
        assertThat(testRecipeCategory.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createRecipeCategoryWithExistingId() throws Exception {
        // Create the RecipeCategory with an existing ID
        recipeCategory.setId(1L);

        int databaseSizeBeforeCreate = recipeCategoryRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRecipeCategoryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(recipeCategory))
            )
            .andExpect(status().isBadRequest());

        // Validate the RecipeCategory in the database
        List<RecipeCategory> recipeCategoryList = recipeCategoryRepository.findAll();
        assertThat(recipeCategoryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = recipeCategoryRepository.findAll().size();
        // set the field null
        recipeCategory.setName(null);

        // Create the RecipeCategory, which fails.

        restRecipeCategoryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(recipeCategory))
            )
            .andExpect(status().isBadRequest());

        List<RecipeCategory> recipeCategoryList = recipeCategoryRepository.findAll();
        assertThat(recipeCategoryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllRecipeCategories() throws Exception {
        // Initialize the database
        recipeCategoryRepository.saveAndFlush(recipeCategory);

        // Get all the recipeCategoryList
        restRecipeCategoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(recipeCategory.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getRecipeCategory() throws Exception {
        // Initialize the database
        recipeCategoryRepository.saveAndFlush(recipeCategory);

        // Get the recipeCategory
        restRecipeCategoryMockMvc
            .perform(get(ENTITY_API_URL_ID, recipeCategory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(recipeCategory.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.imageContentType").value(DEFAULT_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.image").value(Base64Utils.encodeToString(DEFAULT_IMAGE)))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getRecipeCategoriesByIdFiltering() throws Exception {
        // Initialize the database
        recipeCategoryRepository.saveAndFlush(recipeCategory);

        Long id = recipeCategory.getId();

        defaultRecipeCategoryShouldBeFound("id.equals=" + id);
        defaultRecipeCategoryShouldNotBeFound("id.notEquals=" + id);

        defaultRecipeCategoryShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultRecipeCategoryShouldNotBeFound("id.greaterThan=" + id);

        defaultRecipeCategoryShouldBeFound("id.lessThanOrEqual=" + id);
        defaultRecipeCategoryShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllRecipeCategoriesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        recipeCategoryRepository.saveAndFlush(recipeCategory);

        // Get all the recipeCategoryList where name equals to DEFAULT_NAME
        defaultRecipeCategoryShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the recipeCategoryList where name equals to UPDATED_NAME
        defaultRecipeCategoryShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllRecipeCategoriesByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        recipeCategoryRepository.saveAndFlush(recipeCategory);

        // Get all the recipeCategoryList where name not equals to DEFAULT_NAME
        defaultRecipeCategoryShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the recipeCategoryList where name not equals to UPDATED_NAME
        defaultRecipeCategoryShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllRecipeCategoriesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        recipeCategoryRepository.saveAndFlush(recipeCategory);

        // Get all the recipeCategoryList where name in DEFAULT_NAME or UPDATED_NAME
        defaultRecipeCategoryShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the recipeCategoryList where name equals to UPDATED_NAME
        defaultRecipeCategoryShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllRecipeCategoriesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        recipeCategoryRepository.saveAndFlush(recipeCategory);

        // Get all the recipeCategoryList where name is not null
        defaultRecipeCategoryShouldBeFound("name.specified=true");

        // Get all the recipeCategoryList where name is null
        defaultRecipeCategoryShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllRecipeCategoriesByNameContainsSomething() throws Exception {
        // Initialize the database
        recipeCategoryRepository.saveAndFlush(recipeCategory);

        // Get all the recipeCategoryList where name contains DEFAULT_NAME
        defaultRecipeCategoryShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the recipeCategoryList where name contains UPDATED_NAME
        defaultRecipeCategoryShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllRecipeCategoriesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        recipeCategoryRepository.saveAndFlush(recipeCategory);

        // Get all the recipeCategoryList where name does not contain DEFAULT_NAME
        defaultRecipeCategoryShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the recipeCategoryList where name does not contain UPDATED_NAME
        defaultRecipeCategoryShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllRecipeCategoriesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        recipeCategoryRepository.saveAndFlush(recipeCategory);

        // Get all the recipeCategoryList where description equals to DEFAULT_DESCRIPTION
        defaultRecipeCategoryShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the recipeCategoryList where description equals to UPDATED_DESCRIPTION
        defaultRecipeCategoryShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllRecipeCategoriesByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        recipeCategoryRepository.saveAndFlush(recipeCategory);

        // Get all the recipeCategoryList where description not equals to DEFAULT_DESCRIPTION
        defaultRecipeCategoryShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the recipeCategoryList where description not equals to UPDATED_DESCRIPTION
        defaultRecipeCategoryShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllRecipeCategoriesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        recipeCategoryRepository.saveAndFlush(recipeCategory);

        // Get all the recipeCategoryList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultRecipeCategoryShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the recipeCategoryList where description equals to UPDATED_DESCRIPTION
        defaultRecipeCategoryShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllRecipeCategoriesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        recipeCategoryRepository.saveAndFlush(recipeCategory);

        // Get all the recipeCategoryList where description is not null
        defaultRecipeCategoryShouldBeFound("description.specified=true");

        // Get all the recipeCategoryList where description is null
        defaultRecipeCategoryShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllRecipeCategoriesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        recipeCategoryRepository.saveAndFlush(recipeCategory);

        // Get all the recipeCategoryList where description contains DEFAULT_DESCRIPTION
        defaultRecipeCategoryShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the recipeCategoryList where description contains UPDATED_DESCRIPTION
        defaultRecipeCategoryShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllRecipeCategoriesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        recipeCategoryRepository.saveAndFlush(recipeCategory);

        // Get all the recipeCategoryList where description does not contain DEFAULT_DESCRIPTION
        defaultRecipeCategoryShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the recipeCategoryList where description does not contain UPDATED_DESCRIPTION
        defaultRecipeCategoryShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRecipeCategoryShouldBeFound(String filter) throws Exception {
        restRecipeCategoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(recipeCategory.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));

        // Check, that the count call also returns 1
        restRecipeCategoryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRecipeCategoryShouldNotBeFound(String filter) throws Exception {
        restRecipeCategoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRecipeCategoryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingRecipeCategory() throws Exception {
        // Get the recipeCategory
        restRecipeCategoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewRecipeCategory() throws Exception {
        // Initialize the database
        recipeCategoryRepository.saveAndFlush(recipeCategory);

        int databaseSizeBeforeUpdate = recipeCategoryRepository.findAll().size();

        // Update the recipeCategory
        RecipeCategory updatedRecipeCategory = recipeCategoryRepository.findById(recipeCategory.getId()).get();
        // Disconnect from session so that the updates on updatedRecipeCategory are not directly saved in db
        em.detach(updatedRecipeCategory);
        updatedRecipeCategory
            .name(UPDATED_NAME)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .description(UPDATED_DESCRIPTION);

        restRecipeCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedRecipeCategory.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedRecipeCategory))
            )
            .andExpect(status().isOk());

        // Validate the RecipeCategory in the database
        List<RecipeCategory> recipeCategoryList = recipeCategoryRepository.findAll();
        assertThat(recipeCategoryList).hasSize(databaseSizeBeforeUpdate);
        RecipeCategory testRecipeCategory = recipeCategoryList.get(recipeCategoryList.size() - 1);
        assertThat(testRecipeCategory.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRecipeCategory.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testRecipeCategory.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
        assertThat(testRecipeCategory.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void putNonExistingRecipeCategory() throws Exception {
        int databaseSizeBeforeUpdate = recipeCategoryRepository.findAll().size();
        recipeCategory.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRecipeCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, recipeCategory.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(recipeCategory))
            )
            .andExpect(status().isBadRequest());

        // Validate the RecipeCategory in the database
        List<RecipeCategory> recipeCategoryList = recipeCategoryRepository.findAll();
        assertThat(recipeCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRecipeCategory() throws Exception {
        int databaseSizeBeforeUpdate = recipeCategoryRepository.findAll().size();
        recipeCategory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecipeCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(recipeCategory))
            )
            .andExpect(status().isBadRequest());

        // Validate the RecipeCategory in the database
        List<RecipeCategory> recipeCategoryList = recipeCategoryRepository.findAll();
        assertThat(recipeCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRecipeCategory() throws Exception {
        int databaseSizeBeforeUpdate = recipeCategoryRepository.findAll().size();
        recipeCategory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecipeCategoryMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(recipeCategory))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the RecipeCategory in the database
        List<RecipeCategory> recipeCategoryList = recipeCategoryRepository.findAll();
        assertThat(recipeCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRecipeCategoryWithPatch() throws Exception {
        // Initialize the database
        recipeCategoryRepository.saveAndFlush(recipeCategory);

        int databaseSizeBeforeUpdate = recipeCategoryRepository.findAll().size();

        // Update the recipeCategory using partial update
        RecipeCategory partialUpdatedRecipeCategory = new RecipeCategory();
        partialUpdatedRecipeCategory.setId(recipeCategory.getId());

        partialUpdatedRecipeCategory
            .name(UPDATED_NAME)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .description(UPDATED_DESCRIPTION);

        restRecipeCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRecipeCategory.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRecipeCategory))
            )
            .andExpect(status().isOk());

        // Validate the RecipeCategory in the database
        List<RecipeCategory> recipeCategoryList = recipeCategoryRepository.findAll();
        assertThat(recipeCategoryList).hasSize(databaseSizeBeforeUpdate);
        RecipeCategory testRecipeCategory = recipeCategoryList.get(recipeCategoryList.size() - 1);
        assertThat(testRecipeCategory.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRecipeCategory.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testRecipeCategory.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
        assertThat(testRecipeCategory.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateRecipeCategoryWithPatch() throws Exception {
        // Initialize the database
        recipeCategoryRepository.saveAndFlush(recipeCategory);

        int databaseSizeBeforeUpdate = recipeCategoryRepository.findAll().size();

        // Update the recipeCategory using partial update
        RecipeCategory partialUpdatedRecipeCategory = new RecipeCategory();
        partialUpdatedRecipeCategory.setId(recipeCategory.getId());

        partialUpdatedRecipeCategory
            .name(UPDATED_NAME)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .description(UPDATED_DESCRIPTION);

        restRecipeCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRecipeCategory.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRecipeCategory))
            )
            .andExpect(status().isOk());

        // Validate the RecipeCategory in the database
        List<RecipeCategory> recipeCategoryList = recipeCategoryRepository.findAll();
        assertThat(recipeCategoryList).hasSize(databaseSizeBeforeUpdate);
        RecipeCategory testRecipeCategory = recipeCategoryList.get(recipeCategoryList.size() - 1);
        assertThat(testRecipeCategory.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRecipeCategory.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testRecipeCategory.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
        assertThat(testRecipeCategory.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingRecipeCategory() throws Exception {
        int databaseSizeBeforeUpdate = recipeCategoryRepository.findAll().size();
        recipeCategory.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRecipeCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, recipeCategory.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(recipeCategory))
            )
            .andExpect(status().isBadRequest());

        // Validate the RecipeCategory in the database
        List<RecipeCategory> recipeCategoryList = recipeCategoryRepository.findAll();
        assertThat(recipeCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRecipeCategory() throws Exception {
        int databaseSizeBeforeUpdate = recipeCategoryRepository.findAll().size();
        recipeCategory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecipeCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(recipeCategory))
            )
            .andExpect(status().isBadRequest());

        // Validate the RecipeCategory in the database
        List<RecipeCategory> recipeCategoryList = recipeCategoryRepository.findAll();
        assertThat(recipeCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRecipeCategory() throws Exception {
        int databaseSizeBeforeUpdate = recipeCategoryRepository.findAll().size();
        recipeCategory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecipeCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(recipeCategory))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the RecipeCategory in the database
        List<RecipeCategory> recipeCategoryList = recipeCategoryRepository.findAll();
        assertThat(recipeCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRecipeCategory() throws Exception {
        // Initialize the database
        recipeCategoryRepository.saveAndFlush(recipeCategory);

        int databaseSizeBeforeDelete = recipeCategoryRepository.findAll().size();

        // Delete the recipeCategory
        restRecipeCategoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, recipeCategory.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<RecipeCategory> recipeCategoryList = recipeCategoryRepository.findAll();
        assertThat(recipeCategoryList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
