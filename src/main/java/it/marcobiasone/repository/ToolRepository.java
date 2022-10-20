package it.marcobiasone.repository;

import it.marcobiasone.domain.Tool;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Tool entity.
 */
@Repository
public interface ToolRepository extends ToolRepositoryWithBagRelationships, JpaRepository<Tool, Long>, JpaSpecificationExecutor<Tool> {
    default Optional<Tool> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<Tool> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<Tool> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }
}
