package org.elas.momentum.rating.domain.port.in;

import org.elas.momentum.rating.domain.model.LeaderboardEntry;

import java.util.List;

public interface GetLeaderboardUseCase {
    List<LeaderboardEntry> getLeaderboard(String sport, int limit);
    List<LeaderboardEntry> getMultiSportLeaderboard(List<String> sports, int limitPerSport);
}
