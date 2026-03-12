package org.elas.momentum.highlight.application.usecase;

import org.elas.momentum.highlight.domain.port.in.UpdateHighlightUseCase;
import org.elas.momentum.highlight.domain.port.out.HighlightRepository;
import org.elas.momentum.shared.exception.NotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class UpdateHighlightService implements UpdateHighlightUseCase {

    private final HighlightRepository highlightRepository;

    UpdateHighlightService(HighlightRepository highlightRepository) {
        this.highlightRepository = highlightRepository;
    }

    @Override
    public void update(Command command) {
        var highlight = highlightRepository.findById(command.highlightId())
                .orElseThrow(() -> new NotFoundException("HIGHLIGHT_NOT_FOUND", "Highlight introuvable"));

        if (!command.isAdmin() && !highlight.getPublisherId().equals(command.requesterId())) {
            throw new AccessDeniedException("Not the owner of this highlight");
        }

        if (command.caption() != null && !command.caption().isBlank()) {
            highlight.updateCaption(command.caption().strip());
        }
        if (command.sport() != null && !command.sport().isBlank()) {
            highlight.updateSport(command.sport().strip());
        }

        highlightRepository.save(highlight);
    }
}