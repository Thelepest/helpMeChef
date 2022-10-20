package it.marcobiasone.repository;

import it.marcobiasone.domain.IngredientQuantity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the IngredientQuantity entity.
 */
@Repository
public interface IngredientQuantityRepository
    extends JpaRepository<IngredientQuantity, Long>, JpaSpecificationExecutor<IngredientQuantity> {
    default Optional<IngredientQuantity> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<IngredientQuantity> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<IngredientQuantity> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct ingredientQuantity from IngredientQuantity ingredientQuantity left join fetch ingredientQuantity.ingredient left join fetch ingredientQuantity.quantity",
        countQuery = "select count(distinct ingredientQuantity) from IngredientQuantity ingredientQuantity"
    )
    Page<IngredientQuantity> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select distinct ingredientQuantity from IngredientQuantity ingredientQuantity left join fetch ingredientQuantity.ingredient left join fetch ingredientQuantity.quantity"
    )
    List<IngredientQuantity> findAllWithToOneRelationships();

    @Query(
        "select ingredientQuantity from IngredientQuantity ingredientQuantity left join fetch ingredientQuantity.ingredient left join fetch ingredientQuantity.quantity where ingredientQuantity.id =:id"
    )
    Optional<IngredientQuantity> findOneWithToOneRelationships(@Param("id") Long id);
}
