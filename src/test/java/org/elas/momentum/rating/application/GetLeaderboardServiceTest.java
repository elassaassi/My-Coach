package org.elas.momentum.rating.application;

import org.elas.momentum.rating.application.usecase.GetLeaderboardService;
import org.elas.momentum.rating.domain.model.LeaderboardEntry;
import org.elas.momentum.rating.domain.port.out.PlayerStatsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetLeaderboardServiceTest {

    @Mock PlayerStatsRepository statsRepository;

    GetLeaderboardService service;

    @BeforeEach
    void setUp() {
        service = new GetLeaderboardService(statsRepository, Executors.newVirtualThreadPerTaskExecutor());
    }

    @Test
    @DisplayName("getLeaderboard() délègue au repository avec sport et limit")
    void getLeaderboard_delegatesToRepo() {
        var entry = new LeaderboardEntry("football", 1, "user-1", 88);
        when(statsRepository.findLeaderboard("football", 10)).thenReturn(List.of(entry));

        var result = service.getLeaderboard("football", 10);

        assertThat(result).containsExactly(entry);
        verify(statsRepository).findLeaderboard("football", 10);
    }

    @Test
    @DisplayName("getLeaderboard() sans résultat — retourne liste vide")
    void getLeaderboard_noResults_returnsEmpty() {
        when(statsRepository.findLeaderboard(any(), anyInt())).thenReturn(List.of());

        assertThat(service.getLeaderboard("tennis", 5)).isEmpty();
    }

    @Test
    @DisplayName("getMultiSportLeaderboard() combine les résultats de chaque sport")
    void getMultiSportLeaderboard_combinesResults() {
        var e1 = new LeaderboardEntry("football", 1, "user-1", 80);
        var e2 = new LeaderboardEntry("tennis",   1, "user-2", 75);
        when(statsRepository.findLeaderboard("football", 5)).thenReturn(List.of(e1));
        when(statsRepository.findLeaderboard("tennis",   5)).thenReturn(List.of(e2));

        var result = service.getMultiSportLeaderboard(List.of("football", "tennis"), 5);

        assertThat(result).containsExactlyInAnyOrder(e1, e2);
    }

    @Test
    @DisplayName("getMultiSportLeaderboard() liste vide — retourne liste vide")
    void getMultiSportLeaderboard_emptySports_returnsEmpty() {
        var result = service.getMultiSportLeaderboard(List.of(), 10);

        assertThat(result).isEmpty();
        verifyNoInteractions(statsRepository);
    }
}