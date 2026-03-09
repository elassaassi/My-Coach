package org.elas.momentum.highlight.domain.port.out;

import org.elas.momentum.highlight.domain.model.HighlightOfDay;

import java.time.LocalDate;
import java.util.Optional;

public interface HighlightOfDayRepository {

    HighlightOfDay save(HighlightOfDay highlightOfDay);

    Optional<HighlightOfDay> findByDate(LocalDate date);
}
