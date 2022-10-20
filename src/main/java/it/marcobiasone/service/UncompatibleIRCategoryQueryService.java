package it.marcobiasone.service;

import it.marcobiasone.domain.*; // for static metamodels
import it.marcobiasone.domain.UncompatibleIRCategory;
import it.marcobiasone.repository.UncompatibleIRCategoryRepository;
import it.marcobiasone.service.criteria.UncompatibleIRCategoryCriteria;
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
 * Service for executing complex queries for {@link UncompatibleIRCategory} entities in the database.
 * The main input is a {@link UncompatibleIRCategoryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link UncompatibleIRCategory} or a {@link Page} of {@link UncompatibleIRCategory} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class UncompatibleIRCategoryQueryService extends QueryService<UncompatibleIRCategory> {

    private final Logger log = LoggerFactory.getLogger(UncompatibleIRCategoryQueryService.class);

    private final UncompatibleIRCategoryRepository uncompatibleIRCategoryRepository;

    public UncompatibleIRCategoryQueryService(UncompatibleIRCategoryRepository uncompatibleIRCategoryRepository) {
        this.uncompatibleIRCategoryRepository = uncompatibleIRCategoryRepository;
    }

    /**
     * Return a {@link List} of {@link UncompatibleIRCategory} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<UncompatibleIRCategory> findByCriteria(UncompatibleIRCategoryCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<UncompatibleIRCategory> specification = createSpecification(criteria);
        return uncompatibleIRCategoryRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link UncompatibleIRCategory} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<UncompatibleIRCategory> findByCriteria(UncompatibleIRCategoryCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<UncompatibleIRCategory> specification = createSpecification(criteria);
        return uncompatibleIRCategoryRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(UncompatibleIRCategoryCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<UncompatibleIRCategory> specification = createSpecification(criteria);
        return uncompatibleIRCategoryRepository.count(specification);
    }

    /**
     * Function to convert {@link UncompatibleIRCategoryCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<UncompatibleIRCategory> createSpecification(UncompatibleIRCategoryCriteria criteria) {
        Specification<UncompatibleIRCategory> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), UncompatibleIRCategory_.id));
            }
            if (criteria.getIngredientcategoryId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getIngredientcategoryId(),
                            root -> root.join(UncompatibleIRCategory_.ingredientcategory, JoinType.LEFT).get(IngredientCategory_.id)
                        )
                    );
            }
            if (criteria.getRecipecategoryId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getRecipecategoryId(),
                            root -> root.join(UncompatibleIRCategory_.recipecategory, JoinType.LEFT).get(RecipeCategory_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
