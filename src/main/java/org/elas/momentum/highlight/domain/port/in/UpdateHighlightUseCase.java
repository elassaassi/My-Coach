package org.elas.momentum.highlight.domain.port.in;

public interface UpdateHighlightUseCase {

    record Command(String highlightId, String requesterId, boolean isAdmin,
                   String caption, String sport) {}

    void update(Command command);
}