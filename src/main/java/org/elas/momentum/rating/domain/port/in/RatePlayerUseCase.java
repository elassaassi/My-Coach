package org.elas.momentum.rating.domain.port.in;

import org.elas.momentum.rating.domain.model.PlayerLevel;

public interface RatePlayerUseCase {

    record Command(String activityId, String raterId, String ratedUserId,
                   int behavior, int technicality, int teamwork,
                   PlayerLevel level, boolean isManOfMatch, String comment) {}

    String rate(Command command);
}
