package org.elas.momentum.highlight.application.usecase;

import org.elas.momentum.highlight.domain.model.Highlight;
import org.elas.momentum.highlight.domain.port.in.PublishHighlightUseCase;
import org.elas.momentum.highlight.domain.port.out.HighlightRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PublishHighlightService implements PublishHighlightUseCase {

    private final HighlightRepository highlightRepository;

    public PublishHighlightService(HighlightRepository highlightRepository) {
        this.highlightRepository = highlightRepository;
    }

    @Override
    public String publish(Command command) {
        Highlight highlight = Highlight.create(
                command.publisherId(),
                command.mediaUrl(),
                command.mediaType(),
                command.caption(),
                command.sport()
        );
        Highlight saved = highlightRepository.save(highlight);
        return saved.getId().value();
    }
}
