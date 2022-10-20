package it.marcobiasone.service;

import it.marcobiasone.domain.*; // for static metamodels
import it.marcobiasone.domain.RecipeCategory;
import it.marcobiasone.repository.RecipeCategoryRepository;
import it.marcobiasone.service.criteria.RecipeCategoryCriteria;
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
 * Service for executing complex queries for {@link RecipeCategory} entities in the database.
 * The main input is a {@link RecipeCategoryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link RecipeCategory} or a {@link Page} of {@link RecipeCategory} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class RecipeCategoryQueryService extends QueryService<RecipeCategory> {

    private final Logger log = LoggerFactory.getLogger(RecipeCategoryQueryService.class);

    private final RecipeCategoryRepository recipeCategoryRepository;

    public RecipeCategoryQueryService(RecipeCategoryRepository recipeCategoryRepository) {
        this.recipeCategoryRepository = recipeCategoryRepository;
    }

    /**
     * Return a {@link List} of {@link RecipeCategory} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<RecipeCategory> findByCriteria(RecipeCategoryCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<RecipeCategory> specification = createSpecification(criteria);
        return recipeCategoryRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link RecipeCategory} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<RecipeCategory> findByCriteria(RecipeCategoryCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<RecipeCategory> specification = createSpecification(criteria);
        return recipeCategoryRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(RecipeCategoryCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<RecipeCategory> specification = createSpecification(criteria);
        return recipeCategoryRepository.count(specification);
    }

    /**
     * Function to convert {@link RecipeCategoryCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<RecipeCategory> createSpecification(RecipeCategoryCriteria criteria) {
        Specification<RecipeCategory> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), RecipeCategory_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), RecipeCategory_.name));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), RecipeCategory_.description));
            }
        }
        return specification;
    }
}
