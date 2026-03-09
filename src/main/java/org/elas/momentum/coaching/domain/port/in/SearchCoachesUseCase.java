package org.elas.momentum.coaching.domain.port.in;

import org.elas.momentum.coaching.domain.model.Coach;

import java.util.List;

public interface SearchCoachesUseCase {
    record Query(String sport, Double maxHourlyRate, Double minRating) {}
    List<Coach> search(Query query);
}
