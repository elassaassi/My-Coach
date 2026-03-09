package org.elas.momentum.rating.infrastructure.persistence;

import org.elas.momentum.rating.domain.model.*;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class RatingMapper {

    public PlayerRatingEntity toEntity(PlayerRating domain) {
        var e = new PlayerRatingEntity();
        e.setId(domain.getId().value());
        e.setActivityId(domain.getActivityId());
        e.setRaterId(domain.getRaterId());
        e.setRatedUserId(domain.getRatedUserId());
        e.setBehavior(domain.getBehavior());
        e.setTechnicality(domain.getTechnicality());
        e.setTeamwork(domain.getTeamwork());
        e.setLevel(domain.getLevel().name());
        e.setManOfMatch(domain.isManOfMatch());
        e.setComment(domain.getComment());
        e.setCreatedAt(domain.getCreatedAt());
        return e;
    }

    public PlayerRating toDomain(PlayerRatingEntity e) {
        return PlayerRating.reconstitute(
                RatingId.of(e.getId()), e.getActivityId(), e.getRaterId(), e.getRatedUserId(),
                e.getBehavior(), e.getTechnicality(), e.getTeamwork(),
                PlayerLevel.valueOf(e.getLevel()), e.isManOfMatch(), e.getComment(), e.getCreatedAt()
        );
    }

    public PlayerStatsEntity toEntity(PlayerStats domain) {
        var e = new PlayerStatsEntity();
        e.setUserId(domain.userId());
        e.setSport(domain.sport());
        e.setTotalRatings(domain.totalRatings());
        e.setAvgBehavior(domain.avgBehavior());
        e.setAvgTechnicality(domain.avgTechnicality());
        e.setAvgTeamwork(domain.avgTeamwork());
        e.setWinRate(domain.winRate());
        e.setManOfMatchCount(domain.manOfMatchCount());
        e.setProScore(domain.proScore());
        e.setUpdatedAt(domain.updatedAt() != null ? domain.updatedAt() : Instant.now());
        return e;
    }

    public PlayerStats toDomain(PlayerStatsEntity e) {
        return new PlayerStats(e.getUserId(), e.getSport(), e.getTotalRatings(),
                e.getAvgBehavior(), e.getAvgTechnicality(), e.getAvgTeamwork(),
                e.getWinRate(), e.getManOfMatchCount(), e.getProScore(), e.getUpdatedAt());
    }
}
