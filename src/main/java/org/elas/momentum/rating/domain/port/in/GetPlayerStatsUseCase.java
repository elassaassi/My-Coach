package org.elas.momentum.rating.domain.port.in;

import org.elas.momentum.rating.domain.model.PlayerStats;

public interface GetPlayerStatsUseCase {
    PlayerStats getStats(String userId, String sport);
}
