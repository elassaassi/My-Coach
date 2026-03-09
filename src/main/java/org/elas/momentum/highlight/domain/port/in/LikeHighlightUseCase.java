package org.elas.momentum.highlight.domain.port.in;

public interface LikeHighlightUseCase {

    record Like(String highlightId, String userId, boolean liked) {}

    void like(Like cmd);
}
