package it.marcobiasone.repository;

import it.marcobiasone.domain.Tool;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface ToolRepositoryWithBagRelationships {
    Optional<Tool> fetchBagRelationships(Optional<Tool> tool);

    List<Tool> fetchBagRelationships(List<Tool> tools);

    Page<Tool> fetchBagRelationships(Page<Tool> tools);
}
