package org.elas.momentum.coaching.domain;

import org.elas.momentum.coaching.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for coaching domain — no Spring context, no external dependencies.
 */
class CoachTest {

    // ── Coach.register ────────────────────────────────────────────────────────

    @Test
    @DisplayName("Coach.register creates coach with correct defaults")
    void register_shouldCreateWithCorrectDefaults() {
        Coach coach = Coach.register("user-1", "Expert en football", BigDecimal.valueOf(150), "MAD");

        assertThat(coach.getId()).isNotNull();
        assertThat(coach.getUserId()).isEqualTo("user-1");
        assertThat(coach.getBio()).isEqualTo("Expert en football");
        assertThat(coach.getHourlyRate()).isEqualByComparingTo(BigDecimal.valueOf(150));
        assertThat(coach.getCurrency()).isEqualTo("MAD");
        assertThat(coach.isAvailable()).isTrue();
        assertThat(coach.getAverageRating()).isEqualTo(0.0);
        assertThat(coach.getCreatedAt()).isNotNull();
        assertThat(coach.getSports()).isEmpty();
        assertThat(coach.getCertifications()).isEmpty();
    }

    @Test
    @DisplayName("updateAvailability changes availability flag")
    void updateAvailability_shouldChangeFlag() {
        Coach coach = Coach.register("user-2", "Coach tennis", BigDecimal.valueOf(200), "EUR");
        assertThat(coach.isAvailable()).isTrue();

        coach.updateAvailability(false);
        assertThat(coach.isAvailable()).isFalse();

        coach.updateAvailability(true);
        assertThat(coach.isAvailable()).isTrue();
    }

    @Test
    @DisplayName("updateRating with valid value updates the rating")
    void updateRating_withValidValue_shouldUpdate() {
        Coach coach = Coach.register("user-3", "Coach natation", BigDecimal.valueOf(100), "MAD");

        coach.updateRating(4.5);
        assertThat(coach.getAverageRating()).isEqualTo(4.5);

        coach.updateRating(0.0);
        assertThat(coach.getAverageRating()).isEqualTo(0.0);

        coach.updateRating(5.0);
        assertThat(coach.getAverageRating()).isEqualTo(5.0);
    }

    @Test
    @DisplayName("updateRating below 0 throws IllegalArgumentException")
    void updateRating_belowZero_shouldThrow() {
        Coach coach = Coach.register("user-4", "Coach running", BigDecimal.valueOf(80), "MAD");

        assertThatThrownBy(() -> coach.updateRating(-0.1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Rating must be between 0 and 5");
    }

    @Test
    @DisplayName("updateRating above 5 throws IllegalArgumentException")
    void updateRating_aboveFive_shouldThrow() {
        Coach coach = Coach.register("user-5", "Coach basket", BigDecimal.valueOf(120), "MAD");

        assertThatThrownBy(() -> coach.updateRating(5.1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Rating must be between 0 and 5");
    }

    // ── CoachingOffer.create ──────────────────────────────────────────────────

    @Test
    @DisplayName("CoachingOffer.create validates duration must be greater than 0")
    void createOffer_withZeroDuration_shouldThrow() {
        CoachId coachId = CoachId.generate();

        assertThatThrownBy(() -> CoachingOffer.create(
                coachId, "Session individuelle", "Initiation au foot",
                TargetAudience.INDIVIDUAL, "football",
                0, BigDecimal.valueOf(200), "MAD"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Duration must be greater than 0");
    }

    @Test
    @DisplayName("CoachingOffer.create validates price must be greater than 0")
    void createOffer_withZeroPrice_shouldThrow() {
        CoachId coachId = CoachId.generate();

        assertThatThrownBy(() -> CoachingOffer.create(
                coachId, "Session individuelle", "Initiation au foot",
                TargetAudience.INDIVIDUAL, "football",
                60, BigDecimal.ZERO, "MAD"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Price must be greater than 0");
    }

    @Test
    @DisplayName("CoachingOffer.create with valid data succeeds")
    void createOffer_withValidData_shouldSucceed() {
        CoachId coachId = CoachId.generate();

        CoachingOffer offer = CoachingOffer.create(
                coachId, "Programme entreprise", "Coaching collectif",
                TargetAudience.ENTERPRISE, "yoga",
                90, BigDecimal.valueOf(500), "MAD");

        assertThat(offer.id()).isNotNull();
        assertThat(offer.coachId()).isEqualTo(coachId);
        assertThat(offer.targetAudience()).isEqualTo(TargetAudience.ENTERPRISE);
        assertThat(offer.durationMin()).isEqualTo(90);
        assertThat(offer.price()).isEqualByComparingTo(BigDecimal.valueOf(500));
    }

    // ── CoachingBooking ───────────────────────────────────────────────────────

    @Test
    @DisplayName("CoachingBooking.create initializes with PENDING status")
    void createBooking_shouldHavePendingStatus() {
        OfferId offerId = OfferId.generate();
        Instant scheduledAt = Instant.now().plusSeconds(3600);

        CoachingBooking booking = CoachingBooking.create(offerId, "client-1", "USER", scheduledAt);

        assertThat(booking.getId()).isNotNull();
        assertThat(booking.getOfferId()).isEqualTo(offerId);
        assertThat(booking.getClientId()).isEqualTo("client-1");
        assertThat(booking.getClientType()).isEqualTo("USER");
        assertThat(booking.getScheduledAt()).isEqualTo(scheduledAt);
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.PENDING);
        assertThat(booking.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("CoachingBooking.confirm transitions PENDING to CONFIRMED")
    void confirm_shouldTransitionToConfirmed() {
        CoachingBooking booking = CoachingBooking.create(
                OfferId.generate(), "client-2", "USER", Instant.now().plusSeconds(7200));

        booking.confirm();

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
    }

    @Test
    @DisplayName("CoachingBooking.cancel from CONFIRMED transitions to CANCELLED")
    void cancel_fromConfirmed_shouldTransitionToCancelled() {
        CoachingBooking booking = CoachingBooking.create(
                OfferId.generate(), "client-3", "ENTERPRISE", Instant.now().plusSeconds(7200));
        booking.confirm();
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CONFIRMED);

        booking.cancel();

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
    }

    @Test
    @DisplayName("Double cancel throws IllegalStateException")
    void cancel_whenAlreadyCancelled_shouldThrow() {
        CoachingBooking booking = CoachingBooking.create(
                OfferId.generate(), "client-4", "USER", Instant.now().plusSeconds(7200));
        booking.cancel();

        assertThatThrownBy(booking::cancel)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already CANCELLED");
    }

    @Test
    @DisplayName("Cancel a DONE booking throws IllegalStateException")
    void cancel_whenDone_shouldThrow() {
        CoachingBooking booking = CoachingBooking.reconstitute(
                BookingId.generate(),
                OfferId.generate(),
                "client-5",
                "USER",
                Instant.now().plusSeconds(3600),
                BookingStatus.DONE,
                Instant.now()
        );

        assertThatThrownBy(booking::cancel)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("DONE");
    }

    @Test
    @DisplayName("CoachId.generate produces unique IDs")
    void coachId_generate_shouldProduceUniqueIds() {
        Set<String> ids = IntStream.range(0, 100)
                .mapToObj(i -> CoachId.generate().value())
                .collect(Collectors.toSet());

        assertThat(ids).hasSize(100);
        ids.forEach(id -> assertThat(id).isNotBlank());
    }
}
