package it.marcobiasone.repository;

import it.marcobiasone.domain.Pantry;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface PantryRepositoryWithBagRelationships {
    Optional<Pantry> fetchBagRelationships(Optional<Pantry> pantry);

    List<Pantry> fetchBagRelationships(List<Pantry> pantries);

    Page<Pantry> fetchBagRelationships(Page<Pantry> pantries);
}
