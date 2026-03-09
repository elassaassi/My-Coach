package org.elas.momentum.rating.infrastructure.persistence;

import org.elas.momentum.rating.domain.model.*;
import org.elas.momentum.rating.domain.port.out.PlayerStatsRepository;
import org.elas.momentum.rating.domain.port.out.RatingRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
public class RatingPersistenceAdapter implements RatingRepository, PlayerStatsRepository {

    private final PlayerRatingJpaRepository ratingRepo;
    private final PlayerStatsJpaRepository statsRepo;
    private final LeaderboardCacheJpaRepository leaderboardRepo;
    private final RatingMapper mapper;

    public RatingPersistenceAdapter(PlayerRatingJpaRepository ratingRepo,
                                     PlayerStatsJpaRepository statsRepo,
                                     LeaderboardCacheJpaRepository leaderboardRepo,
                                     RatingMapper mapper) {
        this.ratingRepo = ratingRepo;
        this.statsRepo = statsRepo;
        this.leaderboardRepo = leaderboardRepo;
        this.mapper = mapper;
    }

    // ── RatingRepository ────────────────────────────────────────────────────────

    @Override
    public void save(PlayerRating rating) {
        ratingRepo.save(mapper.toEntity(rating));
    }

    @Override
    public boolean existsByActivityIdAndRaterIdAndRatedUserId(String activityId, String raterId, String ratedUserId) {
        return ratingRepo.existsByActivityIdAndRaterIdAndRatedUserId(activityId, raterId, ratedUserId);
    }

    @Override
    public List<PlayerRating> findByActivityIdAndRatedUserId(String activityId, String ratedUserId) {
        return ratingRepo.findByActivityIdAndRatedUserId(activityId, ratedUserId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public int countManOfMatchVotes(String activityId, String userId) {
        return ratingRepo.countManOfMatchVotes(activityId, userId);
    }

    @Override
    public int countActivityParticipants(String activityId) {
        return ratingRepo.countActivityParticipants(activityId);
    }

    // ── PlayerStatsRepository ───────────────────────────────────────────────────

    @Override
    public Optional<PlayerStats> findByUserIdAndSport(String userId, String sport) {
        return statsRepo.findByUserIdAndSport(userId, sport).map(mapper::toDomain);
    }

    @Override
    public void save(PlayerStats stats) {
        statsRepo.save(mapper.toEntity(stats));
    }

    @Override
    public List<LeaderboardEntry> findLeaderboard(String sport, int limit) {
        return leaderboardRepo.findBySportOrderByScoreDesc(sport, PageRequest.of(0, limit))
                .stream()
                .map(e -> new LeaderboardEntry(e.getId().getSport(), e.getId().getRank(), e.getUserId(), e.getScore()))
                .toList();
    }

    @Override
    public void refreshLeaderboard(String sport) {
        leaderboardRepo.deleteBySport(sport);
        var topStats = statsRepo.findAll().stream()
                .filter(s -> "all".equals(s.getSport()) || sport.equals(s.getSport()))
                .sorted((a, b) -> Integer.compare(b.getProScore(), a.getProScore()))
                .limit(100)
                .toList();
        for (int i = 0; i < topStats.size(); i++) {
            var entry = new LeaderboardCacheEntity();
            entry.setId(new LeaderboardCacheEntity.LeaderboardId(sport, i + 1));
            entry.setUserId(topStats.get(i).getUserId());
            entry.setScore(topStats.get(i).getProScore());
            entry.setUpdatedAt(Instant.now());
            leaderboardRepo.save(entry);
        }
    }
}
