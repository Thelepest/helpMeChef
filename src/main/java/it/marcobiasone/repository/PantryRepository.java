package it.marcobiasone.repository;

import it.marcobiasone.domain.Pantry;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Pantry entity.
 */
@Repository
public interface PantryRepository
    extends PantryRepositoryWithBagRelationships, JpaRepository<Pantry, Long>, JpaSpecificationExecutor<Pantry> {
    @Query("select pantry from Pantry pantry where pantry.user.login = ?#{principal.preferredUsername}")
    List<Pantry> findByUserIsCurrentUser();

    default Optional<Pantry> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findOneWithToOneRelationships(id));
    }

    default List<Pantry> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships());
    }

    default Page<Pantry> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships(pageable));
    }

    @Query(
        value = "select distinct pantry from Pantry pantry left join fetch pantry.user",
        countQuery = "select count(distinct pantry) from Pantry pantry"
    )
    Page<Pantry> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct pantry from Pantry pantry left join fetch pantry.user")
    List<Pantry> findAllWithToOneRelationships();

    @Query("select pantry from Pantry pantry left join fetch pantry.user where pantry.id =:id")
    Optional<Pantry> findOneWithToOneRelationships(@Param("id") Long id);
}
