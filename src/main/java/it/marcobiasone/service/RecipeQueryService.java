package it.marcobiasone.service;

import it.marcobiasone.domain.*; // for static metamodels
import it.marcobiasone.domain.Recipe;
import it.marcobiasone.repository.RecipeRepository;
import it.marcobiasone.service.criteria.RecipeCriteria;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Recipe} entities in the database.
 * The main input is a {@link RecipeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Recipe} or a {@link Page} of {@link Recipe} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class RecipeQueryService extends QueryService<Recipe> {

    private final Logger log = LoggerFactory.getLogger(RecipeQueryService.class);

    private final RecipeRepository recipeRepository;

    public RecipeQueryService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    /**
     * Return a {@link List} of {@link Recipe} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Recipe> findByCriteria(RecipeCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Recipe> specification = createSpecification(criteria);
        return recipeRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Recipe} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Recipe> findByCriteria(RecipeCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Recipe> specification = createSpecification(criteria);
        return recipeRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(RecipeCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Recipe> specification = createSpecification(criteria);
        return recipeRepository.count(specification);
    }

    /**
     * Function to convert {@link RecipeCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Recipe> createSpecification(RecipeCriteria criteria) {
        Specification<Recipe> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Recipe_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Recipe_.name));
            }
            if (criteria.getTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTime(), Recipe_.time));
            }
            if (criteria.getDiners() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDiners(), Recipe_.diners));
            }
            if (criteria.getRecipecategoryId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getRecipecategoryId(),
                            root -> root.join(Recipe_.recipecategory, JoinType.LEFT).get(RecipeCategory_.id)
                        )
                    );
            }
            if (criteria.getIngredientquantityId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getIngredientquantityId(),
                            root -> root.join(Recipe_.ingredientquantities, JoinType.LEFT).get(IngredientQuantity_.id)
                        )
                    );
            }
            if (criteria.getToolId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getToolId(), root -> root.join(Recipe_.tools, JoinType.LEFT).get(Tool_.id))
                    );
            }
        }
        return specification;
    }
}
