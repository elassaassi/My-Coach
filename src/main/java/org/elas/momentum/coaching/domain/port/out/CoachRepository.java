package org.elas.momentum.coaching.domain.port.out;

import org.elas.momentum.coaching.domain.model.Coach;

import java.util.List;
import java.util.Optional;

public interface CoachRepository {
    void save(Coach coach);
    Optional<Coach> findById(String id);
    Optional<Coach> findByUserId(String userId);
    List<Coach> findAvailable(String sport, Double maxHourlyRate, Double minRating);
}
