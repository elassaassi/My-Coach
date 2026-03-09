package org.elas.momentum.coaching.domain.port.out;

import org.elas.momentum.coaching.domain.model.CoachingOffer;

import java.util.List;
import java.util.Optional;

public interface OfferRepository {
    void save(CoachingOffer offer);
    Optional<CoachingOffer> findById(String id);
    List<CoachingOffer> findByCoachId(String coachId);
}
