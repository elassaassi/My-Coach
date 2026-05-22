package org.elas.momentum.rating.application.usecase;

import org.elas.momentum.rating.domain.port.out.RatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HasRatedActivityServiceTest {

    @Mock RatingRepository ratingRepository;

    HasRatedActivityService service;

    @BeforeEach
    void setUp() {
        service = new HasRatedActivityService(ratingRepository);
    }

    @Test
    @DisplayName("hasRated() retourne true si déjà noté")
    void hasRated_true() {
        when(ratingRepository.hasRatedActivity("act-1", "user-1")).thenReturn(true);

        assertThat(service.hasRated("act-1", "user-1")).isTrue();
    }

    @Test
    @DisplayName("hasRated() retourne false si pas encore noté")
    void hasRated_false() {
        when(ratingRepository.hasRatedActivity("act-1", "user-1")).thenReturn(false);

        assertThat(service.hasRated("act-1", "user-1")).isFalse();
    }
}