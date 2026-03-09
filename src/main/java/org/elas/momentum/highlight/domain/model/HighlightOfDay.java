package org.elas.momentum.highlight.domain.model;

import java.time.Instant;
import java.time.LocalDate;

public record HighlightOfDay(LocalDate date, String highlightId, Instant selectedAt) {
    public static HighlightOfDay select(LocalDate date, String highlightId) {
        return new HighlightOfDay(date, highlightId, Instant.now());
    }
}
