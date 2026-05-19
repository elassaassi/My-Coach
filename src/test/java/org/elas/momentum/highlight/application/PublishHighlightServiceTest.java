package org.elas.momentum.highlight.application;

import org.elas.momentum.highlight.application.usecase.PublishHighlightService;
import org.elas.momentum.highlight.domain.model.Highlight;
import org.elas.momentum.highlight.domain.model.MediaType;
import org.elas.momentum.highlight.domain.port.in.PublishHighlightUseCase.Command;
import org.elas.momentum.highlight.domain.port.out.HighlightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PublishHighlightService")
class PublishHighlightServiceTest {

    @Mock
    HighlightRepository highlightRepository;

    PublishHighlightService service;

    @BeforeEach
    void setUp() {
        service = new PublishHighlightService(highlightRepository);
        when(highlightRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    @DisplayName("publish crée un highlight avec les bons attributs et renvoie son id")
    void publish_validCommand_createsHighlightAndReturnsId() {
        var command = new Command("user-42", "https://cdn.example.com/photo.jpg",
                MediaType.PHOTO, "Superbe tir !", "football");

        String id = service.publish(command);

        assertThat(id).isNotBlank();
        var captor = ArgumentCaptor.forClass(Highlight.class);
        verify(highlightRepository).save(captor.capture());

        Highlight saved = captor.getValue();
        assertThat(saved.getPublisherId()).isEqualTo("user-42");
        assertThat(saved.getMediaUrl()).isEqualTo("https://cdn.example.com/photo.jpg");
        assertThat(saved.getMediaType()).isEqualTo(MediaType.PHOTO);
        assertThat(saved.getCaption()).isEqualTo("Superbe tir !");
        assertThat(saved.getSport()).isEqualTo("football");
        assertThat(saved.getLikeCount()).isZero();
        assertThat(saved.isArchived()).isFalse();
    }

    @Test
    @DisplayName("publish sans caption crée quand même le highlight")
    void publish_nullCaption_stillCreatesHighlight() {
        var command = new Command("user-1", "https://cdn.example.com/video.mp4",
                MediaType.VIDEO, null, "basketball");

        String id = service.publish(command);

        assertThat(id).isNotBlank();
        verify(highlightRepository).save(any(Highlight.class));
    }

    @Test
    @DisplayName("publish appelle save exactement une fois")
    void publish_callsSaveOnce() {
        var command = new Command("user-1", "https://cdn.example.com/photo.jpg",
                MediaType.PHOTO, "caption", "tennis");

        service.publish(command);

        verify(highlightRepository, times(1)).save(any(Highlight.class));
    }

    @Test
    @DisplayName("deux publications distinctes produisent des ids différents")
    void publish_twiceDifferentCommands_producesDistinctIds() {
        var c1 = new Command("user-1", "https://cdn.example.com/a.jpg", MediaType.PHOTO, null, "football");
        var c2 = new Command("user-2", "https://cdn.example.com/b.jpg", MediaType.PHOTO, null, "tennis");

        String id1 = service.publish(c1);
        String id2 = service.publish(c2);

        assertThat(id1).isNotEqualTo(id2);
    }
}