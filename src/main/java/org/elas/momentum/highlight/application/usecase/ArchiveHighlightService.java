package org.elas.momentum.highlight.application.usecase;

import org.elas.momentum.highlight.domain.port.in.ArchiveHighlightUseCase;
import org.elas.momentum.highlight.domain.port.out.HighlightRepository;
import org.elas.momentum.shared.exception.NotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class ArchiveHighlightService implements ArchiveHighlightUseCase {

    private final HighlightRepository highlightRepository;

    ArchiveHighlightService(HighlightRepository highlightRepository) {
        this.highlightRepository = highlightRepository;
    }

    @Override
    public void archive(Command command) {
        var highlight = highlightRepository.findById(command.highlightId())
                .orElseThrow(() -> new NotFoundException("HIGHLIGHT_NOT_FOUND", "Highlight introuvable"));

        if (!command.isAdmin() && !highlight.getPublisherId().equals(command.requesterId())) {
            throw new AccessDeniedException("Not the owner of this highlight");
        }

        if (command.archive()) {
            highlight.archive();
        } else {
            highlight.unarchive();
        }

        highlightRepository.save(highlight);
    }
}