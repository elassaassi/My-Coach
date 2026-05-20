package org.elas.momentum.rating.application;

import org.elas.momentum.rating.application.usecase.RatePlayerService;
import org.elas.momentum.rating.domain.exception.AlreadyRatedException;
import org.elas.momentum.rating.domain.model.PlayerLevel;
import org.elas.momentum.rating.domain.model.PlayerStats;
import org.elas.momentum.rating.domain.port.in.RatePlayerUseCase.Command;
import org.elas.momentum.rating.domain.port.out.PlayerStatsRepository;
import org.elas.momentum.rating.domain.port.out.RatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatePlayerServiceTest {

    @Mock RatingRepository ratingRepository;
    @Mock PlayerStatsRepository statsRepository;
    @Mock ApplicationEventPublisher eventPublisher;

    RatePlayerService service;

    private static final Command NOMINAL = new Command(
            "act-1", "rater-1", "rated-1",
            4, 3, 5, PlayerLevel.SEMI_PRO, false, "Bon match"
    );

    @BeforeEach
    void setUp() {
        service = new RatePlayerService(ratingRepository, statsRepository, eventPublisher);
    }

    @Test
    @DisplayName("rate() nominal — sauvegarde la note et retourne un id non vide")
    void rate_nominal_savesAndReturnsId() {
        when(ratingRepository.existsByActivityIdAndRaterIdAndRatedUserId(any(), any(), any())).thenReturn(false);
        when(statsRepository.findByUserIdAndSport(any(), any())).thenReturn(Optional.empty());

        String id = service.rate(NOMINAL);

        assertThat(id).isNotBlank();
        verify(ratingRepository).save(any());
        verify(statsRepository).save(any());
    }

    @Test
    @DisplayName("rate() déjà noté → AlreadyRatedException")
    void rate_alreadyRated_throws() {
        when(ratingRepository.existsByActivityIdAndRaterIdAndRatedUserId("act-1", "rater-1", "rated-1"))
                .thenReturn(true);

        assertThatThrownBy(() -> service.rate(NOMINAL))
                .isInstanceOf(AlreadyRatedException.class);

        verify(ratingRepository, never()).save(any());
    }

    @Test
    @DisplayName("rate() MoM majoritaire — publie ManOfMatchEvent")
    void rate_majorityMotm_publishesEvent() {
        when(ratingRepository.existsByActivityIdAndRaterIdAndRatedUserId(any(), any(), any())).thenReturn(false);
        when(statsRepository.findByUserIdAndSport(any(), any())).thenReturn(Optional.empty());
        when(ratingRepository.countManOfMatchVotes("act-1", "rated-1")).thenReturn(3);
        when(ratingRepository.countActivityParticipants("act-1")).thenReturn(4);

        service.rate(NOMINAL);

        verify(eventPublisher, atLeast(1)).publishEvent(any(Object.class));
    }

    @Test
    @DisplayName("rate() stats existantes — met à jour les moyennes")
    void rate_existingStats_updatesAverages() {
        when(ratingRepository.existsByActivityIdAndRaterIdAndRatedUserId(any(), any(), any())).thenReturn(false);
        var existing = PlayerStats.empty("rated-1", "all");
        when(statsRepository.findByUserIdAndSport("rated-1", "all")).thenReturn(Optional.of(existing));

        service.rate(NOMINAL);

        verify(statsRepository).save(argThat(s -> s.totalRatings() == 1));
    }
}