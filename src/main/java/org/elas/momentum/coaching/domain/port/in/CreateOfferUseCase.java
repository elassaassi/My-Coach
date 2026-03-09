package org.elas.momentum.coaching.domain.port.in;

import org.elas.momentum.coaching.domain.model.TargetAudience;

import java.math.BigDecimal;

public interface CreateOfferUseCase {
    record Command(String coachId, String title, String description,
                   TargetAudience targetAudience, String sport,
                   int durationMin, BigDecimal price, String currency) {}
    String createOffer(Command command);
}
