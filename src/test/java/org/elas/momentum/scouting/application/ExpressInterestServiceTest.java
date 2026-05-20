package org.elas.momentum.scouting.application;

import org.elas.momentum.scouting.application.usecase.ExpressInterestService;
import org.elas.momentum.scouting.domain.model.ScoutingInterest;
import org.elas.momentum.scouting.domain.port.in.ExpressInterestUseCase.Command;
import org.elas.momentum.scouting.domain.port.out.ScoutingInterestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpressInterestServiceTest {

    @Mock ScoutingInterestRepository interestRepository;

    ExpressInterestService service;

    @BeforeEach
    void setUp() {
        service = new ExpressInterestService(interestRepository);
    }

    @Test
    @DisplayName("express() nominal — crée l'intérêt et retourne un id")
    void express_nominal_savesAndReturnsId() {
        when(interestRepository.existsByRecruiterIdAndTalentId("rec-1", "talent-1")).thenReturn(false);
        when(interestRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        String id = service.express(new Command("rec-1", "talent-1", "Très prometteur"));

        assertThat(id).isNotBlank();
        verify(interestRepository).save(any(ScoutingInterest.class));
    }

    @Test
    @DisplayName("express() déjà exprimé → IllegalStateException")
    void express_alreadyExists_throws() {
        when(interestRepository.existsByRecruiterIdAndTalentId("rec-1", "talent-1")).thenReturn(true);

        assertThatThrownBy(() -> service.express(new Command("rec-1", "talent-1", null)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("rec-1");

        verify(interestRepository, never()).save(any());
    }

    @Test
    @DisplayName("express() sans note — fonctionne avec note null")
    void express_nullNote_succeeds() {
        when(interestRepository.existsByRecruiterIdAndTalentId(any(), any())).thenReturn(false);
        when(interestRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        assertThatNoException().isThrownBy(
                () -> service.express(new Command("rec-2", "talent-2", null)));
    }
}