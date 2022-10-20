package it.marcobiasone.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import it.marcobiasone.IntegrationTest;
import it.marcobiasone.domain.MyConfig;
import it.marcobiasone.repository.MyConfigRepository;
import it.marcobiasone.service.criteria.MyConfigCriteria;
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
 * Integration tests for the {@link MyConfigResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MyConfigResourceIT {

    private static final String DEFAULT_MC_KEY = "AAAAAAAAAA";
    private static final String UPDATED_MC_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_MC_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_MC_VALUE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/my-configs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private MyConfigRepository myConfigRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMyConfigMockMvc;

    private MyConfig myConfig;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MyConfig createEntity(EntityManager em) {
        MyConfig myConfig = new MyConfig().mcKey(DEFAULT_MC_KEY).mcValue(DEFAULT_MC_VALUE);
        return myConfig;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MyConfig createUpdatedEntity(EntityManager em) {
        MyConfig myConfig = new MyConfig().mcKey(UPDATED_MC_KEY).mcValue(UPDATED_MC_VALUE);
        return myConfig;
    }

    @BeforeEach
    public void initTest() {
        myConfig = createEntity(em);
    }

    @Test
    @Transactional
    void createMyConfig() throws Exception {
        int databaseSizeBeforeCreate = myConfigRepository.findAll().size();
        // Create the MyConfig
        restMyConfigMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(myConfig))
            )
            .andExpect(status().isCreated());

        // Validate the MyConfig in the database
        List<MyConfig> myConfigList = myConfigRepository.findAll();
        assertThat(myConfigList).hasSize(databaseSizeBeforeCreate + 1);
        MyConfig testMyConfig = myConfigList.get(myConfigList.size() - 1);
        assertThat(testMyConfig.getMcKey()).isEqualTo(DEFAULT_MC_KEY);
        assertThat(testMyConfig.getMcValue()).isEqualTo(DEFAULT_MC_VALUE);
    }

    @Test
    @Transactional
    void createMyConfigWithExistingId() throws Exception {
        // Create the MyConfig with an existing ID
        myConfig.setId(1L);

        int databaseSizeBeforeCreate = myConfigRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMyConfigMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(myConfig))
            )
            .andExpect(status().isBadRequest());

        // Validate the MyConfig in the database
        List<MyConfig> myConfigList = myConfigRepository.findAll();
        assertThat(myConfigList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkMcKeyIsRequired() throws Exception {
        int databaseSizeBeforeTest = myConfigRepository.findAll().size();
        // set the field null
        myConfig.setMcKey(null);

        // Create the MyConfig, which fails.

        restMyConfigMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(myConfig))
            )
            .andExpect(status().isBadRequest());

        List<MyConfig> myConfigList = myConfigRepository.findAll();
        assertThat(myConfigList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMcValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = myConfigRepository.findAll().size();
        // set the field null
        myConfig.setMcValue(null);

        // Create the MyConfig, which fails.

        restMyConfigMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(myConfig))
            )
            .andExpect(status().isBadRequest());

        List<MyConfig> myConfigList = myConfigRepository.findAll();
        assertThat(myConfigList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMyConfigs() throws Exception {
        // Initialize the database
        myConfigRepository.saveAndFlush(myConfig);

        // Get all the myConfigList
        restMyConfigMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(myConfig.getId().intValue())))
            .andExpect(jsonPath("$.[*].mcKey").value(hasItem(DEFAULT_MC_KEY)))
            .andExpect(jsonPath("$.[*].mcValue").value(hasItem(DEFAULT_MC_VALUE)));
    }

    @Test
    @Transactional
    void getMyConfig() throws Exception {
        // Initialize the database
        myConfigRepository.saveAndFlush(myConfig);

        // Get the myConfig
        restMyConfigMockMvc
            .perform(get(ENTITY_API_URL_ID, myConfig.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(myConfig.getId().intValue()))
            .andExpect(jsonPath("$.mcKey").value(DEFAULT_MC_KEY))
            .andExpect(jsonPath("$.mcValue").value(DEFAULT_MC_VALUE));
    }

    @Test
    @Transactional
    void getMyConfigsByIdFiltering() throws Exception {
        // Initialize the database
        myConfigRepository.saveAndFlush(myConfig);

        Long id = myConfig.getId();

        defaultMyConfigShouldBeFound("id.equals=" + id);
        defaultMyConfigShouldNotBeFound("id.notEquals=" + id);

        defaultMyConfigShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultMyConfigShouldNotBeFound("id.greaterThan=" + id);

        defaultMyConfigShouldBeFound("id.lessThanOrEqual=" + id);
        defaultMyConfigShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllMyConfigsByMcKeyIsEqualToSomething() throws Exception {
        // Initialize the database
        myConfigRepository.saveAndFlush(myConfig);

        // Get all the myConfigList where mcKey equals to DEFAULT_MC_KEY
        defaultMyConfigShouldBeFound("mcKey.equals=" + DEFAULT_MC_KEY);

        // Get all the myConfigList where mcKey equals to UPDATED_MC_KEY
        defaultMyConfigShouldNotBeFound("mcKey.equals=" + UPDATED_MC_KEY);
    }

    @Test
    @Transactional
    void getAllMyConfigsByMcKeyIsNotEqualToSomething() throws Exception {
        // Initialize the database
        myConfigRepository.saveAndFlush(myConfig);

        // Get all the myConfigList where mcKey not equals to DEFAULT_MC_KEY
        defaultMyConfigShouldNotBeFound("mcKey.notEquals=" + DEFAULT_MC_KEY);

        // Get all the myConfigList where mcKey not equals to UPDATED_MC_KEY
        defaultMyConfigShouldBeFound("mcKey.notEquals=" + UPDATED_MC_KEY);
    }

    @Test
    @Transactional
    void getAllMyConfigsByMcKeyIsInShouldWork() throws Exception {
        // Initialize the database
        myConfigRepository.saveAndFlush(myConfig);

        // Get all the myConfigList where mcKey in DEFAULT_MC_KEY or UPDATED_MC_KEY
        defaultMyConfigShouldBeFound("mcKey.in=" + DEFAULT_MC_KEY + "," + UPDATED_MC_KEY);

        // Get all the myConfigList where mcKey equals to UPDATED_MC_KEY
        defaultMyConfigShouldNotBeFound("mcKey.in=" + UPDATED_MC_KEY);
    }

    @Test
    @Transactional
    void getAllMyConfigsByMcKeyIsNullOrNotNull() throws Exception {
        // Initialize the database
        myConfigRepository.saveAndFlush(myConfig);

        // Get all the myConfigList where mcKey is not null
        defaultMyConfigShouldBeFound("mcKey.specified=true");

        // Get all the myConfigList where mcKey is null
        defaultMyConfigShouldNotBeFound("mcKey.specified=false");
    }

    @Test
    @Transactional
    void getAllMyConfigsByMcKeyContainsSomething() throws Exception {
        // Initialize the database
        myConfigRepository.saveAndFlush(myConfig);

        // Get all the myConfigList where mcKey contains DEFAULT_MC_KEY
        defaultMyConfigShouldBeFound("mcKey.contains=" + DEFAULT_MC_KEY);

        // Get all the myConfigList where mcKey contains UPDATED_MC_KEY
        defaultMyConfigShouldNotBeFound("mcKey.contains=" + UPDATED_MC_KEY);
    }

    @Test
    @Transactional
    void getAllMyConfigsByMcKeyNotContainsSomething() throws Exception {
        // Initialize the database
        myConfigRepository.saveAndFlush(myConfig);

        // Get all the myConfigList where mcKey does not contain DEFAULT_MC_KEY
        defaultMyConfigShouldNotBeFound("mcKey.doesNotContain=" + DEFAULT_MC_KEY);

        // Get all the myConfigList where mcKey does not contain UPDATED_MC_KEY
        defaultMyConfigShouldBeFound("mcKey.doesNotContain=" + UPDATED_MC_KEY);
    }

    @Test
    @Transactional
    void getAllMyConfigsByMcValueIsEqualToSomething() throws Exception {
        // Initialize the database
        myConfigRepository.saveAndFlush(myConfig);

        // Get all the myConfigList where mcValue equals to DEFAULT_MC_VALUE
        defaultMyConfigShouldBeFound("mcValue.equals=" + DEFAULT_MC_VALUE);

        // Get all the myConfigList where mcValue equals to UPDATED_MC_VALUE
        defaultMyConfigShouldNotBeFound("mcValue.equals=" + UPDATED_MC_VALUE);
    }

    @Test
    @Transactional
    void getAllMyConfigsByMcValueIsNotEqualToSomething() throws Exception {
        // Initialize the database
        myConfigRepository.saveAndFlush(myConfig);

        // Get all the myConfigList where mcValue not equals to DEFAULT_MC_VALUE
        defaultMyConfigShouldNotBeFound("mcValue.notEquals=" + DEFAULT_MC_VALUE);

        // Get all the myConfigList where mcValue not equals to UPDATED_MC_VALUE
        defaultMyConfigShouldBeFound("mcValue.notEquals=" + UPDATED_MC_VALUE);
    }

    @Test
    @Transactional
    void getAllMyConfigsByMcValueIsInShouldWork() throws Exception {
        // Initialize the database
        myConfigRepository.saveAndFlush(myConfig);

        // Get all the myConfigList where mcValue in DEFAULT_MC_VALUE or UPDATED_MC_VALUE
        defaultMyConfigShouldBeFound("mcValue.in=" + DEFAULT_MC_VALUE + "," + UPDATED_MC_VALUE);

        // Get all the myConfigList where mcValue equals to UPDATED_MC_VALUE
        defaultMyConfigShouldNotBeFound("mcValue.in=" + UPDATED_MC_VALUE);
    }

    @Test
    @Transactional
    void getAllMyConfigsByMcValueIsNullOrNotNull() throws Exception {
        // Initialize the database
        myConfigRepository.saveAndFlush(myConfig);

        // Get all the myConfigList where mcValue is not null
        defaultMyConfigShouldBeFound("mcValue.specified=true");

        // Get all the myConfigList where mcValue is null
        defaultMyConfigShouldNotBeFound("mcValue.specified=false");
    }

    @Test
    @Transactional
    void getAllMyConfigsByMcValueContainsSomething() throws Exception {
        // Initialize the database
        myConfigRepository.saveAndFlush(myConfig);

        // Get all the myConfigList where mcValue contains DEFAULT_MC_VALUE
        defaultMyConfigShouldBeFound("mcValue.contains=" + DEFAULT_MC_VALUE);

        // Get all the myConfigList where mcValue contains UPDATED_MC_VALUE
        defaultMyConfigShouldNotBeFound("mcValue.contains=" + UPDATED_MC_VALUE);
    }

    @Test
    @Transactional
    void getAllMyConfigsByMcValueNotContainsSomething() throws Exception {
        // Initialize the database
        myConfigRepository.saveAndFlush(myConfig);

        // Get all the myConfigList where mcValue does not contain DEFAULT_MC_VALUE
        defaultMyConfigShouldNotBeFound("mcValue.doesNotContain=" + DEFAULT_MC_VALUE);

        // Get all the myConfigList where mcValue does not contain UPDATED_MC_VALUE
        defaultMyConfigShouldBeFound("mcValue.doesNotContain=" + UPDATED_MC_VALUE);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMyConfigShouldBeFound(String filter) throws Exception {
        restMyConfigMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(myConfig.getId().intValue())))
            .andExpect(jsonPath("$.[*].mcKey").value(hasItem(DEFAULT_MC_KEY)))
            .andExpect(jsonPath("$.[*].mcValue").value(hasItem(DEFAULT_MC_VALUE)));

        // Check, that the count call also returns 1
        restMyConfigMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultMyConfigShouldNotBeFound(String filter) throws Exception {
        restMyConfigMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restMyConfigMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingMyConfig() throws Exception {
        // Get the myConfig
        restMyConfigMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewMyConfig() throws Exception {
        // Initialize the database
        myConfigRepository.saveAndFlush(myConfig);

        int databaseSizeBeforeUpdate = myConfigRepository.findAll().size();

        // Update the myConfig
        MyConfig updatedMyConfig = myConfigRepository.findById(myConfig.getId()).get();
        // Disconnect from session so that the updates on updatedMyConfig are not directly saved in db
        em.detach(updatedMyConfig);
        updatedMyConfig.mcKey(UPDATED_MC_KEY).mcValue(UPDATED_MC_VALUE);

        restMyConfigMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedMyConfig.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedMyConfig))
            )
            .andExpect(status().isOk());

        // Validate the MyConfig in the database
        List<MyConfig> myConfigList = myConfigRepository.findAll();
        assertThat(myConfigList).hasSize(databaseSizeBeforeUpdate);
        MyConfig testMyConfig = myConfigList.get(myConfigList.size() - 1);
        assertThat(testMyConfig.getMcKey()).isEqualTo(UPDATED_MC_KEY);
        assertThat(testMyConfig.getMcValue()).isEqualTo(UPDATED_MC_VALUE);
    }

    @Test
    @Transactional
    void putNonExistingMyConfig() throws Exception {
        int databaseSizeBeforeUpdate = myConfigRepository.findAll().size();
        myConfig.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMyConfigMockMvc
            .perform(
                put(ENTITY_API_URL_ID, myConfig.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(myConfig))
            )
            .andExpect(status().isBadRequest());

        // Validate the MyConfig in the database
        List<MyConfig> myConfigList = myConfigRepository.findAll();
        assertThat(myConfigList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMyConfig() throws Exception {
        int databaseSizeBeforeUpdate = myConfigRepository.findAll().size();
        myConfig.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMyConfigMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(myConfig))
            )
            .andExpect(status().isBadRequest());

        // Validate the MyConfig in the database
        List<MyConfig> myConfigList = myConfigRepository.findAll();
        assertThat(myConfigList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMyConfig() throws Exception {
        int databaseSizeBeforeUpdate = myConfigRepository.findAll().size();
        myConfig.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMyConfigMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(myConfig))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the MyConfig in the database
        List<MyConfig> myConfigList = myConfigRepository.findAll();
        assertThat(myConfigList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMyConfigWithPatch() throws Exception {
        // Initialize the database
        myConfigRepository.saveAndFlush(myConfig);

        int databaseSizeBeforeUpdate = myConfigRepository.findAll().size();

        // Update the myConfig using partial update
        MyConfig partialUpdatedMyConfig = new MyConfig();
        partialUpdatedMyConfig.setId(myConfig.getId());

        restMyConfigMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMyConfig.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMyConfig))
            )
            .andExpect(status().isOk());

        // Validate the MyConfig in the database
        List<MyConfig> myConfigList = myConfigRepository.findAll();
        assertThat(myConfigList).hasSize(databaseSizeBeforeUpdate);
        MyConfig testMyConfig = myConfigList.get(myConfigList.size() - 1);
        assertThat(testMyConfig.getMcKey()).isEqualTo(DEFAULT_MC_KEY);
        assertThat(testMyConfig.getMcValue()).isEqualTo(DEFAULT_MC_VALUE);
    }

    @Test
    @Transactional
    void fullUpdateMyConfigWithPatch() throws Exception {
        // Initialize the database
        myConfigRepository.saveAndFlush(myConfig);

        int databaseSizeBeforeUpdate = myConfigRepository.findAll().size();

        // Update the myConfig using partial update
        MyConfig partialUpdatedMyConfig = new MyConfig();
        partialUpdatedMyConfig.setId(myConfig.getId());

        partialUpdatedMyConfig.mcKey(UPDATED_MC_KEY).mcValue(UPDATED_MC_VALUE);

        restMyConfigMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMyConfig.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMyConfig))
            )
            .andExpect(status().isOk());

        // Validate the MyConfig in the database
        List<MyConfig> myConfigList = myConfigRepository.findAll();
        assertThat(myConfigList).hasSize(databaseSizeBeforeUpdate);
        MyConfig testMyConfig = myConfigList.get(myConfigList.size() - 1);
        assertThat(testMyConfig.getMcKey()).isEqualTo(UPDATED_MC_KEY);
        assertThat(testMyConfig.getMcValue()).isEqualTo(UPDATED_MC_VALUE);
    }

    @Test
    @Transactional
    void patchNonExistingMyConfig() throws Exception {
        int databaseSizeBeforeUpdate = myConfigRepository.findAll().size();
        myConfig.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMyConfigMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, myConfig.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(myConfig))
            )
            .andExpect(status().isBadRequest());

        // Validate the MyConfig in the database
        List<MyConfig> myConfigList = myConfigRepository.findAll();
        assertThat(myConfigList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMyConfig() throws Exception {
        int databaseSizeBeforeUpdate = myConfigRepository.findAll().size();
        myConfig.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMyConfigMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(myConfig))
            )
            .andExpect(status().isBadRequest());

        // Validate the MyConfig in the database
        List<MyConfig> myConfigList = myConfigRepository.findAll();
        assertThat(myConfigList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMyConfig() throws Exception {
        int databaseSizeBeforeUpdate = myConfigRepository.findAll().size();
        myConfig.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMyConfigMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(myConfig))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the MyConfig in the database
        List<MyConfig> myConfigList = myConfigRepository.findAll();
        assertThat(myConfigList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMyConfig() throws Exception {
        // Initialize the database
        myConfigRepository.saveAndFlush(myConfig);

        int databaseSizeBeforeDelete = myConfigRepository.findAll().size();

        // Delete the myConfig
        restMyConfigMockMvc
            .perform(delete(ENTITY_API_URL_ID, myConfig.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<MyConfig> myConfigList = myConfigRepository.findAll();
        assertThat(myConfigList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
