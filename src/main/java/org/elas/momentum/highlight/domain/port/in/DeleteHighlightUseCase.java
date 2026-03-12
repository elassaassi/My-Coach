package org.elas.momentum.highlight.domain.port.in;

public interface DeleteHighlightUseCase {
    void delete(String highlightId, String requesterId, boolean isAdmin);
}
