package it.marcobiasone.repository;

import it.marcobiasone.domain.Tool;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class ToolRepositoryWithBagRelationshipsImpl implements ToolRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Tool> fetchBagRelationships(Optional<Tool> tool) {
        return tool.map(this::fetchRecipes);
    }

    @Override
    public Page<Tool> fetchBagRelationships(Page<Tool> tools) {
        return new PageImpl<>(fetchBagRelationships(tools.getContent()), tools.getPageable(), tools.getTotalElements());
    }

    @Override
    public List<Tool> fetchBagRelationships(List<Tool> tools) {
        return Optional.of(tools).map(this::fetchRecipes).orElse(Collections.emptyList());
    }

    Tool fetchRecipes(Tool result) {
        return entityManager
            .createQuery("select tool from Tool tool left join fetch tool.recipes where tool is :tool", Tool.class)
            .setParameter("tool", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Tool> fetchRecipes(List<Tool> tools) {
        return entityManager
            .createQuery("select distinct tool from Tool tool left join fetch tool.recipes where tool in :tools", Tool.class)
            .setParameter("tools", tools)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
    }
}
