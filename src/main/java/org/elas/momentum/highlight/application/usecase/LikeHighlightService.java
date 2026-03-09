package org.elas.momentum.highlight.application.usecase;

import org.elas.momentum.highlight.domain.port.in.LikeHighlightUseCase;
import org.elas.momentum.highlight.domain.port.out.HighlightLikeCounterPort;
import org.elas.momentum.highlight.domain.port.out.HighlightRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LikeHighlightService implements LikeHighlightUseCase {

    private final HighlightRepository highlightRepository;
    private final HighlightLikeCounterPort likeCounterPort;

    public LikeHighlightService(HighlightRepository highlightRepository,
                                HighlightLikeCounterPort likeCounterPort) {
        this.highlightRepository = highlightRepository;
        this.likeCounterPort = likeCounterPort;
    }

    @Override
    public void like(Like cmd) {
        var highlight = highlightRepository.findById(cmd.highlightId())
                .orElseThrow(() -> new IllegalArgumentException("Highlight not found: " + cmd.highlightId()));

        if (cmd.liked()) {
            likeCounterPort.increment(cmd.highlightId());
            highlight.incrementLikes();
        } else {
            likeCounterPort.decrement(cmd.highlightId());
            highlight.decrementLikes();
        }
        highlightRepository.save(highlight);
    }
}
