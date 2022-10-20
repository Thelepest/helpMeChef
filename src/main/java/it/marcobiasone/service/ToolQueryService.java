package it.marcobiasone.service;

import it.marcobiasone.domain.*; // for static metamodels
import it.marcobiasone.domain.Tool;
import it.marcobiasone.repository.ToolRepository;
import it.marcobiasone.service.criteria.ToolCriteria;
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
 * Service for executing complex queries for {@link Tool} entities in the database.
 * The main input is a {@link ToolCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Tool} or a {@link Page} of {@link Tool} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ToolQueryService extends QueryService<Tool> {

    private final Logger log = LoggerFactory.getLogger(ToolQueryService.class);

    private final ToolRepository toolRepository;

    public ToolQueryService(ToolRepository toolRepository) {
        this.toolRepository = toolRepository;
    }

    /**
     * Return a {@link List} of {@link Tool} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Tool> findByCriteria(ToolCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Tool> specification = createSpecification(criteria);
        return toolRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Tool} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Tool> findByCriteria(ToolCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Tool> specification = createSpecification(criteria);
        return toolRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ToolCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Tool> specification = createSpecification(criteria);
        return toolRepository.count(specification);
    }

    /**
     * Function to convert {@link ToolCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Tool> createSpecification(ToolCriteria criteria) {
        Specification<Tool> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Tool_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Tool_.name));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Tool_.description));
            }
            if (criteria.getRecipeId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getRecipeId(), root -> root.join(Tool_.recipes, JoinType.LEFT).get(Recipe_.id))
                    );
            }
        }
        return specification;
    }
}
