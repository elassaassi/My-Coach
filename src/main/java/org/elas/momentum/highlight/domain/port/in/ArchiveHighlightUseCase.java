package org.elas.momentum.highlight.domain.port.in;

public interface ArchiveHighlightUseCase {

    record Command(String highlightId, String requesterId, boolean isAdmin, boolean archive) {}

    void archive(Command command);
}