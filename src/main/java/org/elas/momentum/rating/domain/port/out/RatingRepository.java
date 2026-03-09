package org.elas.momentum.rating.domain.port.out;

import org.elas.momentum.rating.domain.model.PlayerRating;

import java.util.List;

public interface RatingRepository {
    void save(PlayerRating rating);
    boolean existsByActivityIdAndRaterIdAndRatedUserId(String activityId, String raterId, String ratedUserId);
    List<PlayerRating> findByActivityIdAndRatedUserId(String activityId, String ratedUserId);
    int countManOfMatchVotes(String activityId, String userId);
    int countActivityParticipants(String activityId);
}
