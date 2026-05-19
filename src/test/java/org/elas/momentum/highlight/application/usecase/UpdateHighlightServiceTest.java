package org.elas.momentum.highlight.application.usecase;

import org.elas.momentum.highlight.application.usecase.UpdateHighlightService;
import org.elas.momentum.highlight.domain.model.Highlight;
import org.elas.momentum.highlight.domain.model.MediaType;
import org.elas.momentum.highlight.domain.port.in.UpdateHighlightUseCase.Command;
import org.elas.momentum.highlight.domain.port.out.HighlightRepository;
import org.elas.momentum.shared.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;
@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateHighlightService")
class UpdateHighlightServiceTest {

    @Mock
    HighlightRepository highlightRepository;

    UpdateHighlightService service;

    private Highlight existingHighlight;

    @BeforeEach
    void setUp() {
        service = new UpdateHighlightService(highlightRepository);
        existingHighlight = Highlight.create("owner-1", "https://cdn.example.com/vid.mp4",
                MediaType.VIDEO, "Caption initiale", "football");
        lenient().when(highlightRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    @DisplayName("le propriétaire peut modifier la caption")
    void update_byOwner_updatesCaption() {
        when(highlightRepository.findById("h-1")).thenReturn(Optional.of(existingHighlight));
        var cmd = new Command("h-1", "owner-1", false, "Nouvelle caption", null);

        service.update(cmd);

        var captor = ArgumentCaptor.forClass(Highlight.class);
        verify(highlightRepository).save(captor.capture());
        assertThat(captor.getValue().getCaption()).isEqualTo("Nouvelle caption");
    }

    @Test
    @DisplayName("le propriétaire peut modifier le sport")
    void update_byOwner_updatesSport() {
        when(highlightRepository.findById("h-1")).thenReturn(Optional.of(existingHighlight));
        var cmd = new Command("h-1", "owner-1", false, null, "basketball");

        service.update(cmd);

        var captor = ArgumentCaptor.forClass(Highlight.class);
        verify(highlightRepository).save(captor.capture());
        assertThat(captor.getValue().getSport()).isEqualTo("basketball");
    }

    @Test
    @DisplayName("un admin peut modifier un highlight dont il n'est pas propriétaire")
    void update_byAdmin_succeeds() {
        when(highlightRepository.findById("h-1")).thenReturn(Optional.of(existingHighlight));
        var cmd = new Command("h-1", "admin-99", true, "Admin edit", "tennis");

        assertThatCode(() -> service.update(cmd)).doesNotThrowAnyException();
        verify(highlightRepository).save(any());
    }

    @Test
    @DisplayName("un non-propriétaire non-admin est rejeté avec AccessDeniedException")
    void update_byStranger_throwsAccessDenied() {
        when(highlightRepository.findById("h-1")).thenReturn(Optional.of(existingHighlight));
        var cmd = new Command("h-1", "stranger-99", false, "hack", "football");

        assertThatThrownBy(() -> service.update(cmd))
                .isInstanceOf(AccessDeniedException.class);
        verify(highlightRepository, never()).save(any());
    }

    @Test
    @DisplayName("highlight introuvable lève NotFoundException")
    void update_unknownHighlight_throwsNotFound() {
        when(highlightRepository.findById("h-unknown")).thenReturn(Optional.empty());
        var cmd = new Command("h-unknown", "owner-1", false, "caption", null);

        assertThatThrownBy(() -> service.update(cmd))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("une caption vide est ignorée — la valeur précédente est conservée")
    void update_blankCaption_isIgnored() {
        when(highlightRepository.findById("h-1")).thenReturn(Optional.of(existingHighlight));
        var cmd = new Command("h-1", "owner-1", false, "   ", null);

        service.update(cmd);

        var captor = ArgumentCaptor.forClass(Highlight.class);
        verify(highlightRepository).save(captor.capture());
        assertThat(captor.getValue().getCaption()).isEqualTo("Caption initiale");
    }
}