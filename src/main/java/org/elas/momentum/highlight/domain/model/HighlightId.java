package org.elas.momentum.highlight.domain.model;

import java.util.UUID;

public record HighlightId(String value) {
    public static HighlightId generate() { return new HighlightId(UUID.randomUUID().toString()); }
    public static HighlightId of(String value) { return new HighlightId(value); }
}
