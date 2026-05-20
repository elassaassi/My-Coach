package org.elas.momentum.rating.application;

import org.elas.momentum.rating.application.usecase.GetPlayerStatsService;
import org.elas.momentum.rating.domain.model.PlayerStats;
import org.elas.momentum.rating.domain.port.out.PlayerStatsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetPlayerStatsServiceTest {

    @Mock PlayerStatsRepository statsRepository;

    GetPlayerStatsService service;

    @BeforeEach
    void setUp() {
        service = new GetPlayerStatsService(statsRepository);
    }

    @Test
    @DisplayName("getStats() — retourne les stats existantes")
    void getStats_found_returnsStats() {
        var stats = PlayerStats.empty("user-1", "football");
        when(statsRepository.findByUserIdAndSport("user-1", "football"))
                .thenReturn(Optional.of(stats));

        var result = service.getStats("user-1", "football");

        assertThat(result).isSameAs(stats);
    }

    @Test
    @DisplayName("getStats() introuvable — retourne des stats vides sans exception")
    void getStats_notFound_returnsEmpty() {
        when(statsRepository.findByUserIdAndSport("user-99", "tennis"))
                .thenReturn(Optional.empty());

        var result = service.getStats("user-99", "tennis");

        assertThat(result).isNotNull();
        assertThat(result.totalRatings()).isZero();
        assertThat(result.proScore()).isZero();
    }
}