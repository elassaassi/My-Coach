package org.elas.momentum.highlight.domain.port.in;

import org.elas.momentum.highlight.domain.model.Highlight;
import org.elas.momentum.highlight.domain.model.HighlightOfDay;

import java.util.Optional;

public interface GetHighlightOfDayUseCase {

    HighlightOfDay getToday();

    Optional<Highlight> getTodayHighlight();
}
