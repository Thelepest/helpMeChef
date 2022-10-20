package it.marcobiasone.service;

import it.marcobiasone.domain.*; // for static metamodels
import it.marcobiasone.domain.Quantity;
import it.marcobiasone.repository.QuantityRepository;
import it.marcobiasone.service.criteria.QuantityCriteria;
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
 * Service for executing complex queries for {@link Quantity} entities in the database.
 * The main input is a {@link QuantityCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Quantity} or a {@link Page} of {@link Quantity} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class QuantityQueryService extends QueryService<Quantity> {

    private final Logger log = LoggerFactory.getLogger(QuantityQueryService.class);

    private final QuantityRepository quantityRepository;

    public QuantityQueryService(QuantityRepository quantityRepository) {
        this.quantityRepository = quantityRepository;
    }

    /**
     * Return a {@link List} of {@link Quantity} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Quantity> findByCriteria(QuantityCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Quantity> specification = createSpecification(criteria);
        return quantityRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Quantity} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Quantity> findByCriteria(QuantityCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Quantity> specification = createSpecification(criteria);
        return quantityRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(QuantityCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Quantity> specification = createSpecification(criteria);
        return quantityRepository.count(specification);
    }

    /**
     * Function to convert {@link QuantityCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Quantity> createSpecification(QuantityCriteria criteria) {
        Specification<Quantity> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Quantity_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Quantity_.name));
            }
            if (criteria.getAmount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAmount(), Quantity_.amount));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Quantity_.description));
            }
        }
        return specification;
    }
}
