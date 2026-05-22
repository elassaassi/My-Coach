package org.elas.momentum.coaching.application;

import org.elas.momentum.coaching.application.usecase.CreateOfferService;
import org.elas.momentum.coaching.domain.model.Coach;
import org.elas.momentum.coaching.domain.model.TargetAudience;
import org.elas.momentum.coaching.domain.port.in.CreateOfferUseCase.Command;
import org.elas.momentum.coaching.domain.port.out.CoachRepository;
import org.elas.momentum.coaching.domain.port.out.OfferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateOfferServiceTest {

    @Mock CoachRepository coachRepository;
    @Mock OfferRepository offerRepository;

    CreateOfferService service;

    @BeforeEach
    void setUp() {
        service = new CreateOfferService(coachRepository, offerRepository);
    }

    @Test
    @DisplayName("createOffer() nominal — persiste l'offre et retourne un id")
    void createOffer_nominal_savesAndReturnsId() {
        when(coachRepository.findById("coach-1")).thenReturn(Optional.of(mock(Coach.class)));

        String id = service.createOffer(new Command(
                "coach-1", "Coaching foot", "Améliore ta technique",
                TargetAudience.INDIVIDUAL, "football", 60,
                new BigDecimal("80.00"), "EUR"));

        assertThat(id).isNotBlank();
        verify(offerRepository).save(any());
    }

    @Test
    @DisplayName("createOffer() coach inexistant → IllegalArgumentException")
    void createOffer_coachNotFound_throws() {
        when(coachRepository.findById("bad-coach")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createOffer(new Command(
                "bad-coach", "Titre", "Desc",
                TargetAudience.ENTERPRISE, "tennis", 90,
                new BigDecimal("100"), "MAD")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("bad-coach");

        verify(offerRepository, never()).save(any());
    }
}