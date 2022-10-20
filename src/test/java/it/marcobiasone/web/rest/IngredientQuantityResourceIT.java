package it.marcobiasone.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import it.marcobiasone.IntegrationTest;
import it.marcobiasone.domain.Ingredient;
import it.marcobiasone.domain.IngredientQuantity;
import it.marcobiasone.domain.Pantry;
import it.marcobiasone.domain.Quantity;
import it.marcobiasone.domain.Recipe;
import it.marcobiasone.repository.IngredientQuantityRepository;
import it.marcobiasone.service.IngredientQuantityService;
import it.marcobiasone.service.criteria.IngredientQuantityCriteria;
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
 * Integration tests for the {@link IngredientQuantityResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class IngredientQuantityResourceIT {

    private static final String ENTITY_API_URL = "/api/ingredient-quantities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private IngredientQuantityRepository ingredientQuantityRepository;

    @Mock
    private IngredientQuantityRepository ingredientQuantityRepositoryMock;

    @Mock
    private IngredientQuantityService ingredientQuantityServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restIngredientQuantityMockMvc;

    private IngredientQuantity ingredientQuantity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static IngredientQuantity createEntity(EntityManager em) {
        IngredientQuantity ingredientQuantity = new IngredientQuantity();
        return ingredientQuantity;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static IngredientQuantity createUpdatedEntity(EntityManager em) {
        IngredientQuantity ingredientQuantity = new IngredientQuantity();
        return ingredientQuantity;
    }

    @BeforeEach
    public void initTest() {
        ingredientQuantity = createEntity(em);
    }

    @Test
    @Transactional
    void createIngredientQuantity() throws Exception {
        int databaseSizeBeforeCreate = ingredientQuantityRepository.findAll().size();
        // Create the IngredientQuantity
        restIngredientQuantityMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ingredientQuantity))
            )
            .andExpect(status().isCreated());

        // Validate the IngredientQuantity in the database
        List<IngredientQuantity> ingredientQuantityList = ingredientQuantityRepository.findAll();
        assertThat(ingredientQuantityList).hasSize(databaseSizeBeforeCreate + 1);
        IngredientQuantity testIngredientQuantity = ingredientQuantityList.get(ingredientQuantityList.size() - 1);
    }

    @Test
    @Transactional
    void createIngredientQuantityWithExistingId() throws Exception {
        // Create the IngredientQuantity with an existing ID
        ingredientQuantity.setId(1L);

        int databaseSizeBeforeCreate = ingredientQuantityRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restIngredientQuantityMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ingredientQuantity))
            )
            .andExpect(status().isBadRequest());

        // Validate the IngredientQuantity in the database
        List<IngredientQuantity> ingredientQuantityList = ingredientQuantityRepository.findAll();
        assertThat(ingredientQuantityList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllIngredientQuantities() throws Exception {
        // Initialize the database
        ingredientQuantityRepository.saveAndFlush(ingredientQuantity);

        // Get all the ingredientQuantityList
        restIngredientQuantityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ingredientQuantity.getId().intValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllIngredientQuantitiesWithEagerRelationshipsIsEnabled() throws Exception {
        when(ingredientQuantityServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restIngredientQuantityMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(ingredientQuantityServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllIngredientQuantitiesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(ingredientQuantityServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restIngredientQuantityMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(ingredientQuantityServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getIngredientQuantity() throws Exception {
        // Initialize the database
        ingredientQuantityRepository.saveAndFlush(ingredientQuantity);

        // Get the ingredientQuantity
        restIngredientQuantityMockMvc
            .perform(get(ENTITY_API_URL_ID, ingredientQuantity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ingredientQuantity.getId().intValue()));
    }

    @Test
    @Transactional
    void getIngredientQuantitiesByIdFiltering() throws Exception {
        // Initialize the database
        ingredientQuantityRepository.saveAndFlush(ingredientQuantity);

        Long id = ingredientQuantity.getId();

        defaultIngredientQuantityShouldBeFound("id.equals=" + id);
        defaultIngredientQuantityShouldNotBeFound("id.notEquals=" + id);

        defaultIngredientQuantityShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultIngredientQuantityShouldNotBeFound("id.greaterThan=" + id);

        defaultIngredientQuantityShouldBeFound("id.lessThanOrEqual=" + id);
        defaultIngredientQuantityShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllIngredientQuantitiesByIngredientIsEqualToSomething() throws Exception {
        // Initialize the database
        ingredientQuantityRepository.saveAndFlush(ingredientQuantity);
        Ingredient ingredient;
        if (TestUtil.findAll(em, Ingredient.class).isEmpty()) {
            ingredient = IngredientResourceIT.createEntity(em);
            em.persist(ingredient);
            em.flush();
        } else {
            ingredient = TestUtil.findAll(em, Ingredient.class).get(0);
        }
        em.persist(ingredient);
        em.flush();
        ingredientQuantity.setIngredient(ingredient);
        ingredientQuantityRepository.saveAndFlush(ingredientQuantity);
        Long ingredientId = ingredient.getId();

        // Get all the ingredientQuantityList where ingredient equals to ingredientId
        defaultIngredientQuantityShouldBeFound("ingredientId.equals=" + ingredientId);

        // Get all the ingredientQuantityList where ingredient equals to (ingredientId + 1)
        defaultIngredientQuantityShouldNotBeFound("ingredientId.equals=" + (ingredientId + 1));
    }

    @Test
    @Transactional
    void getAllIngredientQuantitiesByQuantityIsEqualToSomething() throws Exception {
        // Initialize the database
        ingredientQuantityRepository.saveAndFlush(ingredientQuantity);
        Quantity quantity;
        if (TestUtil.findAll(em, Quantity.class).isEmpty()) {
            quantity = QuantityResourceIT.createEntity(em);
            em.persist(quantity);
            em.flush();
        } else {
            quantity = TestUtil.findAll(em, Quantity.class).get(0);
        }
        em.persist(quantity);
        em.flush();
        ingredientQuantity.setQuantity(quantity);
        ingredientQuantityRepository.saveAndFlush(ingredientQuantity);
        Long quantityId = quantity.getId();

        // Get all the ingredientQuantityList where quantity equals to quantityId
        defaultIngredientQuantityShouldBeFound("quantityId.equals=" + quantityId);

        // Get all the ingredientQuantityList where quantity equals to (quantityId + 1)
        defaultIngredientQuantityShouldNotBeFound("quantityId.equals=" + (quantityId + 1));
    }

    @Test
    @Transactional
    void getAllIngredientQuantitiesByPantryIsEqualToSomething() throws Exception {
        // Initialize the database
        ingredientQuantityRepository.saveAndFlush(ingredientQuantity);
        Pantry pantry;
        if (TestUtil.findAll(em, Pantry.class).isEmpty()) {
            pantry = PantryResourceIT.createEntity(em);
            em.persist(pantry);
            em.flush();
        } else {
            pantry = TestUtil.findAll(em, Pantry.class).get(0);
        }
        em.persist(pantry);
        em.flush();
        ingredientQuantity.addPantry(pantry);
        ingredientQuantityRepository.saveAndFlush(ingredientQuantity);
        Long pantryId = pantry.getId();

        // Get all the ingredientQuantityList where pantry equals to pantryId
        defaultIngredientQuantityShouldBeFound("pantryId.equals=" + pantryId);

        // Get all the ingredientQuantityList where pantry equals to (pantryId + 1)
        defaultIngredientQuantityShouldNotBeFound("pantryId.equals=" + (pantryId + 1));
    }

    @Test
    @Transactional
    void getAllIngredientQuantitiesByRecipeIsEqualToSomething() throws Exception {
        // Initialize the database
        ingredientQuantityRepository.saveAndFlush(ingredientQuantity);
        Recipe recipe;
        if (TestUtil.findAll(em, Recipe.class).isEmpty()) {
            recipe = RecipeResourceIT.createEntity(em);
            em.persist(recipe);
            em.flush();
        } else {
            recipe = TestUtil.findAll(em, Recipe.class).get(0);
        }
        em.persist(recipe);
        em.flush();
        ingredientQuantity.addRecipe(recipe);
        ingredientQuantityRepository.saveAndFlush(ingredientQuantity);
        Long recipeId = recipe.getId();

        // Get all the ingredientQuantityList where recipe equals to recipeId
        defaultIngredientQuantityShouldBeFound("recipeId.equals=" + recipeId);

        // Get all the ingredientQuantityList where recipe equals to (recipeId + 1)
        defaultIngredientQuantityShouldNotBeFound("recipeId.equals=" + (recipeId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultIngredientQuantityShouldBeFound(String filter) throws Exception {
        restIngredientQuantityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ingredientQuantity.getId().intValue())));

        // Check, that the count call also returns 1
        restIngredientQuantityMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultIngredientQuantityShouldNotBeFound(String filter) throws Exception {
        restIngredientQuantityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restIngredientQuantityMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingIngredientQuantity() throws Exception {
        // Get the ingredientQuantity
        restIngredientQuantityMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewIngredientQuantity() throws Exception {
        // Initialize the database
        ingredientQuantityRepository.saveAndFlush(ingredientQuantity);

        int databaseSizeBeforeUpdate = ingredientQuantityRepository.findAll().size();

        // Update the ingredientQuantity
        IngredientQuantity updatedIngredientQuantity = ingredientQuantityRepository.findById(ingredientQuantity.getId()).get();
        // Disconnect from session so that the updates on updatedIngredientQuantity are not directly saved in db
        em.detach(updatedIngredientQuantity);

        restIngredientQuantityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedIngredientQuantity.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedIngredientQuantity))
            )
            .andExpect(status().isOk());

        // Validate the IngredientQuantity in the database
        List<IngredientQuantity> ingredientQuantityList = ingredientQuantityRepository.findAll();
        assertThat(ingredientQuantityList).hasSize(databaseSizeBeforeUpdate);
        IngredientQuantity testIngredientQuantity = ingredientQuantityList.get(ingredientQuantityList.size() - 1);
    }

    @Test
    @Transactional
    void putNonExistingIngredientQuantity() throws Exception {
        int databaseSizeBeforeUpdate = ingredientQuantityRepository.findAll().size();
        ingredientQuantity.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIngredientQuantityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ingredientQuantity.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ingredientQuantity))
            )
            .andExpect(status().isBadRequest());

        // Validate the IngredientQuantity in the database
        List<IngredientQuantity> ingredientQuantityList = ingredientQuantityRepository.findAll();
        assertThat(ingredientQuantityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchIngredientQuantity() throws Exception {
        int databaseSizeBeforeUpdate = ingredientQuantityRepository.findAll().size();
        ingredientQuantity.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIngredientQuantityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ingredientQuantity))
            )
            .andExpect(status().isBadRequest());

        // Validate the IngredientQuantity in the database
        List<IngredientQuantity> ingredientQuantityList = ingredientQuantityRepository.findAll();
        assertThat(ingredientQuantityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamIngredientQuantity() throws Exception {
        int databaseSizeBeforeUpdate = ingredientQuantityRepository.findAll().size();
        ingredientQuantity.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIngredientQuantityMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ingredientQuantity))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the IngredientQuantity in the database
        List<IngredientQuantity> ingredientQuantityList = ingredientQuantityRepository.findAll();
        assertThat(ingredientQuantityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateIngredientQuantityWithPatch() throws Exception {
        // Initialize the database
        ingredientQuantityRepository.saveAndFlush(ingredientQuantity);

        int databaseSizeBeforeUpdate = ingredientQuantityRepository.findAll().size();

        // Update the ingredientQuantity using partial update
        IngredientQuantity partialUpdatedIngredientQuantity = new IngredientQuantity();
        partialUpdatedIngredientQuantity.setId(ingredientQuantity.getId());

        restIngredientQuantityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedIngredientQuantity.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedIngredientQuantity))
            )
            .andExpect(status().isOk());

        // Validate the IngredientQuantity in the database
        List<IngredientQuantity> ingredientQuantityList = ingredientQuantityRepository.findAll();
        assertThat(ingredientQuantityList).hasSize(databaseSizeBeforeUpdate);
        IngredientQuantity testIngredientQuantity = ingredientQuantityList.get(ingredientQuantityList.size() - 1);
    }

    @Test
    @Transactional
    void fullUpdateIngredientQuantityWithPatch() throws Exception {
        // Initialize the database
        ingredientQuantityRepository.saveAndFlush(ingredientQuantity);

        int databaseSizeBeforeUpdate = ingredientQuantityRepository.findAll().size();

        // Update the ingredientQuantity using partial update
        IngredientQuantity partialUpdatedIngredientQuantity = new IngredientQuantity();
        partialUpdatedIngredientQuantity.setId(ingredientQuantity.getId());

        restIngredientQuantityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedIngredientQuantity.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedIngredientQuantity))
            )
            .andExpect(status().isOk());

        // Validate the IngredientQuantity in the database
        List<IngredientQuantity> ingredientQuantityList = ingredientQuantityRepository.findAll();
        assertThat(ingredientQuantityList).hasSize(databaseSizeBeforeUpdate);
        IngredientQuantity testIngredientQuantity = ingredientQuantityList.get(ingredientQuantityList.size() - 1);
    }

    @Test
    @Transactional
    void patchNonExistingIngredientQuantity() throws Exception {
        int databaseSizeBeforeUpdate = ingredientQuantityRepository.findAll().size();
        ingredientQuantity.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIngredientQuantityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ingredientQuantity.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ingredientQuantity))
            )
            .andExpect(status().isBadRequest());

        // Validate the IngredientQuantity in the database
        List<IngredientQuantity> ingredientQuantityList = ingredientQuantityRepository.findAll();
        assertThat(ingredientQuantityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchIngredientQuantity() throws Exception {
        int databaseSizeBeforeUpdate = ingredientQuantityRepository.findAll().size();
        ingredientQuantity.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIngredientQuantityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ingredientQuantity))
            )
            .andExpect(status().isBadRequest());

        // Validate the IngredientQuantity in the database
        List<IngredientQuantity> ingredientQuantityList = ingredientQuantityRepository.findAll();
        assertThat(ingredientQuantityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamIngredientQuantity() throws Exception {
        int databaseSizeBeforeUpdate = ingredientQuantityRepository.findAll().size();
        ingredientQuantity.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIngredientQuantityMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ingredientQuantity))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the IngredientQuantity in the database
        List<IngredientQuantity> ingredientQuantityList = ingredientQuantityRepository.findAll();
        assertThat(ingredientQuantityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteIngredientQuantity() throws Exception {
        // Initialize the database
        ingredientQuantityRepository.saveAndFlush(ingredientQuantity);

        int databaseSizeBeforeDelete = ingredientQuantityRepository.findAll().size();

        // Delete the ingredientQuantity
        restIngredientQuantityMockMvc
            .perform(delete(ENTITY_API_URL_ID, ingredientQuantity.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<IngredientQuantity> ingredientQuantityList = ingredientQuantityRepository.findAll();
        assertThat(ingredientQuantityList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
