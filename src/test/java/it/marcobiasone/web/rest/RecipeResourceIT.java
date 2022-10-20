package it.marcobiasone.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import it.marcobiasone.IntegrationTest;
import it.marcobiasone.domain.IngredientQuantity;
import it.marcobiasone.domain.Recipe;
import it.marcobiasone.domain.RecipeCategory;
import it.marcobiasone.domain.Tool;
import it.marcobiasone.repository.RecipeRepository;
import it.marcobiasone.service.RecipeService;
import it.marcobiasone.service.criteria.RecipeCriteria;
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
 * Integration tests for the {@link RecipeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class RecipeResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Double DEFAULT_TIME = 1D;
    private static final Double UPDATED_TIME = 2D;
    private static final Double SMALLER_TIME = 1D - 1D;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/recipes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RecipeRepository recipeRepository;

    @Mock
    private RecipeRepository recipeRepositoryMock;

    @Mock
    private RecipeService recipeServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRecipeMockMvc;

    private Recipe recipe;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Recipe createEntity(EntityManager em) {
        Recipe recipe = new Recipe().name(DEFAULT_NAME).time(DEFAULT_TIME).description(DEFAULT_DESCRIPTION);
        return recipe;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Recipe createUpdatedEntity(EntityManager em) {
        Recipe recipe = new Recipe().name(UPDATED_NAME).time(UPDATED_TIME).description(UPDATED_DESCRIPTION);
        return recipe;
    }

    @BeforeEach
    public void initTest() {
        recipe = createEntity(em);
    }

    @Test
    @Transactional
    void createRecipe() throws Exception {
        int databaseSizeBeforeCreate = recipeRepository.findAll().size();
        // Create the Recipe
        restRecipeMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(recipe))
            )
            .andExpect(status().isCreated());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeCreate + 1);
        Recipe testRecipe = recipeList.get(recipeList.size() - 1);
        assertThat(testRecipe.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testRecipe.getTime()).isEqualTo(DEFAULT_TIME);
        assertThat(testRecipe.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createRecipeWithExistingId() throws Exception {
        // Create the Recipe with an existing ID
        recipe.setId(1L);

        int databaseSizeBeforeCreate = recipeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRecipeMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(recipe))
            )
            .andExpect(status().isBadRequest());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = recipeRepository.findAll().size();
        // set the field null
        recipe.setName(null);

        // Create the Recipe, which fails.

        restRecipeMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(recipe))
            )
            .andExpect(status().isBadRequest());

        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllRecipes() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        // Get all the recipeList
        restRecipeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(recipe.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].time").value(hasItem(DEFAULT_TIME.doubleValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRecipesWithEagerRelationshipsIsEnabled() throws Exception {
        when(recipeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRecipeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(recipeServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRecipesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(recipeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRecipeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(recipeServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getRecipe() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        // Get the recipe
        restRecipeMockMvc
            .perform(get(ENTITY_API_URL_ID, recipe.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(recipe.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.time").value(DEFAULT_TIME.doubleValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    void getRecipesByIdFiltering() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        Long id = recipe.getId();

        defaultRecipeShouldBeFound("id.equals=" + id);
        defaultRecipeShouldNotBeFound("id.notEquals=" + id);

        defaultRecipeShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultRecipeShouldNotBeFound("id.greaterThan=" + id);

        defaultRecipeShouldBeFound("id.lessThanOrEqual=" + id);
        defaultRecipeShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllRecipesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        // Get all the recipeList where name equals to DEFAULT_NAME
        defaultRecipeShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the recipeList where name equals to UPDATED_NAME
        defaultRecipeShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllRecipesByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        // Get all the recipeList where name not equals to DEFAULT_NAME
        defaultRecipeShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the recipeList where name not equals to UPDATED_NAME
        defaultRecipeShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllRecipesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        // Get all the recipeList where name in DEFAULT_NAME or UPDATED_NAME
        defaultRecipeShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the recipeList where name equals to UPDATED_NAME
        defaultRecipeShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllRecipesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        // Get all the recipeList where name is not null
        defaultRecipeShouldBeFound("name.specified=true");

        // Get all the recipeList where name is null
        defaultRecipeShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllRecipesByNameContainsSomething() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        // Get all the recipeList where name contains DEFAULT_NAME
        defaultRecipeShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the recipeList where name contains UPDATED_NAME
        defaultRecipeShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllRecipesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        // Get all the recipeList where name does not contain DEFAULT_NAME
        defaultRecipeShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the recipeList where name does not contain UPDATED_NAME
        defaultRecipeShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllRecipesByTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        // Get all the recipeList where time equals to DEFAULT_TIME
        defaultRecipeShouldBeFound("time.equals=" + DEFAULT_TIME);

        // Get all the recipeList where time equals to UPDATED_TIME
        defaultRecipeShouldNotBeFound("time.equals=" + UPDATED_TIME);
    }

    @Test
    @Transactional
    void getAllRecipesByTimeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        // Get all the recipeList where time not equals to DEFAULT_TIME
        defaultRecipeShouldNotBeFound("time.notEquals=" + DEFAULT_TIME);

        // Get all the recipeList where time not equals to UPDATED_TIME
        defaultRecipeShouldBeFound("time.notEquals=" + UPDATED_TIME);
    }

    @Test
    @Transactional
    void getAllRecipesByTimeIsInShouldWork() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        // Get all the recipeList where time in DEFAULT_TIME or UPDATED_TIME
        defaultRecipeShouldBeFound("time.in=" + DEFAULT_TIME + "," + UPDATED_TIME);

        // Get all the recipeList where time equals to UPDATED_TIME
        defaultRecipeShouldNotBeFound("time.in=" + UPDATED_TIME);
    }

    @Test
    @Transactional
    void getAllRecipesByTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        // Get all the recipeList where time is not null
        defaultRecipeShouldBeFound("time.specified=true");

        // Get all the recipeList where time is null
        defaultRecipeShouldNotBeFound("time.specified=false");
    }

    @Test
    @Transactional
    void getAllRecipesByTimeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        // Get all the recipeList where time is greater than or equal to DEFAULT_TIME
        defaultRecipeShouldBeFound("time.greaterThanOrEqual=" + DEFAULT_TIME);

        // Get all the recipeList where time is greater than or equal to UPDATED_TIME
        defaultRecipeShouldNotBeFound("time.greaterThanOrEqual=" + UPDATED_TIME);
    }

    @Test
    @Transactional
    void getAllRecipesByTimeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        // Get all the recipeList where time is less than or equal to DEFAULT_TIME
        defaultRecipeShouldBeFound("time.lessThanOrEqual=" + DEFAULT_TIME);

        // Get all the recipeList where time is less than or equal to SMALLER_TIME
        defaultRecipeShouldNotBeFound("time.lessThanOrEqual=" + SMALLER_TIME);
    }

    @Test
    @Transactional
    void getAllRecipesByTimeIsLessThanSomething() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        // Get all the recipeList where time is less than DEFAULT_TIME
        defaultRecipeShouldNotBeFound("time.lessThan=" + DEFAULT_TIME);

        // Get all the recipeList where time is less than UPDATED_TIME
        defaultRecipeShouldBeFound("time.lessThan=" + UPDATED_TIME);
    }

    @Test
    @Transactional
    void getAllRecipesByTimeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        // Get all the recipeList where time is greater than DEFAULT_TIME
        defaultRecipeShouldNotBeFound("time.greaterThan=" + DEFAULT_TIME);

        // Get all the recipeList where time is greater than SMALLER_TIME
        defaultRecipeShouldBeFound("time.greaterThan=" + SMALLER_TIME);
    }

    @Test
    @Transactional
    void getAllRecipesByRecipecategoryIsEqualToSomething() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);
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
        recipe.setRecipecategory(recipecategory);
        recipeRepository.saveAndFlush(recipe);
        Long recipecategoryId = recipecategory.getId();

        // Get all the recipeList where recipecategory equals to recipecategoryId
        defaultRecipeShouldBeFound("recipecategoryId.equals=" + recipecategoryId);

        // Get all the recipeList where recipecategory equals to (recipecategoryId + 1)
        defaultRecipeShouldNotBeFound("recipecategoryId.equals=" + (recipecategoryId + 1));
    }

    @Test
    @Transactional
    void getAllRecipesByIngredientquantityIsEqualToSomething() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);
        IngredientQuantity ingredientquantity;
        if (TestUtil.findAll(em, IngredientQuantity.class).isEmpty()) {
            ingredientquantity = IngredientQuantityResourceIT.createEntity(em);
            em.persist(ingredientquantity);
            em.flush();
        } else {
            ingredientquantity = TestUtil.findAll(em, IngredientQuantity.class).get(0);
        }
        em.persist(ingredientquantity);
        em.flush();
        recipe.addIngredientquantity(ingredientquantity);
        recipeRepository.saveAndFlush(recipe);
        Long ingredientquantityId = ingredientquantity.getId();

        // Get all the recipeList where ingredientquantity equals to ingredientquantityId
        defaultRecipeShouldBeFound("ingredientquantityId.equals=" + ingredientquantityId);

        // Get all the recipeList where ingredientquantity equals to (ingredientquantityId + 1)
        defaultRecipeShouldNotBeFound("ingredientquantityId.equals=" + (ingredientquantityId + 1));
    }

    @Test
    @Transactional
    void getAllRecipesByToolIsEqualToSomething() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);
        Tool tool;
        if (TestUtil.findAll(em, Tool.class).isEmpty()) {
            tool = ToolResourceIT.createEntity(em);
            em.persist(tool);
            em.flush();
        } else {
            tool = TestUtil.findAll(em, Tool.class).get(0);
        }
        em.persist(tool);
        em.flush();
        recipe.addTool(tool);
        recipeRepository.saveAndFlush(recipe);
        Long toolId = tool.getId();

        // Get all the recipeList where tool equals to toolId
        defaultRecipeShouldBeFound("toolId.equals=" + toolId);

        // Get all the recipeList where tool equals to (toolId + 1)
        defaultRecipeShouldNotBeFound("toolId.equals=" + (toolId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRecipeShouldBeFound(String filter) throws Exception {
        restRecipeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(recipe.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].time").value(hasItem(DEFAULT_TIME.doubleValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));

        // Check, that the count call also returns 1
        restRecipeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRecipeShouldNotBeFound(String filter) throws Exception {
        restRecipeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRecipeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingRecipe() throws Exception {
        // Get the recipe
        restRecipeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewRecipe() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        int databaseSizeBeforeUpdate = recipeRepository.findAll().size();

        // Update the recipe
        Recipe updatedRecipe = recipeRepository.findById(recipe.getId()).get();
        // Disconnect from session so that the updates on updatedRecipe are not directly saved in db
        em.detach(updatedRecipe);
        updatedRecipe.name(UPDATED_NAME).time(UPDATED_TIME).description(UPDATED_DESCRIPTION);

        restRecipeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedRecipe.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedRecipe))
            )
            .andExpect(status().isOk());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate);
        Recipe testRecipe = recipeList.get(recipeList.size() - 1);
        assertThat(testRecipe.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRecipe.getTime()).isEqualTo(UPDATED_TIME);
        assertThat(testRecipe.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void putNonExistingRecipe() throws Exception {
        int databaseSizeBeforeUpdate = recipeRepository.findAll().size();
        recipe.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRecipeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, recipe.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(recipe))
            )
            .andExpect(status().isBadRequest());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRecipe() throws Exception {
        int databaseSizeBeforeUpdate = recipeRepository.findAll().size();
        recipe.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecipeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(recipe))
            )
            .andExpect(status().isBadRequest());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRecipe() throws Exception {
        int databaseSizeBeforeUpdate = recipeRepository.findAll().size();
        recipe.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecipeMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(recipe))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRecipeWithPatch() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        int databaseSizeBeforeUpdate = recipeRepository.findAll().size();

        // Update the recipe using partial update
        Recipe partialUpdatedRecipe = new Recipe();
        partialUpdatedRecipe.setId(recipe.getId());

        partialUpdatedRecipe.name(UPDATED_NAME).time(UPDATED_TIME);

        restRecipeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRecipe.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRecipe))
            )
            .andExpect(status().isOk());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate);
        Recipe testRecipe = recipeList.get(recipeList.size() - 1);
        assertThat(testRecipe.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRecipe.getTime()).isEqualTo(UPDATED_TIME);
        assertThat(testRecipe.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateRecipeWithPatch() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        int databaseSizeBeforeUpdate = recipeRepository.findAll().size();

        // Update the recipe using partial update
        Recipe partialUpdatedRecipe = new Recipe();
        partialUpdatedRecipe.setId(recipe.getId());

        partialUpdatedRecipe.name(UPDATED_NAME).time(UPDATED_TIME).description(UPDATED_DESCRIPTION);

        restRecipeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRecipe.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRecipe))
            )
            .andExpect(status().isOk());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate);
        Recipe testRecipe = recipeList.get(recipeList.size() - 1);
        assertThat(testRecipe.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRecipe.getTime()).isEqualTo(UPDATED_TIME);
        assertThat(testRecipe.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingRecipe() throws Exception {
        int databaseSizeBeforeUpdate = recipeRepository.findAll().size();
        recipe.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRecipeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, recipe.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(recipe))
            )
            .andExpect(status().isBadRequest());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRecipe() throws Exception {
        int databaseSizeBeforeUpdate = recipeRepository.findAll().size();
        recipe.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecipeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(recipe))
            )
            .andExpect(status().isBadRequest());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRecipe() throws Exception {
        int databaseSizeBeforeUpdate = recipeRepository.findAll().size();
        recipe.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecipeMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(recipe))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRecipe() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        int databaseSizeBeforeDelete = recipeRepository.findAll().size();

        // Delete the recipe
        restRecipeMockMvc
            .perform(delete(ENTITY_API_URL_ID, recipe.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
