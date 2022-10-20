package it.marcobiasone.service;

import it.marcobiasone.domain.*; // for static metamodels
import it.marcobiasone.domain.Comment;
import it.marcobiasone.repository.CommentRepository;
import it.marcobiasone.service.criteria.CommentCriteria;
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
 * Service for executing complex queries for {@link Comment} entities in the database.
 * The main input is a {@link CommentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Comment} or a {@link Page} of {@link Comment} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CommentQueryService extends QueryService<Comment> {

    private final Logger log = LoggerFactory.getLogger(CommentQueryService.class);

    private final CommentRepository commentRepository;

    public CommentQueryService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    /**
     * Return a {@link List} of {@link Comment} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Comment> findByCriteria(CommentCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Comment> specification = createSpecification(criteria);
        return commentRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Comment} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Comment> findByCriteria(CommentCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Comment> specification = createSpecification(criteria);
        return commentRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CommentCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Comment> specification = createSpecification(criteria);
        return commentRepository.count(specification);
    }

    /**
     * Function to convert {@link CommentCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Comment> createSpecification(CommentCriteria criteria) {
        Specification<Comment> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Comment_.id));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), Comment_.title));
            }
            if (criteria.getUserId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getUserId(), root -> root.join(Comment_.user, JoinType.LEFT).get(User_.id))
                    );
            }
            if (criteria.getRecipeId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getRecipeId(), root -> root.join(Comment_.recipe, JoinType.LEFT).get(Recipe_.id))
                    );
            }
        }
        return specification;
    }
}
