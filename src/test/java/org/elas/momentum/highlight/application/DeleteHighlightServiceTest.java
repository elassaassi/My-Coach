package org.elas.momentum.highlight.application;

import org.elas.momentum.highlight.application.usecase.DeleteHighlightService;
import org.elas.momentum.highlight.domain.model.Highlight;
import org.elas.momentum.highlight.domain.model.MediaType;
import org.elas.momentum.highlight.domain.port.out.HighlightRepository;
import org.elas.momentum.shared.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteHighlightService")
class DeleteHighlightServiceTest {

    @Mock
    HighlightRepository highlightRepository;

    DeleteHighlightService service;

    private Highlight highlight;

    @BeforeEach
    void setUp() {
        service = new DeleteHighlightService(highlightRepository);
        highlight = Highlight.create("owner-1", "https://cdn.example.com/photo.jpg",
                MediaType.PHOTO, "caption", "football");
    }

    @Test
    @DisplayName("le propriétaire peut supprimer son highlight")
    void delete_byOwner_callsDeleteById() {
        when(highlightRepository.findById("h-1")).thenReturn(Optional.of(highlight));

        service.delete("h-1", "owner-1", false);

        verify(highlightRepository).deleteById("h-1");
    }

    @Test
    @DisplayName("un admin peut supprimer un highlight dont il n'est pas propriétaire")
    void delete_byAdmin_succeeds() {
        when(highlightRepository.findById("h-1")).thenReturn(Optional.of(highlight));

        assertThatCode(() -> service.delete("h-1", "admin-99", true)).doesNotThrowAnyException();
        verify(highlightRepository).deleteById("h-1");
    }

    @Test
    @DisplayName("un non-propriétaire non-admin est rejeté avec 403")
    void delete_byStranger_throwsForbidden() {
        when(highlightRepository.findById("h-1")).thenReturn(Optional.of(highlight));

        assertThatThrownBy(() -> service.delete("h-1", "intrus", false))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode().value()).isEqualTo(403));
        verify(highlightRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("highlight introuvable lève NotFoundException")
    void delete_unknownHighlight_throwsNotFound() {
        when(highlightRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete("unknown", "owner-1", false))
                .isInstanceOf(NotFoundException.class);
    }
}