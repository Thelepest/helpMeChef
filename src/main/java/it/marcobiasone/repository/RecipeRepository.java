package it.marcobiasone.repository;

import it.marcobiasone.domain.Recipe;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Recipe entity.
 */
@Repository
public interface RecipeRepository
    extends RecipeRepositoryWithBagRelationships, JpaRepository<Recipe, Long>, JpaSpecificationExecutor<Recipe> {
    default Optional<Recipe> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findOneWithToOneRelationships(id));
    }

    default List<Recipe> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships());
    }

    default Page<Recipe> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships(pageable));
    }

    @Query(
        value = "select distinct recipe from Recipe recipe left join fetch recipe.recipecategory",
        countQuery = "select count(distinct recipe) from Recipe recipe"
    )
    Page<Recipe> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct recipe from Recipe recipe left join fetch recipe.recipecategory")
    List<Recipe> findAllWithToOneRelationships();

    @Query("select recipe from Recipe recipe left join fetch recipe.recipecategory where recipe.id =:id")
    Optional<Recipe> findOneWithToOneRelationships(@Param("id") Long id);
}
