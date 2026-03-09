package org.elas.momentum.coaching.infrastructure.persistence;

import org.elas.momentum.coaching.domain.model.*;

import java.time.Instant;
import java.util.ArrayList;

/**
 * Converts between domain objects and JPA entities for the coaching module.
 */
final class CoachingMapper {

    private CoachingMapper() {}

    // ── Coach ─────────────────────────────────────────────────────────────────

    static Coach toDomain(CoachEntity e) {
        return Coach.reconstitute(
                CoachId.of(e.getId()),
                e.getUserId(),
                e.getBio(),
                new ArrayList<>(e.getSports()),
                new ArrayList<>(e.getCertifications()),
                e.getHourlyRate(),
                e.getCurrency(),
                e.isAvailable(),
                e.getAverageRating() == null ? 0.0 : e.getAverageRating().doubleValue(),
                e.getCreatedAt()
        );
    }

    static CoachEntity toEntity(Coach coach) {
        var e = new CoachEntity();
        e.setId(coach.getId().value());
        e.setUserId(coach.getUserId());
        e.setBio(coach.getBio());
        e.setSports(new ArrayList<>(coach.getSports()));
        e.setCertifications(new ArrayList<>(coach.getCertifications()));
        e.setHourlyRate(coach.getHourlyRate());
        e.setCurrency(coach.getCurrency());
        e.setAvailable(coach.isAvailable());
        e.setAverageRating(java.math.BigDecimal.valueOf(coach.getAverageRating()).setScale(2, java.math.RoundingMode.HALF_UP));
        e.setCreatedAt(coach.getCreatedAt());
        return e;
    }

    // ── CoachingOffer ─────────────────────────────────────────────────────────

    static CoachingOffer toDomain(CoachingOfferEntity e) {
        return new CoachingOffer(
                OfferId.of(e.getId()),
                CoachId.of(e.getCoachId()),
                e.getTitle(),
                e.getDescription(),
                TargetAudience.valueOf(e.getTargetAudience()),
                e.getSport(),
                e.getDurationMin(),
                e.getPrice(),
                e.getCurrency()
        );
    }

    static CoachingOfferEntity toEntity(CoachingOffer offer) {
        var e = new CoachingOfferEntity();
        e.setId(offer.id().value());
        e.setCoachId(offer.coachId().value());
        e.setTitle(offer.title());
        e.setDescription(offer.description());
        e.setTargetAudience(offer.targetAudience().name());
        e.setSport(offer.sport());
        e.setDurationMin(offer.durationMin());
        e.setPrice(offer.price());
        e.setCurrency(offer.currency());
        e.setCreatedAt(Instant.now());
        return e;
    }

    // ── CoachingBooking ───────────────────────────────────────────────────────

    static CoachingBooking toDomain(CoachingBookingEntity e) {
        return CoachingBooking.reconstitute(
                BookingId.of(e.getId()),
                OfferId.of(e.getOfferId()),
                e.getClientId(),
                e.getClientType(),
                e.getScheduledAt(),
                BookingStatus.valueOf(e.getStatus()),
                e.getCreatedAt()
        );
    }

    static CoachingBookingEntity toEntity(CoachingBooking booking) {
        var e = new CoachingBookingEntity();
        e.setId(booking.getId().value());
        e.setOfferId(booking.getOfferId().value());
        e.setClientId(booking.getClientId());
        e.setClientType(booking.getClientType());
        e.setScheduledAt(booking.getScheduledAt());
        e.setStatus(booking.getStatus().name());
        e.setCreatedAt(booking.getCreatedAt());
        return e;
    }
}
