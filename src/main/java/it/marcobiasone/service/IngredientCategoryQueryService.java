package it.marcobiasone.service;

import it.marcobiasone.domain.*; // for static metamodels
import it.marcobiasone.domain.IngredientCategory;
import it.marcobiasone.repository.IngredientCategoryRepository;
import it.marcobiasone.service.criteria.IngredientCategoryCriteria;
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
 * Service for executing complex queries for {@link IngredientCategory} entities in the database.
 * The main input is a {@link IngredientCategoryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link IngredientCategory} or a {@link Page} of {@link IngredientCategory} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class IngredientCategoryQueryService extends QueryService<IngredientCategory> {

    private final Logger log = LoggerFactory.getLogger(IngredientCategoryQueryService.class);

    private final IngredientCategoryRepository ingredientCategoryRepository;

    public IngredientCategoryQueryService(IngredientCategoryRepository ingredientCategoryRepository) {
        this.ingredientCategoryRepository = ingredientCategoryRepository;
    }

    /**
     * Return a {@link List} of {@link IngredientCategory} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<IngredientCategory> findByCriteria(IngredientCategoryCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<IngredientCategory> specification = createSpecification(criteria);
        return ingredientCategoryRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link IngredientCategory} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<IngredientCategory> findByCriteria(IngredientCategoryCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<IngredientCategory> specification = createSpecification(criteria);
        return ingredientCategoryRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(IngredientCategoryCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<IngredientCategory> specification = createSpecification(criteria);
        return ingredientCategoryRepository.count(specification);
    }

    /**
     * Function to convert {@link IngredientCategoryCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<IngredientCategory> createSpecification(IngredientCategoryCriteria criteria) {
        Specification<IngredientCategory> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), IngredientCategory_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), IngredientCategory_.name));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), IngredientCategory_.description));
            }
        }
        return specification;
    }
}
