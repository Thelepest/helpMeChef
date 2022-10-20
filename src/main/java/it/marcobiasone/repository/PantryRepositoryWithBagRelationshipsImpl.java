package it.marcobiasone.repository;

import it.marcobiasone.domain.Pantry;
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
public class PantryRepositoryWithBagRelationshipsImpl implements PantryRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Pantry> fetchBagRelationships(Optional<Pantry> pantry) {
        return pantry.map(this::fetchIngredientquantities);
    }

    @Override
    public Page<Pantry> fetchBagRelationships(Page<Pantry> pantries) {
        return new PageImpl<>(fetchBagRelationships(pantries.getContent()), pantries.getPageable(), pantries.getTotalElements());
    }

    @Override
    public List<Pantry> fetchBagRelationships(List<Pantry> pantries) {
        return Optional.of(pantries).map(this::fetchIngredientquantities).orElse(Collections.emptyList());
    }

    Pantry fetchIngredientquantities(Pantry result) {
        return entityManager
            .createQuery(
                "select pantry from Pantry pantry left join fetch pantry.ingredientquantities where pantry is :pantry",
                Pantry.class
            )
            .setParameter("pantry", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Pantry> fetchIngredientquantities(List<Pantry> pantries) {
        return entityManager
            .createQuery(
                "select distinct pantry from Pantry pantry left join fetch pantry.ingredientquantities where pantry in :pantries",
                Pantry.class
            )
            .setParameter("pantries", pantries)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
    }
}
