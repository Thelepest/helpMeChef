package it.marcobiasone.web.rest;

import static it.marcobiasone.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import it.marcobiasone.IntegrationTest;
import it.marcobiasone.domain.IngredientQuantity;
import it.marcobiasone.domain.Pantry;
import it.marcobiasone.domain.User;
import it.marcobiasone.repository.PantryRepository;
import it.marcobiasone.service.PantryService;
import it.marcobiasone.service.criteria.PantryCriteria;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
 * Integration tests for the {@link PantryResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PantryResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String ENTITY_API_URL = "/api/pantries";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PantryRepository pantryRepository;

    @Mock
    private PantryRepository pantryRepositoryMock;

    @Mock
    private PantryService pantryServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPantryMockMvc;

    private Pantry pantry;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pantry createEntity(EntityManager em) {
        Pantry pantry = new Pantry()
            .name(DEFAULT_NAME)
            .active(DEFAULT_ACTIVE)
            .description(DEFAULT_DESCRIPTION)
            .createdAt(DEFAULT_CREATED_AT);
        return pantry;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pantry createUpdatedEntity(EntityManager em) {
        Pantry pantry = new Pantry()
            .name(UPDATED_NAME)
            .active(UPDATED_ACTIVE)
            .description(UPDATED_DESCRIPTION)
            .createdAt(UPDATED_CREATED_AT);
        return pantry;
    }

    @BeforeEach
    public void initTest() {
        pantry = createEntity(em);
    }

    @Test
    @Transactional
    void createPantry() throws Exception {
        int databaseSizeBeforeCreate = pantryRepository.findAll().size();
        // Create the Pantry
        restPantryMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pantry))
            )
            .andExpect(status().isCreated());

        // Validate the Pantry in the database
        List<Pantry> pantryList = pantryRepository.findAll();
        assertThat(pantryList).hasSize(databaseSizeBeforeCreate + 1);
        Pantry testPantry = pantryList.get(pantryList.size() - 1);
        assertThat(testPantry.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPantry.getActive()).isEqualTo(DEFAULT_ACTIVE);
        assertThat(testPantry.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testPantry.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
    }

    @Test
    @Transactional
    void createPantryWithExistingId() throws Exception {
        // Create the Pantry with an existing ID
        pantry.setId(1L);

        int databaseSizeBeforeCreate = pantryRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPantryMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pantry))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pantry in the database
        List<Pantry> pantryList = pantryRepository.findAll();
        assertThat(pantryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = pantryRepository.findAll().size();
        // set the field null
        pantry.setName(null);

        // Create the Pantry, which fails.

        restPantryMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pantry))
            )
            .andExpect(status().isBadRequest());

        List<Pantry> pantryList = pantryRepository.findAll();
        assertThat(pantryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        int databaseSizeBeforeTest = pantryRepository.findAll().size();
        // set the field null
        pantry.setActive(null);

        // Create the Pantry, which fails.

        restPantryMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pantry))
            )
            .andExpect(status().isBadRequest());

        List<Pantry> pantryList = pantryRepository.findAll();
        assertThat(pantryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = pantryRepository.findAll().size();
        // set the field null
        pantry.setCreatedAt(null);

        // Create the Pantry, which fails.

        restPantryMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pantry))
            )
            .andExpect(status().isBadRequest());

        List<Pantry> pantryList = pantryRepository.findAll();
        assertThat(pantryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPantries() throws Exception {
        // Initialize the database
        pantryRepository.saveAndFlush(pantry);

        // Get all the pantryList
        restPantryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pantry.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPantriesWithEagerRelationshipsIsEnabled() throws Exception {
        when(pantryServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPantryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(pantryServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPantriesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(pantryServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPantryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(pantryServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getPantry() throws Exception {
        // Initialize the database
        pantryRepository.saveAndFlush(pantry);

        // Get the pantry
        restPantryMockMvc
            .perform(get(ENTITY_API_URL_ID, pantry.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(pantry.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)));
    }

    @Test
    @Transactional
    void getPantriesByIdFiltering() throws Exception {
        // Initialize the database
        pantryRepository.saveAndFlush(pantry);

        Long id = pantry.getId();

        defaultPantryShouldBeFound("id.equals=" + id);
        defaultPantryShouldNotBeFound("id.notEquals=" + id);

        defaultPantryShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPantryShouldNotBeFound("id.greaterThan=" + id);

        defaultPantryShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPantryShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPantriesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        pantryRepository.saveAndFlush(pantry);

        // Get all the pantryList where name equals to DEFAULT_NAME
        defaultPantryShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the pantryList where name equals to UPDATED_NAME
        defaultPantryShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPantriesByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        pantryRepository.saveAndFlush(pantry);

        // Get all the pantryList where name not equals to DEFAULT_NAME
        defaultPantryShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the pantryList where name not equals to UPDATED_NAME
        defaultPantryShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPantriesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        pantryRepository.saveAndFlush(pantry);

        // Get all the pantryList where name in DEFAULT_NAME or UPDATED_NAME
        defaultPantryShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the pantryList where name equals to UPDATED_NAME
        defaultPantryShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPantriesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        pantryRepository.saveAndFlush(pantry);

        // Get all the pantryList where name is not null
        defaultPantryShouldBeFound("name.specified=true");

        // Get all the pantryList where name is null
        defaultPantryShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllPantriesByNameContainsSomething() throws Exception {
        // Initialize the database
        pantryRepository.saveAndFlush(pantry);

        // Get all the pantryList where name contains DEFAULT_NAME
        defaultPantryShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the pantryList where name contains UPDATED_NAME
        defaultPantryShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPantriesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        pantryRepository.saveAndFlush(pantry);

        // Get all the pantryList where name does not contain DEFAULT_NAME
        defaultPantryShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the pantryList where name does not contain UPDATED_NAME
        defaultPantryShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPantriesByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        pantryRepository.saveAndFlush(pantry);

        // Get all the pantryList where active equals to DEFAULT_ACTIVE
        defaultPantryShouldBeFound("active.equals=" + DEFAULT_ACTIVE);

        // Get all the pantryList where active equals to UPDATED_ACTIVE
        defaultPantryShouldNotBeFound("active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllPantriesByActiveIsNotEqualToSomething() throws Exception {
        // Initialize the database
        pantryRepository.saveAndFlush(pantry);

        // Get all the pantryList where active not equals to DEFAULT_ACTIVE
        defaultPantryShouldNotBeFound("active.notEquals=" + DEFAULT_ACTIVE);

        // Get all the pantryList where active not equals to UPDATED_ACTIVE
        defaultPantryShouldBeFound("active.notEquals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllPantriesByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        pantryRepository.saveAndFlush(pantry);

        // Get all the pantryList where active in DEFAULT_ACTIVE or UPDATED_ACTIVE
        defaultPantryShouldBeFound("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE);

        // Get all the pantryList where active equals to UPDATED_ACTIVE
        defaultPantryShouldNotBeFound("active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllPantriesByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        pantryRepository.saveAndFlush(pantry);

        // Get all the pantryList where active is not null
        defaultPantryShouldBeFound("active.specified=true");

        // Get all the pantryList where active is null
        defaultPantryShouldNotBeFound("active.specified=false");
    }

    @Test
    @Transactional
    void getAllPantriesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        pantryRepository.saveAndFlush(pantry);

        // Get all the pantryList where description equals to DEFAULT_DESCRIPTION
        defaultPantryShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the pantryList where description equals to UPDATED_DESCRIPTION
        defaultPantryShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllPantriesByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        pantryRepository.saveAndFlush(pantry);

        // Get all the pantryList where description not equals to DEFAULT_DESCRIPTION
        defaultPantryShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the pantryList where description not equals to UPDATED_DESCRIPTION
        defaultPantryShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllPantriesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        pantryRepository.saveAndFlush(pantry);

        // Get all the pantryList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultPantryShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the pantryList where description equals to UPDATED_DESCRIPTION
        defaultPantryShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllPantriesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        pantryRepository.saveAndFlush(pantry);

        // Get all the pantryList where description is not null
        defaultPantryShouldBeFound("description.specified=true");

        // Get all the pantryList where description is null
        defaultPantryShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllPantriesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        pantryRepository.saveAndFlush(pantry);

        // Get all the pantryList where description contains DEFAULT_DESCRIPTION
        defaultPantryShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the pantryList where description contains UPDATED_DESCRIPTION
        defaultPantryShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllPantriesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        pantryRepository.saveAndFlush(pantry);

        // Get all the pantryList where description does not contain DEFAULT_DESCRIPTION
        defaultPantryShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the pantryList where description does not contain UPDATED_DESCRIPTION
        defaultPantryShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllPantriesByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        pantryRepository.saveAndFlush(pantry);

        // Get all the pantryList where createdAt equals to DEFAULT_CREATED_AT
        defaultPantryShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

        // Get all the pantryList where createdAt equals to UPDATED_CREATED_AT
        defaultPantryShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllPantriesByCreatedAtIsNotEqualToSomething() throws Exception {
        // Initialize the database
        pantryRepository.saveAndFlush(pantry);

        // Get all the pantryList where createdAt not equals to DEFAULT_CREATED_AT
        defaultPantryShouldNotBeFound("createdAt.notEquals=" + DEFAULT_CREATED_AT);

        // Get all the pantryList where createdAt not equals to UPDATED_CREATED_AT
        defaultPantryShouldBeFound("createdAt.notEquals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllPantriesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        pantryRepository.saveAndFlush(pantry);

        // Get all the pantryList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
        defaultPantryShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

        // Get all the pantryList where createdAt equals to UPDATED_CREATED_AT
        defaultPantryShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllPantriesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        pantryRepository.saveAndFlush(pantry);

        // Get all the pantryList where createdAt is not null
        defaultPantryShouldBeFound("createdAt.specified=true");

        // Get all the pantryList where createdAt is null
        defaultPantryShouldNotBeFound("createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllPantriesByCreatedAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        pantryRepository.saveAndFlush(pantry);

        // Get all the pantryList where createdAt is greater than or equal to DEFAULT_CREATED_AT
        defaultPantryShouldBeFound("createdAt.greaterThanOrEqual=" + DEFAULT_CREATED_AT);

        // Get all the pantryList where createdAt is greater than or equal to UPDATED_CREATED_AT
        defaultPantryShouldNotBeFound("createdAt.greaterThanOrEqual=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllPantriesByCreatedAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        pantryRepository.saveAndFlush(pantry);

        // Get all the pantryList where createdAt is less than or equal to DEFAULT_CREATED_AT
        defaultPantryShouldBeFound("createdAt.lessThanOrEqual=" + DEFAULT_CREATED_AT);

        // Get all the pantryList where createdAt is less than or equal to SMALLER_CREATED_AT
        defaultPantryShouldNotBeFound("createdAt.lessThanOrEqual=" + SMALLER_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllPantriesByCreatedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        pantryRepository.saveAndFlush(pantry);

        // Get all the pantryList where createdAt is less than DEFAULT_CREATED_AT
        defaultPantryShouldNotBeFound("createdAt.lessThan=" + DEFAULT_CREATED_AT);

        // Get all the pantryList where createdAt is less than UPDATED_CREATED_AT
        defaultPantryShouldBeFound("createdAt.lessThan=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllPantriesByCreatedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        pantryRepository.saveAndFlush(pantry);

        // Get all the pantryList where createdAt is greater than DEFAULT_CREATED_AT
        defaultPantryShouldNotBeFound("createdAt.greaterThan=" + DEFAULT_CREATED_AT);

        // Get all the pantryList where createdAt is greater than SMALLER_CREATED_AT
        defaultPantryShouldBeFound("createdAt.greaterThan=" + SMALLER_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllPantriesByUserIsEqualToSomething() throws Exception {
        // Initialize the database
        pantryRepository.saveAndFlush(pantry);
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            user = UserResourceIT.createEntity(em);
            em.persist(user);
            em.flush();
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        pantry.setUser(user);
        pantryRepository.saveAndFlush(pantry);
        String userId = user.getId();

        // Get all the pantryList where user equals to userId
        defaultPantryShouldBeFound("userId.equals=" + userId);

        // Get all the pantryList where user equals to "invalid-id"
        defaultPantryShouldNotBeFound("userId.equals=" + "invalid-id");
    }

    @Test
    @Transactional
    void getAllPantriesByIngredientquantityIsEqualToSomething() throws Exception {
        // Initialize the database
        pantryRepository.saveAndFlush(pantry);
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
        pantry.addIngredientquantity(ingredientquantity);
        pantryRepository.saveAndFlush(pantry);
        Long ingredientquantityId = ingredientquantity.getId();

        // Get all the pantryList where ingredientquantity equals to ingredientquantityId
        defaultPantryShouldBeFound("ingredientquantityId.equals=" + ingredientquantityId);

        // Get all the pantryList where ingredientquantity equals to (ingredientquantityId + 1)
        defaultPantryShouldNotBeFound("ingredientquantityId.equals=" + (ingredientquantityId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPantryShouldBeFound(String filter) throws Exception {
        restPantryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pantry.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))));

        // Check, that the count call also returns 1
        restPantryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPantryShouldNotBeFound(String filter) throws Exception {
        restPantryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPantryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPantry() throws Exception {
        // Get the pantry
        restPantryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewPantry() throws Exception {
        // Initialize the database
        pantryRepository.saveAndFlush(pantry);

        int databaseSizeBeforeUpdate = pantryRepository.findAll().size();

        // Update the pantry
        Pantry updatedPantry = pantryRepository.findById(pantry.getId()).get();
        // Disconnect from session so that the updates on updatedPantry are not directly saved in db
        em.detach(updatedPantry);
        updatedPantry.name(UPDATED_NAME).active(UPDATED_ACTIVE).description(UPDATED_DESCRIPTION).createdAt(UPDATED_CREATED_AT);

        restPantryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPantry.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedPantry))
            )
            .andExpect(status().isOk());

        // Validate the Pantry in the database
        List<Pantry> pantryList = pantryRepository.findAll();
        assertThat(pantryList).hasSize(databaseSizeBeforeUpdate);
        Pantry testPantry = pantryList.get(pantryList.size() - 1);
        assertThat(testPantry.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPantry.getActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testPantry.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testPantry.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void putNonExistingPantry() throws Exception {
        int databaseSizeBeforeUpdate = pantryRepository.findAll().size();
        pantry.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPantryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pantry.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pantry))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pantry in the database
        List<Pantry> pantryList = pantryRepository.findAll();
        assertThat(pantryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPantry() throws Exception {
        int databaseSizeBeforeUpdate = pantryRepository.findAll().size();
        pantry.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPantryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pantry))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pantry in the database
        List<Pantry> pantryList = pantryRepository.findAll();
        assertThat(pantryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPantry() throws Exception {
        int databaseSizeBeforeUpdate = pantryRepository.findAll().size();
        pantry.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPantryMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pantry))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Pantry in the database
        List<Pantry> pantryList = pantryRepository.findAll();
        assertThat(pantryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePantryWithPatch() throws Exception {
        // Initialize the database
        pantryRepository.saveAndFlush(pantry);

        int databaseSizeBeforeUpdate = pantryRepository.findAll().size();

        // Update the pantry using partial update
        Pantry partialUpdatedPantry = new Pantry();
        partialUpdatedPantry.setId(pantry.getId());

        partialUpdatedPantry.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restPantryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPantry.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPantry))
            )
            .andExpect(status().isOk());

        // Validate the Pantry in the database
        List<Pantry> pantryList = pantryRepository.findAll();
        assertThat(pantryList).hasSize(databaseSizeBeforeUpdate);
        Pantry testPantry = pantryList.get(pantryList.size() - 1);
        assertThat(testPantry.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPantry.getActive()).isEqualTo(DEFAULT_ACTIVE);
        assertThat(testPantry.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testPantry.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
    }

    @Test
    @Transactional
    void fullUpdatePantryWithPatch() throws Exception {
        // Initialize the database
        pantryRepository.saveAndFlush(pantry);

        int databaseSizeBeforeUpdate = pantryRepository.findAll().size();

        // Update the pantry using partial update
        Pantry partialUpdatedPantry = new Pantry();
        partialUpdatedPantry.setId(pantry.getId());

        partialUpdatedPantry.name(UPDATED_NAME).active(UPDATED_ACTIVE).description(UPDATED_DESCRIPTION).createdAt(UPDATED_CREATED_AT);

        restPantryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPantry.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPantry))
            )
            .andExpect(status().isOk());

        // Validate the Pantry in the database
        List<Pantry> pantryList = pantryRepository.findAll();
        assertThat(pantryList).hasSize(databaseSizeBeforeUpdate);
        Pantry testPantry = pantryList.get(pantryList.size() - 1);
        assertThat(testPantry.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPantry.getActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testPantry.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testPantry.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void patchNonExistingPantry() throws Exception {
        int databaseSizeBeforeUpdate = pantryRepository.findAll().size();
        pantry.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPantryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, pantry.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(pantry))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pantry in the database
        List<Pantry> pantryList = pantryRepository.findAll();
        assertThat(pantryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPantry() throws Exception {
        int databaseSizeBeforeUpdate = pantryRepository.findAll().size();
        pantry.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPantryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(pantry))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pantry in the database
        List<Pantry> pantryList = pantryRepository.findAll();
        assertThat(pantryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPantry() throws Exception {
        int databaseSizeBeforeUpdate = pantryRepository.findAll().size();
        pantry.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPantryMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(pantry))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Pantry in the database
        List<Pantry> pantryList = pantryRepository.findAll();
        assertThat(pantryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePantry() throws Exception {
        // Initialize the database
        pantryRepository.saveAndFlush(pantry);

        int databaseSizeBeforeDelete = pantryRepository.findAll().size();

        // Delete the pantry
        restPantryMockMvc
            .perform(delete(ENTITY_API_URL_ID, pantry.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Pantry> pantryList = pantryRepository.findAll();
        assertThat(pantryList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
