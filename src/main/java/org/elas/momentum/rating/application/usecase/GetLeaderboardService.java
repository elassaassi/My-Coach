package org.elas.momentum.rating.application.usecase;

import org.elas.momentum.rating.domain.model.LeaderboardEntry;
import org.elas.momentum.rating.domain.port.in.GetLeaderboardUseCase;
import org.elas.momentum.rating.domain.port.out.PlayerStatsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@Transactional(readOnly = true)
public class GetLeaderboardService implements GetLeaderboardUseCase {

    private final PlayerStatsRepository statsRepository;
    private final Executor virtualThreadExecutor;

    public GetLeaderboardService(PlayerStatsRepository statsRepository,
                                  Executor virtualThreadExecutor) {
        this.statsRepository = statsRepository;
        this.virtualThreadExecutor = virtualThreadExecutor;
    }

    @Override
    public List<LeaderboardEntry> getLeaderboard(String sport, int limit) {
        return statsRepository.findLeaderboard(sport, limit);
    }

    @Override
    public List<LeaderboardEntry> getMultiSportLeaderboard(List<String> sports, int limitPerSport) {
        List<CompletableFuture<List<LeaderboardEntry>>> futures = sports.stream()
                .map(sport -> CompletableFuture.supplyAsync(
                        () -> statsRepository.findLeaderboard(sport, limitPerSport),
                        virtualThreadExecutor))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        List<LeaderboardEntry> result = new ArrayList<>();
        for (var future : futures) {
            result.addAll(future.join());
        }
        return result;
    }
}
