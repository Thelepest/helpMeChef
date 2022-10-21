package it.marcobiasone.repository;

import it.marcobiasone.domain.Recipe;
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
public class RecipeRepositoryWithBagRelationshipsImpl implements RecipeRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Recipe> fetchBagRelationships(Optional<Recipe> recipe) {
        return recipe.map(this::fetchIngredientquantities).map(this::fetchTools);
    }

    @Override
    public Page<Recipe> fetchBagRelationships(Page<Recipe> recipes) {
        return new PageImpl<>(fetchBagRelationships(recipes.getContent()), recipes.getPageable(), recipes.getTotalElements());
    }

    @Override
    public List<Recipe> fetchBagRelationships(List<Recipe> recipes) {
        return Optional.of(recipes).map(this::fetchIngredientquantities).map(this::fetchTools).orElse(Collections.emptyList());
    }

    Recipe fetchIngredientquantities(Recipe result) {
        return entityManager
            .createQuery(
                "select recipe from Recipe recipe left join fetch recipe.ingredientquantities where recipe is :recipe",
                Recipe.class
            )
            .setParameter("recipe", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Recipe> fetchIngredientquantities(List<Recipe> recipes) {
        return entityManager
            .createQuery(
                "select distinct recipe from Recipe recipe left join fetch recipe.ingredientquantities where recipe in :recipes",
                Recipe.class
            )
            .setParameter("recipes", recipes)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
    }

    Recipe fetchTools(Recipe result) {
        return entityManager
            .createQuery("select recipe from Recipe recipe left join fetch recipe.tools where recipe is :recipe", Recipe.class)
            .setParameter("recipe", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Recipe> fetchTools(List<Recipe> recipes) {
        return entityManager
            .createQuery("select distinct recipe from Recipe recipe left join fetch recipe.tools where recipe in :recipes", Recipe.class)
            .setParameter("recipes", recipes)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
    }
}
