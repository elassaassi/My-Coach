package org.elas.momentum.highlight.application.usecase;

import org.elas.momentum.highlight.domain.port.in.DeleteHighlightUseCase;
import org.elas.momentum.highlight.domain.port.out.HighlightRepository;
import org.elas.momentum.shared.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DeleteHighlightService implements DeleteHighlightUseCase {

    private final HighlightRepository highlightRepository;

    public DeleteHighlightService(HighlightRepository highlightRepository) {
        this.highlightRepository = highlightRepository;
    }

    @Override
    public void delete(String highlightId, String requesterId) {
        var highlight = highlightRepository.findById(highlightId)
                .orElseThrow(() -> new NotFoundException("Highlight", highlightId));
        if (!highlight.getPublisherId().equals(requesterId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your highlight");
        }
        highlightRepository.deleteById(highlightId);
    }
}
