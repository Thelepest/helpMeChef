package it.marcobiasone.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import it.marcobiasone.IntegrationTest;
import it.marcobiasone.domain.Ingredient;
import it.marcobiasone.domain.Ingredient;
import it.marcobiasone.domain.IngredientCategory;
import it.marcobiasone.repository.IngredientRepository;
import it.marcobiasone.service.IngredientService;
import it.marcobiasone.service.criteria.IngredientCriteria;
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
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link IngredientResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class IngredientResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/ingredients";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private IngredientRepository ingredientRepository;

    @Mock
    private IngredientRepository ingredientRepositoryMock;

    @Mock
    private IngredientService ingredientServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restIngredientMockMvc;

    private Ingredient ingredient;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ingredient createEntity(EntityManager em) {
        Ingredient ingredient = new Ingredient().name(DEFAULT_NAME).image(DEFAULT_IMAGE).imageContentType(DEFAULT_IMAGE_CONTENT_TYPE);
        return ingredient;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ingredient createUpdatedEntity(EntityManager em) {
        Ingredient ingredient = new Ingredient().name(UPDATED_NAME).image(UPDATED_IMAGE).imageContentType(UPDATED_IMAGE_CONTENT_TYPE);
        return ingredient;
    }

    @BeforeEach
    public void initTest() {
        ingredient = createEntity(em);
    }

    @Test
    @Transactional
    void createIngredient() throws Exception {
        int databaseSizeBeforeCreate = ingredientRepository.findAll().size();
        // Create the Ingredient
        restIngredientMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ingredient))
            )
            .andExpect(status().isCreated());

        // Validate the Ingredient in the database
        List<Ingredient> ingredientList = ingredientRepository.findAll();
        assertThat(ingredientList).hasSize(databaseSizeBeforeCreate + 1);
        Ingredient testIngredient = ingredientList.get(ingredientList.size() - 1);
        assertThat(testIngredient.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testIngredient.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testIngredient.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void createIngredientWithExistingId() throws Exception {
        // Create the Ingredient with an existing ID
        ingredient.setId(1L);

        int databaseSizeBeforeCreate = ingredientRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restIngredientMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ingredient))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ingredient in the database
        List<Ingredient> ingredientList = ingredientRepository.findAll();
        assertThat(ingredientList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = ingredientRepository.findAll().size();
        // set the field null
        ingredient.setName(null);

        // Create the Ingredient, which fails.

        restIngredientMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ingredient))
            )
            .andExpect(status().isBadRequest());

        List<Ingredient> ingredientList = ingredientRepository.findAll();
        assertThat(ingredientList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllIngredients() throws Exception {
        // Initialize the database
        ingredientRepository.saveAndFlush(ingredient);

        // Get all the ingredientList
        restIngredientMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ingredient.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllIngredientsWithEagerRelationshipsIsEnabled() throws Exception {
        when(ingredientServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restIngredientMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(ingredientServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllIngredientsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(ingredientServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restIngredientMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(ingredientServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getIngredient() throws Exception {
        // Initialize the database
        ingredientRepository.saveAndFlush(ingredient);

        // Get the ingredient
        restIngredientMockMvc
            .perform(get(ENTITY_API_URL_ID, ingredient.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ingredient.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.imageContentType").value(DEFAULT_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.image").value(Base64Utils.encodeToString(DEFAULT_IMAGE)));
    }

    @Test
    @Transactional
    void getIngredientsByIdFiltering() throws Exception {
        // Initialize the database
        ingredientRepository.saveAndFlush(ingredient);

        Long id = ingredient.getId();

        defaultIngredientShouldBeFound("id.equals=" + id);
        defaultIngredientShouldNotBeFound("id.notEquals=" + id);

        defaultIngredientShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultIngredientShouldNotBeFound("id.greaterThan=" + id);

        defaultIngredientShouldBeFound("id.lessThanOrEqual=" + id);
        defaultIngredientShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllIngredientsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        ingredientRepository.saveAndFlush(ingredient);

        // Get all the ingredientList where name equals to DEFAULT_NAME
        defaultIngredientShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the ingredientList where name equals to UPDATED_NAME
        defaultIngredientShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllIngredientsByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ingredientRepository.saveAndFlush(ingredient);

        // Get all the ingredientList where name not equals to DEFAULT_NAME
        defaultIngredientShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the ingredientList where name not equals to UPDATED_NAME
        defaultIngredientShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllIngredientsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        ingredientRepository.saveAndFlush(ingredient);

        // Get all the ingredientList where name in DEFAULT_NAME or UPDATED_NAME
        defaultIngredientShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the ingredientList where name equals to UPDATED_NAME
        defaultIngredientShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllIngredientsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        ingredientRepository.saveAndFlush(ingredient);

        // Get all the ingredientList where name is not null
        defaultIngredientShouldBeFound("name.specified=true");

        // Get all the ingredientList where name is null
        defaultIngredientShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllIngredientsByNameContainsSomething() throws Exception {
        // Initialize the database
        ingredientRepository.saveAndFlush(ingredient);

        // Get all the ingredientList where name contains DEFAULT_NAME
        defaultIngredientShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the ingredientList where name contains UPDATED_NAME
        defaultIngredientShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllIngredientsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        ingredientRepository.saveAndFlush(ingredient);

        // Get all the ingredientList where name does not contain DEFAULT_NAME
        defaultIngredientShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the ingredientList where name does not contain UPDATED_NAME
        defaultIngredientShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllIngredientsByIngredientcategoryIsEqualToSomething() throws Exception {
        // Initialize the database
        ingredientRepository.saveAndFlush(ingredient);
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
        ingredient.setIngredientcategory(ingredientcategory);
        ingredientRepository.saveAndFlush(ingredient);
        Long ingredientcategoryId = ingredientcategory.getId();

        // Get all the ingredientList where ingredientcategory equals to ingredientcategoryId
        defaultIngredientShouldBeFound("ingredientcategoryId.equals=" + ingredientcategoryId);

        // Get all the ingredientList where ingredientcategory equals to (ingredientcategoryId + 1)
        defaultIngredientShouldNotBeFound("ingredientcategoryId.equals=" + (ingredientcategoryId + 1));
    }

    @Test
    @Transactional
    void getAllIngredientsByParentIsEqualToSomething() throws Exception {
        // Initialize the database
        ingredientRepository.saveAndFlush(ingredient);
        Ingredient parent;
        if (TestUtil.findAll(em, Ingredient.class).isEmpty()) {
            parent = IngredientResourceIT.createEntity(em);
            em.persist(parent);
            em.flush();
        } else {
            parent = TestUtil.findAll(em, Ingredient.class).get(0);
        }
        em.persist(parent);
        em.flush();
        ingredient.setParent(parent);
        ingredientRepository.saveAndFlush(ingredient);
        Long parentId = parent.getId();

        // Get all the ingredientList where parent equals to parentId
        defaultIngredientShouldBeFound("parentId.equals=" + parentId);

        // Get all the ingredientList where parent equals to (parentId + 1)
        defaultIngredientShouldNotBeFound("parentId.equals=" + (parentId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultIngredientShouldBeFound(String filter) throws Exception {
        restIngredientMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ingredient.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))));

        // Check, that the count call also returns 1
        restIngredientMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultIngredientShouldNotBeFound(String filter) throws Exception {
        restIngredientMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restIngredientMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingIngredient() throws Exception {
        // Get the ingredient
        restIngredientMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewIngredient() throws Exception {
        // Initialize the database
        ingredientRepository.saveAndFlush(ingredient);

        int databaseSizeBeforeUpdate = ingredientRepository.findAll().size();

        // Update the ingredient
        Ingredient updatedIngredient = ingredientRepository.findById(ingredient.getId()).get();
        // Disconnect from session so that the updates on updatedIngredient are not directly saved in db
        em.detach(updatedIngredient);
        updatedIngredient.name(UPDATED_NAME).image(UPDATED_IMAGE).imageContentType(UPDATED_IMAGE_CONTENT_TYPE);

        restIngredientMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedIngredient.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedIngredient))
            )
            .andExpect(status().isOk());

        // Validate the Ingredient in the database
        List<Ingredient> ingredientList = ingredientRepository.findAll();
        assertThat(ingredientList).hasSize(databaseSizeBeforeUpdate);
        Ingredient testIngredient = ingredientList.get(ingredientList.size() - 1);
        assertThat(testIngredient.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testIngredient.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testIngredient.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingIngredient() throws Exception {
        int databaseSizeBeforeUpdate = ingredientRepository.findAll().size();
        ingredient.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIngredientMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ingredient.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ingredient))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ingredient in the database
        List<Ingredient> ingredientList = ingredientRepository.findAll();
        assertThat(ingredientList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchIngredient() throws Exception {
        int databaseSizeBeforeUpdate = ingredientRepository.findAll().size();
        ingredient.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIngredientMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ingredient))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ingredient in the database
        List<Ingredient> ingredientList = ingredientRepository.findAll();
        assertThat(ingredientList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamIngredient() throws Exception {
        int databaseSizeBeforeUpdate = ingredientRepository.findAll().size();
        ingredient.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIngredientMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ingredient))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Ingredient in the database
        List<Ingredient> ingredientList = ingredientRepository.findAll();
        assertThat(ingredientList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateIngredientWithPatch() throws Exception {
        // Initialize the database
        ingredientRepository.saveAndFlush(ingredient);

        int databaseSizeBeforeUpdate = ingredientRepository.findAll().size();

        // Update the ingredient using partial update
        Ingredient partialUpdatedIngredient = new Ingredient();
        partialUpdatedIngredient.setId(ingredient.getId());

        partialUpdatedIngredient.name(UPDATED_NAME).image(UPDATED_IMAGE).imageContentType(UPDATED_IMAGE_CONTENT_TYPE);

        restIngredientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedIngredient.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedIngredient))
            )
            .andExpect(status().isOk());

        // Validate the Ingredient in the database
        List<Ingredient> ingredientList = ingredientRepository.findAll();
        assertThat(ingredientList).hasSize(databaseSizeBeforeUpdate);
        Ingredient testIngredient = ingredientList.get(ingredientList.size() - 1);
        assertThat(testIngredient.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testIngredient.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testIngredient.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateIngredientWithPatch() throws Exception {
        // Initialize the database
        ingredientRepository.saveAndFlush(ingredient);

        int databaseSizeBeforeUpdate = ingredientRepository.findAll().size();

        // Update the ingredient using partial update
        Ingredient partialUpdatedIngredient = new Ingredient();
        partialUpdatedIngredient.setId(ingredient.getId());

        partialUpdatedIngredient.name(UPDATED_NAME).image(UPDATED_IMAGE).imageContentType(UPDATED_IMAGE_CONTENT_TYPE);

        restIngredientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedIngredient.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedIngredient))
            )
            .andExpect(status().isOk());

        // Validate the Ingredient in the database
        List<Ingredient> ingredientList = ingredientRepository.findAll();
        assertThat(ingredientList).hasSize(databaseSizeBeforeUpdate);
        Ingredient testIngredient = ingredientList.get(ingredientList.size() - 1);
        assertThat(testIngredient.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testIngredient.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testIngredient.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingIngredient() throws Exception {
        int databaseSizeBeforeUpdate = ingredientRepository.findAll().size();
        ingredient.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIngredientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ingredient.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ingredient))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ingredient in the database
        List<Ingredient> ingredientList = ingredientRepository.findAll();
        assertThat(ingredientList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchIngredient() throws Exception {
        int databaseSizeBeforeUpdate = ingredientRepository.findAll().size();
        ingredient.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIngredientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ingredient))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ingredient in the database
        List<Ingredient> ingredientList = ingredientRepository.findAll();
        assertThat(ingredientList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamIngredient() throws Exception {
        int databaseSizeBeforeUpdate = ingredientRepository.findAll().size();
        ingredient.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIngredientMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ingredient))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Ingredient in the database
        List<Ingredient> ingredientList = ingredientRepository.findAll();
        assertThat(ingredientList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteIngredient() throws Exception {
        // Initialize the database
        ingredientRepository.saveAndFlush(ingredient);

        int databaseSizeBeforeDelete = ingredientRepository.findAll().size();

        // Delete the ingredient
        restIngredientMockMvc
            .perform(delete(ENTITY_API_URL_ID, ingredient.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Ingredient> ingredientList = ingredientRepository.findAll();
        assertThat(ingredientList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
