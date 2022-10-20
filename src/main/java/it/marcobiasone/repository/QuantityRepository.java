package it.marcobiasone.repository;

import it.marcobiasone.domain.Quantity;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Quantity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface QuantityRepository extends JpaRepository<Quantity, Long>, JpaSpecificationExecutor<Quantity> {}
