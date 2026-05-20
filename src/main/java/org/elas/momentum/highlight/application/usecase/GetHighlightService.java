package org.elas.momentum.highlight.application.usecase;

import org.elas.momentum.highlight.application.dto.HighlightResponse;
import org.elas.momentum.highlight.domain.port.in.GetHighlightUseCase;
import org.elas.momentum.highlight.domain.port.out.CommentRepository;
import org.elas.momentum.highlight.domain.port.out.HighlightRepository;
import org.elas.momentum.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GetHighlightService implements GetHighlightUseCase {

    private final HighlightRepository highlightRepository;
    private final CommentRepository   commentRepository;

    public GetHighlightService(HighlightRepository highlightRepository,
                               CommentRepository commentRepository) {
        this.highlightRepository = highlightRepository;
        this.commentRepository   = commentRepository;
    }

    @Override
    public List<HighlightResponse> getFeed(int limit) {
        var highlights = highlightRepository.findTopByLikesAndRecency(limit);
        var ids        = highlights.stream().map(h -> h.getId().value()).toList();
        var counts     = commentRepository.countByHighlightIds(ids);
        return highlights.stream()
                .map(h -> HighlightResponse.from(h, counts.getOrDefault(h.getId().value(), 0L).intValue()))
                .toList();
    }

    @Override
    public HighlightResponse getById(String highlightId) {
        return highlightRepository.findById(highlightId)
                .map(HighlightResponse::from)
                .orElseThrow(() -> new NotFoundException("Highlight", highlightId));
    }

    @Override
    public List<HighlightResponse> getArchivedByUser(String userId) {
        return highlightRepository.findArchivedByPublisherId(userId).stream()
                .map(HighlightResponse::from)
                .toList();
    }
}