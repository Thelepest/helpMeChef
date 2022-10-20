package it.marcobiasone.repository;

import it.marcobiasone.domain.RecipeCategory;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the RecipeCategory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RecipeCategoryRepository extends JpaRepository<RecipeCategory, Long>, JpaSpecificationExecutor<RecipeCategory> {}
