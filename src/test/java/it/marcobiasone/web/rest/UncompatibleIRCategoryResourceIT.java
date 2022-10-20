package it.marcobiasone.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import it.marcobiasone.IntegrationTest;
import it.marcobiasone.domain.IngredientCategory;
import it.marcobiasone.domain.RecipeCategory;
import it.marcobiasone.domain.UncompatibleIRCategory;
import it.marcobiasone.repository.UncompatibleIRCategoryRepository;
import it.marcobiasone.service.UncompatibleIRCategoryService;
import it.marcobiasone.service.criteria.UncompatibleIRCategoryCriteria;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link UncompatibleIRCategoryResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class UncompatibleIRCategoryResourceIT {

    private static final String ENTITY_API_URL = "/api/uncompatible-ir-categories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UncompatibleIRCategoryRepository uncompatibleIRCategoryRepository;

    @Mock
    private UncompatibleIRCategoryRepository uncompatibleIRCategoryRepositoryMock;

    @Mock
    private UncompatibleIRCategoryService uncompatibleIRCategoryServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUncompatibleIRCategoryMockMvc;

    private UncompatibleIRCategory uncompatibleIRCategory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UncompatibleIRCategory createEntity(EntityManager em) {
        UncompatibleIRCategory uncompatibleIRCategory = new UncompatibleIRCategory();
        return uncompatibleIRCategory;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UncompatibleIRCategory createUpdatedEntity(EntityManager em) {
        UncompatibleIRCategory uncompatibleIRCategory = new UncompatibleIRCategory();
        return uncompatibleIRCategory;
    }

    @BeforeEach
    public void initTest() {
        uncompatibleIRCategory = createEntity(em);
    }

    @Test
    @Transactional
    void createUncompatibleIRCategory() throws Exception {
        int databaseSizeBeforeCreate = uncompatibleIRCategoryRepository.findAll().size();
        // Create the UncompatibleIRCategory
        restUncompatibleIRCategoryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(uncompatibleIRCategory))
            )
            .andExpect(status().isCreated());

        // Validate the UncompatibleIRCategory in the database
        List<UncompatibleIRCategory> uncompatibleIRCategoryList = uncompatibleIRCategoryRepository.findAll();
        assertThat(uncompatibleIRCategoryList).hasSize(databaseSizeBeforeCreate + 1);
        UncompatibleIRCategory testUncompatibleIRCategory = uncompatibleIRCategoryList.get(uncompatibleIRCategoryList.size() - 1);
    }

    @Test
    @Transactional
    void createUncompatibleIRCategoryWithExistingId() throws Exception {
        // Create the UncompatibleIRCategory with an existing ID
        uncompatibleIRCategory.setId(1L);

        int databaseSizeBeforeCreate = uncompatibleIRCategoryRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUncompatibleIRCategoryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(uncompatibleIRCategory))
            )
            .andExpect(status().isBadRequest());

        // Validate the UncompatibleIRCategory in the database
        List<UncompatibleIRCategory> uncompatibleIRCategoryList = uncompatibleIRCategoryRepository.findAll();
        assertThat(uncompatibleIRCategoryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllUncompatibleIRCategories() throws Exception {
        // Initialize the database
        uncompatibleIRCategoryRepository.saveAndFlush(uncompatibleIRCategory);

        // Get all the uncompatibleIRCategoryList
        restUncompatibleIRCategoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(uncompatibleIRCategory.getId().intValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllUncompatibleIRCategoriesWithEagerRelationshipsIsEnabled() throws Exception {
        when(uncompatibleIRCategoryServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restUncompatibleIRCategoryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(uncompatibleIRCategoryServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllUncompatibleIRCategoriesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(uncompatibleIRCategoryServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restUncompatibleIRCategoryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(uncompatibleIRCategoryServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getUncompatibleIRCategory() throws Exception {
        // Initialize the database
        uncompatibleIRCategoryRepository.saveAndFlush(uncompatibleIRCategory);

        // Get the uncompatibleIRCategory
        restUncompatibleIRCategoryMockMvc
            .perform(get(ENTITY_API_URL_ID, uncompatibleIRCategory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(uncompatibleIRCategory.getId().intValue()));
    }

    @Test
    @Transactional
    void getUncompatibleIRCategoriesByIdFiltering() throws Exception {
        // Initialize the database
        uncompatibleIRCategoryRepository.saveAndFlush(uncompatibleIRCategory);

        Long id = uncompatibleIRCategory.getId();

        defaultUncompatibleIRCategoryShouldBeFound("id.equals=" + id);
        defaultUncompatibleIRCategoryShouldNotBeFound("id.notEquals=" + id);

        defaultUncompatibleIRCategoryShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultUncompatibleIRCategoryShouldNotBeFound("id.greaterThan=" + id);

        defaultUncompatibleIRCategoryShouldBeFound("id.lessThanOrEqual=" + id);
        defaultUncompatibleIRCategoryShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllUncompatibleIRCategoriesByIngredientcategoryIsEqualToSomething() throws Exception {
        // Initialize the database
        uncompatibleIRCategoryRepository.saveAndFlush(uncompatibleIRCategory);
        IngredientCategory ingredientcategory;
        if (TestUtil.findAll(em, IngredientCategory.class).isEmpty()) {
            ingredientcategory = IngredientCategoryResourceIT.createEntity(em);
            em.persist(ingredientcategory);
            em.flush();
        } else {
            ingredientcategory = TestUtil.findAll(em, IngredientCategory.class).get(0);
        }
        em.persist(ingredientcategory);
        em.flush();
        uncompatibleIRCategory.setIngredientcategory(ingredientcategory);
        uncompatibleIRCategoryRepository.saveAndFlush(uncompatibleIRCategory);
        Long ingredientcategoryId = ingredientcategory.getId();

        // Get all the uncompatibleIRCategoryList where ingredientcategory equals to ingredientcategoryId
        defaultUncompatibleIRCategoryShouldBeFound("ingredientcategoryId.equals=" + ingredientcategoryId);

        // Get all the uncompatibleIRCategoryList where ingredientcategory equals to (ingredientcategoryId + 1)
        defaultUncompatibleIRCategoryShouldNotBeFound("ingredientcategoryId.equals=" + (ingredientcategoryId + 1));
    }

    @Test
    @Transactional
    void getAllUncompatibleIRCategoriesByRecipecategoryIsEqualToSomething() throws Exception {
        // Initialize the database
        uncompatibleIRCategoryRepository.saveAndFlush(uncompatibleIRCategory);
        RecipeCategory recipecategory;
        if (TestUtil.findAll(em, RecipeCategory.class).isEmpty()) {
            recipecategory = RecipeCategoryResourceIT.createEntity(em);
            em.persist(recipecategory);
            em.flush();
        } else {
            recipecategory = TestUtil.findAll(em, RecipeCategory.class).get(0);
        }
        em.persist(recipecategory);
        em.flush();
        uncompatibleIRCategory.setRecipecategory(recipecategory);
        uncompatibleIRCategoryRepository.saveAndFlush(uncompatibleIRCategory);
        Long recipecategoryId = recipecategory.getId();

        // Get all the uncompatibleIRCategoryList where recipecategory equals to recipecategoryId
        defaultUncompatibleIRCategoryShouldBeFound("recipecategoryId.equals=" + recipecategoryId);

        // Get all the uncompatibleIRCategoryList where recipecategory equals to (recipecategoryId + 1)
        defaultUncompatibleIRCategoryShouldNotBeFound("recipecategoryId.equals=" + (recipecategoryId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultUncompatibleIRCategoryShouldBeFound(String filter) throws Exception {
        restUncompatibleIRCategoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(uncompatibleIRCategory.getId().intValue())));

        // Check, that the count call also returns 1
        restUncompatibleIRCategoryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultUncompatibleIRCategoryShouldNotBeFound(String filter) throws Exception {
        restUncompatibleIRCategoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restUncompatibleIRCategoryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingUncompatibleIRCategory() throws Exception {
        // Get the uncompatibleIRCategory
        restUncompatibleIRCategoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewUncompatibleIRCategory() throws Exception {
        // Initialize the database
        uncompatibleIRCategoryRepository.saveAndFlush(uncompatibleIRCategory);

        int databaseSizeBeforeUpdate = uncompatibleIRCategoryRepository.findAll().size();

        // Update the uncompatibleIRCategory
        UncompatibleIRCategory updatedUncompatibleIRCategory = uncompatibleIRCategoryRepository
            .findById(uncompatibleIRCategory.getId())
            .get();
        // Disconnect from session so that the updates on updatedUncompatibleIRCategory are not directly saved in db
        em.detach(updatedUncompatibleIRCategory);

        restUncompatibleIRCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedUncompatibleIRCategory.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedUncompatibleIRCategory))
            )
            .andExpect(status().isOk());

        // Validate the UncompatibleIRCategory in the database
        List<UncompatibleIRCategory> uncompatibleIRCategoryList = uncompatibleIRCategoryRepository.findAll();
        assertThat(uncompatibleIRCategoryList).hasSize(databaseSizeBeforeUpdate);
        UncompatibleIRCategory testUncompatibleIRCategory = uncompatibleIRCategoryList.get(uncompatibleIRCategoryList.size() - 1);
    }

    @Test
    @Transactional
    void putNonExistingUncompatibleIRCategory() throws Exception {
        int databaseSizeBeforeUpdate = uncompatibleIRCategoryRepository.findAll().size();
        uncompatibleIRCategory.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUncompatibleIRCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, uncompatibleIRCategory.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(uncompatibleIRCategory))
            )
            .andExpect(status().isBadRequest());

        // Validate the UncompatibleIRCategory in the database
        List<UncompatibleIRCategory> uncompatibleIRCategoryList = uncompatibleIRCategoryRepository.findAll();
        assertThat(uncompatibleIRCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUncompatibleIRCategory() throws Exception {
        int databaseSizeBeforeUpdate = uncompatibleIRCategoryRepository.findAll().size();
        uncompatibleIRCategory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUncompatibleIRCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(uncompatibleIRCategory))
            )
            .andExpect(status().isBadRequest());

        // Validate the UncompatibleIRCategory in the database
        List<UncompatibleIRCategory> uncompatibleIRCategoryList = uncompatibleIRCategoryRepository.findAll();
        assertThat(uncompatibleIRCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUncompatibleIRCategory() throws Exception {
        int databaseSizeBeforeUpdate = uncompatibleIRCategoryRepository.findAll().size();
        uncompatibleIRCategory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUncompatibleIRCategoryMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(uncompatibleIRCategory))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the UncompatibleIRCategory in the database
        List<UncompatibleIRCategory> uncompatibleIRCategoryList = uncompatibleIRCategoryRepository.findAll();
        assertThat(uncompatibleIRCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUncompatibleIRCategoryWithPatch() throws Exception {
        // Initialize the database
        uncompatibleIRCategoryRepository.saveAndFlush(uncompatibleIRCategory);

        int databaseSizeBeforeUpdate = uncompatibleIRCategoryRepository.findAll().size();

        // Update the uncompatibleIRCategory using partial update
        UncompatibleIRCategory partialUpdatedUncompatibleIRCategory = new UncompatibleIRCategory();
        partialUpdatedUncompatibleIRCategory.setId(uncompatibleIRCategory.getId());

        restUncompatibleIRCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUncompatibleIRCategory.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUncompatibleIRCategory))
            )
            .andExpect(status().isOk());

        // Validate the UncompatibleIRCategory in the database
        List<UncompatibleIRCategory> uncompatibleIRCategoryList = uncompatibleIRCategoryRepository.findAll();
        assertThat(uncompatibleIRCategoryList).hasSize(databaseSizeBeforeUpdate);
        UncompatibleIRCategory testUncompatibleIRCategory = uncompatibleIRCategoryList.get(uncompatibleIRCategoryList.size() - 1);
    }

    @Test
    @Transactional
    void fullUpdateUncompatibleIRCategoryWithPatch() throws Exception {
        // Initialize the database
        uncompatibleIRCategoryRepository.saveAndFlush(uncompatibleIRCategory);

        int databaseSizeBeforeUpdate = uncompatibleIRCategoryRepository.findAll().size();

        // Update the uncompatibleIRCategory using partial update
        UncompatibleIRCategory partialUpdatedUncompatibleIRCategory = new UncompatibleIRCategory();
        partialUpdatedUncompatibleIRCategory.setId(uncompatibleIRCategory.getId());

        restUncompatibleIRCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUncompatibleIRCategory.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUncompatibleIRCategory))
            )
            .andExpect(status().isOk());

        // Validate the UncompatibleIRCategory in the database
        List<UncompatibleIRCategory> uncompatibleIRCategoryList = uncompatibleIRCategoryRepository.findAll();
        assertThat(uncompatibleIRCategoryList).hasSize(databaseSizeBeforeUpdate);
        UncompatibleIRCategory testUncompatibleIRCategory = uncompatibleIRCategoryList.get(uncompatibleIRCategoryList.size() - 1);
    }

    @Test
    @Transactional
    void patchNonExistingUncompatibleIRCategory() throws Exception {
        int databaseSizeBeforeUpdate = uncompatibleIRCategoryRepository.findAll().size();
        uncompatibleIRCategory.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUncompatibleIRCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, uncompatibleIRCategory.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(uncompatibleIRCategory))
            )
            .andExpect(status().isBadRequest());

        // Validate the UncompatibleIRCategory in the database
        List<UncompatibleIRCategory> uncompatibleIRCategoryList = uncompatibleIRCategoryRepository.findAll();
        assertThat(uncompatibleIRCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUncompatibleIRCategory() throws Exception {
        int databaseSizeBeforeUpdate = uncompatibleIRCategoryRepository.findAll().size();
        uncompatibleIRCategory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUncompatibleIRCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(uncompatibleIRCategory))
            )
            .andExpect(status().isBadRequest());

        // Validate the UncompatibleIRCategory in the database
        List<UncompatibleIRCategory> uncompatibleIRCategoryList = uncompatibleIRCategoryRepository.findAll();
        assertThat(uncompatibleIRCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUncompatibleIRCategory() throws Exception {
        int databaseSizeBeforeUpdate = uncompatibleIRCategoryRepository.findAll().size();
        uncompatibleIRCategory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUncompatibleIRCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(uncompatibleIRCategory))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the UncompatibleIRCategory in the database
        List<UncompatibleIRCategory> uncompatibleIRCategoryList = uncompatibleIRCategoryRepository.findAll();
        assertThat(uncompatibleIRCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUncompatibleIRCategory() throws Exception {
        // Initialize the database
        uncompatibleIRCategoryRepository.saveAndFlush(uncompatibleIRCategory);

        int databaseSizeBeforeDelete = uncompatibleIRCategoryRepository.findAll().size();

        // Delete the uncompatibleIRCategory
        restUncompatibleIRCategoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, uncompatibleIRCategory.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<UncompatibleIRCategory> uncompatibleIRCategoryList = uncompatibleIRCategoryRepository.findAll();
        assertThat(uncompatibleIRCategoryList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
