package it.marcobiasone.repository;

import it.marcobiasone.domain.UncompatibleIRCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the UncompatibleIRCategory entity.
 */
@Repository
public interface UncompatibleIRCategoryRepository
    extends JpaRepository<UncompatibleIRCategory, Long>, JpaSpecificationExecutor<UncompatibleIRCategory> {
    default Optional<UncompatibleIRCategory> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<UncompatibleIRCategory> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<UncompatibleIRCategory> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct uncompatibleIRCategory from UncompatibleIRCategory uncompatibleIRCategory left join fetch uncompatibleIRCategory.ingredientcategory left join fetch uncompatibleIRCategory.recipecategory",
        countQuery = "select count(distinct uncompatibleIRCategory) from UncompatibleIRCategory uncompatibleIRCategory"
    )
    Page<UncompatibleIRCategory> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select distinct uncompatibleIRCategory from UncompatibleIRCategory uncompatibleIRCategory left join fetch uncompatibleIRCategory.ingredientcategory left join fetch uncompatibleIRCategory.recipecategory"
    )
    List<UncompatibleIRCategory> findAllWithToOneRelationships();

    @Query(
        "select uncompatibleIRCategory from UncompatibleIRCategory uncompatibleIRCategory left join fetch uncompatibleIRCategory.ingredientcategory left join fetch uncompatibleIRCategory.recipecategory where uncompatibleIRCategory.id =:id"
    )
    Optional<UncompatibleIRCategory> findOneWithToOneRelationships(@Param("id") Long id);
}
