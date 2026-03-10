package org.elas.momentum.highlight.domain.port.out;

import org.elas.momentum.highlight.domain.model.Highlight;

import java.util.List;
import java.util.Optional;

public interface HighlightRepository {

    Highlight save(Highlight highlight);

    Optional<Highlight> findById(String id);

    List<Highlight> findTopByLikesAndRecency(int limit);

    void deleteById(String id);
}
