package org.elas.momentum.rating.application.usecase;

import org.elas.momentum.rating.domain.model.PlayerStats;
import org.elas.momentum.rating.domain.port.in.GetPlayerStatsUseCase;
import org.elas.momentum.rating.domain.port.out.PlayerStatsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetPlayerStatsService implements GetPlayerStatsUseCase {

    private final PlayerStatsRepository statsRepository;

    public GetPlayerStatsService(PlayerStatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    @Override
    public PlayerStats getStats(String userId, String sport) {
        return statsRepository.findByUserIdAndSport(userId, sport)
                .orElse(PlayerStats.empty(userId, sport));
    }
}
