package it.marcobiasone.repository;

import it.marcobiasone.domain.IngredientCategory;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the IngredientCategory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface IngredientCategoryRepository
    extends JpaRepository<IngredientCategory, Long>, JpaSpecificationExecutor<IngredientCategory> {}
