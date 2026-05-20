package org.elas.momentum.coaching.domain;

import org.elas.momentum.coaching.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;

class CoachingBookingTest {

    private CoachingBooking pendingBooking() {
        return CoachingBooking.create(
                OfferId.of("offer-1"),
                "client-1",
                "INDIVIDUAL",
                Instant.now().plusSeconds(3600)
        );
    }

    // ── confirm() guard ───────────────────────────────────────────────────────

    @Test
    @DisplayName("confirm() sur PENDING passe à CONFIRMED")
    void confirm_fromPending_succeeds() {
        var booking = pendingBooking();
        booking.confirm();
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
    }

    @Test
    @DisplayName("confirm() sur CONFIRMED lève IllegalStateException")
    void confirm_alreadyConfirmed_throws() {
        var booking = pendingBooking();
        booking.confirm();
        assertThatThrownBy(booking::confirm).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("confirm() sur CANCELLED lève IllegalStateException")
    void confirm_fromCancelled_throws() {
        var booking = pendingBooking();
        booking.cancel();
        assertThatThrownBy(booking::confirm).isInstanceOf(IllegalStateException.class);
    }

    // ── cancel() guard ────────────────────────────────────────────────────────

    @Test
    @DisplayName("cancel() sur PENDING passe à CANCELLED")
    void cancel_fromPending_succeeds() {
        var booking = pendingBooking();
        booking.cancel();
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
    }

    @Test
    @DisplayName("cancel() sur CONFIRMED passe à CANCELLED")
    void cancel_fromConfirmed_succeeds() {
        var booking = pendingBooking();
        booking.confirm();
        booking.cancel();
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
    }

    @Test
    @DisplayName("cancel() sur CANCELLED lève IllegalStateException")
    void cancel_alreadyCancelled_throws() {
        var booking = pendingBooking();
        booking.cancel();
        assertThatThrownBy(booking::cancel).isInstanceOf(IllegalStateException.class);
    }

    // ── reconstitute() ────────────────────────────────────────────────────────

    @Test
    @DisplayName("reconstitute() préserve tous les champs")
    void reconstitute_preservesAllFields() {
        var id = BookingId.generate();
        var offerId = OfferId.of("offer-99");
        var now = Instant.now();
        var booking = CoachingBooking.reconstitute(id, offerId, "client-2", "TEAM", now,
                BookingStatus.CONFIRMED, now);
        assertThat(booking.getId()).isEqualTo(id);
        assertThat(booking.getOfferId()).isEqualTo(offerId);
        assertThat(booking.getClientId()).isEqualTo("client-2");
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
    }

    // ── Value objects ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("BookingId.generate() produit des IDs uniques")
    void bookingId_generate_isUnique() {
        var a = BookingId.generate();
        var b = BookingId.generate();
        assertThat(a).isNotEqualTo(b);
        assertThat(a.value()).isNotBlank();
    }

    @Test
    @DisplayName("OfferId.of() restitue la valeur")
    void offerId_of_restoresValue() {
        var id = OfferId.of("offer-42");
        assertThat(id.value()).isEqualTo("offer-42");
    }
}