package org.elas.momentum.highlight.domain.port.in;

import org.elas.momentum.highlight.application.dto.HighlightResponse;

import java.util.List;

public interface GetHighlightUseCase {
    List<HighlightResponse> getFeed(int limit);
    HighlightResponse getById(String highlightId);
    List<HighlightResponse> getArchivedByUser(String userId);
}