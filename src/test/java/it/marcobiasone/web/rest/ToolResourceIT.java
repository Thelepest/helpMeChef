package it.marcobiasone.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import it.marcobiasone.IntegrationTest;
import it.marcobiasone.domain.Recipe;
import it.marcobiasone.domain.Tool;
import it.marcobiasone.repository.ToolRepository;
import it.marcobiasone.service.criteria.ToolCriteria;
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
 * Integration tests for the {@link ToolResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ToolResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/tools";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ToolRepository toolRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restToolMockMvc;

    private Tool tool;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tool createEntity(EntityManager em) {
        Tool tool = new Tool().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION);
        return tool;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tool createUpdatedEntity(EntityManager em) {
        Tool tool = new Tool().name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        return tool;
    }

    @BeforeEach
    public void initTest() {
        tool = createEntity(em);
    }

    @Test
    @Transactional
    void createTool() throws Exception {
        int databaseSizeBeforeCreate = toolRepository.findAll().size();
        // Create the Tool
        restToolMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tool))
            )
            .andExpect(status().isCreated());

        // Validate the Tool in the database
        List<Tool> toolList = toolRepository.findAll();
        assertThat(toolList).hasSize(databaseSizeBeforeCreate + 1);
        Tool testTool = toolList.get(toolList.size() - 1);
        assertThat(testTool.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTool.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createToolWithExistingId() throws Exception {
        // Create the Tool with an existing ID
        tool.setId(1L);

        int databaseSizeBeforeCreate = toolRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restToolMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tool))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tool in the database
        List<Tool> toolList = toolRepository.findAll();
        assertThat(toolList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = toolRepository.findAll().size();
        // set the field null
        tool.setName(null);

        // Create the Tool, which fails.

        restToolMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tool))
            )
            .andExpect(status().isBadRequest());

        List<Tool> toolList = toolRepository.findAll();
        assertThat(toolList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTools() throws Exception {
        // Initialize the database
        toolRepository.saveAndFlush(tool);

        // Get all the toolList
        restToolMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tool.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getTool() throws Exception {
        // Initialize the database
        toolRepository.saveAndFlush(tool);

        // Get the tool
        restToolMockMvc
            .perform(get(ENTITY_API_URL_ID, tool.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(tool.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getToolsByIdFiltering() throws Exception {
        // Initialize the database
        toolRepository.saveAndFlush(tool);

        Long id = tool.getId();

        defaultToolShouldBeFound("id.equals=" + id);
        defaultToolShouldNotBeFound("id.notEquals=" + id);

        defaultToolShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultToolShouldNotBeFound("id.greaterThan=" + id);

        defaultToolShouldBeFound("id.lessThanOrEqual=" + id);
        defaultToolShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllToolsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        toolRepository.saveAndFlush(tool);

        // Get all the toolList where name equals to DEFAULT_NAME
        defaultToolShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the toolList where name equals to UPDATED_NAME
        defaultToolShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllToolsByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        toolRepository.saveAndFlush(tool);

        // Get all the toolList where name not equals to DEFAULT_NAME
        defaultToolShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the toolList where name not equals to UPDATED_NAME
        defaultToolShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllToolsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        toolRepository.saveAndFlush(tool);

        // Get all the toolList where name in DEFAULT_NAME or UPDATED_NAME
        defaultToolShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the toolList where name equals to UPDATED_NAME
        defaultToolShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllToolsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        toolRepository.saveAndFlush(tool);

        // Get all the toolList where name is not null
        defaultToolShouldBeFound("name.specified=true");

        // Get all the toolList where name is null
        defaultToolShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllToolsByNameContainsSomething() throws Exception {
        // Initialize the database
        toolRepository.saveAndFlush(tool);

        // Get all the toolList where name contains DEFAULT_NAME
        defaultToolShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the toolList where name contains UPDATED_NAME
        defaultToolShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllToolsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        toolRepository.saveAndFlush(tool);

        // Get all the toolList where name does not contain DEFAULT_NAME
        defaultToolShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the toolList where name does not contain UPDATED_NAME
        defaultToolShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllToolsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        toolRepository.saveAndFlush(tool);

        // Get all the toolList where description equals to DEFAULT_DESCRIPTION
        defaultToolShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the toolList where description equals to UPDATED_DESCRIPTION
        defaultToolShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllToolsByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        toolRepository.saveAndFlush(tool);

        // Get all the toolList where description not equals to DEFAULT_DESCRIPTION
        defaultToolShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the toolList where description not equals to UPDATED_DESCRIPTION
        defaultToolShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllToolsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        toolRepository.saveAndFlush(tool);

        // Get all the toolList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultToolShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the toolList where description equals to UPDATED_DESCRIPTION
        defaultToolShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllToolsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        toolRepository.saveAndFlush(tool);

        // Get all the toolList where description is not null
        defaultToolShouldBeFound("description.specified=true");

        // Get all the toolList where description is null
        defaultToolShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllToolsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        toolRepository.saveAndFlush(tool);

        // Get all the toolList where description contains DEFAULT_DESCRIPTION
        defaultToolShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the toolList where description contains UPDATED_DESCRIPTION
        defaultToolShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllToolsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        toolRepository.saveAndFlush(tool);

        // Get all the toolList where description does not contain DEFAULT_DESCRIPTION
        defaultToolShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the toolList where description does not contain UPDATED_DESCRIPTION
        defaultToolShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllToolsByRecipeIsEqualToSomething() throws Exception {
        // Initialize the database
        toolRepository.saveAndFlush(tool);
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
        tool.addRecipe(recipe);
        toolRepository.saveAndFlush(tool);
        Long recipeId = recipe.getId();

        // Get all the toolList where recipe equals to recipeId
        defaultToolShouldBeFound("recipeId.equals=" + recipeId);

        // Get all the toolList where recipe equals to (recipeId + 1)
        defaultToolShouldNotBeFound("recipeId.equals=" + (recipeId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultToolShouldBeFound(String filter) throws Exception {
        restToolMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tool.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));

        // Check, that the count call also returns 1
        restToolMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultToolShouldNotBeFound(String filter) throws Exception {
        restToolMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restToolMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTool() throws Exception {
        // Get the tool
        restToolMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTool() throws Exception {
        // Initialize the database
        toolRepository.saveAndFlush(tool);

        int databaseSizeBeforeUpdate = toolRepository.findAll().size();

        // Update the tool
        Tool updatedTool = toolRepository.findById(tool.getId()).get();
        // Disconnect from session so that the updates on updatedTool are not directly saved in db
        em.detach(updatedTool);
        updatedTool.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restToolMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTool.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTool))
            )
            .andExpect(status().isOk());

        // Validate the Tool in the database
        List<Tool> toolList = toolRepository.findAll();
        assertThat(toolList).hasSize(databaseSizeBeforeUpdate);
        Tool testTool = toolList.get(toolList.size() - 1);
        assertThat(testTool.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTool.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void putNonExistingTool() throws Exception {
        int databaseSizeBeforeUpdate = toolRepository.findAll().size();
        tool.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restToolMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tool.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tool))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tool in the database
        List<Tool> toolList = toolRepository.findAll();
        assertThat(toolList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTool() throws Exception {
        int databaseSizeBeforeUpdate = toolRepository.findAll().size();
        tool.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restToolMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tool))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tool in the database
        List<Tool> toolList = toolRepository.findAll();
        assertThat(toolList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTool() throws Exception {
        int databaseSizeBeforeUpdate = toolRepository.findAll().size();
        tool.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restToolMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tool))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tool in the database
        List<Tool> toolList = toolRepository.findAll();
        assertThat(toolList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateToolWithPatch() throws Exception {
        // Initialize the database
        toolRepository.saveAndFlush(tool);

        int databaseSizeBeforeUpdate = toolRepository.findAll().size();

        // Update the tool using partial update
        Tool partialUpdatedTool = new Tool();
        partialUpdatedTool.setId(tool.getId());

        partialUpdatedTool.name(UPDATED_NAME);

        restToolMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTool.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTool))
            )
            .andExpect(status().isOk());

        // Validate the Tool in the database
        List<Tool> toolList = toolRepository.findAll();
        assertThat(toolList).hasSize(databaseSizeBeforeUpdate);
        Tool testTool = toolList.get(toolList.size() - 1);
        assertThat(testTool.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTool.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateToolWithPatch() throws Exception {
        // Initialize the database
        toolRepository.saveAndFlush(tool);

        int databaseSizeBeforeUpdate = toolRepository.findAll().size();

        // Update the tool using partial update
        Tool partialUpdatedTool = new Tool();
        partialUpdatedTool.setId(tool.getId());

        partialUpdatedTool.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restToolMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTool.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTool))
            )
            .andExpect(status().isOk());

        // Validate the Tool in the database
        List<Tool> toolList = toolRepository.findAll();
        assertThat(toolList).hasSize(databaseSizeBeforeUpdate);
        Tool testTool = toolList.get(toolList.size() - 1);
        assertThat(testTool.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTool.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingTool() throws Exception {
        int databaseSizeBeforeUpdate = toolRepository.findAll().size();
        tool.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restToolMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, tool.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tool))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tool in the database
        List<Tool> toolList = toolRepository.findAll();
        assertThat(toolList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTool() throws Exception {
        int databaseSizeBeforeUpdate = toolRepository.findAll().size();
        tool.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restToolMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tool))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tool in the database
        List<Tool> toolList = toolRepository.findAll();
        assertThat(toolList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTool() throws Exception {
        int databaseSizeBeforeUpdate = toolRepository.findAll().size();
        tool.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restToolMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tool))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tool in the database
        List<Tool> toolList = toolRepository.findAll();
        assertThat(toolList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTool() throws Exception {
        // Initialize the database
        toolRepository.saveAndFlush(tool);

        int databaseSizeBeforeDelete = toolRepository.findAll().size();

        // Delete the tool
        restToolMockMvc
            .perform(delete(ENTITY_API_URL_ID, tool.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Tool> toolList = toolRepository.findAll();
        assertThat(toolList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
