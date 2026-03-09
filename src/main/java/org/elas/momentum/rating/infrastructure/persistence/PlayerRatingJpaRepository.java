package org.elas.momentum.rating.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlayerRatingJpaRepository extends JpaRepository<PlayerRatingEntity, String> {

    boolean existsByActivityIdAndRaterIdAndRatedUserId(String activityId, String raterId, String ratedUserId);

    List<PlayerRatingEntity> findByActivityIdAndRatedUserId(String activityId, String ratedUserId);

    @Query("SELECT COUNT(r) FROM PlayerRatingEntity r WHERE r.activityId = :activityId AND r.ratedUserId = :userId AND r.isManOfMatch = true")
    int countManOfMatchVotes(@Param("activityId") String activityId, @Param("userId") String userId);

    @Query("SELECT COUNT(DISTINCT r.raterId) FROM PlayerRatingEntity r WHERE r.activityId = :activityId")
    int countActivityParticipants(@Param("activityId") String activityId);
}
