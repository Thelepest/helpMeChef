package it.marcobiasone.repository;

import it.marcobiasone.domain.MyConfig;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the MyConfig entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MyConfigRepository extends JpaRepository<MyConfig, Long>, JpaSpecificationExecutor<MyConfig> {}
