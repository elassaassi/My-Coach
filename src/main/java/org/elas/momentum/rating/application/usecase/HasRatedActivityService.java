package org.elas.momentum.rating.application.usecase;

import org.elas.momentum.rating.domain.port.in.HasRatedActivityUseCase;
import org.elas.momentum.rating.domain.port.out.RatingRepository;
import org.springframework.stereotype.Service;

@Service
class HasRatedActivityService implements HasRatedActivityUseCase {

    private final RatingRepository ratingRepository;

    HasRatedActivityService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    @Override
    public boolean hasRated(String activityId, String raterId) {
        return ratingRepository.hasRatedActivity(activityId, raterId);
    }
}