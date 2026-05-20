package org.elas.momentum.coaching.application;

import org.elas.momentum.coaching.application.usecase.RegisterCoachService;
import org.elas.momentum.coaching.domain.port.in.RegisterCoachUseCase.Command;
import org.elas.momentum.coaching.domain.port.out.CoachRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterCoachServiceTest {

    @Mock CoachRepository coachRepository;

    RegisterCoachService service;

    @BeforeEach
    void setUp() {
        service = new RegisterCoachService(coachRepository);
    }

    @Test
    @DisplayName("register() nominal — persiste le coach et retourne un id non vide")
    void register_nominal_savesAndReturnsId() {
        String id = service.register(new Command("user-1", "Coach pro", new BigDecimal("50.00"), "EUR"));

        assertThat(id).isNotBlank();
        verify(coachRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("register() deux fois — produit des ids différents")
    void register_twice_producesDistinctIds() {
        String id1 = service.register(new Command("user-1", "Bio 1", new BigDecimal("40"), "MAD"));
        String id2 = service.register(new Command("user-2", "Bio 2", new BigDecimal("60"), "EUR"));

        assertThat(id1).isNotEqualTo(id2);
    }
}