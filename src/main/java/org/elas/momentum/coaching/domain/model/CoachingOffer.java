package org.elas.momentum.coaching.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Value Object / Aggregate — CoachingOffer Bounded Context.
 * No Spring annotations — pure domain class.
 */
public record CoachingOffer(
        OfferId id,
        CoachId coachId,
        String title,
        String description,
        TargetAudience targetAudience,
        String sport,
        int durationMin,
        BigDecimal price,
        String currency
) {

    // ── Factory : creation with validation ────────────────────────────────────

    public static CoachingOffer create(CoachId coachId, String title, String description,
                                       TargetAudience targetAudience, String sport,
                                       int durationMin, BigDecimal price, String currency) {
        Objects.requireNonNull(coachId, "coachId must not be null");
        Objects.requireNonNull(title, "title must not be null");
        Objects.requireNonNull(targetAudience, "targetAudience must not be null");
        Objects.requireNonNull(sport, "sport must not be null");
        Objects.requireNonNull(price, "price must not be null");
        Objects.requireNonNull(currency, "currency must not be null");

        if (durationMin <= 0) {
            throw new IllegalArgumentException("Duration must be greater than 0, got: " + durationMin);
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0, got: " + price);
        }

        return new CoachingOffer(
                OfferId.generate(),
                coachId,
                title,
                description,
                targetAudience,
                sport,
                durationMin,
                price,
                currency
        );
    }
}
