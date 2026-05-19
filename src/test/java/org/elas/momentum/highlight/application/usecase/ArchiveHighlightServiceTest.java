package org.elas.momentum.highlight.application.usecase;

import org.elas.momentum.highlight.application.usecase.ArchiveHighlightService;
import org.elas.momentum.highlight.domain.model.Highlight;
import org.elas.momentum.highlight.domain.model.MediaType;
import org.elas.momentum.highlight.domain.port.in.ArchiveHighlightUseCase.Command;
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
@DisplayName("ArchiveHighlightService")
class ArchiveHighlightServiceTest {

    @Mock
    HighlightRepository highlightRepository;

    ArchiveHighlightService service;

    private Highlight highlight;

    @BeforeEach
    void setUp() {
        service = new ArchiveHighlightService(highlightRepository);
        highlight = Highlight.create("owner-1", "https://cdn.example.com/photo.jpg",
                MediaType.PHOTO, "caption", "tennis");
        lenient().when(highlightRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    @DisplayName("archive positionne archivedAt sur le highlight")
    void archive_byOwner_setsArchivedAt() {
        when(highlightRepository.findById("h-1")).thenReturn(Optional.of(highlight));
        var cmd = new Command("h-1", "owner-1", false, true);

        service.archive(cmd);

        var captor = ArgumentCaptor.forClass(Highlight.class);
        verify(highlightRepository).save(captor.capture());
        assertThat(captor.getValue().isArchived()).isTrue();
        assertThat(captor.getValue().getArchivedAt()).isNotNull();
    }

    @Test
    @DisplayName("unarchive remet archivedAt à null")
    void unarchive_byOwner_clearsArchivedAt() {
        highlight.archive(); // pré-état : déjà archivé
        when(highlightRepository.findById("h-1")).thenReturn(Optional.of(highlight));
        var cmd = new Command("h-1", "owner-1", false, false);

        service.archive(cmd);

        var captor = ArgumentCaptor.forClass(Highlight.class);
        verify(highlightRepository).save(captor.capture());
        assertThat(captor.getValue().isArchived()).isFalse();
        assertThat(captor.getValue().getArchivedAt()).isNull();
    }

    @Test
    @DisplayName("un admin peut archiver un highlight dont il n'est pas propriétaire")
    void archive_byAdmin_succeeds() {
        when(highlightRepository.findById("h-1")).thenReturn(Optional.of(highlight));
        var cmd = new Command("h-1", "admin-99", true, true);

        assertThatCode(() -> service.archive(cmd)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("un non-propriétaire est rejeté avec AccessDeniedException")
    void archive_byStranger_throwsAccessDenied() {
        when(highlightRepository.findById("h-1")).thenReturn(Optional.of(highlight));
        var cmd = new Command("h-1", "intrus", false, true);

        assertThatThrownBy(() -> service.archive(cmd))
                .isInstanceOf(AccessDeniedException.class);
        verify(highlightRepository, never()).save(any());
    }

    @Test
    @DisplayName("highlight introuvable lève NotFoundException")
    void archive_unknownHighlight_throwsNotFound() {
        when(highlightRepository.findById("x")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.archive(new Command("x", "owner-1", false, true)))
                .isInstanceOf(NotFoundException.class);
    }
}