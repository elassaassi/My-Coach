package org.elas.momentum.rating.domain.port.out;

import org.elas.momentum.rating.domain.model.LeaderboardEntry;
import org.elas.momentum.rating.domain.model.PlayerStats;

import java.util.List;
import java.util.Optional;

public interface PlayerStatsRepository {
    Optional<PlayerStats> findByUserIdAndSport(String userId, String sport);
    void save(PlayerStats stats);
    List<LeaderboardEntry> findLeaderboard(String sport, int limit);
    void refreshLeaderboard(String sport);
}
