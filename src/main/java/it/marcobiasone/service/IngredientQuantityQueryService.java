package it.marcobiasone.service;

import it.marcobiasone.domain.*; // for static metamodels
import it.marcobiasone.domain.IngredientQuantity;
import it.marcobiasone.repository.IngredientQuantityRepository;
import it.marcobiasone.service.criteria.IngredientQuantityCriteria;
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
 * Service for executing complex queries for {@link IngredientQuantity} entities in the database.
 * The main input is a {@link IngredientQuantityCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link IngredientQuantity} or a {@link Page} of {@link IngredientQuantity} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class IngredientQuantityQueryService extends QueryService<IngredientQuantity> {

    private final Logger log = LoggerFactory.getLogger(IngredientQuantityQueryService.class);

    private final IngredientQuantityRepository ingredientQuantityRepository;

    public IngredientQuantityQueryService(IngredientQuantityRepository ingredientQuantityRepository) {
        this.ingredientQuantityRepository = ingredientQuantityRepository;
    }

    /**
     * Return a {@link List} of {@link IngredientQuantity} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<IngredientQuantity> findByCriteria(IngredientQuantityCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<IngredientQuantity> specification = createSpecification(criteria);
        return ingredientQuantityRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link IngredientQuantity} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<IngredientQuantity> findByCriteria(IngredientQuantityCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<IngredientQuantity> specification = createSpecification(criteria);
        return ingredientQuantityRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(IngredientQuantityCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<IngredientQuantity> specification = createSpecification(criteria);
        return ingredientQuantityRepository.count(specification);
    }

    /**
     * Function to convert {@link IngredientQuantityCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<IngredientQuantity> createSpecification(IngredientQuantityCriteria criteria) {
        Specification<IngredientQuantity> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), IngredientQuantity_.id));
            }
            if (criteria.getIngredientId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getIngredientId(),
                            root -> root.join(IngredientQuantity_.ingredient, JoinType.LEFT).get(Ingredient_.id)
                        )
                    );
            }
            if (criteria.getQuantityId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getQuantityId(),
                            root -> root.join(IngredientQuantity_.quantity, JoinType.LEFT).get(Quantity_.id)
                        )
                    );
            }
            if (criteria.getPantryId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getPantryId(),
                            root -> root.join(IngredientQuantity_.pantries, JoinType.LEFT).get(Pantry_.id)
                        )
                    );
            }
            if (criteria.getRecipeId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getRecipeId(),
                            root -> root.join(IngredientQuantity_.recipes, JoinType.LEFT).get(Recipe_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
