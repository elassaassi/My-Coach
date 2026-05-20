package org.elas.momentum.coaching.application;

import org.elas.momentum.coaching.application.usecase.SearchCoachesService;
import org.elas.momentum.coaching.domain.model.Coach;
import org.elas.momentum.coaching.domain.port.in.SearchCoachesUseCase.Query;
import org.elas.momentum.coaching.domain.port.out.CoachRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchCoachesServiceTest {

    @Mock CoachRepository coachRepository;

    SearchCoachesService service;

    @BeforeEach
    void setUp() {
        service = new SearchCoachesService(coachRepository);
    }

    @Test
    @DisplayName("search() délègue au repository avec les bons paramètres")
    void search_delegatesToRepository() {
        var coach = mock(Coach.class);
        when(coachRepository.findAvailable("football", 100.0, 4.0)).thenReturn(List.of(coach));

        var result = service.search(new Query("football", 100.0, 4.0));

        assertThat(result).containsExactly(coach);
        verify(coachRepository).findAvailable("football", 100.0, 4.0);
    }

    @Test
    @DisplayName("search() sans résultat — retourne liste vide")
    void search_noResults_returnsEmpty() {
        when(coachRepository.findAvailable(any(), any(), any())).thenReturn(List.of());

        var result = service.search(new Query("tennis", null, null));

        assertThat(result).isEmpty();
    }
}