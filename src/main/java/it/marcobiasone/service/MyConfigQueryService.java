package it.marcobiasone.service;

import it.marcobiasone.domain.*; // for static metamodels
import it.marcobiasone.domain.MyConfig;
import it.marcobiasone.repository.MyConfigRepository;
import it.marcobiasone.service.criteria.MyConfigCriteria;
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
 * Service for executing complex queries for {@link MyConfig} entities in the database.
 * The main input is a {@link MyConfigCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link MyConfig} or a {@link Page} of {@link MyConfig} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class MyConfigQueryService extends QueryService<MyConfig> {

    private final Logger log = LoggerFactory.getLogger(MyConfigQueryService.class);

    private final MyConfigRepository myConfigRepository;

    public MyConfigQueryService(MyConfigRepository myConfigRepository) {
        this.myConfigRepository = myConfigRepository;
    }

    /**
     * Return a {@link List} of {@link MyConfig} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<MyConfig> findByCriteria(MyConfigCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<MyConfig> specification = createSpecification(criteria);
        return myConfigRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link MyConfig} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<MyConfig> findByCriteria(MyConfigCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<MyConfig> specification = createSpecification(criteria);
        return myConfigRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(MyConfigCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<MyConfig> specification = createSpecification(criteria);
        return myConfigRepository.count(specification);
    }

    /**
     * Function to convert {@link MyConfigCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<MyConfig> createSpecification(MyConfigCriteria criteria) {
        Specification<MyConfig> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), MyConfig_.id));
            }
            if (criteria.getMcKey() != null) {
                specification = specification.and(buildStringSpecification(criteria.getMcKey(), MyConfig_.mcKey));
            }
            if (criteria.getMcValue() != null) {
                specification = specification.and(buildStringSpecification(criteria.getMcValue(), MyConfig_.mcValue));
            }
        }
        return specification;
    }
}
