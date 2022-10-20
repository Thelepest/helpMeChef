package it.marcobiasone.service;

import it.marcobiasone.domain.*; // for static metamodels
import it.marcobiasone.domain.Pantry;
import it.marcobiasone.repository.PantryRepository;
import it.marcobiasone.service.criteria.PantryCriteria;
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
 * Service for executing complex queries for {@link Pantry} entities in the database.
 * The main input is a {@link PantryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Pantry} or a {@link Page} of {@link Pantry} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PantryQueryService extends QueryService<Pantry> {

    private final Logger log = LoggerFactory.getLogger(PantryQueryService.class);

    private final PantryRepository pantryRepository;

    public PantryQueryService(PantryRepository pantryRepository) {
        this.pantryRepository = pantryRepository;
    }

    /**
     * Return a {@link List} of {@link Pantry} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Pantry> findByCriteria(PantryCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Pantry> specification = createSpecification(criteria);
        return pantryRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Pantry} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Pantry> findByCriteria(PantryCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Pantry> specification = createSpecification(criteria);
        return pantryRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PantryCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Pantry> specification = createSpecification(criteria);
        return pantryRepository.count(specification);
    }

    /**
     * Function to convert {@link PantryCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Pantry> createSpecification(PantryCriteria criteria) {
        Specification<Pantry> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Pantry_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Pantry_.name));
            }
            if (criteria.getActive() != null) {
                specification = specification.and(buildSpecification(criteria.getActive(), Pantry_.active));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Pantry_.description));
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedAt(), Pantry_.createdAt));
            }
            if (criteria.getUserId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getUserId(), root -> root.join(Pantry_.user, JoinType.LEFT).get(User_.id))
                    );
            }
            if (criteria.getIngredientquantityId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getIngredientquantityId(),
                            root -> root.join(Pantry_.ingredientquantities, JoinType.LEFT).get(IngredientQuantity_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
